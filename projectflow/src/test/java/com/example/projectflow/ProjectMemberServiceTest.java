package com.example.projectflow;

import com.example.projectflow.model.Project;
import com.example.projectflow.model.ProjectMember;
import com.example.projectflow.model.User;
import com.example.projectflow.repository.ProjectMemberRepository;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.repository.UserRepository;
import com.example.projectflow.service.ProjectMemberService;
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

@SpringBootTest(classes = ProjectMemberService.class)
@ActiveProfiles("test")
class ProjectMemberServiceTest {

    @Autowired
    private ProjectMemberService projectMemberService;

    @MockBean
    private ProjectMemberRepository projectMemberRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Test
    void testAddMember_Success() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        String role = "MEMBER";

        Project project = new Project();
        project.setId(projectId);

        User user = new User();
        user.setId(userId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        projectMemberService.addMember(projectId, userId, role);

        // Then
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectMemberRepository).existsByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void testAddMember_AlreadyExists() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        String role = "MEMBER";

        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            projectMemberService.addMember(projectId, userId, role);
        });

        verify(projectRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
        verify(projectMemberRepository).existsByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void testRemoveMember() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        // When
        projectMemberService.removeMember(projectId, userId);

        // Then
        verify(projectMemberRepository).deleteByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void testGetProjectMembers() {
        // Given
        Long projectId = 1L;

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        ProjectMember member1 = new ProjectMember();
        member1.setUser(user1);
        ProjectMember member2 = new ProjectMember();
        member2.setUser(user2);

        List<ProjectMember> members = Arrays.asList(member1, member2);

        when(projectMemberRepository.findByProjectId(projectId)).thenReturn(members);

        // When
        List<User> result = projectMemberService.getProjectMembers(projectId);

        // Then
        assertEquals(2, result.size());
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());

        verify(projectMemberRepository).findByProjectId(projectId);
    }

    @Test
    void testGetUserProjects() {
        // Given
        Long userId = 1L;

        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);

        ProjectMember member1 = new ProjectMember();
        member1.setProject(project1);
        ProjectMember member2 = new ProjectMember();
        member2.setProject(project2);

        List<ProjectMember> members = Arrays.asList(member1, member2);

        when(projectMemberRepository.findByUserId(userId)).thenReturn(members);

        // When
        List<Project> result = projectMemberService.getUserProjects(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals(project1.getId(), result.get(0).getId());
        assertEquals(project2.getId(), result.get(1).getId());

        verify(projectMemberRepository).findByUserId(userId);
    }

    @Test
    void testIsOwner_True() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        ProjectMember member = new ProjectMember();
        member.setRole("OWNER");

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(member));

        // When
        boolean result = projectMemberService.isOwner(projectId, userId);

        // Then
        assertTrue(result);

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void testIsOwner_False() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        ProjectMember member = new ProjectMember();
        member.setRole("MEMBER");

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(member));

        // When
        boolean result = projectMemberService.isOwner(projectId, userId);

        // Then
        assertFalse(result);

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void testIsOwner_NotFound() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        // When
        boolean result = projectMemberService.isOwner(projectId, userId);

        // Then
        assertFalse(result);

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void testFindByProjectIdAndUserId_Found() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        ProjectMember expected = new ProjectMember();
        expected.setId(10L);

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(expected));

        // When
        ProjectMember result = projectMemberService.findByProjectIdAndUserId(projectId, userId);

        // Then
        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void testFindByProjectIdAndUserId_NotFound() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        // When
        ProjectMember result = projectMemberService.findByProjectIdAndUserId(projectId, userId);

        // Then
        assertNull(result);

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }
}