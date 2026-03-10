package com.neuralFlux.Saas_OOH_demo.dtos;

import com.neuralFlux.Saas_OOH_demo.models.Customer;

public record CustomerResponseDTO(
        Long id,
        String corporateName,
        String fantasyName,
        String cnpj,
        String telephone,
        String email
) {
    public CustomerResponseDTO(Customer customer) {
        this(
                customer.getId(),
                customer.getCorporateName(),
                customer.getFantasyName(),
                customer.getCnpj(),
                customer.getTelephone(),
                customer.getEmail()
        );
    }
}
