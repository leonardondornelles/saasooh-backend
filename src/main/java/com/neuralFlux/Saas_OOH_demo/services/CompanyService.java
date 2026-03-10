package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.companyDTO.CompanyRequestDTO;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company createCompany(CompanyRequestDTO dto){
        Company company = new Company();
        company.setCorporateName(dto.corporateName());
        company.setFantasyName(dto.fantasyName());
        company.setCnpj(dto.cnpj());
        company.setSaasPlan(dto.saasPlan());

        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies(){
        return companyRepository.findAll();
    }
}
