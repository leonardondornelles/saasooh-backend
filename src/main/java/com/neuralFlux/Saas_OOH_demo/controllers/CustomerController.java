package com.neuralFlux.Saas_OOH_demo.controllers;


import com.neuralFlux.Saas_OOH_demo.dtos.CustomerRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.CustomerResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Customer;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    private Long getCompanyIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userDetails.getUser().getCompany().getId();
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Valid CustomerRequestDTO dto){
        Long companyId = getCompanyIdFromToken();
        CustomerResponseDTO created = customerService.createCustomer(companyId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> listAll(){
        Long companyId = getCompanyIdFromToken();
        return ResponseEntity.ok(customerService.listCustomers(companyId));
    }
}
