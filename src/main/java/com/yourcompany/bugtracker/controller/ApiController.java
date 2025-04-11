package com.yourcompany.bugtracker.controller;

import com.yourcompany.bugtracker.dto.BugDTO;
import com.yourcompany.bugtracker.service.BugService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs") // Base path for API
public class ApiController {

    @Autowired
    private BugService bugService;

    @GetMapping
    public ResponseEntity<List<BugDTO>> getAllBugs() {
        List<BugDTO> bugs = bugService.findAllBugs();
        return ResponseEntity.ok(bugs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BugDTO> getBugById(@PathVariable Long id) {
        return bugService.findBugById(id)
                .map(ResponseEntity::ok) // If found, return 200 OK with bug
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404
    }

    @PostMapping
    public ResponseEntity<?> createBug(@RequestBody BugDTO bugDto) {
        // Note: Authentication/Authorization for API endpoints needs separate handling
        // (e.g., JWT, OAuth2, or Session-based if called from same origin JS)
        // For simplicity, this is currently open.
        try {
            BugDTO createdBug = bugService.createBug(bugDto); // Uses current user from context if service is called correctly
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBug);
        } catch (Exception e) {
            // Basic error handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error creating bug: " + e.getMessage());
        }
    }

     @PutMapping("/{id}")
     public ResponseEntity<?> updateBug(@PathVariable Long id, @RequestBody BugDTO bugDto) {
         try {
             BugDTO updatedBug = bugService.updateBug(id, bugDto);
             return ResponseEntity.ok(updatedBug);
         } catch (EntityNotFoundException e) {
             return ResponseEntity.notFound().build();
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("Error updating bug: " + e.getMessage());
         }
     }

      @DeleteMapping("/{id}")
      public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
          try {
              bugService.deleteBug(id);
              return ResponseEntity.noContent().build(); // 204 No Content on successful deletion
          } catch (EntityNotFoundException e) {
              return ResponseEntity.notFound().build();
          } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          }
      }
}