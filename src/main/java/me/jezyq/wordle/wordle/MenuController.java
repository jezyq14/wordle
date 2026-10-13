package me.jezyq.wordle.wordle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MenuController {

    @FXML private Button dailyButton;
    @FXML private Label dailyStatusLabel;

    private final NytService nytService = new NytService();
    private DailyGameState currentDailyState;

    @FXML
    public void initialize() {
        currentDailyState = nytService.loadDailyState();

        if (currentDailyState != null) {
            if (currentDailyState.isCompleted()) {
                dailyButton.setDisable(true);
                dailyStatusLabel.setText(currentDailyState.isWon()
                        ? "Dzisiejsze słowo NYT odgadnięte!"
                        : "Nie udało ci się dzisiaj zgadnąć słowa.");
            } else if (!currentDailyState.getGuesses().isEmpty()) {
                dailyButton.setText("Kontynuuj Daily (" + currentDailyState.getGuesses().size() + "/6)");
            }
        }
    }

    @FXML
    private void onDailyClicked() {
        try {
            if (currentDailyState == null) {
                // fetch new game
                String word = nytService.fetchDailyWord();
                currentDailyState = new DailyGameState(LocalDate.now().toString(), word);
                nytService.saveDailyState(currentDailyState);
            }

            startGame(true, currentDailyState);
        } catch (Exception e) {
            e.printStackTrace();
            dailyStatusLabel.setText("Błąd pobierania słowa z NYT. Sprawdź połączenie.");
        }
    }

    @FXML
    private void onUnlimitedClicked() {
        startGame(false, null);
    }

    private void startGame(boolean isDaily, DailyGameState dailyState) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("wordle-view.fxml"));
            Parent root = loader.load();

            WordleController controller = loader.getController();
            controller.setupGame(isDaily, dailyState);

            Stage stage = (Stage) dailyButton.getScene().getWindow();
            stage.setScene(new Scene(root, 520, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onResetDailyDebug() {
        new java.io.File("daily_status.json").delete();
        dailyButton.setDisable(false);
        dailyButton.setText("Tryb Daily (NYT)");
        dailyStatusLabel.setText("Zresetowano!");
        currentDailyState = null;
    }
}