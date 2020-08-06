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
    static File customSim;
    static int DataBitsVar = 8;
    static int ParityModeVar = 0;
    static int StopBitsVar = 1;
    static int BaudRateVar = 4800;
    static int HiddenSettingsButton = 13;
    static int HiddenExitButton = 15;
    static int HiddenSettingsButtonLock = 0;
    static String Password = "";
    static int PassFails = 0;
    static int Timeout = 0;
    static float countdown = 0;
    static String getLastScreen = "none";
    static int selection;
    static boolean selected;
    static final File passFile = new File("/home/pi/Desktop/Debug/ConfigurationFiles/Passwords");
    static String sim1 = "";
    static String sim2 = "";
    static String sim3 = "";
    static String sim4 = "";
    static String sim5 = "";
    static String sim6 = "";

    /**
     * @param sim1Text The text of the first delimited item in the LocationsText file
     * @param sim2Text The text of the second delimited item in the LocationsText file
     * @param sim3Text The text of the third delimited item in the LocationsText file
     * @param sim4Text The text of the fourth delimited item in the LocationsText file
     * @param sim5Text The text of the fifth delimited item in the LocationsText file
     * @param sim6Text The text of the sixth delimited item in the LocationsText file
     */
    static void setLocations(Text sim1Text, Text sim2Text, Text sim3Text, Text sim4Text, Text sim5Text, Text sim6Text) {
        sim1Text.setText(sim1);
        sim2Text.setText(sim2);
        sim3Text.setText(sim3);
        sim4Text.setText(sim4);
        sim5Text.setText(sim5);
        sim6Text.setText(sim6);
    }

    /**
     * @param pass The password that is to be stored
     * @return Returns a boolean if when it checks the password it has been successfully changed returns true, otherwise it will return false
     * @throws IOException Throws an Exception due to FileOutputStream being capable of throwing IO or FileNotFound Exceptions
     */
    static boolean storePass(String pass) throws IOException {

        FileWriter fileWriter = new FileWriter(passFile, false);
        fileWriter.write(pass);
        fileWriter.close();
        return checkPass(pass);
    }

    /**
     * @param pass The password that is to be checked against the password in the Passwords file
     * @return Returns a boolean if when it checks the password it has been successfully changed returns true, otherwise it will return false
     * @throws IOException Throws and Exception due to FileReader being capable of throwing IO or FileNotFound Exceptions
     */
    private static boolean checkPass(String pass) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(passFile));
        String checkPass = (fileReader.readLine());
        fileReader.close();
        return Objects.equals(checkPass, pass);
    }

    /**
     * @param window The current window that is to be set
     */
    static void maximize(Window window) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        window.setX(bounds.getMinX());
        window.setY(bounds.getMinY());
        window.setWidth(bounds.getWidth());
        window.setHeight(bounds.getHeight());
    }

    /**
     * @param actionEvent The action of a button press
     * @param view The scene of the Parent FXML to be set
     */
    static void windowInit(ActionEvent actionEvent, Scene view) {
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