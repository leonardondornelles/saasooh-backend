package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;

import java.util.List;

public record ExecutivePerformanceDTO(
        Long executiveId,
        String name,
        String email,
        String role,
        Double totalMrr,
        Long activeCampaignsCount,
        List<CampaignResponseDTO> campaigns
) {
}
