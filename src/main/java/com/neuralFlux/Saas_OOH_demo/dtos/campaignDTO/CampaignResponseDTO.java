package com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO;

import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;

import java.time.LocalDate;

public record CampaignResponseDTO(
        Long id,
        String customerName,
        String faceName,
        String panelAddress,
        LocalDate startDate,
        LocalDate endDate,
        Double monthlyValue,
        StatusCampaign status
) {
    public CampaignResponseDTO(Campaign campaign) {
        this(
                campaign.getId(),
                campaign.getCustomer().getFantasyName(),
                campaign.getFace().getName(),
                campaign.getFace().getPanel().getAddress(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getMonthlyValue(),
                campaign.getStatus()
        );
    }
}