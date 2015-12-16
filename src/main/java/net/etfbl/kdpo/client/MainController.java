package net.etfbl.kdpo.client;

import javafx.collections.ObservableArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Stijak on 16.12.2015..
 */

public class MainController {

    private Tab tabVirtualAlbum;
    private Tab tabFileSystem;
    private ListView<String> listOfAlbums;
    private Menu menu;
    private Button btnSlideshow;
    private Button btnAdd;
    private Button btnOK;
    private FlowPane images;
    //private ObservableArray<ImageFrame> image;


    @FXML
    void initialize() {
    }
}
