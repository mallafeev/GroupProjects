package com.example.projectflow.service;

import com.example.projectflow.model.Project;
import com.example.projectflow.model.ProjectMember;
import com.example.projectflow.model.Task;
import com.example.projectflow.model.TaskStatus;
import com.example.projectflow.repository.ProjectMemberRepository;
import com.example.projectflow.repository.TaskRepository;
import com.example.projectflow.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public Task createTask(String name, Long projectId, Long assignedMemberId) {
        // assignedMemberId — это ID из таблицы project_members
        ProjectMember member = projectMemberRepository.findById(assignedMemberId).orElseThrow(() -> new RuntimeException("Member not found"));

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = new Task();
        task.setName(name);
        task.setProject(project);
        task.setAssignedMember(member);
        task.setStatus(TaskStatus.PENDING);

        return taskRepository.save(task);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task updateTaskAssignee(Long taskId, Long newMemberId) {
        ProjectMember member = projectMemberRepository.findById(newMemberId).orElseThrow(() -> new RuntimeException("Member not found"));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setAssignedMember(member);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task findById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}