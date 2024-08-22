package com.estate.domain.service.impl;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.User;
import com.estate.domain.enumaration.Level;
import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Role;
import com.estate.domain.mail.EmailHelper;
import com.estate.domain.service.face.UserService;
import com.estate.repository.LogRepository;
import com.estate.repository.UserRepository;
import com.estate.utils.TextUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Locale;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailHelper emailHelper;

    @Override
    public long count(){
        return userRepository.countAllByDeletedFalse();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByDeletedFalseOrderByLastLoginDesc();
    }

    @Override
    public ModelAndView getById(long id) {
        User user = userRepository.findById(id).orElse(new User());
        ModelAndView view = new ModelAndView("admin/user/save");
        view.getModel().put("user", user);
        view.getModel().put("creation", user.getId() == null);
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        int n = 0;
        for(Long id: ids){
            User user = userRepository.findById(id).orElse(null);
            if(user != null){
                try {
                    userRepository.delete(user);
                }catch (Exception e){
                    user.setDeleted(true);
                    userRepository.save(user);
                    logRepository.save(Log.error("Erreur lors de la suppression de l'utilisateur <b>" + user.getName() + "</b>.", ExceptionUtils.getStackTrace(e)));
                }
                n++;
            }
        }
        String plural = n > 1 ? "s" : "";
        notification.setMessage( n + " utilisateur" + plural + " supprimée" + plural +".");
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/user/list", true);
    }

    @Override
    public Notification toggleById(long id, Principal principal){
        Notification notification = new Notification();
        try {
            User user = userRepository.findById(id).orElse(null);
            if(user != null){
                user.setEnabled(!user.isEnabled());
                userRepository.save(user);
                notification.setMessage("<b>" + user.getName() + "</b> a été " + (user.isEnabled() ? "activé" : "désactivé") + " avec succès.");
                logRepository.save(Log.info(notification.getMessage() + " Par <b>" + principal.getName() + "</b>."));
            }
        }catch (Exception e){
            notification = Notification.error("Erreur lors du changement de statut de l'utilisateur d'identifiant <b>" + id + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return notification;
    }

    @Override
    public ModelAndView createOrUpdate(User user, List<String> authorities, List<String> responsibilities, Boolean multiple, HttpSession session, RedirectAttributes attributes) {
        User user$ = user;
        boolean creation = true;
        if(user.getId() != null){
            Optional<User> _user = userRepository.findById(user.getId());
            if(_user.isPresent()){
                user$ = _user.get();
                user$.setFirstName(user.getFirstName());
                user$.setLastName(user.getLastName());
                user$.setPhoneNumber(user.getPhoneNumber());
                user$.setEmail(user.getEmail());
                user$.setSex(user.getSex());
                creation = false;
            }
        }
        Notification notification = new Notification();
        user$.setRoles(authorities.stream().map(Role::valueOf).collect(Collectors.toList()));
        user$.setModes(responsibilities.stream().map(Mode::valueOf).collect(Collectors.toList()));
        try {
            if(creation){
                HashMap<String, Object> context = new HashMap<>();
                String password = TextUtils.generatePassword();
                user$.setPassword(passwordEncoder.encode(password));
                context.put("name", user$.getName());
                context.put("password", password);
                emailHelper.sendMail(user$.getEmail(),"", "Nouveau compte TRAFFIC", "new_account.ftl", Locale.FRENCH, context, Collections.emptyList());
            }
            user$ = userRepository.save(user$);
            notification.setMessage("<b>" + user$.getName() +"</b> a été " + (creation ? "ajouté." : "modifié."));
            logRepository.save(Log.info(notification.getMessage()));
            if(!creation){
                user = (User) session.getAttribute("user");
                if(user != null && user.getId().equals(user$.getId())){
                    user$ = userRepository.findByEmail(user$.getEmail()).orElse(null);
                    if(user$ != null){
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        Collection<SimpleGrantedAuthority> authorities$ = user$.getRoles().stream().map(Enum::name).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                        auth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities$);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        session.setAttribute("user", user$);
                    }
                }
            }
            creation = true;
            user$ = new User();
        } catch (Exception e){
            assert user$ != null;
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " de l'utilisateur <b>" + user$.getName() + "</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("user", user$);
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.setViewName("admin/user/save");
        }else{
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/user/list");
        }
        return view;
    }

    @Override
    public void updateProfile(User user, long cityId, HttpSession session, Principal principal, RedirectAttributes attributes) {
        User user$ = userRepository.findById(user.getId()).orElse(new User());
        user$.setFirstName(user.getFirstName());
        user$.setLastName(user.getLastName());
        user$.setPhoneNumber(user.getPhoneNumber());
        user$.setEmail(user.getEmail());
        user$.setSex(user.getSex());
        Notification notification = new Notification();
        try {
            user$ = userRepository.save(user$);
            notification.setMessage("<b>" + user$.getName() +"</b> a été modifié.");
            logRepository.save(Log.info(notification.getMessage() + " Par <b>" + principal.getName() + "</b>."));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Collection<SimpleGrantedAuthority> authorities = user$.getRoles().stream().map(Enum::name).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            auth = new UsernamePasswordAuthenticationToken(user$.getEmail(), auth.getCredentials(), authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute("user", user$);
        } catch (Exception e){
            notification.setMessage("Erreur lors de la modification de l'utilisateur <b>[ " + user$.getName() + " ]</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }


    @Override
    public ModelAndView getProfile(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        ModelAndView view = new ModelAndView("redirect:/error/404");
        user.ifPresent((value) -> {
            view.setViewName("admin/user/profile");
            view.getModel().put("user", value);
        });
        return view;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, Principal principal, RedirectAttributes attributes) {
        Notification notification = new Notification();
        User user = userRepository.findByEmail(principal.getName()).orElse(new User());
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            notification.setMessage("Mot de passe actuel incorrect");
            attributes.addFlashAttribute("oldPassword", oldPassword);
            attributes.addFlashAttribute("newPassword", newPassword);
        }else{
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        attributes.addFlashAttribute("notification", notification);
    }
}
