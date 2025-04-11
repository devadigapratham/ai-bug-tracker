package com.yourcompany.bugtracker.config;

import com.yourcompany.bugtracker.model.*; // Import Bug model too
import com.yourcompany.bugtracker.repository.BugRepository; // Import BugRepository
import com.yourcompany.bugtracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Import LocalDateTime

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BugRepository bugRepository; // Inject BugRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Keep transactional support
    public void run(String... args) throws Exception {
        log.info("Checking and initializing default users and bugs...");

        User adminUser = null;
        User developerUser = null;
        User reporterUser = null;

        // --- Admin User ---
        if (userRepository.findByUsername("admin").isEmpty()) {
            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpass"));
            adminUser.setEmail("admin@example.com");
            adminUser.setRole(Role.ADMIN);
            adminUser.setEnabled(true);
            adminUser = userRepository.save(adminUser); // Save and re-assign to get the ID
            log.info("Created default admin user.");
        } else {
            adminUser = userRepository.findByUsername("admin").get(); // Fetch existing
            log.info("Admin user already exists.");
        }

        // --- Developer User ---
        if (userRepository.findByUsername("developer").isEmpty()) {
            developerUser = new User();
            developerUser.setUsername("developer");
            developerUser.setPassword(passwordEncoder.encode("devpass"));
            developerUser.setEmail("dev@example.com");
            developerUser.setRole(Role.DEVELOPER);
            developerUser.setEnabled(true);
            developerUser = userRepository.save(developerUser); // Save and re-assign
            log.info("Created default developer user.");
        } else {
            developerUser = userRepository.findByUsername("developer").get(); // Fetch existing
            log.info("Developer user already exists.");
        }

        // --- Reporter User ---
        if (userRepository.findByUsername("reporter").isEmpty()) {
            reporterUser = new User();
            reporterUser.setUsername("reporter");
            reporterUser.setPassword(passwordEncoder.encode("reporterpass"));
            reporterUser.setEmail("reporter@example.com");
            reporterUser.setRole(Role.REPORTER);
            reporterUser.setEnabled(true);
            reporterUser = userRepository.save(reporterUser); // Save and re-assign
            log.info("Created default reporter user.");
        } else {
            reporterUser = userRepository.findByUsername("reporter").get(); // Fetch existing
            log.info("Reporter user already exists.");
        }

        log.info("Default user initialization check complete.");

        // --- Initial Bugs ---
        // Only proceed if the necessary users were found or created
        if (reporterUser != null && developerUser != null) {
            // Check if bug 1 exists (simple check by title)
            if (bugRepository.findAll().stream().noneMatch(b -> "UI Button Misaligned".equals(b.getTitle()))) {
                Bug bug1 = new Bug();
                bug1.setTitle("UI Button Misaligned");
                bug1.setDescription("The submit button on the login form is off-center on mobile devices.");
                bug1.setStatus(BugStatus.NEW);
                bug1.setPriority(Priority.MEDIUM);
                bug1.setReporter(reporterUser); // Assign the user object
                bug1.setCreatedAt(LocalDateTime.now()); // Can set explicitly or rely on @CreationTimestamp
                bugRepository.save(bug1);
                log.info("Created initial bug: UI Button Misaligned");
            } else {
                 log.info("Bug 'UI Button Misaligned' already exists.");
            }

            // Check if bug 2 exists
            if (bugRepository.findAll().stream().noneMatch(b -> "API Call Fails".equals(b.getTitle()))) {
                Bug bug2 = new Bug();
                bug2.setTitle("API Call Fails");
                bug2.setDescription("The GET /api/users endpoint returns 500 error sometimes.");
                bug2.setStatus(BugStatus.IN_PROGRESS); // Example status
                bug2.setPriority(Priority.HIGH);
                bug2.setReporter(developerUser); // Assign the user object (or reporterUser)
                bug2.setAssignee(developerUser); // Example assignment
                bug2.setCreatedAt(LocalDateTime.now());
                bugRepository.save(bug2);
                 log.info("Created initial bug: API Call Fails");
            } else {
                 log.info("Bug 'API Call Fails' already exists.");
            }
        } else {
             log.warn("Could not create initial bugs because default users were not available.");
        }

        log.info("Initial data initialization complete.");
    }
}
