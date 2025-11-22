package com.example.projectflow.controller;

import com.example.projectflow.model.Project;
import com.example.projectflow.model.User;
import com.example.projectflow.service.ProjectMemberService;
import com.example.projectflow.service.ProjectService;
import com.example.projectflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        List<Project> allProjects = projectService.getAllProjects();
        model.addAttribute("projects", allProjects);
        return "index";
    }

    @GetMapping("/projects")
    public String myProjects(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Project> userProjects = projectMemberService.getUserProjects(userId);
        model.addAttribute("projects", userProjects);
        return "projects";
    }


    @GetMapping("/projects/create")
    public String showCreateProjectForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("project", new Project());
        return "project-form";
    }

    @PostMapping("/projects/create")
    public String createProject(@ModelAttribute Project project, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Project savedProject = projectService.createProject(project.getName(), project.getDescription(), userId);
        projectMemberService.addMember(savedProject.getId(), userId, "OWNER");
        return "redirect:/projects";
    }

    @GetMapping("/projects/{id}")
    public String projectDetail(@PathVariable Long id, HttpSession session, Model model) {
        Project project = projectService.getProjectById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<User> members = projectMemberService.getProjectMembers(id);
        boolean isOwner = projectMemberService.isOwner(id, userId);

        model.addAttribute("project", project);
        model.addAttribute("members", members);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "project-detail";
    }


    @PostMapping("/projects/{id}/add-member")
    public String addMember(@PathVariable Long id,
                            @RequestParam Long userId,
                            HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        if (!projectMemberService.isOwner(id, currentUserId)) {
            return "redirect:/projects/" + id;
        }

        projectMemberService.addMember(id, userId, "MEMBER");
        return "redirect:/projects/" + id;
    }


    @PostMapping("/projects/{id}/remove-member")
    public String removeMember(@PathVariable Long id,
                               @RequestParam Long userId,
                               HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/login";
        }

        if (!projectMemberService.isOwner(id, currentUserId)) {
            return "redirect:/projects/" + id;
        }

        projectMemberService.removeMember(id, userId);
        return "redirect:/projects/" + id;
    }

    @GetMapping("/projects/{id}/edit")
    public String showEditProjectForm(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Project project = projectService.getProjectById(id).orElseThrow(() -> new RuntimeException("Project not found"));

        if (!projectMemberService.isOwner(id, userId)) {
            return "redirect:/projects/" + id; // нельзя редактировать чужой проект
        }

        model.addAttribute("project", project);
        return "project-form";
    }

    @PostMapping("/projects/{id}/edit")
    public String updateProject(@PathVariable Long id, @ModelAttribute Project project, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        if (!projectMemberService.isOwner(id, userId)) {
            return "redirect:/projects/" + id;
        }

        projectService.updateProject(id, project.getName(), project.getDescription());
        return "redirect:/projects/" + id;
    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        if (!projectMemberService.isOwner(id, userId)) {
            return "redirect:/projects/" + id;
        }

        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}