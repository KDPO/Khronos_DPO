package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
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
    private Button btnRemove;

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

    @FXML
    private AnchorPane controlLine;

    private ImageViewPane ivp;

    private int CONTROL_LINE_COUNTER = 0;

    private int INDEX;
    private ObservableList<File> images;
    private Parent oldRoot;
    private Stage stage;
    private boolean canZoomOut = false;
    private boolean canZoomIn = true;

    @FXML
    void initialize() {
        images = FXCollections.observableArrayList();
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        ivp = new ImageViewPane(imageView);
        hBoxImageContainer.getChildren().add(ivp);
        ivp.prefHeightProperty().bind(hBoxImageContainer.heightProperty());
        ivp.prefWidthProperty().bind(hBoxImageContainer.widthProperty());

        btnNextImage.setOnMouseClicked((MouseEvent) -> {
            nextImage();
        });

        btnPrevImage.setOnMouseClicked((MouseEvent) -> {
            prevImage();
        });

        btnBack.setOnMouseClicked((MouseEvent) -> stage.getScene().setRoot(oldRoot));

        btnZoomIn.setOnMouseClicked(event -> {
            if (canZoomIn) {
                canZoomOut = true;
                imageView.setScaleX(imageView.getScaleX() + 0.5);
                imageView.setScaleY(imageView.getScaleY() + 0.5);
            }
            if (imageView.getScaleX() >= 10)
                canZoomIn = false;
        });

        btnZoomOut.setOnMouseClicked(event -> {
            if (canZoomOut) {
                canZoomIn = true;
                imageView.setScaleX(imageView.getScaleX() - 0.5);
                imageView.setScaleY(imageView.getScaleY() - 0.5);
            }
            if (imageView.getScaleX() <= 1)
                canZoomOut = false;
        });

        imageView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (CONTROL_LINE_COUNTER == 0) {
                    controlLine.setVisible(false);
                    CONTROL_LINE_COUNTER = 1;
                } else {
                    controlLine.setVisible(true);
                    CONTROL_LINE_COUNTER = 0;
                }
            }
        });

        // treba popraviti računanje
        imageView.setOnMouseDragged(event -> {
            imageView.setTranslateX(event.getX());
            imageView.setTranslateY(event.getY());
        });
    }

    public void setImages(ObservableList<File> images, int index) {
        this.images = images;
        INDEX = index;
        if (images.size() == 1) {
            btnNextImage.setVisible(false);
            btnPrevImage.setVisible(false);
        }
        showImage();
    }

    public void initParams(Stage stage, Parent root) {
        this.stage = stage;
        this.oldRoot = root;
        stage.getScene().getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if ((event.getCode().equals(KeyCode.RIGHT) || event.getCode().equals(KeyCode.UP)))
                nextImage();
            else if (event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.LEFT))
                prevImage();
        });
    }

    private void showImage() {
        imageView.setImage(new Image("file:" + images.get(INDEX).getPath()));
        ivp.setMaxHeight(imageView.getImage().getHeight());
        if (imageView.getScaleX() != 1) {
            imageView.setScaleX(1);
            imageView.setScaleY(1);
        }
    }

    private void nextImage() {
        if (++INDEX == images.size())
            INDEX = 0;
        showImage();
    }

    private void prevImage() {
        if (--INDEX < 0)
            INDEX = images.size() - 1;
        showImage();
    }
}
