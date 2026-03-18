package com.neuralFlux.Saas_OOH_demo.dtos.details;

import com.neuralFlux.Saas_OOH_demo.enums.FaceStatus;

import java.util.List;

public record FaceDetailDTO(
        Long id,
        String name,
        String format,
        FaceStatus status,
        Boolean lighting,
        List<CampaignSnippetDTO> campaigns // Can be null, when it's not occupied
) {}