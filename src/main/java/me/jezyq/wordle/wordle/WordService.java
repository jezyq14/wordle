package me.jezyq.wordle.wordle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordService {

    private final Set<String> validWords = new HashSet<>();
    private final List<String> targetWords = new ArrayList<>();
    private final Random random = new Random();

    public WordService() {
        loadWords();
    }

    private void loadWords() {
        try (InputStream is = getClass().getResourceAsStream("words.txt")) {
            if (is == null) {
                System.err.println("ERROR: Nie znaleziono pliku words.txt w resources!");
                List<String> fallback = List.of("CRANE", "SLATE", "REACT", "PLANT", "GHOST");
                validWords.addAll(fallback);
                targetWords.addAll(fallback);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim().toUpperCase();
                    if (word.length() == 5) {
                        validWords.add(word);
                        targetWords.add(word);
                    }
                }
            }
            System.out.println("Załadowano słowa: " + validWords.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidWord(String word) {
        return validWords.contains(word.toUpperCase());
    }

    public String getRandomTargetWord() {
        if (targetWords.isEmpty()) return "REACT";
        return targetWords.get(random.nextInt(targetWords.size()));
    }

    public TileStatus[] evaluateGuess(String guess, String target) {
        TileStatus[] result = new TileStatus[5];
        char[] targetChars = target.toCharArray();
        char[] guessChars = guess.toCharArray();

        Map<Character, Integer> letterCounts = new HashMap<>();
        for (char c : targetChars) {
            letterCounts.put(c, letterCounts.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == targetChars[i]) {
                result[i] = TileStatus.CORRECT;
                letterCounts.put(guessChars[i], letterCounts.get(guessChars[i]) - 1);
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] == null) {
                char c = guessChars[i];
                if (letterCounts.getOrDefault(c, 0) > 0) {
                    result[i] = TileStatus.PRESENT;
                    letterCounts.put(c, letterCounts.get(c) - 1);
                } else {
                    result[i] = TileStatus.ABSENT;
                }
            }
        }

        return result;
    }
}