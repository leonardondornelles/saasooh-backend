package com.neuralFlux.Saas_OOH_demo.enums;

public enum PaymentStatus {
    PENDING,        // Waiting for payment (Soon to be overdue)
    PAID,           // Payment Confirmed (Manual Effectuation)
    OVERDUE,
    CANCELLED
}
