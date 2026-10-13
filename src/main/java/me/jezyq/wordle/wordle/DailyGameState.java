package me.jezyq.wordle.wordle;

import java.util.ArrayList;
import java.util.List;

public class DailyGameState {
    private String date;
    private String targetWord;
    private List<String> guesses = new ArrayList<>();
    private boolean completed = false;
    private boolean won = false;

    public DailyGameState() {}

    public DailyGameState(String date, String targetWord) {
        this.date = date;
        this.targetWord = targetWord;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTargetWord() { return targetWord; }
    public void setTargetWord(String targetWord) { this.targetWord = targetWord; }

    public List<String> getGuesses() { return guesses; }
    public void setGuesses(List<String> guesses) { this.guesses = guesses; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isWon() { return won; }
    public void setWon(boolean won) { this.won = won; }
}