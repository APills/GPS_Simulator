package GPS_Simulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Simulations {
    @FXML Button Simulation1;
    @FXML Button Simulation2;
    @FXML Button Simulation3;
    @FXML Button Simulation4;
    @FXML Button Simulation5;
    @FXML Button Simulation6;

    final static String pathToSimulations = "/home/pi/Desktop/Simulations";
    static int directoryListLength = requireNonNull(new File(pathToSimulations).listFiles()).length;

    public void initialize() {
        VariableStorage.getLastScreen = "Simulations";
        directoryListLength = requireNonNull(new File(pathToSimulations).listFiles()).length;
        buttonUpdate();
    }
    /**
     * @author APills        buttonUpdate() disables and enables buttons based on the number of files in the simulation directory, if 6 or
     *         more files are present all 6 will be enabled if less than 6 are present an amount of buttons equal to the number
     *         of files that exist will be enabled.
     */
    public void buttonUpdate() {
        int dirLen = directoryListLength;
        if (dirLen <= 6) {
            if (dirLen < 6) {
                if (dirLen < 5) {
                    if (dirLen < 4) {
                        if (dirLen < 3) {
                            if (dirLen < 2) {
                                if (dirLen < 1) {
                                    GPS_Simulator.WarningBox.warn("No Simulations", "There are no simulations");
                                    Simulation1.setDisable(true);
                                }
//                                Simulation2.setDisable(true);
                            }
                            Simulation3.setDisable(true);
                        }
                        Simulation4.setDisable(true);
                    }
                    Simulation5.setDisable(true);
                }
                Simulation6.setDisable(true);
            }
        }
    }

    /**
     * @author APills
     * Takes the user back to Primary once a simulation is selected, selection is set to the SimNum and
     * selectionPlusOneForConvenience is set to 1 more than the SimNum because people don't usually list things
     * starting at 0 and it allows the program to set the simulation number in the Primary window easier than casting
     * the variable back and forth to add 1 to it.
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException FXMLLoader throws an IOException when loading a Parent
     * @param SimNum Gets the simulation number from the selection
     *
     */
    public void SimulationSelected(ActionEvent actionEvent, int SimNum) throws IOException {
        VariableStorage.selection = SimNum;
        Parent runnerViewParent = FXMLLoader.load(getClass().getResource("Runner.fxml"));
        windowInit(actionEvent, runnerViewParent);

    }

    private void windowInit(ActionEvent actionEvent, Parent scene) {
        Scene view = new Scene(scene);
        VariableStorage.windowInit(actionEvent, view);
    }

    private void setSelected(int simNum){
        VariableStorage.selected = true;
        VariableStorage.simNum = simNum;
    }

    /**
     * @author APills
     * Runs SimulationSelected() with the simulation's SimNum variable
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException Throws SimulationSelected's IOException
     */
    public void Simulation1(ActionEvent actionEvent) throws IOException {
        //
        int SimNum = 1;
        setSelected(SimNum);
        SimulationSelected(actionEvent, SimNum);
    }

    /**
     * @author APills
     * Runs SimulationSelected() with the simulation's SimNum variable
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException Throws SimulationSelected's IOException
     */
    public void Simulation2(ActionEvent actionEvent) throws IOException {
        //
        int SimNum = 2;
        setSelected(SimNum);
        SimulationSelected(actionEvent, SimNum);
    }

    /**
     * @author APills
     * Runs SimulationSelected() with the simulation's SimNum variable
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException Throws SimulationSelected's IOException
     */
    public void Simulation3(ActionEvent actionEvent) throws IOException {
        //
        int SimNum = 3;
        setSelected(SimNum);
        SimulationSelected(actionEvent, SimNum);
    }

    /**
     * @author APills
     * Runs SimulationSelected() with the simulation's SimNum variable
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException Throws SimulationSelected's IOException
     */
    public void Simulation4(ActionEvent actionEvent) throws IOException {
        //
        int SimNum = 4;
        setSelected(SimNum);
        SimulationSelected(actionEvent, SimNum);
    }

    /**
     * @author APills
     * Runs SimulationSelected() with the simulation's SimNum variable
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException Throws SimulationSelected's IOException
     */
    public void Simulation5(ActionEvent actionEvent) throws IOException {
        //
        int SimNum = 5;
        setSelected(SimNum);
        SimulationSelected(actionEvent, SimNum);
    }

    /**
     * @author APills
     * Runs SimulationSelected() with the simulation's SimNum variable
     *
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @throws IOException Throws SimulationSelected's IOException
     */
    public void Simulation6(ActionEvent actionEvent) throws IOException {
        //
        int SimNum = 6;
        setSelected(SimNum);
        SimulationSelected(actionEvent, SimNum);
    }

    public void Settings(ActionEvent actionEvent) throws IOException {
        Parent settingsViewParent = FXMLLoader.load(getClass().getResource("Settings.fxml"));
        windowInit(actionEvent, settingsViewParent);
    }

    public void ExternalSimulations(ActionEvent actionEvent) {
        System.exit(0);
    }
}
