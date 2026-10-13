module me.jezyq.wordle.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    requires java.security.sasl;
    requires jdk.crypto.cryptoki;
    requires jdk.crypto.ec;

    opens me.jezyq.wordle.wordle to javafx.fxml;
    exports me.jezyq.wordle.wordle;
}