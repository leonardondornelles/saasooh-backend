package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.*;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.PanelRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CampaignRepository campaignRepository;
    private final PanelRepository panelRepository;

    @Transactional(readOnly = true)
    public FinanceDashboardDTO getDashboardData(Long companyId) {
        List<StatusCampaign> activeStatuses = List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED);

        // MRR, ARR e ticket Médio
        Double currentMrr = campaignRepository.sumMrrByCompany(companyId, activeStatuses);
        if(currentMrr == null) currentMrr = 0.0;
        Double annualArr = currentMrr * 12;

        long totalActive = campaignRepository.countByCompanyIdAndStatusIn(companyId, activeStatuses);
        Double averageTicket = totalActive > 0 ? currentMrr / totalActive : 0.0;

        List<ExecutiveRankingDTO> ranking = campaignRepository.getExecutiveRanking(companyId, activeStatuses);
        LocalDate today = LocalDate.now();
        List<Campaign> expiring = campaignRepository.findExpiringCampaigns(companyId, activeStatuses, today, today.plusDays(30));

        List<ExpiringContractDTO> expiringContractDTOS = expiring.stream().map(camp -> {
            long remainingDays = ChronoUnit.DAYS.between(today, camp.getEndDate());
            String urgency = remainingDays <= 7 ? "urgent" : (remainingDays <= 15 ? "soon" : "ok");

            return new ExpiringContractDTO(
                    camp.getId(), camp.getCustomer().getFantasyName(), 1,
                    camp.getMonthlyValue(), remainingDays, urgency,
                    camp.getFace().getPanel().getId(), camp.getFace().getPanel().getAddress(), camp.getEndDate()
            );
        }).toList();

        List<PipelineStageDTO> pipeline = List.of(
                new PipelineStageDTO("Propostas", getCount(companyId, StatusCampaign.PROPOSAL), "#2563eb"),
                new PipelineStageDTO("Negociação", getCount(companyId, StatusCampaign.NEGOTIATION), "#7c3aed"),
                new PipelineStageDTO("Aprovados", getCount(companyId, StatusCampaign.APPROVED), "#0891b2")
        );

        List<Panel> allPanels = panelRepository.findByCompanyIdAndActiveTrue(companyId);
        Map<String, List<Panel>> panelsByCity = allPanels.stream()
                .filter(p -> p.getCity() != null)
                .collect(Collectors.groupingBy(Panel::getCity));

        List<OccupancyCityDTO> occupancyData = panelsByCity.entrySet().stream().map(entry -> {
            int totalFaces = 0, activeFaces = 0;
            for (Panel p : entry.getValue()) {
                if (p.getFaces() != null) {
                    totalFaces += p.getFaces().size();
                    activeFaces += (int) p.getFaces().stream()
                            .filter(f -> f.getCampaigns() != null && f.getCampaigns().stream()
                                    .anyMatch(c -> activeStatuses.contains(c.getStatus())))
                            .count();
                }
            }
            int pct = totalFaces > 0 ? Math.round(((float) activeFaces / totalFaces) * 100) : 0;
            return new OccupancyCityDTO(entry.getKey(), pct, totalFaces, activeFaces);
        }).toList();

        // 🚀 5. EVOLUÇÃO DO FATURAMENTO (Simulação para o gráfico baseda no MRR atual)
        List<RevenueEvolutionDTO> revenueEvolution = List.of(
                new RevenueEvolutionDTO("Mês -4", currentMrr * 0.8, currentMrr * 0.85),
                new RevenueEvolutionDTO("Mês -3", currentMrr * 0.85, currentMrr * 0.9),
                new RevenueEvolutionDTO("Mês -2", currentMrr * 0.92, currentMrr * 0.95),
                new RevenueEvolutionDTO("Mês -1", currentMrr * 0.98, currentMrr * 0.98),
                new RevenueEvolutionDTO("Atual", currentMrr, currentMrr),
                new RevenueEvolutionDTO("Próximo", null, currentMrr * 1.1)
        );

        // 6. Inadimplência vazia (Será preenchida quando criarmos o módulo de Faturas)
        List<DelinquentClientDTO> delinquencies = List.of();

        return new FinanceDashboardDTO(
                currentMrr, annualArr, averageTicket, ranking, expiringContractDTOS, pipeline,
                occupancyData, revenueEvolution, delinquencies
        );
    }

    private Long getCount(Long companyId, StatusCampaign status) {
        Long count = campaignRepository.countByCompanyIdAndStatus(companyId, status);
        return count != null ? count : 0L;
    }
}

