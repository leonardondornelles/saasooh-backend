package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.InvoiceResponseDTO;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Auxiliary function to get the logged
    private UserDetailsImpl getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    /**
     * Route to list all the invoices (to receive)
     */
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices() {
        Long companyId = getAuthenticatedUser().getUser().getCompany().getId();

        List<InvoiceResponseDTO> invoices = invoiceService.getAllCompanyInvoices(companyId);

        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Void> registerPayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload
            ) {
        // Extracts the data sent to frontend
        String paymentMethod = payload.get("paymentMethod"); // Ex: "PIX", "Boleto"
        String notes = payload.get("notes");                 // Ex: "Comprovante Salvo na nuvem

        invoiceService.registerPayment(id, paymentMethod, notes);

        return ResponseEntity.noContent().build();
    }
}
