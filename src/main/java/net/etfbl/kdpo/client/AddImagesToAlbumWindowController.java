package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
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
    private Label lblErrorText;

    @FXML
    private ChoiceBox<VirtualAlbum> albums;

    @FXML
    private AnchorPane anchorPane;

    private Stage stage;
    private ObservableList<File> images;
    private boolean cancel = false;
    private int indexOfVA = -1;
    private double x = 0;
    private double y = 0;

    @FXML
    void initialize() {
        lblErrorText.setVisible(false);
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

        /*albums.setOnAction(event -> {
            if (lblErrorText.isVisible())
                lblErrorText.setVisible(false);
        });*/
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

    public void setAlbumList(ObservableList<VirtualAlbum> albums) {
        this.albums.setItems(albums);
    }

    public void setImagesList(ObservableList<File> images) {
        this.images = images;
    }

    private void selectAlbum() {
        if (albums.getValue() == null) {
            lblErrorText.setText("You need to select some album!");
            lblErrorText.setVisible(true);
        } else {
            albums.getValue().setImages(images);
            indexOfVA = albums.getSelectionModel().getSelectedIndex();
            stage.close();
        }
    }

    public int getIndexOfVA() {
        return indexOfVA;
    }
}
