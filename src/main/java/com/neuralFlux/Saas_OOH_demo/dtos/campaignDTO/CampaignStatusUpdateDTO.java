package com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO;

import java.time.LocalDate;

public record CampaignStatusUpdateDTO(
        String status,
        LocalDate startDate,
        LocalDate endDate,
        String observations
) {
}
