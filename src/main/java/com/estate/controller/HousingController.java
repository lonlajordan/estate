package com.estate.controller;

import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Notification;
import com.estate.domain.form.HousingForm;
import com.estate.domain.form.HousingSearch;
import com.estate.domain.service.face.HousingService;
import com.estate.domain.service.face.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/housing")
public class HousingController {
    private final HousingService housingService;
    private final StandingService standingService;

    @GetMapping("list")
    public String findAll(Model model, HttpServletRequest request){
        HousingSearch form = new HousingSearch();
        List<Housing> housings;
        Map<String, ?> attributes = RequestContextUtils.getInputFlashMap(request);
        boolean search = attributes != null && attributes.containsKey("searchForm");
        if(search){
            form = (HousingSearch) attributes.get("searchForm");
            housings = housingService.findAll(form);
            if(housings.isEmpty()) model.addAttribute("notification", Notification.info("Aucun résultat"));
        } else {
            housings = housingService.findAll();
        }
        model.addAttribute("standings", standingService.findAllByActiveTrue());
        model.addAttribute("housings", housings);
        model.addAttribute("searchForm", form);
        return "admin/housing/list";
    }

    @GetMapping("save")
    public String findById(@RequestParam(required = false) Long id, Model model, RedirectAttributes attributes){
        Housing housing = new Housing();
        if(id != null)  housing = housingService.findById(id).orElse(null);
        if(housing == null){
            attributes.addFlashAttribute("notification", Notification.error("Logement introuvable"));
            return "redirect:/logement/list";
        }
        model.addAttribute("housing", housing.toForm());
        model.addAttribute("standings", standingService.findAllByActiveTrue());
        return "admin/housing/save";
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("housing") HousingForm housing, BindingResult result, @RequestParam(required = false, defaultValue = "false") boolean multiple, Model model, RedirectAttributes attributes){
        if(result.hasErrors()){
            model.addAttribute("standings", standingService.findAllByActiveTrue());
            return "admin/housing/save";
        }
        Notification notification =  housingService.save(housing);
        if(multiple || notification.hasError()){
            model.addAttribute("notification", notification);
            model.addAttribute("housing", notification.hasError() ? housing : new HousingForm());
            model.addAttribute("standings", standingService.findAllByActiveTrue());
            return "admin/housing/save";
        }
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/housing/list";
    }

    @GetMapping("view/{id}")
    public String findById(@PathVariable long id, Model model, RedirectAttributes attributes){
        Housing housing = housingService.findById(id).orElse(null);
        if(housing == null){
            attributes.addFlashAttribute("notification", Notification.error("Logement introuvable"));
            return "redirect:/housing/list";
        }
        model.addAttribute("housing", housing);
        return "admin/housing/view";
    }

    @PostMapping("search")
    public String search(HousingSearch form, RedirectAttributes attributes){
        attributes.addFlashAttribute("searchForm", form);
        return "redirect:/housing/list";
    }

    @RequestMapping(value="toggle/{id}")
    public String toggle(@PathVariable long id, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", housingService.toggleById(id));
        return "redirect:/housing/list";
    }

    @RequestMapping(value="liberate/{id}")
    public String liberate(@PathVariable long id, RedirectAttributes attributes){
        attributes.addFlashAttribute("notification", housingService.liberate(id));
        return "redirect:/housing/list";
    }

    @RequestMapping(value="delete")
    public String deleteById(@RequestParam long id, @RequestParam(required = false, defaultValue = "false") boolean force, RedirectAttributes attributes, HttpServletRequest request){
        Notification notification =  housingService.deleteById(id, force, request);
        attributes.addFlashAttribute("notification", notification);
        return "redirect:/housing/list";
    }
}
