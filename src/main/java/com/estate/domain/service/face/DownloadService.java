package com.estate.domain.service.face;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface DownloadService {
    void downloadTemplate(String fileName, HttpServletResponse response) throws IOException;
}
