package com.neuralFlux.Saas_OOH_demo.models;

import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleUser role;

    private Boolean active = true;
}
