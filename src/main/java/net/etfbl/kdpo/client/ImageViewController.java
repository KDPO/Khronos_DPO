package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;

/**
 * Created by Stijak on 18.12.2015..
 */
public class ImageViewController {
    @FXML
    private Button btnBack;

    @FXML
    private Button btnFullScreen;

    @FXML
    private Button btnSlideShow;

    @FXML
    private Button btnRotateRight;

    @FXML
    private Button btnRotateLeft;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnPrevImage;

    @FXML
    private Button btnNextImage;

    @FXML
    private Button btnZoomIn;

    @FXML
    private Button btnZoomOut;

    @FXML
    private ImageView imageView;

    private Image image;
    private Scene scene;
    private Stage stage;

    private AnchorPane anchorPaneFullScreen;

    @FXML
    void initialize() {
        btnBack.setOnMouseClicked((MouseEvent) -> {
            stage.setScene(scene);
        });
    }

    public void setImage(Image image) {
        this.image = image;
        imageView.setImage(image);
        imageView.setFitHeight(500);
    }

    public void initParams(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }
}
