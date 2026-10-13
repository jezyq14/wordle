package me.jezyq.wordle.wordle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class AppConfig {

    private static final Map<String, String> env = new HashMap<>();

    static {
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        env.put(parts[0].trim(), parts[1].trim());
                    }
                }
                System.out.println("Wczytano config z pliku .env");
            } catch (Exception e) {
                System.err.println("Nie udało się odczytać pliku .env, użyto defaultowych wartości.");
            }
        }
    }

    public static String getServerUrl() {
        return env.getOrDefault("SERVER_URL", "http://localhost:3000");
    }

    public static String getNytApiUrl() {
        return env.getOrDefault("NYT_API_URL", "https://www.nytimes.com/svc/wordle/v2");
    }
}