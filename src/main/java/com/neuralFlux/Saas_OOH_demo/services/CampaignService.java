package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignStatusUpdateDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.exceptions.ResourceNotFoundException;
import com.neuralFlux.Saas_OOH_demo.models.*;
import com.neuralFlux.Saas_OOH_demo.repositories.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CustomerRepository customerRepository;
    private final FaceRepository faceRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final InvoiceService invoiceService;

    @Transactional
    public Campaign createCampaign(CampaignRequestDTO dto, Long executiveId, Long companyId){
        if(dto.startDate() != null && dto.endDate() != null) {
            if (dto.endDate().isBefore(dto.startDate()))
                throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
        }

        long overlaps = campaignRepository.countOverlappingCampaigns(
                dto.faceId(),
                dto.startDate(),
                dto.endDate(),
                List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED)
            );

        if(overlaps > 0)
            throw new IllegalArgumentException("Overbooking: Esta Face já está reservada ou ativa para este período");

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Face face = faceRepository.findById(dto.faceId())
                .orElseThrow(() -> new IllegalArgumentException("Face não encontrada"));

        User executive = userRepository.findById(executiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Executivo não encontrado"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não foi encontrada"));

        Campaign campaign = new Campaign();
        campaign.setCustomer(customer);
        campaign.setFace(face);
        campaign.setStartDate(dto.startDate());
        campaign.setEndDate(dto.endDate());
        campaign.setMonthlyValue(dto.monthlyValue());
        campaign.setStatus(StatusCampaign.RESERVED);
        campaign.setExecutive(executive);
        campaign.setCompany(company);


        if (dto.startDate() == null || dto.endDate() == null) {
            campaign.setStatus(StatusCampaign.PROPOSAL);
        } else {
            LocalDate today = LocalDate.now();
            if (dto.startDate().isAfter(today)) {
                campaign.setStatus(StatusCampaign.RESERVED);
            } else {
                campaign.setStatus(StatusCampaign.ACTIVE);
            }
        }

        Campaign savedCampaign = campaignRepository.save(campaign);


        if (savedCampaign.getStatus() == StatusCampaign.ACTIVE) {
            invoiceService.generateInvoicesForCampaign(savedCampaign);
        }

        return savedCampaign;
    }

    /**
     * Searches all the campaigns from a company and formats them to frontend
     */

    public List<CampaignResponseDTO> getAllCompanyCampaigns(Long companyId){
        List<Campaign> campaigns = campaignRepository.findByCompanyIdOrderByStartDateDesc(companyId);
        return campaigns.stream().map(CampaignResponseDTO::new).toList();
    }

    /**
     * Updates the status of the campaign with the validations and generates de receipt in case activated
     */
    @Transactional
    public Campaign updateCampaignStatus(Long campaignId, Long companyId, CampaignStatusUpdateDTO dto) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));

        // Security: Guarantees that the campaign is from the current company
        if(!campaign.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Sem permissão para alterar esta campanha");
        }

        StatusCampaign newStatus = StatusCampaign.valueOf(dto.status());
        StatusCampaign oldStatus = campaign.getStatus();

        if((oldStatus == StatusCampaign.ACTIVE || oldStatus == StatusCampaign.RESERVED || oldStatus == StatusCampaign.COMPLETED)
                && (newStatus == StatusCampaign.PROPOSAL || newStatus == StatusCampaign.NEGOTIATION)){
            throw new IllegalArgumentException("Não é permitido retroceder uma campanha para fases de negociação");
        }

        campaign.setStatus(newStatus);

        // Updates the optional camps
        if(dto.startDate() != null) campaign.setStartDate(dto.startDate());
        if(dto.endDate() != null) campaign.setEndDate(dto.endDate());
        if(dto.observations() != null && !dto.observations().trim().isEmpty()) {
            campaign.setObservations(dto.observations());
        }

        // Automatic activation based on the date
        LocalDate today = LocalDate.now();
        if ((newStatus == StatusCampaign.APPROVED || newStatus == StatusCampaign.RESERVED)
                && campaign.getStartDate() != null
                && !campaign.getStartDate().isAfter(today)) {
            campaign.setStatus(StatusCampaign.ACTIVE);
        }

        Campaign updated = campaignRepository.save(campaign);

        // If the campaign isn't active, and from this update became active:
        if (oldStatus != StatusCampaign.ACTIVE && updated.getStatus() == StatusCampaign.ACTIVE) {
            invoiceService.generateInvoicesForCampaign(updated);
        }

        return updated;
    }

    /**
     * Runs every day in midnight and always when the Spring boot server is initialized
     */
    @Scheduled(cron = "0 0 0 * * *") // Midnight trigger
    @EventListener(ApplicationReadyEvent.class) // Server initialization trrigger
    @Transactional
    public void updateCampaignLifecycles() {
        LocalDate today = LocalDate.now();

        // If the campaign start date has arrived or passed turns it into ACTIVE
        List<Campaign> toActive = campaignRepository.findAllByStatusIn(
                List.of(StatusCampaign.APPROVED, StatusCampaign.RESERVED)
        );
        for(Campaign c : toActive) {
            // Verifies if the date exists before comparing, avoiding NullPointerException
            if(c.getStartDate() != null && !c.getStartDate().isAfter(today)) {
                c.setStatus(StatusCampaign.ACTIVE);
                campaignRepository.save(c);

                invoiceService.generateInvoicesForCampaign(c);
            }
        }

        List<Campaign> toComplete = campaignRepository.findAllByStatusIn(List.of(StatusCampaign.ACTIVE));
        for(Campaign c : toComplete){
            if(c.getEndDate() != null && c.getEndDate().isBefore(today)) {
                c.setStatus(StatusCampaign.COMPLETED);
                campaignRepository.save(c);
            }
        }
    }
}
