package net.etfbl.kdpo.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.File;
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

    @FXML
    void initialize() {
    }

}
