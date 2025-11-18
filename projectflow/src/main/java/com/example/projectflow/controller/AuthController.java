package com.example.projectflow.controller;

import com.example.projectflow.model.User;
import com.example.projectflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam String confirmPassword,
                           Model model, HttpSession session) {

        if (!user.getPasswordHash().equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "auth/register";
        }

        try {
            User savedUser = userService.register(user.getLogin(), user.getPasswordHash());
            session.setAttribute("userId", savedUser.getId());
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        Model model, HttpSession session) {

        var authenticatedUser = userService.authenticate(login, password);

        if (authenticatedUser.isPresent()) {
            session.setAttribute("userId", authenticatedUser.get().getId());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Неверный логин или пароль");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}