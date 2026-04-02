package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.EmployeeRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.ExecutivePerformanceDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.UserRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.exceptions.ResourceNotFoundException;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Customer;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.repositories.CampaignRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.CustomerRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CampaignRepository campaignRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createEmployee(EmployeeRequestDTO dto, Long companyId) {
        if(userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está em uso.");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        User employee = new User();
        employee.setCompany(company);
        employee.setName(dto.name());
        employee.setEmail(dto.email());
        employee.setPassword(passwordEncoder.encode(dto.password()));
        employee.setRole(dto.role());
        employee.setActive(true);

        return userRepository.save(employee);
    }

    @Transactional
    public User createCustomerUser(UserRequestDTO dto, Long companyId) {
        if(userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está em uso");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (company.getSaasPlan() == SaasPlan.BASIC){
            throw new IllegalArgumentException("Faça o upgrade para o plano PRO para liberar o Portal do Cliente");
        }

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        User clientUser = new User();
        clientUser.setCompany(company);
        clientUser.setCustomer(customer);
        clientUser.setName(dto.name());
        clientUser.setEmail(dto.email());
        clientUser.setPassword(passwordEncoder.encode(dto.password()));
        clientUser.setRole(RoleUser.CUSTOMER);
        clientUser.setActive(true);

        return userRepository.save(clientUser);
    }

    public List<User> getEmployeesByCompany(Long companyId) {
        return userRepository.findByCompanyIdAndRoleNot(companyId, RoleUser.CUSTOMER);
    }


    private void setupCustomerUser(User user, Long customerId, Company company){
        if (company.getSaasPlan() == SaasPlan.BASIC){
            throw new IllegalArgumentException("Faça o upgrade para o plano PRO");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        user.setCustomer(customer);
        user.setRole(RoleUser.CUSTOMER);
    }

    public ExecutivePerformanceDTO getExecutivePerformance(Long executiveId, Long companyId){
        // Searches the executive
        User executive = userRepository.findById(executiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Executivo não encontrado"));

        if(!executive.getCompany().getId().equals(companyId)){
            throw new IllegalArgumentException("Acesso negado. Este usúario não pertence a esta empresa");
        }

        List<StatusCampaign> activeStatuses = List.of(StatusCampaign.ACTIVE, StatusCampaign.RESERVED);

        Double totalMrr = campaignRepository.sumMrrByExecutive(executiveId, companyId, activeStatuses);
        Long activeCount = campaignRepository.countActiveCampaignsByExecutive(executiveId, companyId, activeStatuses);

        List<Campaign> campaigns = campaignRepository.findByExecutiveIdAndCompanyIdOrderByStartDateDesc(executiveId, companyId);
        List<CampaignResponseDTO> campaignDtos = campaigns.stream().map(CampaignResponseDTO::new).toList();

        return new ExecutivePerformanceDTO(
                executive.getId(),
                executive.getName(),
                executive.getEmail(),
                executive.getRole().name(),
                totalMrr,
                activeCount,
                campaignDtos
        );
    }

}
