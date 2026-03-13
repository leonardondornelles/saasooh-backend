package com.neuralFlux.Saas_OOH_demo.models;

import com.neuralFlux.Saas_OOH_demo.enums.PanelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "panels")
@Data
public class Panel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "panel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Face> faces;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    @NotBlank(message = "O código de identificação é obrigatório")
    private String identificationCode;

    @NotBlank(message = "A cidade é obrigatória")
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "O endereço é obrigatório")
    private String address;

    @NotNull(message = "A latitude não pode estar vazia")
    private Double latitude;
    @NotNull(message = "A longitude não pode estar vazia")
    private Double longitude;

    @NotNull(message = "O tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    private PanelType type;

    private Boolean illuminated = false;
    private Boolean active = true;



}
