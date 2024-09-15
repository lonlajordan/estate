package com.estate.domain.service.impl;

import com.estate.domain.entity.Picture;
import com.estate.domain.service.face.PictureService;
import com.estate.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PictureServiceServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;


    public List<Picture> findAll(){return pictureRepository.findAll();}
}
