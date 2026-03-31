package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.EmployeeRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.UserRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.UserResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    // Uses the admin token, to create a new Employee
    @PostMapping("/employee")
    public ResponseEntity<UserResponseDTO> addEmployee(@RequestBody EmployeeRequestDTO dto){
        UserDetailsImpl admin = getAuthenticatedUser();
        Long companyId = admin.getUser().getId();

        User savedUser = userService.createEmployee(dto, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(savedUser));
    }

    @GetMapping("/company")
    public ResponseEntity<List<UserResponseDTO>> getCompanyEmployees() {
        UserDetailsImpl admin = getAuthenticatedUser();
        Long companyId = admin.getUser().getId();

        List<User> employees = userService.getEmployeesByCompany(companyId);

        List<UserResponseDTO> dtos = employees.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/customer")
    public ResponseEntity<UserResponseDTO> addCustomerUser(@RequestBody UserRequestDTO dto) {
        UserDetailsImpl admin = getAuthenticatedUser();
        Long companyId = admin.getUser().getId();

        User savedUser = userService.createCustomerUser(dto, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(savedUser));
    }
}
