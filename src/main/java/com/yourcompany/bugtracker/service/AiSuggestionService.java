package com.yourcompany.bugtracker.service;

import com.yourcompany.bugtracker.model.Bug;
import com.yourcompany.bugtracker.model.Priority;
import com.yourcompany.bugtracker.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// --- Placeholder AI Service ---
@Service
public class AiSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionService.class);

    // TODO: Implement real AI logic using NLP/ML libraries or external APIs

    public Priority suggestPriority(Bug bug) {
        log.info("AI: Suggesting priority for bug '{}'", bug.getTitle());
        String description = bug.getDescription().toLowerCase();
        String title = bug.getTitle().toLowerCase();

        if (description.contains("crash") || description.contains("blocker") || title.contains("crash")) {
            return Priority.CRITICAL;
        } else if (description.contains("error") || description.contains("fail") || title.contains("error")) {
            return Priority.HIGH;
        } else if (description.contains("slow") || description.contains("performance") || title.contains("ui")) {
            return Priority.MEDIUM;
        } else {
            return Priority.LOW; // Default suggestion
        }
    }

    public String suggestCategory(Bug bug) {
        log.info("AI: Suggesting category for bug '{}'", bug.getTitle());
        String text = (bug.getTitle() + " " + bug.getDescription()).toLowerCase();

        if (text.contains("ui") || text.contains("button") || text.contains("layout") || text.contains("style")) {
            return "UI/Frontend";
        } else if (text.contains("api") || text.contains("database") || text.contains("backend") || text.contains("server")) {
            return "Backend/API";
        } else if (text.contains("security") || text.contains("login") || text.contains("password")) {
            return "Security";
        } else {
            return "General"; // Default suggestion
        }
    }

    public User suggestAssignee(Bug bug) {
        log.warn("AI: Assignee suggestion not implemented yet for bug '{}'. Returning null.", bug.getTitle());
        // TODO: Implement logic based on bug category, keywords, developer skills/load etc.
        // This would likely involve querying the UserRepository
        return null;
    }
}
