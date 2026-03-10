package com.neuralFlux.Saas_OOH_demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "faces")
@Data
public class Face {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "panel_id",  nullable = false)
    private Panel panel;

    @NotBlank(message = "O nome da face é obrigatório")
    private String name; // Ex: "Face A"

    @NotBlank(message = "O formato da face é obrigatório")
    private String format; // Ex: "7x3,60"
    private Double tableValue;
    private Boolean active;
}
