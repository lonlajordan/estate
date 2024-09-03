package com.estate.controller;

import com.estate.domain.entity.*;
import com.estate.domain.enumaration.Status;
import com.estate.domain.form.HousingSearch;
import com.estate.domain.form.PaymentForm;
import com.estate.domain.form.PaymentSearch;
import com.estate.domain.service.face.HousingService;
import com.estate.domain.service.face.PaymentService;
import com.estate.domain.service.face.StandingService;
import com.estate.domain.service.face.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public String findAll(@RequestParam(required = false, defaultValue = "1") int page, Model model, HttpServletRequest request){
        PaymentSearch form = new PaymentSearch();
        Page<Payment> payments;
        Map<String, ?> attributes = RequestContextUtils.getInputFlashMap(request);
        boolean search = attributes != null && attributes.containsKey("searchForm");
        if(search){
            form = (PaymentSearch) attributes.get("searchForm");
            payments = paymentService.findAll(form);
            if(payments.isEmpty()) model.addAttribute("notification", Notification.info("Aucun résultat"));
        } else {
            payments = paymentService.findAll(page);
        }
        model.addAttribute("payments", payments.toList());
        model.addAttribute("totalPages", payments.getTotalPages());
        model.addAttribute("currentPage", payments.getPageable().getPageNumber());
        model.addAttribute("searchForm", form);
        model.addAttribute("search", search);
        return "admin/payment/list";
    }

    @GetMapping(value="accounting")
    public String accounting(@RequestParam(required = false) Integer year, Model model){
        int currentYear = LocalDate.now().getYear();
        List<Integer> years = IntStream.range(currentYear - 4, currentYear + 1).boxed().collect(Collectors.toList());
        if(year == null || !years.contains(year)) year = currentYear;
        List<Payment> payments = paymentService.findAllByStatusAndYear(Status.CONFIRMED, year);
        model.addAttribute("payments", payments);
        model.addAttribute("years", years);
        model.addAttribute("exercise", year);
        model.addAttribute("total", payments.stream().mapToLong(Payment::getAmount).reduce(Long::sum).orElse(0));
        return "admin/payment/accounting";
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
                    if(student.getHousing() == null){
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
            housings = housingService.findAllByStandingIdAndActiveTrue(payment.getStanding().getId());
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
    public String save(@Valid @ModelAttribute("payment") PaymentForm payment, BindingResult result, Model model, RedirectAttributes attributes, Principal principal){
        if(payment.getId() == null) {
            String notNullMessage = "javax.validation.constraints.NotNull.message";
            String defaultMessage = "ne doit pas être nul";
            if(payment.getProofFile() == null || payment.getProofFile().isEmpty()) result.rejectValue("proofFile", notNullMessage, defaultMessage);
        }
        Notification notification = null;
        if(!result.hasErrors()) notification =  paymentService.save(payment, principal);
        if(result.hasErrors() || (notification != null && notification.hasError())){
            List<Standing> standings = standingService.findAll();
            model.addAttribute("notification", notification);
            model.addAttribute("payment", payment);
            model.addAttribute("student", studentService.findById(payment.getStudentId()).orElse(null));
            model.addAttribute("standings", standings);
            model.addAttribute("housings", housingService.findAllByStandingIdAndActiveTrue(payment.getStandingId()));
            return "admin/payment/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/payment/list";
    }

    @GetMapping(value="view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Payment payment = paymentService.findById(id).orElse(null);
        if(payment == null){
            attributes.addFlashAttribute("notification", Notification.error("Paiement introuvable"));
            return "redirect:/payment/list";
        }
        model.addAttribute("payment", payment);
        return "admin/payment/view";
    }

    @GetMapping(value="submit/{id}")
    public String submitById(@PathVariable long id, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", paymentService.submit(id));
        return "redirect:/payment/list";
    }

    @GetMapping(value="validate/{id}")
    public String validateById(@PathVariable long id, RedirectAttributes attributes, HttpSession session){
        attributes.addFlashAttribute("notification", paymentService.validate(id, session));
        return "redirect:/payment/list";
    }

    @PostMapping("search")
    public String search(HousingSearch form, RedirectAttributes attributes){
        attributes.addFlashAttribute("searchForm", form);
        return "redirect:/payment/list";
    }
}
