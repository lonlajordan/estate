package com.estate.domain.service.face;

import com.estate.domain.entity.Log;
import com.estate.domain.form.LogSearch;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

public interface LogService {
    Page<Log> findAll(int page);

    Page<Log> findAll(LogSearch form);

    Optional<Log> findById(long id);

    RedirectView deleteAllByIds(ArrayList<Long> ids, RedirectAttributes attributes);

    void handleError(Integer status, HttpSession session, Model model, Principal principal, Exception exception);
}
