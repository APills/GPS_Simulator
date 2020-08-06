package GPS_Simulator;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main extends Application {
    static Stage originStage;

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("/home/pi/Desktop/Debug/ConfigurationFiles/Passwords");
        Scanner scanner = new Scanner(file);
        String pass = "";
        try {pass = String.valueOf(scanner.next());}
        catch (NoSuchElementException e){VariableStorage.Password = "";}
        VariableStorage.Password = pass;
        launch(args);
    }
    /**
     *  Due to how Serial Ports work, only 1 instance of a serial port can be open at once and due to either my own
     *  error or otheriwse they do not close correctly when a program is closed, if you close the application with the
     *  serial port open it will stay in use permanently. Opening a new instance of the application will not allow you
     *  to instantiate that previous port to correctly or properly close it, however data can still be sent to it if you
     *  reinstantiate it as if it was never opened.
     *
     *  To counteract this issue I have made it so that the application can not be closed normally and to make the
     *  application nicer upon boot the windows are undecorated which conveniently adds to the inability to close it.
     *  If the user requests to exit the program the request is consumed and not acknowledged the program can
     *  still be minimized to the task bar or moved should you find a way to undecorate window. If the program is
     *  closed using task manager or a similar application it will immediately close as normal and cause the issue with
     *  the serial port.
     *
     *
     *  The start method is run on startup similarly to initialize when the JavaFX thread is launched, this start
     *  method calls the Splash class and shows it for 4.5 seconds before closing it, in that time Splash's initialize
     *  method can run.
     */
    @Override public void start(Stage primaryStage) throws IOException {

        Parent primary = FXMLLoader.load(getClass().getResource("Splash.fxml"));
        originStage = primaryStage;
        originStage.setTitle("GPS Simulator");
        originStage.setScene(new Scene(primary, 800,480));
        originStage.setAlwaysOnTop(true);
        originStage.initStyle(StageStyle.UNDECORATED);
        originStage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(4.5));
        delay.setOnFinished(event -> originStage.close());
        delay.play();
        originStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);
    }
}
