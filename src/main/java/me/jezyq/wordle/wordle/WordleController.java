package me.jezyq.wordle.wordle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;


import java.util.HashMap;
import java.util.Map;

public class WordleController {

    @FXML
    private VBox rootContainer;

    @FXML
    private GridPane boardGrid;

    @FXML
    private VBox keyboardContainer;

    private final Label[][] tiles = new Label[6][5];
    private final Map<String, Button> keyboardButtons = new HashMap<>();

    private int currentRow = 0;
    private int currentCol = 0;

    private final WordService wordService = new WordService();
    private String targetWord;
    private boolean gameOver = false;

    private boolean isDailyMode = false;
    private final NytService nytService = new NytService();

    private DailyGameState dailyState;

    @FXML
    private Label headerTitleLabel;

    @FXML
    public void initialize() {
        createBoard();
        createKeyboard();
        setupPhysicalKeyboard();
    }

    private void createBoard() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                Label tile = new Label();
                tile.getStyleClass().add("tile");
                tiles[row][col] = tile;
                boardGrid.add(tile, col, row);
            }
        }
    }

    private void createKeyboard() {
        String[][] keyRows = {
                {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
                {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
                {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "BACKSPACE"}
        };

        for (String[] rowKeys : keyRows) {
            HBox rowBox = new HBox(6);
            rowBox.setAlignment(Pos.CENTER);

            for (String key : rowKeys) {
                Button btn = new Button();
                btn.getStyleClass().add("keyboard-key");

                btn.setFocusTraversable(false);

                if (key.equals("BACKSPACE")) {
                    btn.setText("⌫");
                    btn.getStyleClass().add("keyboard-key-wide");
                } else if (key.equals("ENTER")) {
                    btn.setText("ENTER");
                    btn.getStyleClass().add("keyboard-key-wide");
                } else {
                    btn.setText(key);
                }

                btn.setOnAction(e -> processKey(key));

                keyboardButtons.put(key, btn);
                rowBox.getChildren().add(btn);
            }

            keyboardContainer.getChildren().add(rowBox);
        }
    }

    private void setupPhysicalKeyboard() {
        rootContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handlePhysicalKeyPress);
            }
        });
    }

    private void handlePhysicalKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.BACK_SPACE) {
            processKey("BACKSPACE");
        } else if (code == KeyCode.ENTER) {
            processKey("ENTER");
        } else if (code.isLetterKey()) {
            processKey(code.getName().toUpperCase());
        }
    }

    private void processKey(String key) {
        if (currentRow >= 6) {
            return;
        }

        switch (key) {
            case "BACKSPACE" -> handleBackspace();
            case "ENTER" -> handleEnter();
            default -> {
                if (key.length() == 1 && Character.isLetter(key.charAt(0))) {
                    handleLetter(key);
                }
            }
        }
    }

    private void handleLetter(String letter) {
        if (currentCol < 5) {
            Label tile = tiles[currentRow][currentCol];
            tile.setText(letter);
            tile.getStyleClass().add("tile-filled");
            currentCol++;
        }
    }

    private void handleBackspace() {
        if (currentCol > 0) {
            currentCol--;
            Label tile = tiles[currentRow][currentCol];
            tile.setText("");
            tile.getStyleClass().remove("tile-filled");
        }
    }

    private void handleEnter() {
        if (gameOver) return;

        if (currentCol < 5) {
            showToast("Za mało liter!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < 5; c++) {
            sb.append(tiles[currentRow][c].getText());
        }
        String guess = sb.toString();

        if (!wordService.isValidWord(guess)) {
            showToast("Nie ma takiego słowa w słowniku!");
            return;
        }

        TileStatus[] evaluation = wordService.evaluateGuess(guess, targetWord);

        for (int col = 0; col < 5; col++) {
            Label tile = tiles[currentRow][col];
            TileStatus status = evaluation[col];
            String letter = String.valueOf(guess.charAt(col));

            tile.getStyleClass().removeAll("tile-filled");
            switch (status) {
                case CORRECT -> tile.getStyleClass().add("tile-correct");
                case PRESENT -> tile.getStyleClass().add("tile-present");
                case ABSENT -> tile.getStyleClass().add("tile-absent");
            }

            updateKeyboardKey(letter, status);
        }

        if (isDailyMode && dailyState != null) {
            dailyState.getGuesses().add(guess);
        }

        if (guess.equals(targetWord)) {
            showToast("Brawo! Odgadłeś słowo!");
            gameOver = true;
            if (isDailyMode && dailyState != null) {
                dailyState.setCompleted(true);
                dailyState.setWon(true);
                nytService.saveDailyState(dailyState);
            }
            return;
        }

        currentRow++;
        currentCol = 0;

        // Sprawdź przegraną (po 6 próbach)
        if (currentRow >= 6) {
            showToast("Koniec gry! Słowo to: " + targetWord);
            gameOver = true;
            if (isDailyMode && dailyState != null) {
                dailyState.setCompleted(true);
                dailyState.setWon(false);
                nytService.saveDailyState(dailyState);
            }
            return;
        }

        if (isDailyMode && dailyState != null) {
            nytService.saveDailyState(dailyState);
        }
    }

    private void updateKeyboardKey(String letter, TileStatus status) {
        Button btn = keyboardButtons.get(letter);
        if (btn == null) return;

        if (btn.getStyleClass().contains("key-correct")) {
            return;
        }

        if (status == TileStatus.CORRECT) {
            btn.getStyleClass().removeAll("key-present", "key-absent");
            btn.getStyleClass().add("key-correct");
        } else if (status == TileStatus.PRESENT && !btn.getStyleClass().contains("key-present")) {
            btn.getStyleClass().add("key-present");
        } else if (status == TileStatus.ABSENT && !btn.getStyleClass().contains("key-present")) {
            btn.getStyleClass().add("key-absent");
        }
    }

    private void showToast(String message) {
        Notifications.create()
                .owner(rootContainer)
                .text(message)
                .position(Pos.TOP_CENTER)
                .hideAfter(javafx.util.Duration.seconds(1.8))
                .show();
    }

    @FXML
    private void onBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 520, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setupGame(boolean isDaily, DailyGameState state) {
        this.isDailyMode = isDaily;
        this.dailyState = state;

        headerTitleLabel.setText(isDaily ? "TRYB DAILY (NYT)" : "TRYB UNLIMITED");

        if (isDaily && state != null) {
            this.targetWord = state.getTargetWord();
            restorePreviousGuesses(state.getGuesses());
        } else {
            this.targetWord = wordService.getRandomTargetWord();
        }

        System.out.println("DEBUG (" + (isDaily ? "DAILY" : "UNLIMITED") + "): " + targetWord);
    }

    private void restorePreviousGuesses(java.util.List<String> previousGuesses) {
        for (String guess : previousGuesses) {
            for (int col = 0; col < 5; col++) {
                tiles[currentRow][col].setText(String.valueOf(guess.charAt(col)));
            }

            TileStatus[] evaluation = wordService.evaluateGuess(guess, targetWord);
            for (int col = 0; col < 5; col++) {
                Label tile = tiles[currentRow][col];
                TileStatus status = evaluation[col];
                String letter = String.valueOf(guess.charAt(col));

                switch (status) {
                    case CORRECT -> tile.getStyleClass().add("tile-correct");
                    case PRESENT -> tile.getStyleClass().add("tile-present");
                    case ABSENT -> tile.getStyleClass().add("tile-absent");
                }
                updateKeyboardKey(letter, status);
            }

            currentRow++;
        }
        currentCol = 0;
    }
}