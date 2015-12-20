package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
    private Label labelAlbumDescription;

    @FXML
    private Button buttonAddImages;

    @FXML
    private FlowPane flowPane;

    @FXML
    private MenuButton menu;

    @FXML
    private ListView<String> listView;

    @FXML
    void initialize() {
        for (int i = 0; i < 5; i++) {
            ImageFrame imageFrame = new ImageFrame(new File("/testImages/slika.jpg"));
            imageFrame.setPrefSize(200, 200);
            flowPane.getChildren().add(imageFrame);
        }

        ObservableList<String> data = FXCollections.observableArrayList();
        listView.setItems(data);
        data.addAll("Prvi album", "Drugi album", "Treći album", "Četvrti album");
    }

}