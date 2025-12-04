package com.example.projectflow.service;

import com.example.projectflow.model.Project;
import com.example.projectflow.model.ProjectMember;
import com.example.projectflow.model.User;
import com.example.projectflow.repository.ProjectMemberRepository;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public void addMember(Long projectId, Long userId, String role) {
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new RuntimeException("User is already a member of this project");
        }

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ProjectMember member = new ProjectMember(project, user, role);
        projectMemberRepository.save(member);
    }
    @Transactional
    public void removeMember(Long projectId, Long userId) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    public List<User> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(ProjectMember::getUser)
                .collect(Collectors.toList());
    }

    public List<Project> getUserProjects(Long userId) {
        return projectMemberRepository.findByUserId(userId).stream()
                .map(ProjectMember::getProject)
                .collect(Collectors.toList());
    }

    public boolean isOwner(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId).stream()
                .anyMatch(member -> "OWNER".equals(member.getRole()));
    }

    public ProjectMember findByProjectIdAndUserId(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .stream()
                .findFirst()
                .orElse(null);
    }
}