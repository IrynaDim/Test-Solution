package com.dev.solution.repository;

import com.dev.solution.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Long id);

    void deleteById(Long id);

    Page<User> findByBirthDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phone);
}
