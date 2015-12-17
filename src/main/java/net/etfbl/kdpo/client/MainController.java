package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Stijak on 16.12.2015..
 */

public class MainController {

    @FXML
    private VBox vBoxRight;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Menu menu;

    @FXML
    private Label albumDescription;

    @FXML
    private Tab tabAlbumi;

    @FXML
    private Button btnSlideshow;

    @FXML
    private MenuButton btnMenu;

    @FXML
    private VBox vBoxLeft;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private HBox hBox;

    @FXML
    private Button btnAdd;

    @FXML
    private ListView<String> albumList;

    @FXML
    private Tab tabFileSystem;

    @FXML
    private AnchorPane btnLine;

    @FXML
    void initialize() {
        ObservableList<String> virtuelniAlbumi = FXCollections.observableArrayList();
        albumList.setItems(virtuelniAlbumi);
        virtuelniAlbumi.addAll("Add new album", "Prvi album", "Drugi album", "Treci album");

        ImageView btnMenuImage = new ImageView(new Image("images/menu.png"));
        btnMenuImage.setFitHeight(30);
        btnMenuImage.setFitWidth(60);
        btnMenu.setGraphic(btnMenuImage);

        ImageView btnSlideshowImage = new ImageView(new Image("/images/slideshow.png"));
        btnSlideshowImage.setFitHeight(30);
        btnSlideshowImage.setFitWidth(60);
        btnSlideshow.setGraphic(btnSlideshowImage);

        ImageView btnAddImage = new ImageView(new Image("/images/addImages.png"));
        btnAddImage.setFitHeight(30);
        btnAddImage.setFitWidth(60);
        btnAdd.setGraphic(btnAddImage);


    }
}
