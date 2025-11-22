package com.example.projectflow.repository;

import com.example.projectflow.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProjectId(Long projectId);
}