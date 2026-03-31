package com.neuralFlux.Saas_OOH_demo.repositories;

import com.neuralFlux.Saas_OOH_demo.enums.RoleUser;
import com.neuralFlux.Saas_OOH_demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByCompanyIdAndRoleNot(Long companyId, RoleUser roleUser);
}