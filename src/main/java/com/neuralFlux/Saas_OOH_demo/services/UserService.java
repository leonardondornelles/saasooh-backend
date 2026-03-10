package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.UserRequestDTO;
import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import com.neuralFlux.Saas_OOH_demo.enums.SaasPlan;
import com.neuralFlux.Saas_OOH_demo.exceptions.ResourceNotFoundException;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Customer;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.CustomerRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequestDTO dto) {
        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        User user = new User();
        user.setCompany(company);
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));

        if(dto.customerId() != null){
            setupCustomerUser(user, dto.customerId(), company);
        } else {
            user.setRole(dto.role());
        }
        return userRepository.save(user);
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
}
