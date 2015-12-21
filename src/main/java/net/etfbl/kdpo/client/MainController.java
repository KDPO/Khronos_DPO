package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    private Label lblAlbumDescription;

    @FXML
    private Button btnAddImages;

    @FXML
    private Button btnSlideShow;

    @FXML
    private Label lblMessages;

    @FXML
    private FlowPane flowPane;

    @FXML
    private MenuButton menu;

    @FXML
    private ListView<String> listView;

    @FXML
    private TreeView<String> treeView;

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

        listView.setOnMouseClicked((MouseEvent) -> {
            lblMessages.setText("Još uvijek nemam funkciju.");
        });
    }

    public void addNewVirtualAlbum() {

    }

}