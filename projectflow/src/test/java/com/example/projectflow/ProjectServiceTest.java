package com.example.projectflow;

import com.example.projectflow.model.Project;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.repository.ProjectMemberRepository;
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

    @MockBean
    private ProjectMemberRepository projectMemberRepository;

    @Test
    void testGetAllProjects() {
        // Given
        Project p1 = new Project();
        p1.setId(1L);
        p1.setName("Project 1");

        Project p2 = new Project();
        p2.setId(2L);
        p2.setName("Project 2");

        List<Project> projects = Arrays.asList(p1, p2);

        when(projectRepository.findAll()).thenReturn(projects);

        // When
        List<Project> result = projectService.getAllProjects();

        // Then
        assertEquals(2, result.size());
        assertEquals("Project 1", result.get(0).getName());
        assertEquals("Project 2", result.get(1).getName());

        verify(projectRepository).findAll();
    }

    @Test
    void testGetProjectsByOwnerId() {
        // Given
        Long ownerId = 1L;

        Project p1 = new Project();
        p1.setId(1L);
        p1.setName("My Project 1");
        p1.setOwnerId(ownerId);

        Project p2 = new Project();
        p2.setId(2L);
        p2.setName("My Project 2");
        p2.setOwnerId(ownerId);

        List<Project> projects = Arrays.asList(p1, p2);

        when(projectRepository.findByOwnerId(ownerId)).thenReturn(projects);

        // When
        List<Project> result = projectService.getProjectsByOwnerId(ownerId);

        // Then
        assertEquals(2, result.size());
        assertEquals(ownerId, result.get(0).getOwnerId());
        assertEquals(ownerId, result.get(1).getOwnerId());

        verify(projectRepository).findByOwnerId(ownerId);
    }

    @Test
    void testCreateProject() {
        // Given
        String name = "New Project";
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
        assertNotNull(result.getId());

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testGetProjectById_Found() {
        // Given
        Long projectId = 1L;

        Project expected = new Project();
        expected.setId(projectId);
        expected.setName("Test Project");
        expected.setOwnerId(1L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(expected));

        // When
        Optional<Project> result = projectService.getProjectById(projectId);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Project", result.get().getName());

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
        assertTrue(result.isEmpty());

        verify(projectRepository).findById(projectId);
    }

    @Test
    void testUpdateProject() {
        // Given
        Long projectId = 1L;
        String newName = "Updated Name";
        String newDesc = "Updated Description";

        Project existing = new Project();
        existing.setId(projectId);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");
        existing.setOwnerId(1L);

        Project updated = new Project();
        updated.setId(projectId);
        updated.setName(newName);
        updated.setDescription(newDesc);
        updated.setOwnerId(1L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenReturn(updated);

        // When
        Project result = projectService.updateProject(projectId, newName, newDesc);

        // Then
        assertEquals(newName, result.getName());
        assertEquals(newDesc, result.getDescription());

        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateProject_NotFound() {
        // Given
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            projectService.updateProject(projectId, "New Name", "New Desc");
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
        verify(projectMemberRepository).deleteByProjectId(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void testIsOwner() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        boolean expected = true;

        when(projectMemberService.isOwner(projectId, userId)).thenReturn(expected);

        // When
        boolean result = projectService.isOwner(projectId, userId);

        // Then
        assertEquals(expected, result);

        verify(projectMemberService).isOwner(projectId, userId);
    }
}