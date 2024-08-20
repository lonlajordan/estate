package com.estate.controller;

import com.estate.domain.entity.User;
import com.estate.domain.service.face.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping(value="list")
    public String getAll(Model model){
        model.addAttribute("users", userService.findAll());
        return "admin/user/list";
    }

    @GetMapping(value="profile")
    public ModelAndView profile(Principal principal){
        return userService.getProfile(principal.getName());
    }

    @PostMapping(value="profile")
    public RedirectView saveProfile(User user, @RequestParam long cityId, RedirectAttributes attributes, HttpSession session, Principal principal){
        userService.updateProfile(user, cityId, session, principal, attributes);
        return new RedirectView("/user/profile", true);
    }

    @GetMapping(value="change/password")
    public String changePassword() {
        return "admin/user/password";
    }

    @PostMapping(value="change/password")
    public RedirectView changePasswordSave(@RequestParam String oldPassword, @RequestParam String newPassword, RedirectAttributes attributes, Principal principal){
        userService.changePassword(oldPassword, newPassword, principal, attributes);
        return new RedirectView("/user/change/password", true);
    }

    @GetMapping(value="view/{id}")
    public String view(@PathVariable long id, Model model){
        model.addAttribute("user", userService.findById(id));
        return "admin/user/view";
    }

    @RequestMapping(value="toggle/{id}")
    public RedirectView toggleUser(@PathVariable int id, RedirectAttributes attributes, Principal principal){
        attributes.addFlashAttribute("notification", userService.toggleById(id, principal));
        return new RedirectView("/user/list", true);
    }

    @GetMapping(value = "save")
    private ModelAndView getUser(@RequestParam(required = false, defaultValue = "-1") long id){
        return userService.getById(id);
    }

    @PostMapping(value="save")
    public ModelAndView save(User user, @RequestParam List<String> authorities, @RequestParam(required = false) List<String> responsibilities, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes, HttpSession session){
        return userService.createOrUpdate(user, authorities, responsibilities, multiple, session, attributes);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteAll(@RequestParam ArrayList<Long> ids, RedirectAttributes attributes){
        return userService.deleteAllByIds(ids, attributes);
    }
}
