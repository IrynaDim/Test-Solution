package com.dev.solution.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // For the sake of simplicity in this test task, the address field is represented as a String.
    // In a real-world scenario, it's advisable to create a separate table for addresses,
    // cities, countries and also required classes.
    // This would involve additional complexity, which is avoided here to keep the task concise.
    @Column(name = "address")
    private String address;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
}
