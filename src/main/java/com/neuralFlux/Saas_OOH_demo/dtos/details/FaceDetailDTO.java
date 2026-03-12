package com.neuralFlux.Saas_OOH_demo.dtos.details;

import com.neuralFlux.Saas_OOH_demo.enums.FaceStatus;

public record FaceDetailDTO(
        Long id,
        String name,
        String format,
        FaceStatus status,
        Boolean lighting,
        CampaignSnippetDTO campaign // Can be null, when it's not occupied
) {}