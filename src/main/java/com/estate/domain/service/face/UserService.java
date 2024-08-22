package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public interface UserService {

    List<User> findAll();

    ModelAndView getProfile(String name);

    void updateProfile(User user, long cityId, HttpSession session, Principal principal, RedirectAttributes attributes);

    void changePassword(String oldPassword, String newPassword, Principal principal, RedirectAttributes attributes);

    User findById(long id);

    Notification toggleById(long id, Principal principal);

    long countUsers();

    ModelAndView createOrUpdate(User user, List<String> authorities, List<String> responsibilities, Boolean multiple, HttpSession session, RedirectAttributes attributes);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    ModelAndView getById(long id);
}
