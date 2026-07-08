package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.ExecutiveRankingDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.ExpiringContractDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.FinanceDashboardDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.PipelineStageDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final CampaignRepository campaignRepository;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<FinanceDashboardDTO> getDashboardData() {
        Long companyId = getAuthenticatedUser().getUser().getCompany().getId();

        // Focamos apenas nas campanhas que estão ativas ou reservadas (gerando dinheiro)
        List<StatusCampaign> activeStatuses = List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED);

        // 1. MRR (Faturamento Mensal) e Ticket Médio
        Double currentMrr = campaignRepository.sumMrrByCompany(companyId, activeStatuses);
        if (currentMrr == null) currentMrr = 0.0;

        Double annualArr = currentMrr * 12;

        // Opcional: Se já tiveres um countByCompanyIdAndStatusIn, usa-o aqui. Senão o count() geral quebra o galho no MVP.
        long totalActive = campaignRepository.count();
        Double averageTicket = totalActive > 0 ? currentMrr / totalActive : 0.0;

        // 2. Ranking de Vendedores
        List<ExecutiveRankingDTO> ranking = campaignRepository.getExecutiveRanking(companyId, activeStatuses);

        // 3. Contratos a Vencer (Próximos 30 dias)
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(30);
        List<Campaign> expiring = campaignRepository.findExpiringCampaigns(companyId, activeStatuses, today, limit);

        List<ExpiringContractDTO> expiringDTOs = expiring.stream().map(camp -> {
            long remainingDays = ChronoUnit.DAYS.between(today, camp.getEndDate());

            // Define as cores (urgência) para o React
            String urgency = "ok";
            if (remainingDays <= 7) urgency = "urgent";
            else if (remainingDays <= 15) urgency = "soon";

            return new ExpiringContractDTO(
                    camp.getId(),
                    camp.getCustomer().getFantasyName(), // customerName
                    1, // panelsCount (1 face por campanha)
                    camp.getMonthlyValue(), // monthlyValue
                    remainingDays, // remainingDays
                    urgency,
                    camp.getFace().getPanel().getId(), // panelId
                    camp.getFace().getPanel().getAddress(), // panelAddress
                    camp.getEndDate() // endDate
            );
        }).toList();

        Long countProposals = campaignRepository.countByCompanyIdAndStatus(companyId, StatusCampaign.PROPOSAL);
        Long countNegotiations = campaignRepository.countByCompanyIdAndStatus(companyId, StatusCampaign.NEGOTIATION);
        Long countApproved = campaignRepository.countByCompanyIdAndStatus(companyId, StatusCampaign.APPROVED);
        Long countLost = campaignRepository.countByCompanyIdAndStatus(companyId, StatusCampaign.LOST);

        List<PipelineStageDTO> pipeline = List.of(
                new PipelineStageDTO("Propostas", countProposals != null ? countProposals : 0L, "#2563eb"), // Azul
                new PipelineStageDTO("Negociação", countNegotiations != null ? countNegotiations : 0L, "#7c3aed"), // Roxo
                new PipelineStageDTO("Aprovados", countApproved != null ? countApproved : 0L, "#0891b2") // Teal
        );

        // 5. Monta a resposta final
        FinanceDashboardDTO dashboardData = new FinanceDashboardDTO(
                currentMrr, annualArr, averageTicket, ranking, expiringDTOs, pipeline // 🚀 Pipeline adicionado!
        );

        return ResponseEntity.ok(dashboardData);
    }
}