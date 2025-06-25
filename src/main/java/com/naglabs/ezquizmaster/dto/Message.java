package com.naglabs.ezquizmaster.dto;

import lombok.Data;

import java.util.List;

@Data
public class Message{
    public String role;
    public String content;
    public Object refusal;
    public List<Object> annotations;
}