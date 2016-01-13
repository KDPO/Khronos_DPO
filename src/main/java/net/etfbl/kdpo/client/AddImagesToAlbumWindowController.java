package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.prefs.AbstractPreferences;

/**
 * Created by Vladan on 1/12/2016.
 */
public class AddImagesToAlbumWindowController {
    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private ChoiceBox<VirtualAlbum> albums;

    @FXML
    private AnchorPane anchorPane;

    private Stage stage;
    private ObservableList<File> images;
    private boolean cancel = false;
    private double x = 0;
    private double y = 0;

    @FXML
    void initialize() {
        // za pomijeranje prozora
        anchorPane.setOnMousePressed(event -> {
            this.x = anchorPane.getScene().getWindow().getX() - event.getScreenX();
            this.y = anchorPane.getScene().getWindow().getY() - event.getScreenY();
        });

        anchorPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + this.x);
            stage.setY(event.getScreenY() + this.y);
        });

        btnCancel.setOnMouseClicked(event -> {
            stage.close();
            cancel = true;
        });

        btnSave.setOnMouseClicked(event -> {
            cancel = false;
            selectAlbum();
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setAlbumList(ObservableList<VirtualAlbum> albums) {
        this.albums.setItems(albums);
    }

    public void setImagesList(ObservableList<File> images) {
        this.images = images;
    }

    private void selectAlbum() {
        albums.getValue().setImages(images);
        stage.close();
    }
}
