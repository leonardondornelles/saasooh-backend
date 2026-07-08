package com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO;

import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import lombok.Getter;

import java.time.LocalDate;

/**
 * DTO responsável por enviar as informações das Campanhas de forma segura e limpa para o Frontend.
 */
@Getter
public class CampaignResponseDTO {

    private Long id;
    private String customerName;
    private String faceName;

    // 🚀 Novos campos adicionados!
    private Long panelId;
    private String panelAddress;
    private Double monthlyValue;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    // Construtor que recebe a entidade e a converte num DTO
    public CampaignResponseDTO(Campaign campaign) {
        this.id = campaign.getId();

        // Proteções simples caso o cliente ou a face não existam por algum motivo
        if (campaign.getCustomer() != null) {
            this.customerName = campaign.getCustomer().getFantasyName();
        }

        if (campaign.getFace() != null) {
            this.faceName = campaign.getFace().getName();

            // Navegamos da Campanha -> para a Face -> para o Painel para pegar o ID e o Endereço
            if (campaign.getFace().getPanel() != null) {
                this.panelId = campaign.getFace().getPanel().getId();
                this.panelAddress = campaign.getFace().getPanel().getAddress();
            }
        }

        this.startDate = campaign.getStartDate();
        this.endDate = campaign.getEndDate();
        this.monthlyValue = campaign.getMonthlyValue(); // Mapeia o valor mensal

        if (campaign.getStatus() != null) {
            this.status = campaign.getStatus().name();
        }
    }
}