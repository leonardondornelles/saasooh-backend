package com.neuralFlux.Saas_OOH_demo.dtos.financeDTO;

import java.time.LocalDate;

public record ExpiringContractDTO(
        Long campaignId,
        String customerName,
        Integer panelCount,
        Double monthlyValue,
        Long remainingDays,
        String urgency,
        Long panelId,
        String panelAddress,
        LocalDate endDate
) {
}
