package com.neuralFlux.Saas_OOH_demo.controllers;


import com.neuralFlux.Saas_OOH_demo.dtos.PanelResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.services.PanelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panels")
@RequiredArgsConstructor
public class PanelController {

    private final PanelService panelService;

    @PostMapping("/companies/{companyId}")
    public ResponseEntity<PanelResponseDTO> save(@PathVariable Long companyId, @Valid @RequestBody Panel panel) {
        Panel savedPanel = panelService.createPanel(companyId, panel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PanelResponseDTO(savedPanel));
    }

    @GetMapping
    public ResponseEntity<List<PanelResponseDTO>> listAll(){

        List<PanelResponseDTO> dtos = panelService.getAllPanels().stream()
                .map(PanelResponseDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
