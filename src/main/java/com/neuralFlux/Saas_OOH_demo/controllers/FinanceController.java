package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.ExecutiveRankingDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.ExpiringContractDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.FinanceDashboardDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.PipelineStageDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.FinanceService;
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

    private final FinanceService financeService;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<FinanceDashboardDTO> getDashboardData() {
        Long companyId = getAuthenticatedUser().getUser().getCompany().getId();

        FinanceDashboardDTO dashboardData = financeService.getDashboardData(companyId);

        return ResponseEntity.ok(dashboardData);
    }
}