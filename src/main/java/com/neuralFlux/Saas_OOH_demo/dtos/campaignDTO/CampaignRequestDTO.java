package com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO;

import java.time.LocalDate;

public record CampaignRequestDTO(
        Long customerId,
        Long faceId,
        LocalDate startDate,
        LocalDate endDate,
        Double monthlyValue
) {}