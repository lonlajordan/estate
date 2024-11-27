package com.estate.controller;

import com.estate.domain.mail.EmailHelper;
import com.estate.domain.entity.Notification;
import com.estate.domain.entity.User;
import com.estate.repository.UserRepository;
import com.estate.utils.TextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserRepository userRepository;
    private final EmailHelper emailHelper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/237in")
    public String login() {
        return  isAuthenticated() ? "redirect:home" : "login" ;
    }

    @GetMapping("/password/reset")
    public String forgotPassword(){
        return "forgot";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@RequestParam String email, RedirectAttributes attributes, HttpServletRequest request){
        User user = userRepository.findByEmail(email).orElse(null);
        Notification notification;
        if(user == null){
            notification = Notification.error("utilisateur introuvable");
        }else{
            String token = TextUtils.generateType1UUID().toString();
            user.setToken(token);
            user.setTokenExpirationDate(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);
            notification = Notification.warn("Vérifiez votre boîte de réception");
            HashMap<String, Object> model = new HashMap<>();
            model.put("name", user.getName());
            model.put("link", (request.getRequestURL() + "/" + token).replaceFirst("http", "https"));
            emailHelper.sendMail(email, "", "Réinitialiser votre mot de passe", "admin_reset_password.ftl", Locale.FRENCH, model, Collections.emptyList());
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/237in";
    }

    @GetMapping("/password/reset/{token}")
    public String passwordReset(@PathVariable String token, RedirectAttributes attributes, Model model){
        User user = userRepository.findByToken(token).orElse(null);
        if(user != null){
            if(LocalDateTime.now().isBefore(user.getTokenExpirationDate())){
                model.addAttribute("token", token);
                return "reset";
            }
            attributes.addFlashAttribute("notification", Notification.error("ce lien a expiré"));
        }else{
            attributes.addFlashAttribute("notification", Notification.error("utilisateur introuvable"));
        }
        return "redirect:/237in";
    }

    @PostMapping("/password/reset/{token}")
    public String passwordRenew(@PathVariable String token, @RequestParam String password, RedirectAttributes attributes){
        User user = userRepository.findByToken(token).orElse(null);
        Notification notification;
        if(user != null){
            if(LocalDateTime.now().isBefore(user.getTokenExpirationDate())){
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                notification = Notification.info("votre mot de passe a été modifié.");
            }else{
               notification = Notification.error("ce lien a expiré");
            }
        }else{
            notification = Notification.error("utilisateur introuvable");
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/237in";
    }

    @PostMapping("/237in")
    public String login(
            @RequestParam(required = false, defaultValue = "") String error,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        if(StringUtils.isNotBlank(error)){
            String message = "Une erreur s'est produite. Réessayez plus tard.";
            if("1".equalsIgnoreCase(error)){
                message = "utilisateur introuvable";
            }else if("2".equalsIgnoreCase(error)){
                message = "mot de passe incorrect";
            }else if("3".equalsIgnoreCase(error)){
                message = "votre compte est désactivé";
            }
            model.addAttribute("notification", Notification.error(message));
        }
        return "login";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
