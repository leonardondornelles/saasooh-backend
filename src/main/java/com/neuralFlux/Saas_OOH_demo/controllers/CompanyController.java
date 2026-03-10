package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.companyDTO.CompanyRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.companyDTO.CompanyResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> save(@RequestBody CompanyRequestDTO dto){
        Company savedCompany = companyService.createCompany(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CompanyResponseDTO(savedCompany));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> listAll(){
        List<Company> companies = companyService.getAllCompanies();

        List<CompanyResponseDTO> dtos = companies.stream()
                .map(CompanyResponseDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
