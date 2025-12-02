package com.example.projectflow.service;

import com.example.projectflow.model.Invite;
import com.example.projectflow.model.Project;
import com.example.projectflow.model.User;
import com.example.projectflow.repository.InviteRepository;
import com.example.projectflow.repository.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InviteService {

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectMemberService projectMemberService;

    public String createInvite(Long projectId, Long creatorId, int hoursToExpire) {
        Invite invite = new Invite();
        invite.setCode(UUID.randomUUID().toString().substring(0, 8)); // короткий код
        invite.setProject(new Project());
        invite.getProject().setId(projectId);
        invite.setCreator(new User());
        invite.getCreator().setId(creatorId);
        invite.setExpiresAt(LocalDateTime.now().plusHours(hoursToExpire));

        inviteRepository.save(invite);
        return invite.getCode();
    }

    public Invite validateInvite(String code) {
        Invite invite = inviteRepository.findByCode(code).orElse(null);

        if (invite == null) {
            throw new RuntimeException("Приглашение не найдено");
        }

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Удаляем просроченный инвайт
            inviteRepository.deleteByCode(code);
            throw new RuntimeException("Срок действия приглашения истёк");
        }

        return invite;
    }
    @Transactional
    public void acceptInvite(String code, Long userId) {
        Invite invite = validateInvite(code);

        boolean exists = projectMemberRepository.existsByProjectIdAndUserId(invite.getProject().getId(), userId);
        if (exists) {
            throw new RuntimeException("Пользователь уже состоит в проекте");
        }

        projectMemberService.addMember(invite.getProject().getId(), userId, "MEMBER");

        // Удаляем через отдельный метод
        deleteInvite(code);
    }

    @Transactional
    public void deleteInvite(String code) {
        inviteRepository.deleteByCode(code);
    }
}