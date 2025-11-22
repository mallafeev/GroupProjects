package com.example.projectflow.service;

import com.example.projectflow.model.Comment;
import com.example.projectflow.model.Project;
import com.example.projectflow.model.User;
import com.example.projectflow.repository.CommentRepository;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment createComment(Long projectId, Long authorId, String text) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setProject(project);
        comment.setAuthor(author);
        comment.setText(text);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByProjectId(Long projectId) {
        return commentRepository.findByProjectId(projectId);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        Long projectOwnerId = comment.getProject().getOwnerId();

        if (!projectOwnerId.equals(userId)) {
            throw new RuntimeException("Only project owner can delete comments");
        }

        commentRepository.deleteById(commentId);
    }
}