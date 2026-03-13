package com.neuralFlux.Saas_OOH_demo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
        @NotBlank(message = "A Razão Social é obrigatória")
        String corporateName,

        @NotBlank(message = "O Nome Fantasia é obrigatório")
        String fantasyName,

        @NotBlank(message = "O CNPJ é obrigatório")
        String cnpj,

        @NotBlank(message = "O telefone é obrigatório")
        String telephone,

        @Email(message = "E-mail inválido")
        String email,

        String observation
) {}