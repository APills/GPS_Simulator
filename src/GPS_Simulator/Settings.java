package GPS_Simulator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static GPS_Simulator.VariableStorage.countdown;

public class Settings {
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    @FXML
    public Label passHint;
    @FXML
    public VBox dataBitsBox;
    @FXML
    public VBox parityBox;
    @FXML
    public VBox bitSelectionBox;
    @FXML
    public VBox baudRateBox;
    @FXML
    public ToggleGroup Parity;
    @FXML
    public ToggleGroup Bits;
    @FXML
    Button PasswordButton;
    @FXML
    Button passwordChange;
    @FXML
    RadioButton noParity;
    @FXML
    RadioButton oddParity;
    @FXML
    RadioButton evenParity;
    @FXML
    RadioButton markParity;
    @FXML
    RadioButton spaceParity;
    @FXML
    RadioButton oneBit;
    @FXML
    RadioButton onePointFiveBits;
    @FXML
    RadioButton twoBits;
    @FXML
    TextField dataBits;
    @FXML
    TextField baudRate;
    @FXML
    PasswordField passwordText;
    @FXML
    Label warnings;
    Runnable enable = () -> {
        baudRateBox.setDisable(!baudRateBox.isDisabled());
        bitSelectionBox.setDisable(!bitSelectionBox.isDisabled());
        dataBitsBox.setDisable(!parityBox.isDisabled());
        parityBox.setDisable(!parityBox.isDisabled());
        passwordChange.setDisable(!passwordChange.isDisabled());
    };
    Runnable TimeoutMessage = () -> {
        if (countdown > 0)
            passHint.setText("You have incorrectly entered your password too many times.");
        else
            passHint.setText("Enter Password to Lock/Unlock Settings");
    };
    Runnable countdownTimer = () -> {
        Platform.runLater(TimeoutMessage);
        if (countdown > 60) {
            warnings.setText((int) (Math.ceil(countdown / 60)) + " minutes Remaining");
        } else
            warnings.setText(((int) (countdown)) + " seconds Remaining");

        if (countdown == 0) {
            warnings.setText("");
        }
    };
    Runnable timeout = () -> {
        countdown = VariableStorage.Timeout;
        PasswordButton.setDisable(true);
        while (countdown > 0) {
            Platform.runLater(countdownTimer);
            countdown--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        PasswordButton.setDisable(false);
        Platform.runLater(countdownTimer);
        try {
            executor.awaitTermination(180, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    };
    Runnable PassChangeRevert = () -> passwordChange.setText("Enter New Password");
    Runnable PassChangeRevertWait = () -> {
        int i = 5;
        while (i > 0) {
            i--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(PassChangeRevert);
    };
    Runnable PassChangeSuccess = () -> {
        passwordChange.setText("Password Saved");
        executor.execute(PassChangeRevertWait);
    };
    Runnable PassChangeFail = () -> passwordChange.setText("Password Not Saved");
    private Boolean DataOrBaud = null;

    // 1234567890
    @FXML
    public void one(ActionEvent event) {
        charSend("1");
        serialSetting();
        event.consume();
    }

    @FXML
    public void two(ActionEvent event) {
        charSend("2");
        serialSetting();
        event.consume();
    }

    @FXML
    public void three(ActionEvent event) {
        charSend("3");
        serialSetting();
        event.consume();
    }

    @FXML
    public void four(ActionEvent event) {
        charSend("4");
        serialSetting();
        event.consume();
    }

    @FXML
    public void five(ActionEvent event) {
        charSend("5");
        serialSetting();
        event.consume();
    }

    @FXML
    public void six(ActionEvent event) {
        charSend("6");
        serialSetting();
        event.consume();
    }

    @FXML
    public void seven(ActionEvent event) {
        charSend("7");
        serialSetting();
        event.consume();
    }

    @FXML
    public void eight(ActionEvent event) {
        charSend("8");
        serialSetting();
        event.consume();
    }

    @FXML
    public void nine(ActionEvent event) {
        charSend("9");
        serialSetting();
        event.consume();
    }

    @FXML
    public void zero(ActionEvent event) {
        charSend("0");
        serialSetting();
        event.consume();
    }

    /**
     * /**
     *
     * @author APills 1.0        charSend sends the character to inputTo
     * pressed.
     */
    void charSend(String character) {
        inputTo(character);
    }

    /**
     * @author APills 1.0        The inputTo() method deals with where to append characters that are input via the onscreen keypad using a Boolean
     * Password, Data Bits, or Baud Rate
     */
    void inputTo(String character) {
        if (DataOrBaud == null) {
            passwordText.appendText(character);
        } else if (DataOrBaud) {
            dataBits.appendText(character);
        } else {
            baudRate.appendText(character);
        }
    }

    /**
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @author APills 1.0
     * The backspace button deals with where to remove characters that are in the 4 different textfields using 3 Booleans
     * Simulation Settings or Serial Settings, New Simulation Name or File Description, Data Bits or Baud Rate
     * then it calls the backspaceLogic() method
     */
    @FXML
    public void backspace(ActionEvent actionEvent) {
        if (DataOrBaud == null) {
            passwordText.setText(backspaceLogic(passwordText.getText()));
        } else if (DataOrBaud) {
            dataBits.setText(backspaceLogic(dataBits.getText()));
        } else {
            baudRate.setText(backspaceLogic(baudRate.getText()));
        }
        actionEvent.consume();
    }

    /**
     * @param str Takes an input string of the currently selected textfield
     * @return str returns the string with the last character removed
     * @author APills 1.0
     * The backspaceLogic() method is a convenience tool that allows backspace to work without having to repeat the
     * code block multiple times.
     */
    public String backspaceLogic(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        serialSetting();
        return str;
    }

    /**
     * @author APills 1.0
     * The initialize() method calls the configSetup() method as soon as the window loads.
     */
    public void initialize() {
        VariableStorage.Timeout = 0;
        VariableStorage.PassFails = 0;
        Platform.runLater(enable);
    }

    /**
     * @param actionEvent Uses the button press, this is used to get the current window
     * @throws IOException FXMLLoader throws an IOException when loading a Parent
     * @author APills 1.0
     * Returns the user to the Primary Window
     */
    public void returnTo(ActionEvent actionEvent) throws IOException {
        serialSetting();
        Parent simulationsViewParent = FXMLLoader.load(getClass().getResource("Simulations.fxml"));
        Scene simulationsView = new Scene(simulationsViewParent);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Objects.requireNonNull(window).setScene(simulationsView);
        window.setTitle("GPS Simulator");
        VariableStorage.maximize(window);
        window.show();
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);

    }

    /**
     * @author APills 1.0        The method serialSetting() gets all of the settings that have been input and then sets variables that are called
     * by the Primary window on initialization to update the serial settings.
     */
    public void serialSetting() {
        VariableStorage.BaudRateVar = Integer.parseInt(baudRate.getText());
        VariableStorage.DataBitsVar = Integer.parseInt(dataBits.getText());
        if (noParity.isSelected()) {
            VariableStorage.ParityModeVar = 0;
        }
        if (oddParity.isSelected()) {
            VariableStorage.ParityModeVar = 1;
        }
        if (evenParity.isSelected()) {
            VariableStorage.ParityModeVar = 2;
        }
        if (markParity.isSelected()) {
            VariableStorage.ParityModeVar = 3;
        }
        if (spaceParity.isSelected()) {
            VariableStorage.ParityModeVar = 4;
        }
        if (oneBit.isSelected()) {
            VariableStorage.StopBitsVar = 1;
        }
        if (onePointFiveBits.isSelected()) {
            VariableStorage.StopBitsVar = 2;
        }
        if (twoBits.isSelected()) {
            VariableStorage.StopBitsVar = 3;
        }
    }

    public void baudRateSwitch(ActionEvent actionEvent) {
        VariableStorage.BaudRateVar = Integer.parseInt(baudRate.getText());
        actionEvent.consume();
    }

    public void dataBitSwitch(ActionEvent actionEvent) {
        VariableStorage.DataBitsVar = Integer.parseInt(dataBits.getText());
        actionEvent.consume();
    }

    public void paritySwitch0(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 0;
        actionEvent.consume();
    }

    public void paritySwitch1(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 1;
        actionEvent.consume();
    }

    public void paritySwitch2(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 2;
        actionEvent.consume();
    }

    public void paritySwitch3(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 3;
        actionEvent.consume();
    }

    public void paritySwitch4(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 4;
        actionEvent.consume();
    }

    public void stopBitSwitch1(ActionEvent actionEvent) {
        VariableStorage.StopBitsVar = 1;
        actionEvent.consume();
    }

    public void stopBitSwitch2(ActionEvent actionEvent) {
        VariableStorage.StopBitsVar = 2;
        actionEvent.consume();
    }

    public void stopBitSwitch3(ActionEvent actionEvent) {
        VariableStorage.StopBitsVar = 3;
        actionEvent.consume();
    }

    /**
     * @param mouseEvent Uses the button press, this is unused but is required for a button to function properly
     * @author APills 1.0
     * Sets Data Bits or Baud Rate Boolean to Baud Rate
     */
    public void baudClicked(MouseEvent mouseEvent) {
        DataOrBaud = false;
        mouseEvent.consume();
    }

    /**
     * @param mouseEvent Uses the button press, this is unused but is required for a button to function properly
     * @author APills 1.0
     * Sets Data Bits or Baud Rate Boolean to Data Bits
     */
    public void dataBitsClicked(MouseEvent mouseEvent) {
        DataOrBaud = true;
        mouseEvent.consume();
    }

    /**
     * @param mouseEvent Uses the button press, this is unused but is required for a button to function properly
     * @author APills 1.0
     * Sets Data Bits or Baud Rate Boolean to Password Field
     */
    public void passwordSwitch(MouseEvent mouseEvent) {
        DataOrBaud = null;
        mouseEvent.consume();
    }

    private void enableWithPassword() {
        Platform.runLater(enable);
    }

    public void testpass(ActionEvent actionEvent) {
        String getPass = passwordText.getText();
        //System.out.println(getPass);
        if (getPass.equals(VariableStorage.Password)) {
            VariableStorage.PassFails = 0;
            VariableStorage.Timeout = 0;
            passwordText.setText("");
            enableWithPassword();
        } else if (VariableStorage.PassFails % 5 == 0 && VariableStorage.PassFails > 0) {
            VariableStorage.Timeout = 6 * VariableStorage.PassFails;
            //System.out.println(VariableStorage.Timeout);
            VariableStorage.PassFails++;
            passwordText.setText("");
            if (VariableStorage.Timeout > 0) {
                Thread TimeoutThread = new Thread(timeout);
                executor.execute(TimeoutThread);
            }
        } else {
            VariableStorage.PassFails++;
            passwordText.setText("");
        }
        actionEvent.consume();
    }

    public void changepass(ActionEvent actionEvent) throws IOException {
        boolean testSuccess = VariableStorage.storePass(passwordText.getText());
        if (testSuccess) {
            VariableStorage.Password = passwordText.getText();
            Platform.runLater(PassChangeSuccess);
        } else {
            Platform.runLater(PassChangeFail);
        }
        passwordText.setText("");
        actionEvent.consume();
    }
}

