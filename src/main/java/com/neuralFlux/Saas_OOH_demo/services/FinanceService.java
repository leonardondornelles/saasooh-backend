package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.*;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.Invoice;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.InvoiceRepository;
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
    private final InvoiceRepository invoiceRepository;

    @Transactional(readOnly = true)
    public FinanceDashboardDTO getDashboardData(Long companyId) {
        List<StatusCampaign> activeStatuses = List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED);

        // Calculate MRR, ARR, and Average Ticket
        Double currentMrr = campaignRepository.sumMrrByCompany(companyId, activeStatuses);
        if (currentMrr == null) currentMrr = 0.0;
        Double annualArr = currentMrr * 12;

        long totalActive = campaignRepository.countByCompanyIdAndStatusIn(companyId, activeStatuses);
        Double averageTicket = totalActive > 0 ? currentMrr / totalActive : 0.0;

        // Retrieve executive ranking and expiring contracts
        List<ExecutiveRankingDTO> ranking = campaignRepository.getExecutiveRanking(companyId, activeStatuses);
        LocalDate today = LocalDate.now();
        List<Campaign> expiring = campaignRepository.findExpiringCampaigns(
                companyId,
                activeStatuses,
                today,
                today.plusDays(30)
        );

        List<ExpiringContractDTO> expiringContractDTOS = expiring.stream().map(camp -> {
            long remainingDays = ChronoUnit.DAYS.between(today, camp.getEndDate());
            String urgency = remainingDays <= 7 ? "urgent" : (remainingDays <= 15 ? "soon" : "ok");

            return new ExpiringContractDTO(
                    camp.getId(),
                    camp.getCustomer().getFantasyName(),
                    1,
                    camp.getMonthlyValue(),
                    remainingDays,
                    urgency,
                    camp.getFace().getPanel().getId(),
                    camp.getFace().getPanel().getAddress(),
                    camp.getEndDate()
            );
        }).toList();

        // Build sales pipeline overview
        List<PipelineStageDTO> pipeline = List.of(
                new PipelineStageDTO("Proposals", getCount(companyId, StatusCampaign.PROPOSAL), "#2563eb"),
                new PipelineStageDTO("Negotiation", getCount(companyId, StatusCampaign.NEGOTIATION), "#7c3aed"),
                new PipelineStageDTO("Approved", getCount(companyId, StatusCampaign.APPROVED), "#0891b2")
        );

        // Calculate occupancy rate by city
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
                            .filter(f -> f.getCampaigns() != null &&
                                    f.getCampaigns().stream()
                                            .anyMatch(c -> activeStatuses.contains(c.getStatus())))
                            .count();
                }
            }

            int pct = totalFaces > 0
                    ? Math.round(((float) activeFaces / totalFaces) * 100)
                    : 0;

            return new OccupancyCityDTO(entry.getKey(), pct, totalFaces, activeFaces);
        }).toList();

        // Revenue evolution (currently simulated based on the current MRR)
        List<RevenueEvolutionDTO> revenueEvolution = List.of(
                new RevenueEvolutionDTO("Mês -4", currentMrr * 0.8, currentMrr * 0.85),
                new RevenueEvolutionDTO("Mês -3", currentMrr * 0.85, currentMrr * 0.9),
                new RevenueEvolutionDTO("Mês -2", currentMrr * 0.92, currentMrr * 0.95),
                new RevenueEvolutionDTO("Mês -1", currentMrr * 0.98, currentMrr * 0.98),
                new RevenueEvolutionDTO("Atual", currentMrr, currentMrr),
                new RevenueEvolutionDTO("Próximo", null, currentMrr * 1.1)
        );

        // Empty delinquency list (will be populated once the Invoices module is implemented)
        List<DelinquentClientDTO> delinquencies = List.of();

        Double totalOverdue = invoiceRepository.sumOverdueAmount(companyId, today);
        if(totalOverdue == null){
            totalOverdue = 0.0;
        }

        // Searches the total sum of overdue payment
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(companyId, today);

        // Groups the invoices that are overdue by name of client and calculates the total due and how many days it is late
        delinquencies = overdueInvoices.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getCampaign().getCustomer().getFantasyName(),
                        Collectors.summingDouble(Invoice::getAmount)
                ))
                .entrySet().stream()
                .map(entry -> {
                    // Finds the oldest invoice from the client to know the worst case
                    long maxDays = overdueInvoices.stream()
                            .filter(i -> i.getCampaign().getCustomer().getFantasyName().equals(entry.getKey()))
                            .mapToLong(i -> ChronoUnit.DAYS.between(i.getDueDate(), today))
                            .max().orElse(0L);

                    // Creates another DTO that travels to the frontend
                    return new DelinquentClientDTO(entry.getKey(), entry.getValue(), (int) maxDays);
                }).toList();

        return new FinanceDashboardDTO(
                currentMrr,
                annualArr,
                averageTicket,
                ranking,
                expiringContractDTOS,
                pipeline,
                occupancyData,
                revenueEvolution,
                delinquencies
        );
    }

    private Long getCount(Long companyId, StatusCampaign status) {
        Long count = campaignRepository.countByCompanyIdAndStatus(companyId, status);
        return count != null ? count : 0L;
    }
}