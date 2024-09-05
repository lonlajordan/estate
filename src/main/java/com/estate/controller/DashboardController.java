package com.estate.controller;

import com.estate.domain.enumaration.Status;
import com.estate.domain.service.face.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final StudentService studentService;
    private final HousingService housingService;
    private final PaymentService paymentService;
    private final UserService userService;

    @GetMapping("dashboard")
    private String home(Model model){
        model.addAttribute("users", userService.count());
        model.addAttribute("students", studentService.count());
        model.addAttribute("housings", housingService.count());
        model.addAttribute("payments", paymentService.countByStatus(Status.INITIATED));
        return "index";
    }

    @GetMapping(value = "explorer/**")
    @ResponseBody
    public ResponseEntity<byte[]> moduleStrings(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String path = requestURL.split("explorer/")[1];
        try {
            Path filePath = Paths.get(URLDecoder.decode(path, String.valueOf(StandardCharsets.UTF_8))).toAbsolutePath().normalize();
            MediaType contentType = MediaType.asMediaType(MimeType.valueOf(Files.probeContentType(filePath)));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.inline().filename(filePath.toFile().getName()).build());
            return ResponseEntity.ok().contentType(contentType).headers(headers).body(Files.readAllBytes(filePath));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}