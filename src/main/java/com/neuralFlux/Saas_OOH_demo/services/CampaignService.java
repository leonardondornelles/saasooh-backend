package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignRequestDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.exceptions.ResourceNotFoundException;
import com.neuralFlux.Saas_OOH_demo.models.*;
import com.neuralFlux.Saas_OOH_demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CustomerRepository customerRepository;
    private final FaceRepository faceRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public Campaign createCampaign(CampaignRequestDTO dto){
        if(dto.endDate().isBefore(dto.startDate()))
            throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");

        long overlaps = campaignRepository.countOverlappingCampaigns(dto.faceId(), dto.startDate(), dto.endDate());

        if(overlaps > 0)
            throw new IllegalArgumentException("Overbooking: Esta Face já está reservada ou ativa para este período");

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Face face = faceRepository.findById(dto.faceId())
                .orElseThrow(() -> new IllegalArgumentException("Face não encontrada"));

        User executive = userRepository.findById(dto.executiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Executivo não encontrado"));

        Company company = companyRepository.findById(dto.companyId())
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

        return campaignRepository.save(campaign);
    }
}
