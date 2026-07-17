package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.enums.PaymentStatus;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.Invoice;
import com.neuralFlux.Saas_OOH_demo.repositories.InvoiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    /**
     * Generates automatically invoices using as base the duration of the campaign
     * Example:  A campaign of 3 months will generate 3 invoices with overdue subsequently.
     */
    @Transactional
    public void generateInvoicesForCampaign(Campaign campaign){
        if(campaign.getStartDate() == null || campaign.getEndDate() == null){
            return; // Protection: Doesn't generate any invoices if the dates are not defined
        }

        long months = ChronoUnit.MONTHS.between(campaign.getStartDate(), campaign.getEndDate());
        if(months <= 0){
            months = 1; // Guarantees that, at least one invoice will be generated;
        }

        // Loop to generate invoice for each month of contract
        for(int i = 0; i <months; i++){
            Invoice invoice = new Invoice();
            invoice.setCampaign(campaign);
            invoice.setCompany(campaign.getCompany());
            invoice.setAmount(campaign.getMonthlyValue());

            // Defines the overdue: the date of start summed to the quantity of months from the loop
            invoice.setDueDate(campaign.getStartDate().plusMonths(i));

            invoice.setStatus(PaymentStatus.PENDING); // The invoice always starts as pending

            invoiceRepository.save(invoice);
        }
    }

    /**
     * Registers the manual receipt from an invoice in the system
     */

    @Transactional
    public void registerPayment(Long invoiceId, String paymentMethod, String notes){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada"));

        // Fills the data with payment and finishes the invoice
        invoice.setPaymentDate(LocalDate.now());
        invoice.setPaymentMethod(paymentMethod);
        invoice.setFinancialNotes(notes);
        invoice.setStatus(PaymentStatus.PAID);

        invoiceRepository.save(invoice);
    }
}
