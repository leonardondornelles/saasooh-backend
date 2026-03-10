package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignResponseDTO> createCampaign(@RequestBody CampaignRequestDTO dto){
        Campaign savedCampaign = campaignService.createCampaign(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CampaignResponseDTO(savedCampaign));
    }
}
