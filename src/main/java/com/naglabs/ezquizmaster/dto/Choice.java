package com.naglabs.ezquizmaster.dto;

import lombok.Getter;

@Getter
public  class Choice{
    public int index;
    public Message message;
    public Object logprobs;
    public String finish_reason;
}
