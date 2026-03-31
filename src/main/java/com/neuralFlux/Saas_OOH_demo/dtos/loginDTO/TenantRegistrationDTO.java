package com.neuralFlux.Saas_OOH_demo.dtos.loginDTO;

import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;

public record TenantRegistrationDTO(
        String corporateName,
        String fantasyName,
        String cnpj,
        SaasPlan saasPlan,

        // Company Owner Details
        String adminName,
        String adminEmail,
        String adminPassword
) {


}
