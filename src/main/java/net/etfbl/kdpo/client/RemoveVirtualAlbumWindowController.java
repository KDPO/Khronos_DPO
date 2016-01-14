package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Vladan on 1/14/2016.
 */
public class RemoveVirtualAlbumWindowController {
    @FXML
    private Button btnCancel;

    @FXML
    private Button btnOK;

    @FXML
    private AnchorPane anchorPane;

    private Stage stage;

    private double x = 0;
    private double y = 0;

    private boolean cancel;

    @FXML
    void initialize() {
        btnCancel.setOnMouseClicked(event -> {
            cancel=true;
            stage.close();
        });
        btnOK.setOnMouseClicked(event -> {
            cancel=false;
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
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        //onemogucuje mjenjanje iz popupa
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.initOwner(Main.primaryStage);
    }

    public boolean isCancel() {
        return cancel;
    }
}
