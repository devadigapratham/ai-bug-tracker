package com.yourcompany.bugtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "users") // Explicit table name is good practice
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String username;

    @NotEmpty
    @Column(nullable = false)
    private String password; // Store encoded password

    @Email
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled = true;

    @OneToMany(mappedBy = "reporter")
    private Set<Bug> reportedBugs;

    @OneToMany(mappedBy = "assignee")
    private Set<Bug> assignedBugs;

    // Constructors, other fields (e.g., name) can be added
}