package com.estate.controller;

import com.estate.domain.enumaration.Status;
import com.estate.domain.entity.Payment;
import com.estate.domain.service.face.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Date;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Payment> payments = paymentService.findAll(p);
        model.addAttribute("payments", payments.toList());
        model.addAttribute("totalPages", payments.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/payment/list";
    }

    @GetMapping(value="view/{id}")
    public ModelAndView getDetails(@PathVariable long id){
        ModelAndView view = new ModelAndView("redirect:/error/404");
        Optional<Payment> payment = paymentService.findById(id);
        payment.ifPresent(value -> {
            view.getModel().put("payment", value);
            view.setViewName("admin/payment/view");
        });
        return view;
    }

    @GetMapping(value="status/{id}")
    public String getAll(@PathVariable long id, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", paymentService.status(id));
        return "redirect:/payment/list";
    }

    @PostMapping(value="search")
    public ModelAndView search(@RequestParam(required = false, defaultValue = "1") int page,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) Status status,
                         @RequestParam(required = false, defaultValue = "1970-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
                         @RequestParam(required = false, defaultValue = "1970-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end){
        return paymentService.search(name, phone, status, start, end, page);
    }

    @RequestMapping(value="delete")
    public RedirectView deleteById(@RequestParam long id, RedirectAttributes attributes){
        return paymentService.deleteById(id, attributes);
    }
}
