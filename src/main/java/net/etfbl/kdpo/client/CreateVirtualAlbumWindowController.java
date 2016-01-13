package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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

    @FXML
    private Label lblErrorText;

    @FXML
    private AnchorPane anchorPane;

    private Stage stage;
    private ObservableList<VirtualAlbum> albums;

    private double x = 0;
    private double y = 0;

    @FXML
    void initialize() {
        btnCancel.setOnMouseClicked(event -> stage.close());
        btnSave.setOnMouseClicked(event -> addNewAlbum());
        lblErrorText.setOpacity(0.0);

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
    }

    public void setAlbumList(ObservableList<VirtualAlbum> albums) {
        this.albums = albums;
    }

    private void addNewAlbum() {
        if ("".equals(txtAlbumName.getText())) {
            lblErrorText.setText("Album name must not be empty");
            lblErrorText.setOpacity(1.0);
        } else if (albums.contains(new VirtualAlbum(txtAlbumName.getText(), ""))) {
            lblErrorText.setText("Album with that name already exists");
            lblErrorText.setOpacity(1.0);
        } else {
            albums.add(new VirtualAlbum(txtAlbumName.getText(), txtAlbumDescription.getText()));
            stage.close();
        }
    }
}
