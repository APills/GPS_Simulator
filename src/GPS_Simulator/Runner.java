package GPS_Simulator;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Runner {
    /**
     * A cached threadpool is used in favor of a fixed threadpool because I use many smaller short lived runnable tasks,
     * if the program were to only be using the nmea thread then a fixed thread pool would be better suited to the task
     * of a long lived thread, instead I may need 3 at a given time or I only need 1, should the JVM ever decide to do
     * something incorrectly and close a thread the cached pool will allow the program to run again because it doesn't
     * have to try to reallocate the dead thread
     */
    static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * The PATH and FILEPATH
     */
    final static String PATH = "/home/pi/Desktop/Simulations/";
    final static String FILEPATH = "/home/pi/Desktop/Debug/ConfigurationFiles/SimulationConfiguration";

    final static SerialPort serialPort = SerialPort.getCommPort("/dev/ttyUSB0");
    static OutputStream out = null;

    final ObservableList<String> gpsList = FXCollections.observableArrayList();
    static String fullPath;
    String latitude;
    String longitude;
    String altitude;
    String velocity;
    String time;
    static String parityType;

    static final List<String> gpsReplacementList = new ArrayList<>();
    static final List<String> gpsGarbageCollection = new ArrayList<>();

    static void setGpsGarbageCollection() {
        gpsGarbageCollection.add("");
    }

    static int counter = 0;
    static int gpsListIndex = 0;
    @FXML Button nmeaShowDataButton;
    @FXML Button rs232ShowDataButton;
    @FXML HBox totalHBox;
    @FXML VBox rs232labels;
    @FXML VBox rs232data;
    @FXML Label runningLabel;
    @FXML Label rss;
    @FXML Label db;
    @FXML Label par;
    @FXML Label sb;
    @FXML Label br;
    @FXML Region lreg;

    double timeTaken = 0.0;
    double millisecondsTimeout = 0.0;
    double publicMillisecondsTimeout;

    final AtomicBoolean runWarn = new AtomicBoolean(false);
    boolean halt = false;
    boolean running = false;
    boolean timeRunning = false;
    private boolean firstRun;

    Font rssFont;
    Font font;

    @SuppressWarnings("unused")
    ActionEvent actionEvent;

    @FXML Region hideRegion;
    @FXML Label latitudeLabel;
    @FXML Label longitudeLabel;
    @FXML Button SelectSimulation;
    @FXML Button StartStop;
    @FXML ListView<String> gpsShow;
    @FXML Label dataBitsLabel;
    @FXML Label parityLabel;
    @FXML Label stopBitsLabel;
    @FXML Label baudRateLabel;
    @FXML Label velocityLabel;
    @FXML Label altitudeLabel;
    @FXML Label timeLabel;
    @FXML Label startTimeLabel;
    @FXML Label endTimeLabel;
    @FXML ImageView StartStopImage;
    @FXML ImageView pausePlayBack;
    @FXML Label estTimeLeftLabel;
    @FXML Label lineCountLabel;

    final Runnable header = () -> {
        if (halt) {
            runningLabel.setText("Simulation Paused");
        } else if (running) {
            runningLabel.setText("Simulation Running");
        } else {
            runningLabel.setText("Simulation Stopped");
        }
    };

    /**
     * The data runnable is only called by the nmea thread and only can be called once per second, this avoids slowing
     * down or crashing the Raspberry Pi by running too many things at once, the processor is already divided by a
     * number of threads so having the data update every time a GPS sentence is parsed often crashes. With the data
     * I tested the sentences contained a velocity of over 900 knots which would send data faster than every 250
     * ms and would cause the UI elements to become unresponsive, in the nmea thread comments I elaborate on the timings further.
     * <p>
     * In general UI elements must be updated on the FX thread otherwise they will not take effect, by using
     * Platform.runLater and moving the changes to their own thread allows them to update the UI at a time when the JVM
     * finds it convenient to run the Runnable on the FX thread.
     */
    final Runnable data = () -> {
        if (!timeRunning) {
            startTimeLabel.setText(time);
            timeRunning = true;
        }
        latitudeLabel.setText(latitude);
        longitudeLabel.setText(longitude);
        altitudeLabel.setText(altitude);
        velocityLabel.setText(velocity);
        timeLabel.setText(time);
        endTimeLabel.setText(time);
        double sTimeoutMinutes = (publicMillisecondsTimeout / 100);

        estTimeLeftLabel.setText((int) Math.floor((gpsList.size() - counter) / (sTimeoutMinutes * 60)) + " minutes");
        if (Math.floor((gpsList.size() - counter) / (sTimeoutMinutes * 60)) < 1)
            estTimeLeftLabel.setText((int) Math.floor((gpsList.size() - counter) / (sTimeoutMinutes)) + " seconds");
        lineCountLabel.setText(counter + "/" + gpsList.size() + " Data Sentences");
    };

    /**
     * When the Runnable dataReset is run it will clear out the labels. This should only occur after the nmea thread
     * finishes or the stop button is pressed.
     */
    final Runnable dataReset = () -> {
        latitudeLabel.setText("");
        longitudeLabel.setText("");
        altitudeLabel.setText("");
        velocityLabel.setText("");
        timeLabel.setText("");
        estTimeLeftLabel.setText("");
        gpsList.clear();
        gpsList.addAll(gpsReplacementList);
    };

    /**
     * The StartStopImageSwitch Runnable is called to Platform.runLater to update the FX thread to change the image of
     * the Start/Stop button to either be Start or Stop, after the button changes to either start or stop once it will
     * never need to go back to the blue Start/Stop it initially is because it will always be able to either start or
     * stop GPS data flow.
     */
    final Runnable StartStopImageSwitch = () -> {

        Image startBtn = new Image(String.valueOf(this.getClass().getResource("/assets/Buttons/runner/runner_start.png")));
        Image stopBtn = new Image(String.valueOf(this.getClass().getResource("/assets/Buttons/runner/runner_stop.png")));
        if (StartStop.getText().equals("Stop")) {
            StartStopImage.setImage(stopBtn);
        }
        if (StartStop.getText().equals("Start")) {
            StartStopImage.setImage(startBtn);
        }
    };

    /**
     * The StartStopTextSwitch Runnable is called to Platform.runLater when it is necessary to update the state of the
     * Start/Stop button without triggering any other runnables or methods
     */
    final Runnable StartStopTextSwitch = () -> {
        if (StartStop.getText().equals("Stop")) {
            StartStop.setText("Start");
        } else {
            StartStop.setText("Stop");
        }
    };
    final Runnable PausePlayBackImageSwitch = () -> {
        Image pausePlayBtn = new Image(String.valueOf(this.getClass().getResource("/assets/Buttons/runner/runner_pauseplay.png")));
        Image backBtn = new Image(String.valueOf(this.getClass().getResource("/assets/Buttons/runner/runner_exit.png")));
        if (running) {
            pausePlayBack.setImage(pausePlayBtn);
        } else {
            pausePlayBack.setImage(backBtn);
        }
    };

    /**
     * gpsListing opens the GPS data file and reads its contents then puts it into a ListView, this does not have true
     * protection against extremely large files which could cause Out of Memory exceptions or other problems so it is
     * limited to the first 10000 sentences which is about 3x longer than my longest test
     * <p>
     * the list and add more data ex. if counter greater than 8000 clear 0-8000, 8001-10000 = 0-1998, counter = 0 reader.next
     * until counter + 1998 = 10000
     */
    final Runnable gpsListing = () -> {
        counter = 0;
        File file = new File(fullPath);
        Scanner reader = null;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            assert reader != null;
            if (!(reader.hasNext() && counter < 10000)) break;
            String nexLn = reader.nextLine();
            gpsList.add(gpsListIndex, nexLn);
            gpsListIndex++;
            counter++;
        }
        gpsShow.setItems(gpsList);
        counter = 0;
        gpsListIndex = 0;
    };

    final Runnable hideRS232 = () -> {
        rs232data.setVisible(!rs232data.isVisible());
        rs232labels.setVisible(!rs232labels.isVisible());
        if (rs232labels.isVisible()) {
            rs232labels.setMaxHeight(130);
            lreg.setMaxHeight(20);
            br.setFont(font);
            sb.setFont(font);
            par.setFont(font);
            db.setFont(font);
            rss.setFont(rssFont);
            rs232ShowDataButton.setText("Hide RS232 Data");
        } else {
            rs232labels.setMaxHeight(0);
            lreg.setMaxHeight(0);
            br.setFont(Font.font(0));
            sb.setFont(Font.font(0));
            par.setFont(Font.font(0));
            db.setFont(Font.font(0));
            rss.setFont(Font.font(0));
            rs232ShowDataButton.setText("Show RS232 Data");
        }
        if (rs232data.isVisible()) {
            rs232data.setMaxHeight(145);
            hideRegion.setMinHeight(15);
            baudRateLabel.setFont(font);
            stopBitsLabel.setFont(font);
            parityLabel.setFont(font);
            dataBitsLabel.setFont(font);
        } else {
            rs232data.setMaxHeight(0);
            hideRegion.setMinHeight(28);
            baudRateLabel.setFont(Font.font(0));
            stopBitsLabel.setFont(Font.font(0));
            parityLabel.setFont(Font.font(0));
            dataBitsLabel.setFont(Font.font(0));
        }

    };

    /**
     * The Runnable nmea handles NMEA 0183 data parsing and UI updates for Lat, Long, Alt, Vel, and Time labels including
     * current simulation time and estimated time to completion.
     * <p>
     * <h4>TIMING DATA UPDATES</h4>
     * <p>
     * <p>
     * <p>
     * setData() is polled once per second. The method looks for a time of >750 ms because the minimum that data should
     * be allowed to be sent is at 250 ms, at the end of 1 iteration the minimum time that can be added is 750 ms, if
     * the time is 751 it will subtract the 1000 ms making it -249 ms causing a debt, at the end of the iteration it
     * will add a minimum of 250 putting back to 1 making it a surplus once more and clearing that debt if it was set to
     * >1000 the issue would occur of it updating much later than once per second and causing 2 second jumps to occur on
     * the time label more often.
     * <p>
     * At the current rate real time increases by an extra .003652422 seconds per second meaning about every 5 minutes
     * the time will visibly seem to jump 2 seconds rather than 1 for example from 100.99999 increasing by 1.00365 to
     * 102.00364 seconds.
     *
     *
     * <p>
     * <h4>WRITING SERIAL DATA</h4>
     * <p>
     * <p>
     * <p>
     * When the simulation is selected the data is loaded into a ListView. In the runnable, nmea, the data is sent to
     * the class NMEA to be parsed into readable NMEA 0183 data and distributed to the UI elements and logic that requires it.
     * To get the simulator to send data at a variable speed it uses getApproxTime() which is explained at the method,
     * and returns a time in milliseconds that can be used as a delay, the faster the velocity data the faster the
     * sentences will be sent.
     *
     * <p>
     * <h4>ENDING THE THREAD</h4>
     * <p>
     * <p>
     * <p>
     * The nmea thread can be stopped in 2 ways, pressing the stop button, or when the file runs out of data. When the
     * thread is stopped by the stop button it sets the timeout to lower than .01 seconds, originally when it was first
     * implemented it would near instantly run to the end of the file as it would skip the and operator of the while
     * loop, now it has been corrected, but in the event it doesn't stop correctly it would then run to the end of the
     * file as it did before debugging.
     */
    final Runnable nmea = () -> {
        if (firstRun) {
            gpsReplacementList.clear();
            gpsReplacementList.addAll(gpsList);
            firstRun = false;
        }
        gpsListIndex = 0;
        runWarn.set(false);
        while (gpsList.size() - 1 >= gpsListIndex && (millisecondsTimeout > .01)) {
            if (!halt) {

                // Infinite Recursion Block, Used for long testing to find errors caused by long running files,
                // Runs a data reset to reset the GPS List and resets the index
                /*
                if(gpsListIndex > (gpsList.size() - 80))
                {
                    Platform.runLater(dataReset);
                    gpsListIndex = 0;
                }
                 */

                Platform.runLater(header);
                //noinspection InstantiationOfUtilityClass
                new NMEA();
                if (timeTaken > 750) {
                    timeTaken -= 1000;
                    setData(NMEA.position.latitude, NMEA.position.longitude, NMEA.position.altitude, NMEA.position.velocity);
                    Platform.runLater(data);
                }
                gpsShow.scrollTo(gpsListIndex);
                gpsShow.getSelectionModel().select(gpsListIndex);
                String nexLn = gpsList.get(gpsListIndex);
                gpsListIndex++;
                gpsList.set((gpsListIndex - 1), gpsGarbageCollection.get(0));
                NMEA.parse(nexLn);
                try {
                    out.write(nexLn.getBytes());
                    out.write("\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double speed = NMEA.position.velocity;
                millisecondsTimeout = getApproxTime(speed);
                if (millisecondsTimeout > 250)
                    millisecondsTimeout = 250;
                try {
                    Thread.sleep((long) (millisecondsTimeout));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publicMillisecondsTimeout = millisecondsTimeout;
                timeTaken += millisecondsTimeout;
                counter++;
            } else {
                Platform.runLater(header);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        setData(NMEA.position.latitude, NMEA.position.longitude, NMEA.position.altitude, NMEA.position.velocity);
        Platform.runLater(dataReset);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Platform.runLater(data);
            running = false;
            halt = false;
            gpsListIndex = 0;
            gpsShow.scrollTo(gpsListIndex);
            gpsShow.getSelectionModel().select(gpsListIndex);
            Platform.runLater(header);
            Platform.runLater(StartStopTextSwitch);
            Platform.runLater(StartStopImageSwitch);
            timeRunning = false;
            Platform.runLater(PausePlayBackImageSwitch);
            Platform.runLater(dataReset);
            runWarn.set(true);
        }
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    /**
     * The runner thread originally did a countdown to the start of the simulation, should that functionality need to be
     * reinstated the runner thread stays. While runner is redundant it stays alive longer than the nmea thread and if
     * the program is left for long enough the nmea thread might die, but the runner thread could still make a new one.
     */
    private final Runnable runner = () -> {
        Thread nmeaThread = new Thread(nmea);
        executor.execute(nmeaThread);
        try {
            executor.awaitTermination(180, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    };

    final Runnable showHideGpsList = () -> {
        if (gpsShow.isVisible()) {
            gpsShow.setPrefWidth(0);
            gpsShow.setVisible(false);
            nmeaShowDataButton.setText("Show NMEA Data");
        } else {
            gpsShow.setPrefWidth(741);
            gpsShow.setVisible(true);
            nmeaShowDataButton.setText("Hide NMEA Data");
        }
    };

    /**
     * @param baud   Takes the baudrate from Secondary to be set to the port
     * @param data   Takes the data bits from Secondary to be set to the port
     * @param parity Takes the parity from Secondary to be set to the port
     * @param stop   Takes the stop bits from Secondary to be set to the port
     * @author APills 1.0
     * The setSerialSettings method is called at initialization and sets the port information from the Settings window
     * (known as the Path_Selection window in the package due to feature changes but the name not being updated)
     * the initialization of the serialPort is in initialize() and is explained there.
     */
    static void setSerialSettings(int baud, int parity, int stop, int data) {
        serialPort.setBaudRate(baud);
        serialPort.setParity(parity);
        serialPort.setNumStopBits(stop);
        serialPort.setNumDataBits(data);
        serialPort.setComPortTimeouts(1, 1, 1);
        if (VariableStorage.ParityModeVar == 0) {
            parityType = " (No Parity)";
        }
        if (VariableStorage.ParityModeVar == 1) {
            parityType = " (Odd Parity)";
        }
        if (VariableStorage.ParityModeVar == 2) {
            parityType = " (Even Parity)";
        }
        if (VariableStorage.ParityModeVar == 3) {
            parityType = " (Mark Parity)";
        }
        if (VariableStorage.ParityModeVar == 4) {
            parityType = " (Space Parity)";
        }

    }

    /**
     * @return simSelection returns the simulation number
     * @author APills 1.0
     * The getSimulation() method returns the file name of the selected simulation.
     */
    static String getSimulation() {
        if (Objects.equals(VariableStorage.getLastScreen, "Standard Simulation")) {
            StringBuilder fileContent = new StringBuilder();
            try (Stream<String> stream = Files.lines(Paths.get(FILEPATH), StandardCharsets.UTF_8)) {
                stream.forEach(s -> fileContent.append(s).append("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String configFileString = fileContent.toString();
            String[] simSelection = configFileString.split("%");
            return PATH + simSelection[VariableStorage.selection - 1];
        } else if (Objects.equals(VariableStorage.getLastScreen, "Custom Simulation")) {
            return String.valueOf(VariableStorage.customSim);
        } else
            return null;
    }

    /**
     * @param altitude  Takes parsed altitude data from the nmea runnable
     * @param latitude  Takes parsed latitude data from the nmea runnable
     * @param longitude Takes parsed longitude data from the nmea runnable
     * @param velocity  Takes parsed velocity data from the nmea runnable
     * @author APills 1.0
     * The setData() method sets a variable for all of the NMEA data so that it can be passed from the nmea
     * runnable to the data runnable.
     */
    void setData(Float latitude, Float longitude, Float altitude, Float velocity) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
        this.altitude = String.valueOf(altitude);
        this.velocity = String.valueOf(velocity);
        this.time = String.valueOf(LocalTime.now(ZoneId.of("GMT+0")));
    }

    /**
     * @param input takes the velocity that is in the GPS data
     * @return output sends back the millisecond timeout to wait before the next sentence is sent
     * @author APills 1.0
     * The getApproxTime() method is how the simulator makes a variable delay in sending the sentences, if it sent at a
     * consistent speed 1 mile at 1 knot and 1 mile at 1000 knots would take the same time.
     * <p>
     * It sets the output to a very slow setting for the event the input is less than 300 knots, below 300 knots the sentences
     * send at over 1 second per sentence and the gps fix would be lost due to the navigation system not getting
     * constant data to solve this, anything lower than 1 second is set to 1 second.
     * <p>
     * On the high speed end, anything over 1200 knots is set to the fastest speed of 250 ms by default.
     * <p>
     * If the GPS data does not have a velocity it will send data at 500.
     * <p>
     * The math behind the speed to time conversion is the reciprocal of the speed to allow a larger number to equal a
     * lower ms delay and a smaller number to equal a larger delay. This is then multiplied by 3 because it was about
     * 300000 to move the decimal 5 places to the right, 2 for the initial
     * reciprocal conversion then 3 to get milliseconds in a usable format, to not have to multiply by 1000 it would be
     * possible to sleep for a second delay rather than a millisecond delay, but rather than complicating the rest of
     * the program I chose to complicate 1 line of code.
     */
    double getApproxTime(double input) {
        double output = 500;
        if (input < 1200 && input > 300) {
            output = ((1 / input) * 300000);
        } else if (input > 1200) {
            output = 250;
        } else if (input > 0) {
            output = 1000;
        }
        return output;
    }

    /**
     * @author APills 1.0
     * The initialize() method will always run when the window loads. Its first operation is to check if there is a
     * simulation selected, then if there is set the full path, update the start/stop button and put the data into the
     * list view.
     * <p>
     * The initialize() method will set the serial settings first if there is no selected simulation, this uses the
     * default values of 4800/8-N-1 and because initialize is on the FX thread it updates the serial settings labels to
     * show the port settings.
     * initialize() also sets the labels for the simulation.
     */
    public void initialize() {
        setGpsGarbageCollection();
        firstRun = true;
        runWarn.set(true);
        Platform.runLater(hideRS232);
        rssFont = rss.getFont();
        font = db.getFont();
        Platform.runLater(PausePlayBackImageSwitch);
        if (VariableStorage.selected) {
            StartStop.setText("Start");
            fullPath = getSimulation();
            Platform.runLater(StartStopImageSwitch);
            Platform.runLater(gpsListing);
        }
        setSerialSettings(VariableStorage.BaudRateVar, VariableStorage.ParityModeVar, VariableStorage.StopBitsVar, VariableStorage.DataBitsVar);
        serialPort.openPort();
        assert serialPort.isOpen();
        out = serialPort.getOutputStream();
        baudRateLabel.setText(String.valueOf(VariableStorage.BaudRateVar));
        parityLabel.setText(VariableStorage.ParityModeVar + parityType);
        stopBitsLabel.setText(String.valueOf(VariableStorage.StopBitsVar));
        dataBitsLabel.setText(String.valueOf(VariableStorage.DataBitsVar));
    }

    /**
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @author APills 1.0
     * The SelectSimulation method opens the window to select a simulation.
     */
    @FXML void SelectSimulation(ActionEvent actionEvent) {
        if (running) {
            halt = !halt;
            Platform.runLater(header);
        } else {
            Parent simulationsViewParent = null;
            try {
                simulationsViewParent = FXMLLoader.load(getClass().getResource("Simulations.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert simulationsViewParent != null;
            Scene simulationsView = new Scene(simulationsViewParent);
            VariableStorage.windowInit(actionEvent, simulationsView);
        }
    }

    /**
     * @param actionEvent Uses the button press, this is unused but is required for a button to function properly
     * @author APills 1.0
     * StartStop is a button which when the simulation is selected and stopped/not running displays "Start" however
     * when a simulation is selected and running is displays "Stop."
     * <p>
     * When the button is pressed it displays a confirmation box and if it is cancelled nothing happens.
     * If the simulation is started it runs the runner thread, changes the button to stop.
     */
    @FXML void StartStop(ActionEvent actionEvent) {
        this.actionEvent = actionEvent;
        if (StartStop.getText().equals("Start") && !running) {
            gpsListIndex = 0;
            counter = 0;
            if (runWarn.get()) {
                halt = false;
                millisecondsTimeout = 0.2;
                running = true;
                Platform.runLater(header);
                Platform.runLater(StartStopTextSwitch);
                Platform.runLater(StartStopImageSwitch);
                Platform.runLater(PausePlayBackImageSwitch);
                Thread runnerThread = new Thread(runner);
                executor.execute(runnerThread);
            }
        } else {
            millisecondsTimeout = 0.0;
            timeRunning = false;
            halt = false;
            running = false;
            Platform.runLater(header);
            Platform.runLater(PausePlayBackImageSwitch);
        }
    }

    @FXML void nmeaShowData(ActionEvent actionEvent) {
        Platform.runLater(showHideGpsList);
        actionEvent.consume();
    }

    @FXML void rs232ShowData(ActionEvent actionEvent) {
        Platform.runLater(hideRS232);
        actionEvent.consume();
    }
}