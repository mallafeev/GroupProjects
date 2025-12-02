package com.example.projectflow.controller;

import com.example.projectflow.service.InviteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.projectflow.service.ProjectMemberService;

@Controller
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private ProjectMemberService projectMemberService;


    /**
     * Страница приглашения (просмотр)
     */
    @GetMapping("/invite/{code}")
    public String viewInvite(@PathVariable String code, HttpSession session, Model model) {
        try {
            var invite = inviteService.validateInvite(code);
            Long userId = (Long) session.getAttribute("userId");

            model.addAttribute("projectName", invite.getProject().getName());
            model.addAttribute("creatorName", invite.getCreator().getLogin());
            model.addAttribute("code", code);

            if (userId == null) {
                // Если не вошёл — сохраняем код в сессии
                session.setAttribute("pendingInviteCode", code);
                return "redirect:/login";
            } else {
                // Если вошёл — сразу добавляем в проект
                projectMemberService.addMember(invite.getProject().getId(), userId, "MEMBER");
                // Удаляем инвайт
                inviteService.deleteInvite(code);
                return "redirect:/projects";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    /**
     * Принять приглашение (после входа)
     */
    @PostMapping("/invite/{code}")
    public String acceptInvite(@PathVariable String code, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // Если не вошёл — перенаправляем на вход
            return "redirect:/login?redirectTo=/invite/" + code;
        }

        try {
            inviteService.acceptInvite(code, userId);
            return "redirect:/projects"; // перенаправляем на "мои проекты"
        } catch (RuntimeException e) {
            return "error";
        }
    }
}