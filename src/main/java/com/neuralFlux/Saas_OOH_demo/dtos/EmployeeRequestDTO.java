package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;

public record EmployeeRequestDTO(
        String name,
        String email,
        String password,
        RoleUser role
) {
}
