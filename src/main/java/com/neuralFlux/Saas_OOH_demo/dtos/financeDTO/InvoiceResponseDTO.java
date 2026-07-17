package com.neuralFlux.Saas_OOH_demo.dtos.financeDTO;

import com.neuralFlux.Saas_OOH_demo.models.Invoice;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record InvoiceResponseDTO(
        Long id,
        Long campaignId,
        String customerName,
        Double amount,
        LocalDate dueDate,
        LocalDate paymentDate,
        String paymentMethod,
        String status
) {
    public InvoiceResponseDTO(Invoice invoice){
        this(
                invoice.getId(),
                invoice.getCampaign().getId(),
                invoice.getCampaign().getCustomer().getFantasyName(),
                invoice.getAmount(),
                invoice.getDueDate(),
                invoice.getPaymentDate(),
                invoice.getPaymentMethod(),
                invoice.getStatus().name()
        );
    }
}
