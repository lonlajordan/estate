package com.estate.domain.service.impl;


import com.estate.domain.entity.User;
import com.estate.domain.entity.Visitor;
import com.estate.domain.enumaration.Profil;
import com.estate.domain.form.ContactForm;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.helper.EmailHelper;
import com.estate.domain.service.face.VisitorService;
import com.estate.repository.UserRepository;
import com.estate.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.estate.domain.entity.Notification;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {
    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;
    private final EmailHelper emailHelper;

    @Override
    public Notification contact(ContactForm form){
        HashMap<String, Object> context = new HashMap<>();
        context.put("name",form.getName());
        HashMap<String, Object> context1 = new HashMap<>();
        context1.put("name",form.getName());
        context1.put("message",form.getEmail());
        context1.put("number",form.getPhone());
        context1.put("email",form.getEmail());
        String receiver = userRepository.findByProfil(Profil.STAFF).stream().map(User::getEmail).collect(Collectors.joining(","));
        emailHelper.sendMail(form.getEmail(), "", "Votre demande sur la cité", "contact_user.ftl", Locale.FRENCH, context, Collections.emptyList());
        emailHelper.sendMail(receiver,"",form.getSubject(),"contact_staff.ftl",Locale.FRENCH, context1, Collections.emptyList());
        return Notification.info();
    }

    @Override
    public Notification save(VisitorForm form){
        Visitor visitor = visitorRepository.findByEmail(form.getEmail()).orElse(new Visitor());
        visitor.setEmail(form.getEmail());
        visitor.setName(form.getName());
        visitor.setPhone(form.getPhone());
        return Notification.info("Le visiteur <b>" + form.getName() +"</b> a été enregistré.");
    }

    @Override
    public Notification subscribe(VisitorForm form){
        Notification notification = save(form);
        HashMap<String, Object> context = new HashMap<>();
        context.put("name",form.getName());
        emailHelper.sendMail(form.getEmail(),"","Visite de la mini cité","visitor.ftl", Locale.FRENCH, context, Collections.emptyList());
        return notification;
    }

}
