package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.enums.PanelType;

public record PanelRequestDTO(
        String address,
        String city,
        Double latitude,
        Double longitude,
        PanelType panelType
) {}