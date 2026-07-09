package com.neuralFlux.Saas_OOH_demo.enums;

import java.time.LocalDate;

public record CampaignStatusUpdateDTO(
        String status,
        LocalDate startDate,
        LocalDate endDate,
        String observations
) {
}
