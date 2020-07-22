package GPS_Simulator;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.*;

import java.io.*;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class VariableStorage {
    public static int simNum;
    public static int DataBitsVar = 8;
    public static int ParityModeVar = 0;
    public static int StopBitsVar = 1;
    public static int BaudRateVar = 4800;


    public static String Password = "";
    public static int PassFails = 0;
    public static int Timeout = 0;
    public static float countdown = 0;
    public static String getLastScreen = "none";
    public static int selection;
    public static boolean selected;
    static File passFile = new File("/home/pi/Desktop/Debug/ConfigurationFiles/SimulationConfig");

    public static boolean storePass(String pass) throws IOException {

        FileWriter fileWriter = new FileWriter(passFile, false);
        fileWriter.write(pass);
        fileWriter.close();
        return checkPass(pass);
    }

    private static boolean checkPass(String pass) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(passFile));
        String checkPass = (fileReader.readLine());
        fileReader.close();
        return Objects.equals(checkPass, pass);
    }

    static void maximize(ActionEvent actionEvent) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        Window window = ((Button)actionEvent.getSource()).getScene().getWindow();
        window.setX(bounds.getMinX());
        window.setY(bounds.getMinY());
        window.setWidth(bounds.getWidth());
        window.setHeight(bounds.getHeight());
    }

    static void maximize(Window window) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        window.setX(bounds.getMinX());
        window.setY(bounds.getMinY());
        window.setWidth(bounds.getWidth());
        window.setHeight(bounds.getHeight());
    }

    public static void windowInit(ActionEvent actionEvent, Scene view) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        requireNonNull(window).setScene(view);
        window.setTitle("GPS Simulator");
        //window.initStyle(StageStyle.UNDECORATED);
        window.centerOnScreen();
        VariableStorage.maximize(window);
        window.show();
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);
    }
}
