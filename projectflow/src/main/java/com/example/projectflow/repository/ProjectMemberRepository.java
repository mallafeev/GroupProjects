package com.example.projectflow.repository;

import com.example.projectflow.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUserId(Long userId);
    void deleteByProjectIdAndUserId(Long projectId, Long userId);
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}