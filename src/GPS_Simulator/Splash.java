package GPS_Simulator;

import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class Splash {
    @FXML Label label;
    @FXML javafx.scene.layout.AnchorPane AnchorPane;

    /**
     * The initialize method opens the Simulations FXML and shows it after 4 seconds, because of the .5 second
     * difference between when the Splash FXML disappears and when the Simulations FXML appears the transition is
     * covered by both FXML windows being open at the same time never showing the Desktop.
     */
    public void initialize() throws IOException {
        Parent primaryViewParent = FXMLLoader.load(getClass().getResource("Simulations.fxml"));
        Scene primaryView = new Scene(primaryViewParent);
        Stage window = new Stage();
        window.centerOnScreen();
        VariableStorage.maximize(window);
        Objects.requireNonNull(window).setScene(primaryView);
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        window.setTitle("GPS Simulator");
        window.initStyle(StageStyle.UNDECORATED);
        delay.setOnFinished(event -> window.show());
        delay.play();
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);
    }
}
