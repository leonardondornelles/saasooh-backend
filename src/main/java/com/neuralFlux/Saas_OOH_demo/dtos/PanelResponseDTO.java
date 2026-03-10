package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.enums.PanelType;
import com.neuralFlux.Saas_OOH_demo.models.Panel;

public record PanelResponseDTO(
        Long id,
        String identificationCode,
        String city,
        String address,
        Double latitude,
        Double longitude,
        PanelType type,
        Boolean illuminated
) {
    public PanelResponseDTO(Panel panel) {
        this(
                panel.getId(),
                panel.getIdentificationCode(),
                panel.getCity(),
                panel.getAddress(),
                panel.getLatitude(),
                panel.getLongitude(),
                panel.getType(),
                panel.getIlluminated()
        );
    }
}