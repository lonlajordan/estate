package com.estate.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl  {

    /*
    public ModelAndView save(Media media, MultipartFile file, Boolean multiple, RedirectAttributes attributes) {

        String fileName = "medias/" + new Date().getTime() + "-" + media$.getId() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String link = "";
        media$.setFileName(fileName);
        media$.setLink(link);
        String mimeType = new MimetypesFileTypeMap().getContentType(fileName).toLowerCase();
        if(mimeType.contains("image")){
            media$.setType(MediaType.IMAGE);
        }else if(mimeType.contains("video")){
            media$.setType(MediaType.VIDEO);
        }else if(mimeType.contains("audio")){
            media$.setType(MediaType.AUDIO);
        }else{
            media$.setType(MediaType.DOCUMENT);
        }
        mediaRepository.save(media$);

        return view;
    }*/
}
