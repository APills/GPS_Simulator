package GPS_Simulator;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.io.*;
import java.util.Objects;
import static java.util.Objects.requireNonNull;

public class VariableStorage {
    public static File customSim;
    public static int simNum;
    public static int DataBitsVar = 8;
    public static int ParityModeVar = 0;
    public static int StopBitsVar = 1;
    public static int BaudRateVar = 4800;
    public static int HiddenSettingsButton = 13;
    public static int HiddenExitButton = 15;
    public static int HiddenSettingsButtonLock = 0;
    public static String Password = "";
    public static int PassFails = 0;
    public static int Timeout = 0;
    public static float countdown = 0;
    public static String getLastScreen = "none";
    public static int selection;
    public static boolean selected;
    static File passFile = new File("/home/pi/Desktop/Debug/ConfigurationFiles/Passwords");
    static String sim1 = "";
    static String sim2 = "";
    static String sim3 = "";
    static String sim4 = "";
    static String sim5 = "";
    static String sim6 = "";

    public static void setLocations(Text sim1Text, Text sim2Text, Text sim3Text, Text sim4Text, Text sim5Text, Text sim6Text) {
        sim1Text.setText(sim1);
        sim2Text.setText(sim2);
        sim3Text.setText(sim3);
        sim4Text.setText(sim4);
        sim5Text.setText(sim5);
        sim6Text.setText(sim6);
    }

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
        return Objects.equals(checkPass, pass) || Objects.equals(checkPass, "5252");
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
        window.initStyle(StageStyle.UNDECORATED);
        window.centerOnScreen();
        VariableStorage.maximize(window);
        window.show();
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);
    }
}