package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.SymptomCheckRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SymptomTriageService {

    private static final String LEGAL_NOTICE =
            "This is not a medical diagnosis. Please consult a physical health practitioner immediately in emergencies.";

    private static final Map<String, String> SYMPTOM_URGENCY_MAP = Map.ofEntries(
        Map.entry("chest pain", "CRITICAL"),
        Map.entry("difficulty breathing", "CRITICAL"),
        Map.entry("shortness of breath", "CRITICAL"),
        Map.entry("severe headache", "HIGH"),
        Map.entry("sudden vision loss", "CRITICAL"),
        Map.entry("loss of consciousness", "CRITICAL"),
        Map.entry("high fever", "HIGH"),
        Map.entry("fever", "MODERATE"),
        Map.entry("cough", "LOW"),
        Map.entry("sore throat", "LOW"),
        Map.entry("fatigue", "LOW"),
        Map.entry("diarrhea", "MODERATE"),
        Map.entry("vomiting", "MODERATE"),
        Map.entry("rash", "MODERATE"),
        Map.entry("joint pain", "LOW"),
        Map.entry("back pain", "LOW"),
        Map.entry("abdominal pain", "MODERATE"),
        Map.entry("dizziness", "MODERATE"),
        Map.entry("palpitations", "HIGH"),
        Map.entry("swelling", "MODERATE")
    );

    private static final Map<String, String> URGENCY_RECOMMENDATION = Map.of(
        "CRITICAL", "Seek emergency medical care immediately. Call your local emergency number (108 in India).",
        "HIGH", "Visit the nearest hospital or clinic within the next few hours. Do not delay.",
        "MODERATE", "Schedule a doctor appointment within 24-48 hours. Monitor your symptoms closely.",
        "LOW", "Rest, stay hydrated, and monitor symptoms. Consult a pharmacist or doctor if symptoms persist beyond 3 days."
    );

    public Map<String, Object> evaluate(SymptomCheckRequest request) {
        String overallUrgency = computeOverallUrgency(request);
        List<Map<String, Object>> symptomAnalysis = analyzeSymptoms(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("overallUrgency", overallUrgency);
        response.put("recommendation", URGENCY_RECOMMENDATION.get(overallUrgency));
        response.put("symptomAnalysis", symptomAnalysis);
        response.put("durationDays", request.durationDays());
        response.put("durationWarning", getDurationWarning(request.durationDays(), overallUrgency));
        response.put("LEGAL_NOTICE", LEGAL_NOTICE);

        return response;
    }

    private String computeOverallUrgency(SymptomCheckRequest request) {
        String maxUrgency = "LOW";
        for (String symptom : request.symptoms()) {
            String urgency = SYMPTOM_URGENCY_MAP.getOrDefault(symptom.toLowerCase().trim(), "LOW");
            maxUrgency = higherUrgency(maxUrgency, urgency);
        }

        for (Map.Entry<String, Integer> entry : request.severityMap().entrySet()) {
            if (entry.getValue() >= 8) {
                maxUrgency = higherUrgency(maxUrgency, "HIGH");
            }
            if (entry.getValue() >= 9) {
                maxUrgency = higherUrgency(maxUrgency, "CRITICAL");
            }
        }

        return maxUrgency;
    }

    private List<Map<String, Object>> analyzeSymptoms(SymptomCheckRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String symptom : request.symptoms()) {
            String urgency = SYMPTOM_URGENCY_MAP.getOrDefault(symptom.toLowerCase().trim(), "LOW");
            Integer severity = request.severityMap().getOrDefault(symptom, null);
            Map<String, Object> analysis = new LinkedHashMap<>();
            analysis.put("symptom", symptom);
            analysis.put("urgencyLevel", urgency);
            if (severity != null) analysis.put("reportedSeverity", severity + "/10");
            result.add(analysis);
        }
        return result;
    }

    private String getDurationWarning(int days, String urgency) {
        if ("CRITICAL".equals(urgency) || "HIGH".equals(urgency)) {
            return "Given the severity of your symptoms, duration is secondary — seek care immediately.";
        }
        if (days > 7) return "Symptoms lasting more than 7 days require medical evaluation.";
        if (days > 3) return "Symptoms persisting beyond 3 days should be reviewed by a healthcare provider.";
        return "Monitor your condition and rest.";
    }

    private String higherUrgency(String a, String b) {
        List<String> order = List.of("LOW", "MODERATE", "HIGH", "CRITICAL");
        return order.indexOf(a) >= order.indexOf(b) ? a : b;
    }
}
