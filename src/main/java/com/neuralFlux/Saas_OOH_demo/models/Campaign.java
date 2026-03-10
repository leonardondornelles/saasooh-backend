package com.neuralFlux.Saas_OOH_demo.models;

import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "campaigns")
@Data
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "face_id", nullable = false)
    private Face face;

    @ManyToOne
    @JoinColumn(name = "executive_id", nullable = false)
    private User executive;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private Double monthlyValue;
    private Double totalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCampaign status = StatusCampaign.RESERVED;

    private String observations;
}
