package me.jezyq.wordle.wordle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

public class NytService {

    private static final String STATUS_FILE = "daily_status.json";
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String fetchDailyWord() throws Exception {
        LocalDate today = LocalDate.now();
        String url = String.format("%s/%s.json", AppConfig.getServerUrl(), today);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("NYT status: " + response.statusCode());
        }

        JsonNode jsonNode = objectMapper.readTree(response.body());
        return jsonNode.get("solution").asText().toUpperCase();
    }

    public DailyGameState loadDailyState() {
        File file = new File(STATUS_FILE);

        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try {
            DailyGameState state = objectMapper.readValue(file, DailyGameState.class);
            if (state != null && LocalDate.now().toString().equals(state.getDate())) {
                return state;
            }
        } catch (Exception e) {
            System.err.println("Ostrzeżenie: Plik zapisu był uszkodzony lub pusty. Tworzę nowy stan.");
        }
        return null;
    }

    public void saveDailyState(DailyGameState state) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(STATUS_FILE), state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}