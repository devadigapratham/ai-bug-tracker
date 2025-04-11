// src/main/java/com/yourcompany/bugtracker/dto/BugDTO.java
package com.yourcompany.bugtracker.dto;

// Ensure all necessary imports are present
import com.yourcompany.bugtracker.model.Bug;         // <--- The import for the Bug entity
import com.yourcompany.bugtracker.model.BugStatus;   // Enum for Status
import com.yourcompany.bugtracker.model.Priority;    // Enum for Priority
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data; // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Bug information.
 * Used for transferring bug data between layers (e.g., Controller and Service)
 * and for API responses/requests. Also used for form binding in Thymeleaf.
 */
@Data // Lombok annotation generates getters, setters, toString, etc.
public class BugDTO {

    private Long id;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    // Use NotNull for Enums and objects when they are required during updates/specific operations
    // For creation, the service layer might set defaults, so validation here might depend on the use case.
    @NotNull(message = "Status is required for updates") // Adjusted message for clarity
    private BugStatus status;

    @NotNull(message = "Priority is required for updates") // Adjusted message for clarity
    private Priority priority;

    // Fields primarily for display purposes in the UI or API responses
    private String reporterUsername;
    private String assigneeUsername;

    // Field used when *assigning* a bug via an update
    private Long assigneeId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // AI Suggestions (Optional - for display in UI or API)
    private Priority aiSuggestedPriority;
    private String aiSuggestedCategory;

    /**
     * Static factory method to create a BugDTO from a Bug entity.
     * This is a common pattern for mapping Entities to DTOs.
     *
     * @param bug The Bug entity to map from.
     * @return A BugDTO populated with data from the entity, or null if the input is null.
     */
    public static BugDTO fromEntity(Bug bug) {
        // Always good practice to handle null input
        if (bug == null) {
            return null;
        }

        BugDTO dto = new BugDTO();
        dto.setId(bug.getId());
        dto.setTitle(bug.getTitle());
        dto.setDescription(bug.getDescription());
        dto.setStatus(bug.getStatus());
        dto.setPriority(bug.getPriority());
        dto.setCreatedAt(bug.getCreatedAt());
        dto.setUpdatedAt(bug.getUpdatedAt());

        // Safely access related entities and their properties
        if (bug.getReporter() != null) {
            dto.setReporterUsername(bug.getReporter().getUsername());
        } else {
            // Handle cases where reporter might be unexpectedly null (though unlikely with @Column(nullable=false))
            dto.setReporterUsername("N/A");
        }

        if (bug.getAssignee() != null) {
            dto.setAssigneeUsername(bug.getAssignee().getUsername());
            dto.setAssigneeId(bug.getAssignee().getId()); // Set the ID for potential use in forms
        } else {
            dto.setAssigneeUsername("Unassigned");
            dto.setAssigneeId(null); // Explicitly null if unassigned
        }

        // Map AI suggested fields from the entity to the DTO
        dto.setAiSuggestedPriority(bug.getAiSuggestedPriority());
        dto.setAiSuggestedCategory(bug.getAiSuggestedCategory());

        return dto;
    }

    // Note: You don't typically put a `toEntity` method directly in the DTO.
    // That logic usually resides in the Service layer because it might involve
    // fetching related entities (like User for reporter/assignee) from repositories.
}