package com.example.projectflow;

import com.example.projectflow.model.Project;
import com.example.projectflow.model.ProjectMember;
import com.example.projectflow.model.Task;
import com.example.projectflow.model.TaskStatus;
import com.example.projectflow.repository.ProjectMemberRepository;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.repository.TaskRepository;
import com.example.projectflow.service.TaskService;
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

@SpringBootTest(classes = TaskService.class)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectMemberRepository projectMemberRepository;

    @Test
    void testCreateTask_Success() {
        // Given
        String name = "Новая задача";
        Long projectId = 1L;
        Long assignedMemberId = 2L;

        Project project = new Project();
        project.setId(projectId);

        ProjectMember member = new ProjectMember();
        member.setId(assignedMemberId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findById(assignedMemberId)).thenReturn(Optional.of(member));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Task result = taskService.createTask(name, projectId, assignedMemberId);

        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(projectId, result.getProject().getId());
        assertEquals(assignedMemberId, result.getAssignedMember().getId());
        assertEquals(TaskStatus.PENDING, result.getStatus());

        verify(projectRepository).findById(projectId);
        verify(projectMemberRepository).findById(assignedMemberId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testGetTasksByProjectId() {
        // Given
        Long projectId = 1L;

        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("Задача 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Задача 2");

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);

        // When
        List<Task> result = taskService.getTasksByProjectId(projectId);

        // Then
        assertEquals(2, result.size());
        assertEquals("Задача 1", result.get(0).getName());
        assertEquals("Задача 2", result.get(1).getName());

        verify(taskRepository).findByProjectId(projectId);
    }

    @Test
    void testUpdateTaskStatus() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setStatus(TaskStatus.PENDING);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Task result = taskService.updateTaskStatus(taskId, newStatus);

        // Then
        assertEquals(newStatus, result.getStatus());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus_TaskNotFound() {
        // Given
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            taskService.updateTaskStatus(taskId, newStatus);
        });

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any());
    }


    @Test
    void testDeleteTask() {
        // Given
        Long taskId = 1L;

        // When
        taskService.deleteTask(taskId);

        // Then
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void testFindById() {
        // Given
        Long taskId = 1L;

        Task expectedTask = new Task();
        expectedTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(expectedTask));

        // When
        Task result = taskService.findById(taskId);

        // Then
        assertNotNull(result);
        assertEquals(taskId, result.getId());

        verify(taskRepository).findById(taskId);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            taskService.findById(taskId);
        });

        verify(taskRepository).findById(taskId);
    }
}