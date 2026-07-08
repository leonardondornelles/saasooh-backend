package com.neuralFlux.Saas_OOH_demo.repositories;

import com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.ExecutiveRankingDTO;
import com.neuralFlux.Saas_OOH_demo.enums.StatusCampaign;
import com.neuralFlux.Saas_OOH_demo.models.Campaign;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.face.id = :faceId " +
            "AND c.status IN :statuses " +
            "AND (c.startDate <= :endDate AND c.endDate >= :startDate)")
    long countOverlappingCampaigns(
            @Param("faceId") Long faceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<StatusCampaign> statuses
    );

    List<Campaign> findAllByFaceIdAndStatusIn(Long faceId, List<StatusCampaign> statuses);

    @Query("SELECT COUNT(c) > 0 FROM Campaign c WHERE c.face.id = :faceId " +
           "AND c.status IN :statuses " +
            "AND (c.startDate <= :endDate AND c.endDate >= :startDate)")
    boolean existsOverLappingCampaign(
            @Param("faceId") Long faceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<StatusCampaign> statuses
            );

    // 1. Sums de MRR from a executive (only active campaigns or reserved)
    @Query("SELECT COALESCE(SUM(c.monthlyValue), 0.0) FROM Campaign c " +
            "WHERE c.executive.id = :executiveId AND c.company.id = :companyId AND c.status IN :statuses")
    Double sumMrrByExecutive(
            @Param("executiveId") Long executiveId,
            @Param("companyId") Long companyId,
            @Param("statuses") List<StatusCampaign> statuses
    );

    // 2. Counts how many active or reserved campaigns the executive has
    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.executive.id = :executiveId " +
            "AND c.company.id = :companyId AND c.status IN :statuses")
    Long countActiveCampaignsByExecutive(
            @Param("executiveId") Long executiveId,
            @Param("companyId") Long companyId,
            @Param("statuses") List<StatusCampaign> statuses
    );

    // 3. Brings the history of campaigns from the executive
    List<Campaign> findByExecutiveIdAndCompanyIdOrderByStartDateDesc(Long executiveId, Long companyId);

    // Sums globally all the mrr's
    @Query("SELECT COALESCE(SUM(c.monthlyValue), 0.0) FROM Campaign c WHERE c.company.id = :companyId AND c.status IN :statuses")
    Double sumMrrByCompany(@Param("companyId") Long companyId, @Param("statuses") List<StatusCampaign> statuses);

    //  Sales Ranking:
    @Query("SELECT new com.neuralFlux.Saas_OOH_demo.dtos.financeDTO.ExecutiveRankingDTO(c.executive.name, SUM(c.monthlyValue)) " +
            "FROM Campaign c WHERE c.company.id = :companyId AND c.status IN :statuses " +
            "GROUP BY c.executive.name ORDER BY SUM(c.monthlyValue) DESC")
    List<ExecutiveRankingDTO> getExecutiveRanking(@Param("companyId") Long companyId, @Param("statuses") List<StatusCampaign> statuses);

    // Expiring Contracts
    @Query("SELECT c FROM Campaign c WHERE c.company.id = :companyId AND c.status IN :statuses " +
            "AND c.endDate BETWEEN :hoje AND :limite " +
            "ORDER BY c.endDate ASC")
    List<Campaign> findExpiringCampaigns(@Param("companyId") Long companyId, @Param("statuses") List<StatusCampaign> statuses, @Param("hoje") LocalDate hoje, @Param("limite") LocalDate limite);

    // Counts campaigns per status (Pipeline)
    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.company.id = :companyId AND c.status = :status")
    Long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") StatusCampaign status);

    // Searches campaigns filtering by a statuses list
    List<Campaign> findAllByStatusIn(List<StatusCampaign> statuses);

    // Searches all the campoaigns from a company ordering by most recent
    List<Campaign> findByCompanyIdOrderByStartDateDesc(Long companyId);
}
