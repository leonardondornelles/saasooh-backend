package com.neuralFlux.Saas_OOH_demo.dtos.details;

public record CampaignSnippetDTO(
        String customerName,
        String startDate,
        String endDate,
        Integer daysLeft,
        Integer totalDays,
        String contractValue
) {}