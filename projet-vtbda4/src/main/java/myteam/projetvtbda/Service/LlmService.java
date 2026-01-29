package myteam.projetvtbda.Service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${perplexity.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String poserQuestion(String question) {
        System.out.println("API Key utilisée : " + apiKey); // ← DEBUG

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            System.out.println(" Envoi de la requête à Perplexity..."); // ← DEBUG

            Map<String, Object> body = Map.of(
                    "model", "sonar",
                    "messages", List.of(
                            Map.of("role", "user", "content", question)
                    ),
                    "temperature", 0.7,
                    "max_tokens", 500
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.perplexity.ai/chat/completions",
                    request,
                    Map.class
            );

            System.out.println(" Réponse reçue : " + response.getStatusCode());

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List) responseBody.get("choices");
            Map<String, Object> message = (Map) choices.get(0).get("message");
            String reponse = (String) message.get("content");

            return reponse;

        } catch (Exception e) {
            System.err.println(" Erreur : " + e.getMessage()); // ← DEBUG
            e.printStackTrace();
            return "Erreur lors de l'appel à l'API : " + e.getMessage();
        }
    }
}