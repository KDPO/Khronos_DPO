package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Stijak on 12.01.2016.
 */
public class ActivationWindowController {

    @FXML
    private TextField txtActivationCodeTwo;

    @FXML
    private Button btnCancel;

    @FXML
    private TextField txtUsername;

    @FXML
    private Label activationErrorText;

    @FXML
    private TextField txtActivationCodeThree;

    @FXML
    private Label usernameErrorText;

    @FXML
    private TextField txtActivationCodeOne;

    @FXML
    private Button btnActivate;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;

    @FXML
    private TextField txtActivationCodeFour;

    private Stage stage;

    @FXML
    void initialize() {
        btnCancel.setOnMouseClicked(event -> Platform.exit());

        //usernameErrorText.setVisible(false);
        //activationErrorText.setVisible(false);

        // TODO

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
