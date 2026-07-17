package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

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
