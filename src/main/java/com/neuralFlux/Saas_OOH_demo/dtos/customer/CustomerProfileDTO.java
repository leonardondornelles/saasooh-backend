package com.neuralFlux.Saas_OOH_demo.dtos.customer;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Customer;

import java.util.List;


public record CustomerProfileDTO(
        Long id,
        String corporateName,
        String fantasyName,
        String cnpj,
        String telephone,
        String email,

        // Métricas de Negócio Calculadas
        Double totalRevenue,
        Double averageTicket,
        Integer totalCampaigns,
        Integer activeCampaignsCount,

        // Histórico de Campanhas
        List<CampaignResponseDTO> campaignHistory
) {
    // Construtor personalizado que recebe o Cliente e as métricas calculadas pelo Service
    public CustomerProfileDTO(
            Customer customer,
            Double totalRevenue,
            Double averageTicket,
            Integer totalCampaigns,
            Integer activeCampaignsCount,
            List<CampaignResponseDTO> campaignHistory
    ) {
        this(
                customer.getId(),
                customer.getCorporateName(),
                customer.getFantasyName(),
                customer.getCnpj(),
                customer.getTelephone(),
                customer.getEmail(),
                totalRevenue,
                averageTicket,
                totalCampaigns,
                activeCampaignsCount,
                campaignHistory
        );
    }
}