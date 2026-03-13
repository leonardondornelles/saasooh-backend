package com.neuralFlux.Saas_OOH_demo.repositories;

import com.neuralFlux.Saas_OOH_demo.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByCompanyIdAndActiveTrue(Long companyId);

    boolean existsByCnpjAndCompanyId(String cnpj, Long companyId);

}
