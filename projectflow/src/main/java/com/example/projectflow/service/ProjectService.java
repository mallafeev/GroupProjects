package com.example.projectflow.service;

import com.example.projectflow.model.Project;
import com.example.projectflow.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByOwnerId(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }

    public Project createProject(String name, String description, Long ownerId) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setOwnerId(ownerId);
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project updateProject(Long id, String name, String description) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}