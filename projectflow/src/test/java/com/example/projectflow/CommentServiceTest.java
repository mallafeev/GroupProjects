package com.example.projectflow;

import com.example.projectflow.model.Comment;
import com.example.projectflow.model.Project;
import com.example.projectflow.model.User;
import com.example.projectflow.repository.CommentRepository;
import com.example.projectflow.repository.ProjectRepository;
import com.example.projectflow.repository.UserRepository;
import com.example.projectflow.service.CommentService;
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

@SpringBootTest(classes = CommentService.class)
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testCreateComment() {
        // Given
        Long projectId = 1L;
        Long authorId = 2L;
        String text = "Test comment";

        Project project = new Project();
        project.setId(projectId);

        User author = new User();
        author.setId(authorId);

        Comment expectedComment = new Comment();
        expectedComment.setProject(project);
        expectedComment.setAuthor(author);
        expectedComment.setText(text);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenReturn(expectedComment);

        // When
        Comment result = commentService.createComment(projectId, authorId, text);

        // Then
        assertNotNull(result);
        assertEquals(text, result.getText());
        assertEquals(projectId, result.getProject().getId());
        assertEquals(authorId, result.getAuthor().getId());

        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(authorId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testCreateComment_ProjectNotFound() {
        // Given
        Long projectId = 1L;
        Long authorId = 2L;
        String text = "Test comment";

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            commentService.createComment(projectId, authorId, text);
        });

        verify(projectRepository).findById(projectId);
        verify(userRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testCreateComment_UserNotFound() {
        // Given
        Long projectId = 1L;
        Long authorId = 2L;
        String text = "Test comment";

        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            commentService.createComment(projectId, authorId, text);
        });

        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(authorId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testGetCommentsByProjectId() {
        // Given
        Long projectId = 1L;

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByProjectId(projectId)).thenReturn(comments);

        // When
        List<Comment> result = commentService.getCommentsByProjectId(projectId);

        // Then
        assertEquals(2, result.size());
        assertEquals(comments, result);

        verify(commentRepository).findByProjectId(projectId);
    }

    @Test
    void testDeleteComment_Success() {
        // Given
        Long commentId = 1L;
        Long userId = 2L;
        Long projectOwnerId = 2L;

        Comment comment = new Comment();
        Project project = new Project();
        project.setOwnerId(projectOwnerId);
        comment.setProject(project);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(commentId, userId);

        // Then
        verify(commentRepository).findById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void testDeleteComment_NotOwner() {
        // Given
        Long commentId = 1L;
        Long userId = 2L;
        Long projectOwnerId = 3L; // Не владелец

        Comment comment = new Comment();
        Project project = new Project();
        project.setOwnerId(projectOwnerId);
        comment.setProject(project);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(commentId, userId);
        });

        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        // Given
        Long commentId = 1L;
        Long userId = 2L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(commentId, userId);
        });

        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).deleteById(any());
    }
}