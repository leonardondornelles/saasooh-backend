package com.neuralFlux.Saas_OOH_demo.services;

import com.neuralFlux.Saas_OOH_demo.dtos.CustomerRequestDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.CustomerResponseDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.campaignDTO.CampaignResponseDTO;
import com.neuralFlux.Saas_OOH_demo.dtos.customer.CustomerProfileDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Company;
import com.neuralFlux.Saas_OOH_demo.models.Customer;
import com.neuralFlux.Saas_OOH_demo.repositories.CompanyRepository;
import com.neuralFlux.Saas_OOH_demo.repositories.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public CustomerProfileDTO getCustomerProfile(Long customerId, Long companyId){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        if(!customer.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Sem permissão para aceder a este cliente");
        }

        double totalRevenue = 0.0;
        double averageTicket = 0.0;
        int totalCampaigns = 0;
        int activeCampaignsCount = 0;
        List<CampaignResponseDTO> history = java.util.List.of();

        if(customer.getCampaigns() != null && !customer.getCampaigns().isEmpty()) {
            history = customer.getCampaigns().stream()
                    .map(CampaignResponseDTO::new)
                    .toList();

            totalCampaigns = history.size();

            long validCampaigns = customer.getCampaigns().stream()
                    .filter(c -> c.getStatus() != StatusCampaign.CANCELLED && c.getStatus() != StatusCampaign.LOST)
                    .count();

            totalRevenue = customer.getCampaigns().stream()
                    .filter(c -> c.getStatus() != StatusCampaign.CANCELLED && c.getStatus() != StatusCampaign.LOST)
                    .mapToDouble(c -> c.getMonthlyValue() != null ? c.getMonthlyValue() : 0.0)
                    .sum();

            if(validCampaigns > 0){
                averageTicket = totalRevenue / validCampaigns;
            }

            activeCampaignsCount = (int) customer.getCampaigns().stream()
                    .filter(c -> c.getStatus() == StatusCampaign.ACTIVE)
                    .count();
        }

        return new CustomerProfileDTO(
                customer,
                totalRevenue,
                averageTicket,
                totalCampaigns,
                activeCampaignsCount,
                history
        );
    }
}
