package com.yourcompany.bugtracker.service;

import com.yourcompany.bugtracker.dto.BugDTO;
import com.yourcompany.bugtracker.model.*;
import com.yourcompany.bugtracker.repository.BugRepository;
import com.yourcompany.bugtracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BugService {

    private static final Logger log = LoggerFactory.getLogger(BugService.class);


    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiSuggestionService aiSuggestionService;

    public List<BugDTO> findAllBugs() {
        return bugRepository.findAll().stream()
                .map(BugDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<BugDTO> findBugById(Long id) {
        return bugRepository.findById(id).map(BugDTO::fromEntity);
    }

     public Optional<Bug> findBugEntityById(Long id) {
        return bugRepository.findById(id);
     }


    @Transactional
    public BugDTO createBug(BugDTO bugDto) {
        Bug bug = new Bug();
        bug.setTitle(bugDto.getTitle());
        bug.setDescription(bugDto.getDescription());
        bug.setStatus(BugStatus.NEW); // Initial status

        // --- Get Reporter from Security Context ---
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found: " + currentUsername));
        bug.setReporter(reporter);
        // --- ---

        bug.setCreatedAt(LocalDateTime.now());

        // --- Get AI Suggestions ---
        Priority suggestedPriority = aiSuggestionService.suggestPriority(bug);
        String suggestedCategory = aiSuggestionService.suggestCategory(bug);
        bug.setAiSuggestedPriority(suggestedPriority);
        bug.setAiSuggestedCategory(suggestedCategory);
        // --- ---

        // Set initial priority (maybe based on AI or default)
        bug.setPriority(suggestedPriority != null ? suggestedPriority : Priority.MEDIUM); // Use AI suggestion or default


        Bug savedBug = bugRepository.save(bug);
        log.info("Created new bug with ID: {}", savedBug.getId());
        return BugDTO.fromEntity(savedBug);
    }

    @Transactional
    public BugDTO updateBug(Long id, BugDTO bugDto) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bug not found with id: " + id));

        bug.setTitle(bugDto.getTitle());
        bug.setDescription(bugDto.getDescription());
        bug.setStatus(bugDto.getStatus());
        bug.setPriority(bugDto.getPriority());
        bug.setUpdatedAt(LocalDateTime.now());

        // Handle Assignee Update
        if (bugDto.getAssigneeId() != null) {
            User assignee = userRepository.findById(bugDto.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee User not found with id: " + bugDto.getAssigneeId()));
            bug.setAssignee(assignee);
        } else {
             bug.setAssignee(null); // Unassign
        }


        Bug updatedBug = bugRepository.save(bug);
        log.info("Updated bug with ID: {}", updatedBug.getId());
        return BugDTO.fromEntity(updatedBug);
    }

    @Transactional
    public void deleteBug(Long id) {
        if (!bugRepository.existsById(id)) {
            throw new EntityNotFoundException("Bug not found with id: " + id);
        }
        bugRepository.deleteById(id);
        log.info("Deleted bug with ID: {}", id);
    }

    @Transactional
    public BugDTO assignBug(Long bugId, Long assigneeId) {
         Bug bug = bugRepository.findById(bugId)
                 .orElseThrow(() -> new EntityNotFoundException("Bug not found with id: " + bugId));
         User assignee = userRepository.findById(assigneeId)
                 .orElseThrow(() -> new EntityNotFoundException("Assignee User not found with id: " + assigneeId));

         bug.setAssignee(assignee);
         bug.setStatus(BugStatus.ASSIGNED); // Or IN_PROGRESS depending on workflow
         bug.setUpdatedAt(LocalDateTime.now());
         Bug savedBug = bugRepository.save(bug);
         return BugDTO.fromEntity(savedBug);
     }
}