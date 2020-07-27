package GPS_Simulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;
import static java.util.Objects.requireNonNull;

public class Simulations {
    private static final String filePath = "/home/pi/Desktop/Debug/ConfigurationFiles/LocationsTexts";

    @FXML public Text sim1Text;
    @FXML public Text sim2Text;
    @FXML public Text sim3Text;
    @FXML public Text sim4Text;
    @FXML public Text sim5Text;
    @FXML public Text sim6Text;
    @FXML Button hiddenTR;
    @FXML Button hiddenBR;
    @FXML Button hiddenBL;
    @FXML Button Simulation1;
    @FXML Button Simulation2;
    @FXML Button Simulation3;
    @FXML Button Simulation4;
    @FXML Button Simulation5;
    @FXML Button Simulation6;

    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    final static String pathToSimulations = "/home/pi/Desktop/Simulations";
    static int directoryListLength = requireNonNull(new File(pathToSimulations).listFiles()).length;

    public void initialize() {
        getTexts();
        setTexts();
        VariableStorage.getLastScreen = "Simulations";
        directoryListLength = requireNonNull(new File(pathToSimulations).listFiles()).length;
        buttonUpdate();
        VariableStorage.setLocations(sim1Text,sim2Text,sim3Text,sim4Text,sim5Text,sim6Text);
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

    public static String[] simSelection;
    public static void getTexts(){
        StringBuilder textsContent = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> textsContent.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String textsFileString = textsContent.toString();
        simSelection = textsFileString.split("%");
    }
    public static void setTexts(){
        VariableStorage.sim1 = simSelection[0];
        VariableStorage.sim2 = simSelection[1];
        VariableStorage.sim3 = simSelection[2];
        VariableStorage.sim4 = simSelection[3];
        VariableStorage.sim5 = simSelection[4];
        VariableStorage.sim6 = simSelection[5];
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
        if(VariableStorage.HiddenSettingsButton == 0 && VariableStorage.HiddenSettingsButtonLock == 4){
            VariableStorage.HiddenSettingsButton = 13;
            VariableStorage.HiddenSettingsButtonLock = 0;
            Parent settingsViewParent = FXMLLoader.load(getClass().getResource("Settings.fxml"));
            windowInit(actionEvent, settingsViewParent);
        }
        else{
            VariableStorage.HiddenSettingsButton += 4;
            VariableStorage.HiddenSettingsButtonLock++;
        }
    }

    public void ExternalSimulations(ActionEvent actionEvent) {
        System.exit(0);
    }

    Runnable timeLimit = () -> {
        int x = 0;
        while (x < 10){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            System.out.println(VariableStorage.HiddenSettingsButton + " " + x);
        }
        VariableStorage.HiddenSettingsButton = 13;
        VariableStorage.HiddenSettingsButtonLock = 0;

    };

    public void hiddenTR(ActionEvent actionEvent) {
        if(VariableStorage.HiddenSettingsButtonLock == 1) {
            VariableStorage.HiddenSettingsButton /= 2;
            System.out.println(VariableStorage.HiddenSettingsButton);
            VariableStorage.HiddenSettingsButtonLock++;

        }
    }

    public void hiddenBR(ActionEvent actionEvent) {
        if(VariableStorage.HiddenSettingsButtonLock == 3) {
            VariableStorage.HiddenSettingsButton -= 9;
            System.out.println(VariableStorage.HiddenSettingsButton);
            VariableStorage.HiddenSettingsButtonLock++;
        }
    }

    public void hiddenBL(ActionEvent actionEvent) {
        if(VariableStorage.HiddenSettingsButtonLock == 0) {
            executor.execute(timeLimit);
            VariableStorage.HiddenSettingsButton -= 3;
            System.out.println(VariableStorage.HiddenSettingsButton);
            VariableStorage.HiddenSettingsButtonLock++;
        }
    }
}
