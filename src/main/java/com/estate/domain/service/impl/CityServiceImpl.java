package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Province;
import com.estate.domain.entity.Standing;
import com.estate.domain.enumaration.Level;
import com.estate.domain.service.face.CityService;
import com.estate.repository.CityRepository;
import com.estate.repository.LogRepository;
import com.estate.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final ProvinceRepository provinceRepository;
    private final LogRepository logRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Housing> findById(long id){
        return cityRepository.findById(id);
    }

    @Override
    public ModelAndView getById(long id) {
        Optional<Housing> city = this.findById(id);
        List<Province> provinces = provinceRepository.findAllByOrderByNameAsc();
        ModelAndView view = new ModelAndView("admin/city/save");
        view.getModel().put("city", city.orElse(new Housing()));
        view.getModel().put("creation", city.isEmpty());
        view.getModel().put("provinces", provinces);
        return view;
    }

    @Override
    public ModelAndView findAllByProvinceId(long provinceId){
        List<Province> provinces = provinceRepository.findAllByOrderByNameAsc();
        Province province = provinces.stream().filter(b -> b.getId() == provinceId).findFirst().orElse(provinces.isEmpty() ? null : provinces.get(0));
        List<Housing> cities = new ArrayList<>();
        ModelAndView view = new ModelAndView("admin/city/list");
        view.getModel().put("cities", cities);
        view.getModel().put("provinces", provinces);
        view.getModel().put("province", province);
        return view;
    }

    @Override
    public RedirectView deleteAll(List<Long> ids, RedirectAttributes attributes) {
        Notification notification = Notification.info();
        Housing city = cityRepository.findFirstByIdIn(ids);
        try {
            cityRepository.deleteAllByIdInBatch(ids);
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des villes.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/city/list", true);
    }

    @Override
    public ModelAndView createOrUpdate(Housing city, long provinceId, Boolean multiple, RedirectAttributes attributes) {
        Housing city$ = city;
        Standing province = em.getReference(Standing.class, provinceId);
        boolean creation = true;
        if(city.getId() != null){
            Optional<Housing> _city = cityRepository.findById(city.getId());
            if(_city.isPresent()){
                city$ = _city.get();
                city$.setName(city.getName());
                creation = false;
            }
        }
        city$.setStanding(province);
        Notification notification = new Notification();
        try {
            cityRepository.save(city$);
            notification.setMessage("<b>" + city$.getName() +"</b> a été " + (creation ? "ajoutée." : "modifiée."));
            creation = true;
            city$ = new Housing();
        } catch (Exception e){
            notification.setType(Level.ERROR);
            notification.setMessage("Erreur lors de la " + (creation ? "création" : "modification") + " de la ville <b>[ " + city$.getName() + " ]</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("city", city$);
            view.getModel().put("creation", creation);
            view.getModel().put("provinces", provinceRepository.findAllByOrderByNameAsc());
            view.getModel().put("provinceId", provinceId);
            view.getModel().put("notification", notification);
            view.setViewName("admin/city/save");
        }else{
            attributes.addFlashAttribute("provinceId", provinceId);
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/city/list");
        }
        return view;
    }
}
