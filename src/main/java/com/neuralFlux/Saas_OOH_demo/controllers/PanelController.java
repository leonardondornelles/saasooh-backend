package com.neuralFlux.Saas_OOH_demo.controllers;


import com.neuralFlux.Saas_OOH_demo.dtos.PanelRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.PanelResponseDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.details.PanelDetailsDTO;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Panel;
import com.neuralFlux.Saas_OOH_demo.security.SecurityConfig;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.PanelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panels")
@RequiredArgsConstructor
public class PanelController {

    private final PanelService panelService;

    @PostMapping
    public ResponseEntity<PanelResponseDTO> save(@RequestBody PanelRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        Long companyId = userDetails.getUser().getCompany().getId();

        Panel savedPanel = panelService.createPanel(companyId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new PanelResponseDTO(savedPanel));
    }

    @GetMapping
    public ResponseEntity<List<PanelResponseDTO>> listAll(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        Long companyId = userDetails.getUser().getCompany().getId();

        List<PanelResponseDTO> dtos = panelService.getPanelByCompanyId(companyId)
                .stream().map(PanelResponseDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PanelDetailsDTO> getPanelById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long companyId = userDetails.getUser().getCompany().getId();

        PanelDetailsDTO panelDetails = panelService.getPanelDetails(id, companyId);

        return ResponseEntity.ok(panelDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long companyId = userDetails.getUser().getCompany().getId();

        panelService.deletePanel(id, companyId);

        return ResponseEntity.noContent().build();
    }
}
