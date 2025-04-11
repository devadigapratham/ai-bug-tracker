package com.yourcompany.bugtracker.repository;

import com.yourcompany.bugtracker.model.Bug;
import com.yourcompany.bugtracker.model.BugStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findByStatus(BugStatus status);
    List<Bug> findByAssigneeUsername(String username);
    List<Bug> findByReporterUsername(String username);
    // Add more custom query methods as needed
}