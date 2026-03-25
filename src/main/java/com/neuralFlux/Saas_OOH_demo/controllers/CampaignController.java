package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CampaignRequestDTO dto){
            UserDetailsImpl userDetails = getAuthenticatedUser();
            Long executiveId= userDetails.getUser().getId();
            Long companyId = userDetails.getUser().getCompany().getId();

            Campaign created = campaignService.createCampaign(dto, executiveId, companyId);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        }
    }
