/*
 *  <GPS NMEA 0183 Simulator for Raspberry Pi>
 *  Copyright (C) <2020>  <Andrew Pillsbury>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package GPS_Simulator;

import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class Splash {
    @FXML public Label label;
    @FXML public javafx.scene.layout.AnchorPane AnchorPane;



    /**
     * @author APills 1.0
     * The initialize method opens the primary window and shows it after 2 seconds, because of the .5 second difference
     *  between when the AeroTrainSplash window disappears and when the primary window appears the transition is less
     *  jumpy compared to when they change at the same time.
     *
     * @throws IOException FXMLLoader throws an IOException when loading a Parent
     */
    public void initialize(){
        Parent primaryViewParent = null;
        try {
            primaryViewParent = FXMLLoader.load(getClass().getResource("Simulations.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert primaryViewParent != null;
        Scene primaryView = new Scene(primaryViewParent);
        Stage window = new Stage();

        if(primaryView == null) // Just in case something happens and the FXMLLoader can't load the Primary correctly
            System.out.println("VIEW NULL");
        window.centerOnScreen();
        VariableStorage.maximize(window);
        Objects.requireNonNull(window).setScene(primaryView);
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        window.setTitle("GPS Simulator");
        window.initStyle(StageStyle.UNDECORATED);
        delay.setOnFinished(event -> window.show());
        delay.play();
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, Event::consume);

    }

}
