package com.neuralFlux.Saas_OOH_demo.controllers;

import com.neuralFlux.Saas_OOH_demo.dtos.EmployeeRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.ExecutivePerformanceDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.UserRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.UserResponseDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.PanelRepository;
import com.neuralFlux.Saas_OOH_demo.security.UserDetailsImpl;
import com.neuralFlux.Saas_OOH_demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CampaignRepository campaignRepository;
    private final PanelRepository panelRepository;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) auth.getPrincipal();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(){
        UserDetailsImpl admin = getAuthenticatedUser();
        return ResponseEntity.ok(new UserResponseDTO(admin.getUser()));
    }

    @GetMapping("/company/metrics")
    public ResponseEntity<Map<String, Object>> getCompanyMetrics() {
        UserDetailsImpl admin = getAuthenticatedUser();
        Company company = admin.getUser().getCompany();
        Long companyId = admin.getUser().getCompany().getId();

        List<StatusCampaign> activeStatuses = List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED);
        Double totalMrr = campaignRepository.sumMrrByCompany(companyId, activeStatuses);
        long totalPanels = panelRepository.countByCompanyId(companyId);

        int panelLimit;
        switch (company.getSaasPlan()) {
            case BASIC -> panelLimit = 50;
            case PRO -> panelLimit = 300;
            case ENTERPRISE -> panelLimit = -1; // Unlimited
            default -> panelLimit = 50;
        }

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalMrr", totalMrr);
        metrics.put("totalPanels", totalPanels);
        metrics.put("panelLimit", panelLimit);
        metrics.put("saasPlan", company.getSaasPlan().name());

        return ResponseEntity.ok(metrics);
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

    // ==========================================
    //  ANALYTICS E PERFORMANCE
    // ==========================================
    @GetMapping("/{id}/performance")
    public ResponseEntity<ExecutivePerformanceDTO> getExecutivePerformance(@PathVariable Long id){
        UserDetailsImpl admin = getAuthenticatedUser();
        Long companyId = admin.getUser().getId();

        ExecutivePerformanceDTO performance = userService.getExecutivePerformance(id, companyId);
        return ResponseEntity.ok(performance);
    }

}
