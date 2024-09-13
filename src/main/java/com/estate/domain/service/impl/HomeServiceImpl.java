package com.estate.domain.service.impl;


import com.estate.domain.entity.User;
import com.estate.domain.enumaration.Profil;
import com.estate.domain.form.ContactForm;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.mail.EmailHelper;
import com.estate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl {
    private final EmailHelper emailHelper;
    private final UserRepository userRepository;


    public void notifyForContact(ContactForm contactForm){
        HashMap<String, Object> context = new HashMap<>();
        context.put("name",contactForm.getName());
        HashMap<String, Object> context1 = new HashMap<>();
        context1.put("name",contactForm.getName());
        context1.put("message",contactForm.getEmail());
        context1.put("number",contactForm.getPhone());
        context1.put("email",contactForm.getEmail());
        String receiver = userRepository.findByProfil(Profil.STAFF).stream().map(User::getEmail).collect(Collectors.joining(","));
        emailHelper.sendMail(contactForm.getEmail(), "", "Votre demande sur la cité", "contact_user.ftl", Locale.FRENCH, context, Collections.emptyList());
        emailHelper.sendMail(receiver,"",contactForm.getSubject(),"contact_staff.ftl",Locale.FRENCH, context1, Collections.emptyList());
    }
}
