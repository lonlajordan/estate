package com.estate.controller;

import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Lease;
import com.estate.domain.entity.Notification;
import com.estate.domain.enumaration.Availability;
import com.estate.domain.form.LeaseSearch;
import com.estate.domain.form.MutationForm;
import com.estate.domain.service.face.HousingService;
import com.estate.domain.service.face.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lease")
public class LeaseController {
    private final LeaseService leaseService;
    private final HousingService housingService;

    @GetMapping("list")
    public String findAll(Model model, @RequestParam(required = false, defaultValue = "1") int page, HttpServletRequest request){
        LeaseSearch form = new LeaseSearch();
        Page<Lease> leases;
        Map<String, ?> attributes = RequestContextUtils.getInputFlashMap(request);
        boolean search = attributes != null && attributes.containsKey("searchForm");
        if(search){
            form = (LeaseSearch) attributes.get("searchForm");
            leases = leaseService.findAll(form);
            if(leases.isEmpty()) model.addAttribute("notification", Notification.info("Aucun résultat"));
        } else {
            leases = leaseService.findAll(page);
        }
        model.addAttribute("housings", housingService.findAll());
        model.addAttribute("leases", leases.toList());
        model.addAttribute("totalPages", leases.getTotalPages());
        model.addAttribute("currentPage", leases.getNumber() + 1);
        model.addAttribute("searchForm", form);
        model.addAttribute("search", search);
        return "admin/lease/list";
    }
    
    @GetMapping("view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Lease lease = leaseService.findById(id).orElse(null);
        if(lease == null){
            attributes.addFlashAttribute("notification", Notification.error("Contrat de bail introuvable"));
            return "redirect:/lease/list";
        }
        model.addAttribute("lease", lease);
        return "admin/lease/view";
    }

    @ResponseBody
    @GetMapping("download/{id}")
    public ResponseEntity<?> download(@PathVariable long id){
        return leaseService.download(id);
    }

    @PostMapping("search")
    public String search(LeaseSearch form, RedirectAttributes attributes){
        attributes.addFlashAttribute("searchForm", form);
        return "redirect:/lease/list";
    }

    @RequestMapping(value="activate/{id}")
    public String activate(@PathVariable long id, @RequestParam(required = false) Long housingId, RedirectAttributes attributes, Model model){
        Notification notification = leaseService.activate(id, housingId, model);
        Lease lease = (Lease) model.getAttribute("lease");
        if(notification.hasError() && lease != null){
            List<Housing> housings = housingService.findAllByStandingIdAndStatusAndActiveTrue(lease.getPayment().getStanding().getId(), Availability.FREE);
            model.addAttribute("housings", housings);
            model.addAttribute("notification", notification);
            return "admin/lease/activate";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/lease/list";
    }

    @GetMapping("save")
    public String mutation(@RequestParam long id, Model model, RedirectAttributes attributes){
        Lease lease = leaseService.findById(id).orElse(null);
        if(lease == null){
            attributes.addFlashAttribute("notification", Notification.error("Contrat de bail introuvable"));
            return "redirect:/lease/list";
        }
        MutationForm mutation = new MutationForm();
        mutation.setLeaseId(lease.getId());
        model.addAttribute("lease", lease);
        model.addAttribute("mutation", mutation);
        model.addAttribute("housings", housingService.findAllByStatusAndActiveTrue(Availability.FREE));
        return "admin/lease/save";
    }


    @PostMapping("save")
    public String save(@Valid @ModelAttribute("mutation") MutationForm mutation, BindingResult result, Model model, RedirectAttributes attributes, Principal principal){
        if(result.hasErrors()){
            model.addAttribute("housings", housingService.findAllByStatusAndActiveTrue(Availability.FREE));
            return "admin/lease/save";
        }
        Notification notification =  leaseService.mutate(mutation, principal);
        if(notification.hasError()){
            attributes.addAttribute("id", mutation.getLeaseId());
            attributes.addFlashAttribute("notification", notification);
            return "redirect:/lease/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/lease/list";
    }
}
