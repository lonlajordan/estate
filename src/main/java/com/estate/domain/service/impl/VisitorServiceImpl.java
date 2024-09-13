package com.estate.domain.service.impl;


import com.estate.domain.entity.Visitor;
import com.estate.domain.form.VisitorForm;
import com.estate.domain.mail.EmailHelper;
import com.estate.domain.service.face.VisitorService;
import com.estate.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.estate.domain.entity.Notification;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {
    private final VisitorRepository visitorRepository;
    private final EmailHelper emailHelper;

    @Override
    public Optional<Visitor> findByEmail(String email){
        return visitorRepository.findByEmail(email);
    }

    @Override
    public List<Visitor> findAll(){
        return visitorRepository.findAll();
    }

    @Override
    public Notification save(VisitorForm visitorForm){
        Notification notification = Notification.info();
        boolean create = false;
        Optional<Visitor> visitor = visitorRepository.findByEmail(visitorForm.getEmail());
        if(visitor.isPresent()){
            Visitor visitor1 = visitor.get();
            visitor1.setName(visitorForm.getName());
            visitor1.setPhone(visitorForm.getPhone());
            visitorRepository.save(visitor1);
        } else {
            Visitor visitor1 = new Visitor();
            visitor1.setEmail(visitorForm.getEmail());
            visitor1.setName(visitorForm.getName());
            visitor1.setPhone(visitorForm.getPhone());
            visitorRepository.save(visitor1);
            create = true;

        }

        notification.setMessage("Le visiteur <b>" + visitorForm.getName() +"</b> a été " + (create ? "ajouté." : "modifié."));

        return notification;
    }

    @Override
    public void submitVisitor(VisitorForm visitorForm){
        Notification notification = save(visitorForm);
        HashMap<String, Object> context = new HashMap<>();
        context.put("name",visitorForm.getName());
        emailHelper.sendMail(visitorForm.getEmail(),"","Visite de la mini cité","visitor.ftl", Locale.FRENCH, context, Collections.emptyList());
    }

}
