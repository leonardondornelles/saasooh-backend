package com.neuralFlux.Saas_OOH_demo.dtos.companyDTO;

import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;
import com.neuralFlux.Saas_OOH_demo.models.Company;

public record CompanyResponseDTO(
        Long id,
        String fantasyName,
        String cnpj,
        SaasPlan saasPlan
) {
    public CompanyResponseDTO(Company company){
        this(company.getId(), company.getFantasyName(), company.getCnpj(), company.getSaasPlan());
    }
}
