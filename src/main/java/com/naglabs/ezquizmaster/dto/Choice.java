package com.naglabs.ezquizmaster.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public  class Choice{
    public int index;
    public Message message;
    public Object logprobs;
    public String finish_reason;
}
