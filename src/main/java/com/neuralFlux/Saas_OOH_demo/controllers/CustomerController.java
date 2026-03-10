package com.neuralFlux.Saas_OOH_demo.controllers;


import com.neuralFlux.Saas_OOH_demo.dtos.CustomerResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Customer;
import com.neuralFlux.Saas_OOH_demo.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> save(@Valid @RequestBody Customer customer){
        Customer savedCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomerResponseDTO(savedCustomer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> listAll(){
        List<Customer> customers = customerService.getAllCustomers();

        List<CustomerResponseDTO> dtos = customers.stream()
                .map(CustomerResponseDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
