package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.OpenAiResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;

    public OpenAiService() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();  // Loads .env from project root
        String apiKey = dotenv.get("openai.apikey");
        String baseUrl = dotenv.get("openai.url");

        this.webClient = WebClient.builder()
                                  .baseUrl(baseUrl)
                                  .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                                  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                  .build();
    }

    public String generateQuestions(String prompt) {
        Map<String, Object> requestBody = Map.of(
                //"model", "gpt-3.5-turbo", // commented, as it is not yeidling 19 questions accurately
                "model", "gpt-4o",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 1.0
        );

        String response = webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    return Mono.error(new RuntimeException("OpenAI error: " + errorBody));
                                })
                )
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(5))
                        .maxBackoff(Duration.ofSeconds(20))
                        .filter(e -> e instanceof WebClientResponseException.TooManyRequests))
                .block();
        System.out.println("OpenAI raw response: " + response);
        return response;
    }

    public OpenAiResponse generateQuestionsLocal(String prompt) {
        ObjectMapper objectMapper = new ObjectMapper();

        OpenAiResponse response = null;
        try (InputStream inputStream = OpenAiService.class.getClassLoader()
                .getResourceAsStream("openai-response.json")) {
            response = objectMapper.readValue(
                    inputStream,
                    new TypeReference<OpenAiResponse>() {
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("OpenAI raw response local: " + response);
        return response;
    }
}
