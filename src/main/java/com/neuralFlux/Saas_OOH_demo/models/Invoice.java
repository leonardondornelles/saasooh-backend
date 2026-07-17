package com.neuralFlux.Saas_OOH_demo.models;

import com.neuralFlux.Saas_OOH_demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // To wich contract this Invoice belongs to
    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    // Isolates by tenant (Company that owns the system)
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Value of the parcel
    @Column(nullable = false)
    private Double amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // --- MANUAL CONTROLS ZONE ---

    // The date when the payment was received in the bank account (Manually filled)
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    // Method of payment by the client (Ex: "Boleto Banco X", "PIX", "TED)
    @Column(name = "payment_method")
    private String paymentMethod;

    // Open camp so the financial can paste the receipt number,
    @Column(name = "financial_notes", columnDefinition = "TEXT")
    private String financialNotes;

    // The invoice status (Always starts as PENDING)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;


    
}
