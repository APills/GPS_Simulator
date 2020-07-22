/*
 *   <GPS NMEA 0183 Simulator for Raspberry Pi>
 *   Copyright (C) <2020>  <Andrew Pillsbury>
 *
 */
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
        File file = new File("/home/pi/Desktop/Debug/ConfigurationFiles/SimulationConfig");
        Scanner scanner = new Scanner(file);
        String pass = "";
        try {pass = String.valueOf(scanner.next());}
        catch (NoSuchElementException e){
            VariableStorage.Password = "";
        }
        VariableStorage.Password = pass;
        launch(args);
    }
    /**
     * @author APills 1.0
     *  Due to limitations with Java's Serial Communications only 1 instance of a serial port can be open at once and
     *  they do not close correctly when a program is closed, if you close the application with the serial port open it
     *  will stay in use permanently. Opening a new instance of the application will not allow you to instantiate that
     *  previous port correctly or properly close it, however data can still be sent to it if you reinstantiate it as if
     *  it was never opened.
     *
     *  To counteract this issue I have made it so that the application can not be closed normally. When the user
     *  requests to exit the program the request is directly thrown away and not acknowledged instead the program can
     *  still be minimized to the task bar. If the program is closed using task manager or a similar application it will
     *  immediately close.
     *
     *
     *  The start method is run on startup similarly to how initialize, this start method calls the AeroTrainSplash class
     *  and shows it for 2.5 seconds before closing it, in that time AeroTrainSplash's initialize method can run.
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
