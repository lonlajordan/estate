package com.estate.domain.service.face;

import com.estate.domain.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LogService {
    Page<Log> findAll(int p);

    Optional<Log> findById(long id);

    List<Log> search(String level, String message, Date start, Date end);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    void handleError(Integer status, HttpSession session, Model model, Principal principal, Exception exception);
}
