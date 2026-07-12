package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.enums.PanelType;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Panel;

public record PanelResponseDTO(
        Long id,
        String identificationCode,
        String city,
        String address,
        Double latitude,
        Double longitude,
        PanelType type,
        Boolean illuminated,

        Integer totalFaces,
        Integer availableFaces

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
                panel.getIlluminated(),

                panel.getFaces() != null ? panel.getFaces().size() :  0,

                panel.getFaces() != null ? (int) panel.getFaces().stream()
                                                 .filter(face -> face.getCampaigns() == null || face.getCampaigns().stream()
                                                                                                .noneMatch(camp -> camp.getStatus() == StatusCampaign.ACTIVE || camp.getStatus() == StatusCampaign.RESERVED))
                                                 .count() : 0
        );
    }
}