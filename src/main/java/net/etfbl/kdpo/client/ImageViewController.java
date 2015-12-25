package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

    private ObservableList<Image> images;
    private Scene scene;
    private Stage stage;

    private AnchorPane anchorPaneFullScreen;

    @FXML
    void initialize() {
        images = FXCollections.observableArrayList();

        btnBack.setOnMouseClicked((MouseEvent) -> {
            stage.setScene(scene);
        });
    }

    public void setImages(Image... image) {
        images.setAll(image);
    }

    public void setImages(ObservableList<Image> images) {
        this.images = images;
    }

    public void setImage(Image image) {
        images.setAll(image);
    }

    public void initParams(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }
}
