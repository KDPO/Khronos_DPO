package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

/**
 * Created by Stijak on 12.01.2016.
 */
public class ActivationWindowController {

    @FXML
    private TextField txtActivationCodeTwo;

    @FXML
    private Button btnClose;

    @FXML
    private TextField txtUsername;

    @FXML
    private Label activationErrorText;

    @FXML
    private TextField txtActivationCodeThree;

    @FXML
    private Label usernameErrorText;

    @FXML
    private Label activationText;

    @FXML
    private TextField txtActivationCodeOne;

    @FXML
    private Button btnActivate;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane anchorPaneLabels;

    @FXML
    private VBox vBox;

    @FXML
    private TextField txtActivationCodeFour;

    @FXML
    private ProgressIndicator progressIndicator;

    private Stage stage;
    private String key;
    private String username;
    private BooleanProperty activated;

    private double x = 0;
    private double y = 0;

    @FXML
    void initialize() {

        activated = new SimpleBooleanProperty(false);
        btnActivate.disableProperty().bind(activated);
        activationErrorText.setVisible(false);
        usernameErrorText.setVisible(false);
        progressIndicator.setVisible(false);

        btnClose.setOnMouseClicked(event -> {
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
            username = charSequence.toString();
            key = txtActivationCodeOne.getText() + txtActivationCodeTwo.getText() + txtActivationCodeThree.getText() + txtActivationCodeFour.getText();

            if (key.length() != 16) {
                // još neke provjere
                success = false;
                activationErrorText.setText("Incorrect activation code.");
                activationErrorText.setVisible(true);
            }

            if (success) {
                //provjera na serveru
                checkActivationStatus();
            }

        });

        txtActivationCodeOne.setOnMouseClicked((MouseEvent) -> txtActivationCodeOne.clear());

        txtActivationCodeTwo.setOnMouseClicked((MouseEvent) -> txtActivationCodeTwo.clear());

        txtActivationCodeThree.setOnMouseClicked((MouseEvent) -> txtActivationCodeThree.clear());

        txtActivationCodeFour.setOnMouseClicked((MouseEvent) -> txtActivationCodeFour.clear());

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
        new Thread(() -> {
            try {
                String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "License";
                File outputPath = new File(path);
                if (!outputPath.exists()) {
                    outputPath.mkdirs();
                }
                String file = path + File.separator + "license.txt";
                String hash = "";
                do {
                    hash = Main.getHashCode(username, key);
                } while (hash == null);
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
                writer.println("<username>" + username + "</username>");
                writer.println("<key>" + key + "</key>");
                writer.println("<hashkey>" + hash + "</hashkey>");
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isActivated() {
        return activated.get();
    }

    private boolean checkKeyValidation() {
        // provjerava lokalno
        return false;
    }

    private void checkActivationStatus() {
        // provjerava na serveru
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // ovdje ide konekcija sa serverom i sve ostalo
                try {
                    Socket socket = new Socket(ClientServicesThread.SERVER_IP, ClientServicesThread.SERVER_PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    out.println("TOACTIVATE#" + username + "#" + key);
                    String response = in.readLine();
                    // očekivani odgovor
                    // ACTIVATION#OK ili ACTIVATION#NOK#poruka
                    if (response.split("#")[1].equals("OK")) {
                        // prekopiran markov kod ...
                        activated.set(true);
                        Platform.runLater(() -> {
                            activationText.setStyle("-fx-text-fill: #2aff05;");
                            activationText.setText("Activated!");
                            btnClose.requestFocus();
                        });

                        // sačuvaj na FS
                        saveParametars(username, key);

                        // nakon što je aktiviran, pokreće se service thread
                        // service thread se još treba pokrenuti negdje u mainu nakon provjere da li je SW aktiviran ili ne
                        ClientServicesThread.socket = socket;
                        ClientServicesThread.in = in;
                        ClientServicesThread.out = out;
                        ClientServicesThread.clientServicesThread = new ClientServicesThread();
                        ClientServicesThread.clientServicesThread.start();

                    } else {
                        // TOD prikazati grešku grafički
                        // zatim omogućiti ponovni unos
                        // nisam još siguran kako funkcioniše ovaj kontroler
                        activated.set(false);
                        String[] s = response.split("#");
                        Platform.runLater(() -> {
                            if ("NOK".equals(s[2])) {
                                // username already taken
                                usernameErrorText.setText("Username already taken.");
                                usernameErrorText.setVisible(true);
                            }

                            if ("NOK".equals(s[3])) {
                                // invalid activation key
                                activationErrorText.setText("Incorrect activation code.");
                                activationErrorText.setVisible(true);
                            }
                        });
                    }
                } catch (IOException e) {
                    // pukla veza tokom prijave ili aktivacije
                    // treba obrisati ove printStackTraceove kasnije, i pošteno obraditi izuzetke ;)
                    ClientServicesThread.socket = null;
                    ClientServicesThread.in = null;
                    ClientServicesThread.out = null;
                    // reakcija na prekinutu vezu
                    activated.set(false);
                    Platform.runLater(() -> {
                        btnClose.requestFocus();
                        activationText.setStyle("-fx-text-fill: red;");
                        activationText.setText("Connection problem!");
                    });
                }
                return null;
            }
        };
        progressIndicator.visibleProperty().bind(task.runningProperty());
        anchorPaneLabels.disableProperty().bind(task.runningProperty());
        new Thread(task).start();
    }
}
