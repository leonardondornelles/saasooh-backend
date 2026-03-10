package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import com.neuralFlux.Saas_OOH_demo.models.User;


public record UserResponseDTO(
        Long id,
        String name,
        String email,
        RoleUser role,
        Long companyId,
        Long customerId
) {
    public UserResponseDTO(User user){
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCompany() != null ?user.getCompany().getId() : null,
                user.getCustomer() != null ?user.getCustomer().getId() : null
        );
    }
}
