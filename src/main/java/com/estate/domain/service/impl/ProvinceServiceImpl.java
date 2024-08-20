package com.estate.domain.service.impl;

import com.estate.domain.dto.Notification;
import com.estate.domain.entity.Housing;
import com.estate.domain.entity.Log;
import com.estate.domain.entity.Province;
import com.estate.domain.enumaration.Level;
import com.estate.domain.service.face.ProvinceService;
import com.estate.repository.CityRepository;
import com.estate.repository.LogRepository;
import com.estate.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {
    private final CityRepository cityRepository;
    private final ProvinceRepository provinceRepository;
    private final LogRepository logRepository;

    @Override
    public Optional<Province> findById(long id){
        return provinceRepository.findById(id);
    }

    @Override
    public List<Province> findAll(){
        return provinceRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Housing> findAllCityByProvinceId(long provinceId) {
        return new ArrayList<>();
    }

    @Override
    public ModelAndView createOrUpdate(Province province, Boolean multiple, RedirectAttributes attributes){
        Province province$ = province;
        boolean creation = true;
        if(province.getId() != null){
            Optional<Province> _province = provinceRepository.findById(province.getId());
            if(_province.isPresent()){
                province$ = _province.get();
                province$.setName(province.getName());
                creation = false;
            }
        }
        Notification notification;
        try {
            provinceRepository.save(province$);
            notification = Notification.info("<b>" + province$.getName() +"</b> a été " + (creation ? "ajoutée." : "modifiée."));
            logRepository.save(Log.info(notification.getMessage()));
            creation = true;
            province$ = new Province();
        } catch (Exception e){
            notification = Notification.error("Erreur lors de la " + (creation ? "création" : "modification") + " de la province <b>[ " + province$.getName() + " ]</b>.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }

        ModelAndView view = new ModelAndView();
        if(multiple || Level.ERROR.equals(notification.getType())){
            view.getModel().put("province", province$);
            view.getModel().put("creation", creation);
            view.getModel().put("notification", notification);
            view.setViewName("admin/province/save");
        }else{
            attributes.addFlashAttribute("notification", notification);
            view.setViewName("redirect:/province/list");
        }
        return view;
    }

    @Override
    public RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes){
        Notification notification = Notification.info();
        try {
            provinceRepository.deleteAll(provinceRepository.findAllById(ids));
        }catch (Exception e){
            notification = Notification.error("Erreur lors de la suppression des provinces.");
            logRepository.save(Log.error(notification.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/province/list", true);
    }
}
