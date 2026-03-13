package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.CustomerRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.CustomerResponseDTO;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Customer;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;


    public CustomerResponseDTO createCustomer(Long companyId, CustomerRequestDTO dto){
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (customerRepository.existsByCnpjAndCompanyId(dto.cnpj(), companyId)){
            throw new RuntimeException("Já existe um cliente com este CNPJ cadastrado");
        }

        Customer customer = new Customer();
        customer.setCompany(company);
        customer.setCorporateName(dto.corporateName());
        customer.setFantasyName(dto.fantasyName());
        customer.setCnpj(dto.cnpj());
        customer.setTelephone(dto.telephone());
        customer.setEmail(dto.email());
        customer.setObservation(dto.observation());
        customer.setActive(true);

        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponseDTO(savedCustomer);
    }

    public List<CustomerResponseDTO> listCustomers(Long companyId) {
        return customerRepository.findAllByCompanyIdAndActiveTrue(companyId)
                .stream()
                .map(CustomerResponseDTO::new)
                .toList();
    }
}
