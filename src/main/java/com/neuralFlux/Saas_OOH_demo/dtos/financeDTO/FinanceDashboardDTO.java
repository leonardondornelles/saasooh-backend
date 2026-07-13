package com.neuralFlux.Saas_OOH_demo.dtos.financeDTO;

import java.util.List;

public record FinanceDashboardDTO(
        Double currentMrr,
        Double annualArr,
        Double averageTicket,
        List<ExecutiveRankingDTO> ranking,
        List<ExpiringContractDTO> expiringContracts,
        List<PipelineStageDTO> pipeline,

        List<OccupancyCityDTO> occupancyByCity,
        List<RevenueEvolutionDTO> revenueEvolution,
        List<DelinquentClientDTO> delinquencies
) {
}


