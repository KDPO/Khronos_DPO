package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

/**
 * Created by Stijak on 16.12.2015..
 */
public class PreferencesController {
    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabHelp;

    @FXML
    private TextFlow textFlow;

    private Stage stage;

    private StringBuilder stringBuilder;

    @FXML
    void initialize() {
        //TODO sve što treba :D
        btnCancel.setOnAction(event -> {
            stage.close();
        });

        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File("/config/help.txt")));
                stringBuilder = new StringBuilder();
                String input = "";
                while ((input = reader.readLine()) != null)
                    stringBuilder.append(input);
                Text text = new Text(stringBuilder.toString());
                Platform.runLater(() -> textFlow.getChildren().add(text));
            } catch (Exception e) {

            }
        }).start();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
