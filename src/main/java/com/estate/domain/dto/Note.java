package com.estate.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Note {
    private String subject;
    private String content;
    private Map<String,String> data = new HashMap<>();
    private String image;
}
