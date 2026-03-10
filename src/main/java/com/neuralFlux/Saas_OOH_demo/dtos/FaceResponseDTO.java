package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.models.Face;

public record FaceResponseDTO(
        Long id,
        String name,
        String format,
        Double tablePrice,
        Long panelId,
        String panelAddress
) {
    public FaceResponseDTO(Face face) {
        this(
                face.getId(),
                face.getName(),
                face.getFormat(),
                face.getTableValue(),
                face.getPanel().getId(),
                face.getPanel().getAddress()
        );
    }
}