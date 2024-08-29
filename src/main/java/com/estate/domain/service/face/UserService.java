package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.User;
import com.estate.domain.form.PasswordForm;
import com.estate.domain.form.ProfilForm;
import com.estate.domain.form.UserForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface UserService {

    long count();

    List<User> findAll();

    Optional<User> findById(long id);

    Notification save(UserForm form, HttpSession session, Principal principal);

    Notification updateProfile(ProfilForm form, HttpSession session);

    Notification toggleById(long id, Principal principal);

    Notification changePassword(PasswordForm form, Principal principal);

    Notification deleteById(long id, boolean force, HttpServletRequest request);
}
