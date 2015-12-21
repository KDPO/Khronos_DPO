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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

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

    private Stage stage;

    public MainController() {
    }

    @FXML
    void initialize() {
        ImageFrame imageFrame = new ImageFrame(new File("/testImages/test.jpg"));
        flowPane.getChildren().add(imageFrame);

        for (int i = 1; i < 5; i++) {
            flowPane.getChildren().add(new ImageFrame(new File("/testImages/slika.jpg")));
        }

        ObservableList<String> data = FXCollections.observableArrayList();
        listView.setItems(data);
        data.addAll("Prvi album", "Drugi album", "Treći album", "Četvrti album");

        listView.setOnMouseClicked((MouseEvent) -> {
            lblMessages.setText("Još uvijek nemam funkciju.");
        });

        imageFrame.setOnMouseClicked((MouseEvent) -> {
            if (!imageFrame.getCheckBox().isSelected())
                showImageViewController(imageFrame.getImage());
        });
    }

    public void addNewVirtualAlbum() {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showImageViewController(Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/imageView.fxml"));
            Parent root = loader.load();
            ImageViewController controller = loader.getController();
            controller.setImage(image);
            controller.initParams(stage, stage.getScene());
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}