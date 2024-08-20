package com.estate.controller;


import com.estate.domain.service.face.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class DownloadController {
    private final DownloadService downloadService;

    @GetMapping(value="/templates/{fileName}")
    public void downloadXLSX(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        downloadService.downloadTemplate(fileName,response);
    }

}
