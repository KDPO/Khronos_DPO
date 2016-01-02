package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Stijak on 18.12.2015..
 */
public class ImageViewController {
    @FXML
    private VBox vBox;

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
    private HBox hBoxImageContainer;

    @FXML
    private ImageView imageView;
    private static int INDEX;
    private ObservableList<File> images;
    private Scene scene;
    private Stage stage;

    @FXML
    void initialize() {
        images = FXCollections.observableArrayList();
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        ImageViewPane ivp = new ImageViewPane(imageView);
        hBoxImageContainer.getChildren().add(ivp);
        ivp.prefHeightProperty().bind(hBoxImageContainer.heightProperty());
        ivp.prefWidthProperty().bind(hBoxImageContainer.widthProperty());

        btnNextImage.setOnMouseClicked((MouseEvent) -> {
            nextImage();
        });

        btnPrevImage.setOnMouseClicked((MouseEvent) -> {
            prevImage();
        });

        btnBack.setOnMouseClicked((MouseEvent) -> {
            stage.setScene(scene);
        });

    }

    public void setImages(ObservableList<File> images, int index) {
        this.images = images;
        INDEX = index;
        showImage();
    }

    public void initParams(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        scene.getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.UP) || (event.getCode().equals(KeyCode.RIGHT)))
                nextImage();
            else if (event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.LEFT))
                prevImage();
        });
    }

    private void showImage() {
        imageView.setImage(new Image("file:" + images.get(INDEX).getPath()));
    }

    private void nextImage() {
        if (++INDEX == images.size())
            INDEX = 0;
        imageView.setImage(new Image("file:" + images.get(INDEX).getPath()));
    }

    private void prevImage() {
        if (--INDEX < 0)
            INDEX = images.size() - 1;
        imageView.setImage(new Image("file:" + images.get(INDEX).getPath()));
    }
}
