package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

        btnActivate.setOnMouseClicked((MouseEvent) -> {
            boolean success = true;
            usernameErrorText.setVisible(false);
            activationErrorText.setVisible(false);
            CharSequence charSequence = txtUsername.getCharacters();
            if(charSequence.length() < 8 || charSequence.length() > 32){
                usernameErrorText.setText("Korisnicko ime treba imati  izmedju 8 i 32 karaktera.");
                usernameErrorText.setVisible(true);
                success = false;
            }else{
                for (char character: charSequence.toString().toCharArray()) {
                    if(!(Character.isDigit(character) || Character.isLetter(character) || Character.isWhitespace(character))){
                        usernameErrorText.setText("Korisnicko ime se moze sastojati samo od alfanumerickih znakova i znaka razmaka");
                        usernameErrorText.setVisible(true);
                        success = false;
                        break;
                    }
                }
            }
            activationErrorText.setText("Lozinka nije ispravna.");
            activationErrorText.setVisible(true);

            /*
            checkUsername();
            checkKey();
            ako prodje i provjera lozinke, i jedinstvenosti korisnickog imena, treba pokrenuti mainController
             */
        });


        usernameErrorText.setText("Unesite korisnicko ime");
        usernameErrorText.setVisible(true);

        activationErrorText.setText("Unesite kod za aktivaciju");
        activationErrorText.setVisible(true);

        txtActivationCodeOne.setOnMouseClicked((MouseEvent) -> {
            txtActivationCodeOne.clear();
        });

        txtActivationCodeTwo.setOnMouseClicked((MouseEvent) -> {
            txtActivationCodeTwo.clear();
        });

        txtActivationCodeThree.setOnMouseClicked((MouseEvent) -> {
            txtActivationCodeThree.clear();
        });

        txtActivationCodeFour.setOnMouseClicked((MouseEvent) -> {
            txtActivationCodeFour.clear();
        });

        registerListener(txtActivationCodeOne, txtActivationCodeTwo);
        registerListener(txtActivationCodeTwo, txtActivationCodeThree);
        registerListener(txtActivationCodeThree, txtActivationCodeFour);
    }

    private void registerListener(TextField tf1, TextField tf2) {
        tf1.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText.length() < 4 && newText.length() >= 4) {
                tf2.requestFocus();
            }
        });
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
