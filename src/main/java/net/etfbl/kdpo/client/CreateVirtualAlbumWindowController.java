package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Stijak on 12.01.2016.
 */
public class CreateVirtualAlbumWindowController {
    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private TextArea txtAlbumDescription;

    @FXML
    private TextField txtAlbumName;

    private Stage stage;
    private ObservableList<VirtualAlbum> albums;

    @FXML
    void initialize() {
        btnCancel.setOnMouseClicked(event -> {
            stage.close();
        });

        btnSave.setOnMouseClicked(event -> addNewAlbum());

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAlbumList(ObservableList<VirtualAlbum> albums) {
        this.albums = albums;
    }

    private void addNewAlbum() {
        albums.add(new VirtualAlbum(txtAlbumName.getText(), txtAlbumDescription.getText()));
        stage.close();
    }
}
