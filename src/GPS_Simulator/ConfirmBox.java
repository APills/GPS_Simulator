
package GPS_Simulator;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {

    static boolean answer;

    /**
     * @param accept  Allows the specific call to the ConfirmBox to have it's own Accept dialogue
     * @param decline Allows the specific call to the ConfirmBox to have it's own Decline dialogue
     * @param message Allows the specific call to the ConfirmBox to have it's own Message dialogue
     * @param title   Allows the specific call to the ConfirmBox to have it's own Title
     * @return answer returns whether accept or decline was chosen
     * @author APills 1.0
     * Unlike the other windows this is not using FXML. If it was more complicated then this would be better off as an
     * FXML, for the purpose of a confirmation box however that is unnecessary.
     * <p>
     * The confirm method takes a method call with 4 strings title, message, accept, decline
     * <p>
     * title is the name of the box
     * message is the box's message
     * accept is the text displayed on the confirm button
     * decline is the text displayed on the back button
     */
    public static boolean confirm(String title, String message, String accept, String decline) {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.centerOnScreen();
        window.setTitle(title);
        window.setMinWidth(600);
        window.setMinHeight(250);

        Label label = new Label();
        label.setText(message);
        label.setStyle("-fx-font: 24 arial;");


        Button confirmButton = new Button(accept);
        Button backButton = new Button(decline);

        confirmButton.setMinHeight(75);
        confirmButton.setMinWidth(150);
        confirmButton.setStyle("-fx-font: 18 arial;");

        backButton.setMinHeight(75);
        backButton.setMinWidth(150);
        backButton.setStyle("-fx-font: 18 arial;");

        confirmButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        backButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        HBox hlayout = new HBox(10);
        VBox vlayout = new VBox(10);
        hlayout.getChildren().addAll(confirmButton, backButton);
        vlayout.getChildren().addAll(label, hlayout);
        vlayout.setAlignment(Pos.CENTER);
        hlayout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vlayout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

}
