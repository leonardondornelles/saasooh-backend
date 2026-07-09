package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.enums.CampaignStatusUpdateDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignRepository campaignRepository;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<CampaignResponseDTO> create(@RequestBody CampaignRequestDTO dto){

            UserDetailsImpl userDetails = getAuthenticatedUser();
            Long executiveId= userDetails.getUser().getId();
            Long companyId = userDetails.getUser().getCompany().getId();

            Campaign created = campaignService.createCampaign(dto, executiveId, companyId);

            return ResponseEntity.status(HttpStatus.CREATED).body(new CampaignResponseDTO(created));

        }

    @GetMapping
    public ResponseEntity<List<CampaignResponseDTO>> getAllCompanyCampaigns() {
        UserDetailsImpl userDetails = getAuthenticatedUser();
        Long companyId = userDetails.getUser().getCompany().getId();

        List<Campaign> campaigns = campaignRepository.findByCompanyIdOrderByStartDateDesc(companyId);

        List<CampaignResponseDTO> dtos = campaigns.stream().map(CampaignResponseDTO::new).toList();

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CampaignResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody CampaignStatusUpdateDTO dto) {

        UserDetailsImpl userDetails = getAuthenticatedUser();
        Long companyId = userDetails.getUser().getCompany().getId();

        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));

        if(!campaign.getCompany().getId().equals(companyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        StatusCampaign newStatus = StatusCampaign.valueOf(dto.status());
        campaign.setStatus(newStatus);

        if(dto.startDate() != null) campaign.setStartDate(dto.startDate());
        if(dto.endDate() != null) campaign.setEndDate(dto.endDate());

        if(dto.observations() != null && !dto.observations().trim().isEmpty()) {
            campaign.setObservations(dto.observations());
        }

        LocalDate today = LocalDate.now();
        if ((newStatus == StatusCampaign.APPROVED || newStatus == StatusCampaign.RESERVED)
                && campaign.getStartDate() != null
                && !campaign.getStartDate().isAfter(today)) {
            campaign.setStatus(StatusCampaign.ACTIVE);
        }

        Campaign updated = campaignRepository.save(campaign);
        return ResponseEntity.ok(new CampaignResponseDTO(updated));
    }

}
