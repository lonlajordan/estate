package com.estate.controller;

import com.estate.domain.entity.*;
import com.estate.domain.enumaration.Status;
import com.estate.domain.form.PaymentForm;
import com.estate.domain.service.face.HousingService;
import com.estate.domain.service.face.PaymentService;
import com.estate.domain.service.face.StandingService;
import com.estate.domain.service.face.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final StudentService studentService;
    private final StandingService standingService;
    private final HousingService housingService;
    private final PaymentService paymentService;

    @GetMapping(value="list")
    public String getAll(@RequestParam(required = false, defaultValue = "1") int p, Model model){
        Page<Payment> payments = paymentService.findAll(p);
        model.addAttribute("payments", payments.toList());
        model.addAttribute("totalPages", payments.getTotalPages());
        model.addAttribute("currentPage", p);
        return "admin/payment/list";
    }

    @GetMapping("save")
    public String findById(@RequestParam(required = false) Long id, @RequestParam(required = false) Long standingId, @RequestParam(required = false) Long studentId, Model model, RedirectAttributes attributes){
        Payment payment = null;
        Standing standing = null;
        Student student;
        List<Housing> housings = new ArrayList<>();
        List<Standing> standings = standingService.findAll();
        if(id != null){
            payment = paymentService.findById(id).orElse(null);
        }else if(studentId != null){
            student = studentService.findById(studentId).orElse(null);
            if(student == null){
                attributes.addFlashAttribute("notification", Notification.error("Étudiant introuvable"));
                return "redirect:/student/list";
            }
            payment = new Payment();
            payment.setStudent(student);
            if(student.getHousing() != null){
                payment.setDesiderata(student.getHousing());
                standing = student.getHousing().getStanding();
                if(standing != null){
                    payment.setStanding(standing);
                    payment.setRent(standing.getRent());
                }
            }else {
                if(standingId != null){
                    standing = standingService.findById(standingId).orElse(null);
                } else if(!standings.isEmpty()) {
                    standing = standings.get(0);
                }

                if(standing != null){
                    payment.setStanding(standing);
                    payment.setRent(standing.getRent());
                    if(student.getHousing() == null || !Objects.equals(standing.getId(), student.getHousing().getStanding().getId())){
                        payment.setCaution(standing.getCaution());
                        payment.setRepair(standing.getRepair());
                    }
                }
            }
        }
        if(payment == null){
            attributes.addFlashAttribute("notification", Notification.error("Paiement introuvable"));
            return "redirect:/payment/list";
        }

        if(payment.getStanding() != null){
            housings = housingService.findAllByStandingId(payment.getStanding().getId());
            if(payment.getDesiderata() == null && !housings.isEmpty()){
                payment.setDesiderata(housings.get(0));
            }
        }

        model.addAttribute("payment", payment.toForm());
        model.addAttribute("student", payment.getStudent());
        model.addAttribute("standings", standings);
        model.addAttribute("housings", housings);
        return "admin/payment/save";
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("payment") PaymentForm payment, BindingResult result, Model model, RedirectAttributes attributes){
        if(result.hasErrors()) return "admin/payment/save";
        Notification notification =  paymentService.save(payment);
        if(notification.hasError()){
            List<Standing> standings = standingService.findAll();
            model.addAttribute("notification", notification);
            model.addAttribute("payment", payment);
            model.addAttribute("student", studentService.findById(payment.getStudentId()).orElse(null));
            model.addAttribute("standings", standings);
            model.addAttribute("housings", housingService.findAllByStandingId(payment.getStandingId()));
            return "admin/payment/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/housing/list";
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
