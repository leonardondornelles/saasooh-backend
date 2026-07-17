package com.neuralFlux.Saas_OOH_demo.repositories;

import com.neuralFlux.Saas_OOH_demo.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.status = 'PENDING' AND i.dueDate < :today")
    List<Invoice> findOverdueInvoices(@Param("companyId") Long companyId, @Param("today") LocalDate today);

    /**
     * Sums the value of all the invoices overdued (Total Indiplendicies)
     */
    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.company.id = :companyId AND i.status = 'PENDING' AND i.dueDate < :today")
    Double sumOverdueAmount(@Param("companyId") Long companyId, @Param("today") LocalDate today);
}
