package com.neuralFlux.Saas_OOH_demo.models;

import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "companies")
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String corporateName;
    private String fantasyName;
    private String cnpj;

    @Enumerated(EnumType.STRING)
    private SaasPlan saasPlan;

    private Boolean active = true;

}

