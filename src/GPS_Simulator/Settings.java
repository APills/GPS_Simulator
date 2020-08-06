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
    static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    @FXML Button four;
    @FXML Button seven;
    @FXML Button eight;
    @FXML Button nine;
    @FXML Button five;
    @FXML Button six;
    @FXML Button one;
    @FXML Button two;
    @FXML Button three;
    @FXML Button returnTo;
    @FXML Button zero;
    @FXML Button backspace;
    @FXML Label passHint;
    @FXML VBox dataBitsBox;
    @FXML VBox parityBox;
    @FXML VBox bitSelectionBox;
    @FXML VBox baudRateBox;
    @FXML ToggleGroup Parity;
    @FXML ToggleGroup Bits;
    @FXML Button PasswordButton;
    @FXML Button passwordChange;
    @FXML RadioButton noParity;
    @FXML RadioButton oddParity;
    @FXML RadioButton evenParity;
    @FXML RadioButton markParity;
    @FXML RadioButton spaceParity;
    @FXML RadioButton oneBit;
    @FXML RadioButton onePointFiveBits;
    @FXML RadioButton twoBits;
    @FXML TextField dataBits;
    @FXML TextField baudRate;
    @FXML PasswordField passwordText;
    @FXML Label warnings;
    /**
     * Inverts the disable state of the Serial settings options
     */
    final Runnable enable = () -> {
        baudRateBox.setDisable(!baudRateBox.isDisabled());
        bitSelectionBox.setDisable(!bitSelectionBox.isDisabled());
        dataBitsBox.setDisable(!parityBox.isDisabled());
        parityBox.setDisable(!parityBox.isDisabled());
        passwordChange.setDisable(!passwordChange.isDisabled());
    };
    /**
     * Shows a message to tell the user that password entrance is blocked
     */
    final Runnable TimeoutMessage = () -> {
        if (countdown > 0)
            passHint.setText("You have incorrectly entered your password too many times.");
        else
            passHint.setText("Enter Password to Lock/Unlock Settings");
    };
    /**
     * Handles the password timeout counter that the user sees
     */
    final Runnable countdownTimer = () -> {
        Platform.runLater(TimeoutMessage);
        if (countdown > 60) {
            warnings.setText((int) (Math.ceil(countdown / 60)) + " minutes Remaining");
        } else
            warnings.setText(((int) (countdown)) + " seconds Remaining");

        if (countdown == 0) {
            warnings.setText("");
        }
    };
    /**
     * Handles the actual countdown for the timeout
     */
    final Runnable timeout = () -> {
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
    /**
     * Changes the text of the password change button to its original text
     */
    final Runnable PassChangeRevert = () -> passwordChange.setText("Enter New Password");
    /**
     * Countdown to allow showing the pass change success text
     */
    final Runnable PassChangeRevertWait = () -> {
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
    /**
     * Changes the password change button text to Password Saved" then starts a timer to revert it
     */
    final Runnable PassChangeSuccess = () -> {
        passwordChange.setText("Password Saved");
        executor.execute(PassChangeRevertWait);
    };
    /**
     * Changes the password change button text to show that the password was not saved
     */
    final Runnable PassChangeFail = () -> passwordChange.setText("Password Not Saved");
    private Boolean DataOrBaud = null;

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void one(ActionEvent event) {
        inputTo("1");
        serialSetting();
        event.consume();
    }
    
    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void two(ActionEvent event) {
        inputTo("2");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void three(ActionEvent event) {
        inputTo("3");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void four(ActionEvent event) {
        inputTo("4");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void five(ActionEvent event) {
        inputTo("5");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void six(ActionEvent event) {
        inputTo("6");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void seven(ActionEvent event) {
        inputTo("7");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void eight(ActionEvent event) {
        inputTo("8");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void nine(ActionEvent event) {
        inputTo("9");
        serialSetting();
        event.consume();
    }

    /**
     * @param event The action of pressing the button, it is unused and consumed
     */
    @FXML public void zero(ActionEvent event) {
        inputTo("0");
        serialSetting();
        event.consume();
    }

    /**
     * @param character The character controlled by the button pressed
     * Based on the Boolean the character can be sent to 3 different text fields
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
     * @param actionEvent The action of pressing the button, it is unused and consumed
     */
    @FXML void backspace(ActionEvent actionEvent) {
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
     * @param str Gets an input string of the currently selected textfield
     * @return str Returns the original string with the last character removed
     */
    String backspaceLogic(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        serialSetting();
        return str;
    }

    /**
     * Sets the timeout and password failures to 0 then disables the serial settings
     */
      public void initialize() {
        VariableStorage.Timeout = 0;
        VariableStorage.PassFails = 0;
        Platform.runLater(enable);
    }

    /**
     * @param actionEvent The action of a button press
     * @throws IOException FXMLLoader throws an IOException when loading a Parent from a Resource
     * Returns the user to the Primary Window
     */
    @FXML void returnTo(ActionEvent actionEvent) throws IOException {
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
     * Quick and easy way to set all the globali(z/s)ed variables for serial settings
     */
    void serialSetting() {
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

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for BaudRate
     */
    @FXML void baudRateSwitch(ActionEvent actionEvent) {
        VariableStorage.BaudRateVar = Integer.parseInt(baudRate.getText());
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Data Bits
     */
    @FXML void dataBitSwitch(ActionEvent actionEvent) {
        VariableStorage.DataBitsVar = Integer.parseInt(dataBits.getText());
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Parity
     */
    @FXML void paritySwitch0(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 0;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Parity
     */
    @FXML void paritySwitch1(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 1;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Parity
     */
    @FXML void paritySwitch2(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 2;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Parity
     */
    @FXML void paritySwitch3(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 3;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Parity
     */
    @FXML void paritySwitch4(ActionEvent actionEvent) {
        VariableStorage.ParityModeVar = 4;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Stop Bits
     */
    @FXML void stopBitSwitch1(ActionEvent actionEvent) {
        VariableStorage.StopBitsVar = 1;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Stop Bits
     */
    @FXML void stopBitSwitch2(ActionEvent actionEvent) {
        VariableStorage.StopBitsVar = 2;
        actionEvent.consume();
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Sets the global variable for Stop Bits
     */
    @FXML void stopBitSwitch3(ActionEvent actionEvent) {
        VariableStorage.StopBitsVar = 3;
        actionEvent.consume();
    }

    /**
     * @param mouseEvent The action of pressing the button, it is unused and consumed
     * Sets baud rate as the text field to be used
     */
    @FXML void baudClicked(MouseEvent mouseEvent) {
        DataOrBaud = false;
        mouseEvent.consume();
    }

    /**
     * @param mouseEvent The action of pressing the button, it is unused and consumed
     * Sets Data Bits as the text field to be used
     */
    @FXML void dataBitsClicked(MouseEvent mouseEvent) {
        DataOrBaud = true;
        mouseEvent.consume();
    }

    /**
     * @param mouseEvent The action of pressing the button, it is unused and consumed
     * Sets password as the text field to be used
     */
    @FXML void passwordSwitch(MouseEvent mouseEvent) {
        DataOrBaud = null;
        mouseEvent.consume();
    }

    /**
     * Runs the enable inversion
     */
    private void enableWithPassword() {
        Platform.runLater(enable);
    }

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * Tests the password in the password field and if it is correct enables the serial settings, otherwise counts up the failures and locks out password attempts
     */
    @FXML void testpass(ActionEvent actionEvent) {
        String getPass = passwordText.getText();
        if (getPass.equals(VariableStorage.Password)) {
            VariableStorage.PassFails = 0;
            VariableStorage.Timeout = 0;
            passwordText.setText("");
            enableWithPassword();
        } else if (VariableStorage.PassFails % 5 == 0 && VariableStorage.PassFails > 0) {
            VariableStorage.Timeout = 6 * VariableStorage.PassFails;
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

    /**
     * @param actionEvent The action of pressing the button, it is unused and consumed
     * @throws IOException Throws Exception due to the File Writer
     */
    @FXML void changepass(ActionEvent actionEvent) throws IOException {
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

