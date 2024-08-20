package com.estate.controller;

import com.estate.domain.service.face.LogService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Slf4j
@Setter
@Controller
@RequiredArgsConstructor
public class ErrorControllerImpl {
    private LogService logService;

    @RequestMapping("/error/{status}")
    public String handleError(@PathVariable Integer status, HttpSession session, Model model, Principal principal, Exception exception) {
        logService.handleError(status, session, model, principal, exception);
        return "error";
    }
}