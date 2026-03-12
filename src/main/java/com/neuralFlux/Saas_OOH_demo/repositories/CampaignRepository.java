package com.neuralFlux.Saas_OOH_demo.repositories;

import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.face.id = :faceId " +
            "AND c.status IN ('ACTIVE', 'RESERVED') " +
            "AND (c.startDate <= :endDate AND c.endDate >= :startDate)")
    long countOverlappingCampaigns(
            @Param("faceId") Long faceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<Campaign> findFirstByFaceIdAndStatusIn(Long faceId, List<StatusCampaign> statuses);

}
