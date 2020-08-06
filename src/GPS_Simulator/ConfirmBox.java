
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
     *
     * @param title Is the title for the Warning dialogue
     * @param message Is the message the user receives to make sure they understand what the Warning is about
     * @param accept Is the text that appears for the "Accept" prompt
     * @param decline Is the text that appears for the "Decline" prompt
     * @return Returns a boolean value of true for acceptance, or false for declination
     *
     * Similarly to WarningBox, ConfirmBox does not use FXML because of its simplicity and that is it easier to
     * implement a Confirmation/Warning by popping it up on the same screen because you cannot share variables between
     * multiple FXML scenes without globali(z/s)ing them.
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
