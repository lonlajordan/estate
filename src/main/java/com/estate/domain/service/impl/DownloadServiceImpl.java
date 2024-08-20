package com.estate.domain.service.impl;

import com.estate.domain.service.face.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class DownloadServiceImpl implements DownloadService {

    @Override
    public void downloadTemplate(String fileName, HttpServletResponse httpServletResponse) throws IOException {
        Path filePath = Paths.get("src/main/resources/examples").toAbsolutePath().normalize().resolve(fileName).normalize();
        InputStream inputStream = new InputStreamResource(new FileInputStream(filePath.toFile())).getInputStream();
        httpServletResponse.setContentType(String.valueOf(MediaType.APPLICATION_OCTET_STREAM));
        httpServletResponse.setHeader("Content-Transfer-Encoding","binary");
        httpServletResponse.setHeader("Content-Disposition","attachment;filename="+fileName);
        try {
            IOUtils.copy(inputStream,httpServletResponse.getOutputStream());
            inputStream.close();
            httpServletResponse.flushBuffer();
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du fichier", e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
