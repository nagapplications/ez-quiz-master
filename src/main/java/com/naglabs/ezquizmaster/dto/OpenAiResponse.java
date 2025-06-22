package com.naglabs.ezquizmaster.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponse{
    public String id;
    public String object;
    public int created;
    public String model;
    public List<Choice> choices;
    public Usage usage;
    public String service_tier;
    public Object system_fingerprint;
}



class CompletionTokensDetails{
    public int reasoning_tokens;
    public int audio_tokens;
    public int accepted_prediction_tokens;
    public int rejected_prediction_tokens;
}


class PromptTokensDetails{
    public int cached_tokens;
    public int audio_tokens;
}

class Usage{
    public int prompt_tokens;
    public int completion_tokens;
    public int total_tokens;
    public PromptTokensDetails prompt_tokens_details;
    public CompletionTokensDetails completion_tokens_details;
}

