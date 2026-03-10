package com.neuralFlux.Saas_OOH_demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;


    @NotBlank(message = "O Nome da Marca é obrigatório")
    private String fantasyName;

    @NotBlank(message = "a Razão Social é obrigatória")
    private String corporateName;

    @NotBlank(message = "O CNPJ é obrigatório")
    private String cnpj;

    @NotBlank(message = "É Necessário adicionar um Telefone para contato")
    private String telephone;

    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    @Column(columnDefinition = "TEXT")
    private String observation;

    private Boolean active = true;
}
