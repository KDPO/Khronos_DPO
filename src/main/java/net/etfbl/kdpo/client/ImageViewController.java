package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Stijak on 18.12.2015..
 */
public class ImageViewController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnFullScreen;

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
    private AnchorPane controlLine;

    @FXML
    private AnchorPane anchorPaneImageContainer;

    private ImageViewPane ivp;

    private int CONTROL_LINE_COUNTER = 0;
    private ImageView imageView;
    private int INDEX;
    private ObservableList<File> images;
    private Parent oldRoot;
    private Stage stage;
    private boolean canZoomOut = false;
    private boolean canZoomIn = true;
    private boolean hide = true;
    private VirtualAlbum virtualAlbum;
    private double x = 0;
    private double y = 0;
    private final double STEP = 0.2;

    @FXML
    void initialize() {
        images = FXCollections.observableArrayList();
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        ivp = new ImageViewPane(imageView);
        hBoxImageContainer.getChildren().add(ivp);
        ivp.prefHeightProperty().bind(hBoxImageContainer.heightProperty());
        ivp.prefWidthProperty().bind(hBoxImageContainer.widthProperty());

        btnNextImage.setOnMouseClicked((MouseEvent) -> nextImage());

        btnPrevImage.setOnMouseClicked((MouseEvent) -> prevImage());

        btnBack.setOnMouseClicked((MouseEvent) -> stage.getScene().setRoot(oldRoot));

        btnZoomIn.setOnMouseClicked(event -> zoomIn());

        btnZoomOut.setOnMouseClicked(event -> zoomOut());

        btnRotateLeft.setOnMouseClicked(event -> rotateLeft());

        btnRotateRight.setOnMouseClicked(event -> rotateRight());

        btnFullScreen.setOnMouseClicked(event -> showFullScreenControler());

        btnRemove.setOnMouseClicked(event -> {
            // izbacivanje slike iz Virtuelnog Albuma
            File file = images.get(INDEX);
            nextImage();
            new Thread(() -> {
                images.remove(file);
            }).start();
        });

        btnDelete.setOnMouseClicked(event -> {
            // brisanje slike sa diska
            File file = images.get(INDEX);
            nextImage();
            new Thread(() -> {
                images.remove(file);
                file.deleteOnExit();
            }).start();
        });

        // za pomijeranje prozora
        imageView.setOnMousePressed(event -> {
            this.x = imageView.getTranslateX() - event.getScreenX();
            this.y = imageView.getTranslateY() - event.getScreenY();
        });
        // za pomijeranje prozora
        imageView.setOnMouseDragged(event -> {
            imageView.setTranslateX(event.getScreenX() + this.x);
            imageView.setTranslateY(event.getScreenY() + this.y);
        });

        imageView.setOnDragDetected(event -> hide = false);

        anchorPaneImageContainer.setOnMouseReleased(event -> {
            if (hide && event.getButton().equals(MouseButton.PRIMARY)) {
                if (CONTROL_LINE_COUNTER == 0) {
                    controlLine.setVisible(false);
                    CONTROL_LINE_COUNTER = 1;
                } else {
                    controlLine.setVisible(true);
                    CONTROL_LINE_COUNTER = 0;
                }
            }
            hide = true;
        });

        imageView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (imageView.getScaleX() == 1 && imageView.getScaleY() == 1 && imageView.getTranslateX() == 0 && imageView.getTranslateY() == 0) {
                    imageView.setScaleX(2);
                    imageView.setScaleY(2);
                    canZoomOut = true;
                } else {
                    imageView.setTranslateX(0);
                    imageView.setTranslateY(0);
                    imageView.setScaleX(1);
                    imageView.setScaleY(1);
                    canZoomOut = false;
                }
            }
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

    public void setVirtualAlbum(VirtualAlbum virtualAlbum, int index) {
        this.virtualAlbum = virtualAlbum;
        setImages(virtualAlbum.getImages(), index);
        if (virtualAlbum.isTemporary())
            btnRemove.setVisible(false);
    }

    public void initParams(Stage stage, Parent root) {
        this.stage = stage;
        this.oldRoot = root;
        stage.getScene().getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if ((event.getCode().equals(KeyCode.RIGHT) || event.getCode().equals(KeyCode.UP)))
                nextImage();
            else if (event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.LEFT))
                prevImage();
            else if (event.getCode().equals(KeyCode.PLUS) || event.getCode().equals(KeyCode.ADD))
                zoomIn();
            else if (event.getCode().equals(KeyCode.MINUS) || event.getCode().equals(KeyCode.SUBTRACT))
                zoomOut();
        });

        stage.getScene().getWindow().addEventHandler(ScrollEvent.SCROLL, event -> {
            double value = event.getDeltaY();
            if (value < 0) {
                zoomOut();
            } else {
                zoomIn();
            }
        });
    }

    private void showImage() {
        imageView.setImage(new Image("file:" + images.get(INDEX).getPath()));
        ivp.setMaxHeight(imageView.getImage().getHeight());
        if (imageView.getScaleX() != 1) {
            imageView.setScaleX(1);
            imageView.setScaleY(1);
        }

        if (imageView.getRotate() != 0)
            imageView.setRotate(0);

        if (imageView.getTranslateX() != 0) {
            imageView.setTranslateX(0);
            imageView.setTranslateY(0);
        }
    }

    private void nextImage() {
        if (++INDEX >= images.size())
            INDEX = 0;
        showImage();
    }

    private void prevImage() {
        if (--INDEX < 0)
            INDEX = images.size() - 1;
        showImage();
    }

    private void zoomIn() {
        if (canZoomIn) {
            canZoomOut = true;
            imageView.setScaleX(imageView.getScaleX() + STEP);
            imageView.setScaleY(imageView.getScaleY() + STEP);
        }
        if (imageView.getScaleX() >= 10)
            canZoomIn = false;
    }

    private void zoomOut() {
        if (canZoomOut) {
            canZoomIn = true;
            imageView.setScaleX(imageView.getScaleX() - STEP);
            imageView.setScaleY(imageView.getScaleY() - STEP);
        }
        if (imageView.getScaleX() <= 1)
            canZoomOut = false;
    }

    private void rotateLeft() {
        imageView.setRotate(imageView.getRotate() - 90);
    }

    private void rotateRight() {
        imageView.setRotate(imageView.getRotate() + 90);
    }

    private void showFullScreenControler() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fullScreen.fxml"));
            Parent root = loader.load();
            FullScreenController fullScreenController = loader.getController();
            fullScreenController.setIndex(INDEX);
            fullScreenController.setImages(images);
            fullScreenController.setSceneAndStage(stage.getScene(), stage);
            stage.getScene().setRoot(root);
            stage.setFullScreenExitHint("");
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("OH NO!");
        }

    }
}