package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.File;

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

    private Stage stage;
    private ObservableList<File> images;
    private boolean cancel = false;

    @FXML
    void initialize() {
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
