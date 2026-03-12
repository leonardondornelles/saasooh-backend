package com.neuralFlux.Saas_OOH_demo.dtos.details;

import java.util.List;

public record PanelDetailsDTO(
        String id,
        String type,
        String address,
        String city,
        Double lat,
        Double lng,
        List<FaceDetailDTO> faces
) {}
