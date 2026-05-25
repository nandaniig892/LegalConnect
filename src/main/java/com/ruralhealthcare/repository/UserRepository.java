package com.ruralhealthcare.repository;

import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByRole(Role role);
    List<User> findAllByRoleAndIsApproved(Role role, boolean isApproved);
}
