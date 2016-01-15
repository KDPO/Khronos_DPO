package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

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
    private String key;
    private boolean activated = false;

    private double x = 0;
    private double y = 0;

    @FXML
    void initialize() {

        activationErrorText.setVisible(false);
        usernameErrorText.setVisible(false);

        btnCancel.setOnMouseClicked(event -> {
            activated = false;
            stage.close();
        });

        // za pomijeranje prozora
        anchorPane.setOnMousePressed(event -> {
            this.x = anchorPane.getScene().getWindow().getX() - event.getScreenX();
            this.y = anchorPane.getScene().getWindow().getY() - event.getScreenY();
        });

        anchorPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + this.x);
            stage.setY(event.getScreenY() + this.y);
        });

        btnActivate.setOnMouseClicked(event -> {
            boolean success = true;
            usernameErrorText.setVisible(false);
            activationErrorText.setVisible(false);
            CharSequence charSequence = txtUsername.getCharacters();
            if (charSequence.length() < 8 || charSequence.length() > 32) {
                usernameErrorText.setText("Username should be between 8 and 32 characters long.");
                usernameErrorText.setVisible(true);
                success = false;
            } else {
                for (char character : charSequence.toString().toCharArray()) {
                    if (!(Character.isDigit(character) || Character.isLetter(character) || Character.isWhitespace(character))) {
                        usernameErrorText.setText("Only alphanumeric and whitespace characters are allowed.");
                        usernameErrorText.setVisible(true);
                        success = false;
                        break;
                    }
                }
            }
            key = txtActivationCodeOne.getText() + txtActivationCodeTwo.getText() + txtActivationCodeThree.getText() + txtActivationCodeFour.getText();
            //activationErrorText.setText("Incorrect activation code.");
            activationErrorText.setVisible(true);

            /*
            checkUsername();
            checkKey();
            ako prodje i provjera lozinke, i jedinstvenosti korisnickog imena, treba pokrenuti mainController
             */
            if (success) {
                activated = true;
                new Thread(() -> saveParametars(txtUsername.getText(), key)).start();
                stage.close();
            }

        });

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
        registerListener(txtActivationCodeFour, btnActivate);
    }

    private void registerListener(TextField tf1, Control tf2) {
        tf1.textProperty().addListener((obs, oldText, newText) -> {
            if (oldText.length() < 4 && newText.length() >= 4) {
                tf2.requestFocus();
            }
        });
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // ako je aktivacija uspješna sačuvaj podatke na FS radi provjere aktivacije pri idućem pokretanju
    private void saveParametars(String username, String key) {
        try {
            System.out.println(username);
            System.out.println(key);
            String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "License";
            File outputPath = new File(path);
            if (!outputPath.exists()) {
                outputPath.mkdirs();
            }
            String file = path + File.separator + "license.txt";
            String hash = Main.getHashCode(username, key);
            System.out.println(hash);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
            writer.println("<username>" + username + "</username>");
            writer.println("<key>" + key + "</key>");
            writer.println("<hashkey>" + hash + "</hashkey>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isActivated() {
        return activated;
    }
}
