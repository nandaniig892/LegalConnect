package com.ruralhealthcare;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

@Component
public class QualifierStartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====================================================");
        System.out.println("QUALIFIER STARTUP RUNNER: STARTING WEBOOK GENERATION");
        System.out.println("====================================================");

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "0827CS231166");
            requestBody.put("email", "john@example.com");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("Sending POST to " + url);
            String response = restTemplate.postForObject(url, entity, String.class);

            System.out.println("RESPONSE RECEIVED:");
            System.out.println(response);
            System.out.println("====================================================");
        } catch (Exception e) {
            System.err.println("Error calling generateWebhook API:");
            e.printStackTrace();
        }
    }
}
