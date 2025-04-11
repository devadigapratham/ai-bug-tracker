// src/main/java/com/yourcompany/bugtracker/controller/WebController.java
package com.yourcompany.bugtracker.controller;

import com.yourcompany.bugtracker.dto.BugDTO;
import com.yourcompany.bugtracker.model.Bug; // Existing import - keep
import com.yourcompany.bugtracker.model.BugStatus; // Existing import - keep
import com.yourcompany.bugtracker.model.Priority; // Existing import - keep
import com.yourcompany.bugtracker.model.User; // Existing import - keep
import com.yourcompany.bugtracker.service.BugService; // Existing import - keep
import com.yourcompany.bugtracker.service.UserService; // Existing import - keep
import jakarta.validation.Valid; // Existing import - keep
import org.springframework.beans.factory.annotation.Autowired; // Existing import - keep
import org.springframework.security.access.prepost.PreAuthorize; // Existing import - keep
import org.springframework.stereotype.Controller; // Existing import - keep
import org.springframework.ui.Model; // Existing import - keep
import org.springframework.validation.BindingResult; // Existing import - keep
import org.springframework.web.bind.annotation.*; // Existing import - keep
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Existing import - keep

import java.util.List; // Existing import - keep
import java.util.Optional; // <<<=== ADD THIS IMPORT
import jakarta.persistence.EntityNotFoundException; // <<<=== ADD THIS IMPORT

@Controller
public class WebController {

    @Autowired
    private BugService bugService;
    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public String login() {
        return "login"; // Return login.html template
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Add data for the dashboard/home page if needed
        model.addAttribute("bugCount", bugService.findAllBugs().size());
        return "home"; // Return home.html template
    }

    // --- Bug Management ---

    @GetMapping("/bugs")
    public String listBugs(Model model) {
        model.addAttribute("bugs", bugService.findAllBugs());
        return "bug-list"; // Return bug-list.html
    }

    @GetMapping("/bugs/new")
    public String showCreateBugForm(Model model) {
        model.addAttribute("bugDto", new BugDTO()); // Bind empty DTO
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", BugStatus.values()); // Though usually starts as NEW
        return "bug-form"; // Return bug-form.html
    }

    @PostMapping("/bugs/save")
    public String saveBug(@Valid @ModelAttribute("bugDto") BugDTO bugDto,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", BugStatus.values());
            return "bug-form"; // Return to form with errors
        }

        try {
            BugDTO savedBug = bugService.createBug(bugDto);
            redirectAttributes.addFlashAttribute("successMessage", "Bug reported successfully! ID: " + savedBug.getId());
            return "redirect:/bugs/" + savedBug.getId(); // Redirect to view page
        } catch (Exception e) {
             model.addAttribute("errorMessage", "Error saving bug: " + e.getMessage());
             model.addAttribute("priorities", Priority.values());
             model.addAttribute("statuses", BugStatus.values());
             return "bug-form";
        }
    }

    @GetMapping("/bugs/{id}")
    public String viewBug(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return bugService.findBugById(id)
                .map(bug -> {
                    model.addAttribute("bug", bug);
                    return "bug-view"; // Return bug-view.html
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bug not found with ID: " + id);
                    return "redirect:/bugs";
                });
    }


    @GetMapping("/bugs/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')") // Security check
    public String showEditBugForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
         // Use java.util.Optional here
         Optional<Bug> bugOptional = bugService.findBugEntityById(id); // Make sure findBugEntityById returns Optional<Bug>
         if (bugOptional.isEmpty()) {
              redirectAttributes.addFlashAttribute("errorMessage", "Bug not found with ID: " + id);
              return "redirect:/bugs";
         }

        model.addAttribute("bugDto", BugDTO.fromEntity(bugOptional.get())); // Populate DTO
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", BugStatus.values());
        model.addAttribute("developers", userService.findDevelopers()); // Get potential assignees
        return "bug-form"; // Reuse the form template for editing
    }

    @PostMapping("/bugs/{id}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    public String updateBug(@PathVariable Long id, @Valid @ModelAttribute("bugDto") BugDTO bugDto,
                            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
             model.addAttribute("priorities", Priority.values());
             model.addAttribute("statuses", BugStatus.values());
             model.addAttribute("developers", userService.findDevelopers());
             // Need to fetch the original bug again to avoid losing info not in DTO during validation fail? Or handle differently.
             // Add bugDto again to retain user input.
             model.addAttribute("bugDto", bugDto);
             return "bug-form"; // Return to form with errors
        }

        try {
            bugService.updateBug(id, bugDto);
            redirectAttributes.addFlashAttribute("successMessage", "Bug updated successfully!");
            return "redirect:/bugs/" + id;
        } catch (EntityNotFoundException e) { // Catch jakarta.persistence.EntityNotFoundException
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bugs";
        } catch (Exception e) {
             model.addAttribute("errorMessage", "Error updating bug: " + e.getMessage());
             model.addAttribute("priorities", Priority.values());
             model.addAttribute("statuses", BugStatus.values());
             model.addAttribute("developers", userService.findDevelopers());
             model.addAttribute("bugDto", bugDto); // Keep user's input
             return "bug-form";
        }
    }

     @PostMapping("/bugs/{id}/delete")
     @PreAuthorize("hasRole('ADMIN')") // Only Admins can delete
     public String deleteBug(@PathVariable Long id, RedirectAttributes redirectAttributes) {
         try {
             bugService.deleteBug(id);
             redirectAttributes.addFlashAttribute("successMessage", "Bug deleted successfully!");
         } catch (EntityNotFoundException e) { // Catch jakarta.persistence.EntityNotFoundException
             redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
         } catch (Exception e) {
             redirectAttributes.addFlashAttribute("errorMessage", "Error deleting bug: " + e.getMessage());
         }
         return "redirect:/bugs";
     }

}