
package GPS_Simulator;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WarningBox {

    /**
     * @param title Is the title for the Warning dialogue
     * @param message Is the message the user receives to make sure they understand what the Warning is about
     *
     * Similarly to WarningBox, ConfirmBox does not use FXML because of its simplicity and that is it easier to
     * implement a Confirmation/Warning by popping it up on the same screen because you cannot share variables between
     * multiple FXML scenes without globali(s/z)ing them.
     */
    public static void warn(String title, String message) {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(400);
        window.setMinHeight(400);

        Label label = new Label();
        label.setText(message);

        Button backButton = new Button("Return");
        backButton.minHeight(100);
        backButton.minWidth(200);

        backButton.setOnAction(e -> window.close());
        HBox hlayout = new HBox(10);
        VBox vlayout = new VBox(10);
        hlayout.getChildren().addAll(backButton);
        vlayout.getChildren().addAll(label, hlayout);
        vlayout.setAlignment(Pos.CENTER);
        hlayout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vlayout);
        window.setScene(scene);
        window.showAndWait();

    }

}
