package com.neuralFlux.Saas_OOH_demo.dtos.companyDTO;

import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;

public record CompanyRequestDTO(
        String corporateName,
        String fantasyName,
        String cnpj,
        SaasPlan saasPlan
) {}