package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
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
    private boolean cancel = false;

    private double x = 0;
    private double y = 0;

    @FXML
    void initialize() {
        btnCancel.setOnMouseClicked(event -> {
            cancel = true;
            stage.close();
        });
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
        //onemogucuje mjenjanje iz popupa
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.initOwner(Main.primaryStage);
    }

    public void setAlbumList(ObservableList<VirtualAlbum> albums) {
        this.albums = albums;
    }

    private void addNewAlbum() {
        String txtName = txtAlbumName.getText();
        if (txtName.startsWith(" "))
            txtName = txtName.replaceFirst(" *", "");

        if ("".equals(txtName)) {
            lblErrorText.setText("Album name must not be empty");
            lblErrorText.setOpacity(1.0);
        } else if (albums.contains(new VirtualAlbum(txtName, ""))) {
            lblErrorText.setText("Album with that name already exists");
            lblErrorText.setOpacity(1.0);
        } else {
            albums.add(new VirtualAlbum(txtName, txtAlbumDescription.getText()));
            stage.close();
        }
    }

    public boolean isCancel() {
        return cancel;
    }
}
