package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.InvoiceResponseDTO;
import com.neuralFlux.Saas_OOH_demo.enums.PaymentStatus;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.Invoice;
import com.neuralFlux.Saas_OOH_demo.repositories.InvoiceRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

        List<Invoice> invoicesToSave = new ArrayList<>();

        // Loop to generate invoice for each month of contract
        for(int i = 0; i <months; i++){
            Invoice invoice = new Invoice();
            invoice.setCampaign(campaign);
            invoice.setCompany(campaign.getCompany());
            invoice.setAmount(campaign.getMonthlyValue());

            // Defines the overdue: the date of start summed to the quantity of months from the loop
            Double monthlyValue = campaign.getMonthlyValue() != null ? campaign.getMonthlyValue() : 0.0;
            invoice.setAmount(monthlyValue);

            invoice.setDueDate(campaign.getStartDate().plusMonths(i));
            invoice.setStatus(PaymentStatus.PENDING); // The invoice always starts as pending

            invoicesToSave.add(invoice);
        }

        invoiceRepository.saveAll(invoicesToSave);
        System.out.println("SUCESSO: Geradas " + invoicesToSave.size() + " faturas para a Campanha #" + campaign.getId());
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

    /**
     * Searches all the invoices from the company to the screen of financial listing
     */
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAllCompanyInvoices(Long companyId) {
        return invoiceRepository.findByCompanyIdOrderByDueDateDesc(companyId)
                .stream()
                .map(InvoiceResponseDTO::new)
                .toList();
    }
}
