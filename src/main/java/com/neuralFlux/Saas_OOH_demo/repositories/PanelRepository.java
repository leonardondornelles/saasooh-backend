package com.neuralFlux.Saas_OOH_demo.repositories;

import com.neuralFlux.Saas_OOH_demo.models.Panel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanelRepository extends JpaRepository<Panel, Long> {

    List<Panel> findByCity(String city);
    List<Panel> findByCompanyId(Long companyId);
}
