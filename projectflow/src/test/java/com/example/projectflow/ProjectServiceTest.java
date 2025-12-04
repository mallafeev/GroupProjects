package com.example.projectflow;

import com.example.projectflow.model.Project;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.service.ProjectMemberService;
import com.example.projectflow.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ProjectService.class)
@ActiveProfiles("test")
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectMemberService projectMemberService;

    @Test
    void testGetAllProjects() {
        // Given
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);

        List<Project> projects = Arrays.asList(project1, project2);

        when(projectRepository.findAll()).thenReturn(projects);

        // When
        List<Project> result = projectService.getAllProjects();

        // Then
        assertEquals(2, result.size());
        assertEquals(projects, result);

        verify(projectRepository).findAll();
    }

    @Test
    void testGetProjectsByOwnerId() {
        // Given
        Long ownerId = 1L;
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);

        List<Project> projects = Arrays.asList(project1, project2);

        when(projectRepository.findByOwnerId(ownerId)).thenReturn(projects);

        // When
        List<Project> result = projectService.getProjectsByOwnerId(ownerId);

        // Then
        assertEquals(2, result.size());
        assertEquals(projects, result);

        verify(projectRepository).findByOwnerId(ownerId);
    }

    @Test
    void testCreateProject() {
        // Given
        String name = "Test Project";
        String description = "Test Description";
        Long ownerId = 1L;

        Project inputProject = new Project();
        inputProject.setName(name);
        inputProject.setDescription(description);
        inputProject.setOwnerId(ownerId);

        Project savedProject = new Project();
        savedProject.setId(10L);
        savedProject.setName(name);
        savedProject.setDescription(description);
        savedProject.setOwnerId(ownerId);

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // When
        Project result = projectService.createProject(name, description, ownerId);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(ownerId, result.getOwnerId());

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testGetProjectById_Found() {
        // Given
        Long projectId = 1L;
        Project expectedProject = new Project();
        expectedProject.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(expectedProject));

        // When
        Optional<Project> result = projectService.getProjectById(projectId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedProject.getId(), result.get().getId());

        verify(projectRepository).findById(projectId);
    }

    @Test
    void testGetProjectById_NotFound() {
        // Given
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When
        Optional<Project> result = projectService.getProjectById(projectId);

        // Then
        assertFalse(result.isPresent());

        verify(projectRepository).findById(projectId);
    }

    @Test
    void testUpdateProject() {
        // Given
        Long projectId = 1L;
        String newName = "Updated Name";
        String newDescription = "Updated Description";

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setName("Old Name");
        existingProject.setDescription("Old Description");

        Project updatedProject = new Project();
        updatedProject.setId(projectId);
        updatedProject.setName(newName);
        updatedProject.setDescription(newDescription);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        // When
        Project result = projectService.updateProject(projectId, newName, newDescription);

        // Then
        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newDescription, result.getDescription());

        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateProject_NotFound() {
        // Given
        Long projectId = 1L;
        String newName = "Updated Name";
        String newDescription = "Updated Description";

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            projectService.updateProject(projectId, newName, newDescription);
        });

        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testDeleteProject() {
        // Given
        Long projectId = 1L;

        // When
        projectService.deleteProject(projectId);

        // Then
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void testIsOwner() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        when(projectMemberService.isOwner(projectId, userId)).thenReturn(true);

        // When
        boolean result = projectService.isOwner(projectId, userId);

        // Then
        assertTrue(result);

        verify(projectMemberService).isOwner(projectId, userId);
    }
}