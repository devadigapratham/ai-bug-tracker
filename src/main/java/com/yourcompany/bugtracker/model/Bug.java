package com.yourcompany.bugtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bugs")
@Getter
@Setter
@NoArgsConstructor
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false) // Title is NOT marked as unique here
    private String title;

    @NotEmpty
    @Lob // For potentially long descriptions
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status = BugStatus.NEW; // Default status

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM; // Default priority

    // --- AI Suggested Fields (Optional) ---
    @Enumerated(EnumType.STRING)
    private Priority aiSuggestedPriority;

    private String aiSuggestedCategory; // Example category field
    // --- End AI Fields ---

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch is often better performance
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id") // Can be null if unassigned
    private User assignee;

    // Add Project relationship if needed:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "project_id")
    // private Project project;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors, helper methods
}