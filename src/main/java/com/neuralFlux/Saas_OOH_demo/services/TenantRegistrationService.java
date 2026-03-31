package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.loginDTO.TenantRegistrationDTO;
import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.User;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantRegistrationService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerNewTenant(TenantRegistrationDTO dto){

        if(userRepository.findByEmail(dto.adminEmail()).isPresent()){
            throw new IllegalArgumentException("Este e-mail já está em uso");
        }

        Company company = new Company();
        company.setCorporateName(dto.corporateName());
        company.setFantasyName(dto.fantasyName());
        company.setCnpj(dto.cnpj());
        company.setSaasPlan(dto.saasPlan());
        Company savedCompany = companyRepository.save(company);

        User adminUser = new User();
        adminUser.setCompany(savedCompany);
        adminUser.setName(dto.adminName());
        adminUser.setEmail(dto.adminEmail());
        adminUser.setPassword(passwordEncoder.encode(dto.adminPassword()));
        adminUser.setRole(RoleUser.ADMIN);
        adminUser.setActive(true);

        return userRepository.save(adminUser);
    }
}
