package net.etfbl.kdpo.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by User on 12/15/2015.
 */
public class FullScreenController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    void onKeyTyped(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DOWN) || (event.getCode().equals(KeyCode.RIGHT))) {
            nextImage();
        } else if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.LEFT)) {
            prevImage();
        }

    }


    @FXML
    void initialize() {
        assert anchorPane != null : "fx:id=\"anchorPane\" was not injected: check your FXML file 'fullScreen.fxml'.";
        imageView = new ImageView();
        anchorPane.getChildren().add(imageView);
        anchorPane.setBottomAnchor(imageView, 0.0);
        anchorPane.setTopAnchor(imageView, 0.0);
        anchorPane.setLeftAnchor(imageView, 0.0);
        anchorPane.setRightAnchor(imageView, 0.0);


    }


    private VirtualAlbum virtualAlbum;
    private ObservableList<File> images;
    private ImageView imageView;
    private ArrayList<File> imagesArray;
    private int index;

    private Stage stage;
    private Scene scene;
    private static boolean slideShowOn;
    private Runnable thread;


    public void setVirtualAlbum(VirtualAlbum virtualAlbum) {
        this.virtualAlbum = virtualAlbum;
        images = virtualAlbum.getImages();
        imagesArray = new ArrayList<>(images);
        File file = imagesArray.get(index);
        if (file.isAbsolute()) {
            this.imageView.setImage(new Image("file:" + file.getPath()));
        } else {
            this.imageView.setImage(new Image(file.getPath()));
        }
    }

    public void setSceneAndStage(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
        scene.getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.DOWN) || (event.getCode().equals(KeyCode.RIGHT))) {
                nextImage();
            } else if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.LEFT)) {
                prevImage();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                this.stage.setScene(this.scene);
            } else if (event.getCode().equals(KeyCode.SPACE)) {
                // TO DO slide show
                slideShowOn = !slideShowOn;
                if (slideShowOn) {
                    FullScreenController fsc = this;
                    System.out.println("starting slideshow");
                    thread = new Runnable() {

                        private FullScreenController fullScreenController = fsc;

                        @Override
                        public void run() { // DO NOT LOOK HERE
                            try {
                                while (slideShowOn) {
                                    Thread.sleep(3500);
                                    if (!fsc.nextImage()) {
                                        slideShowOn = false;
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    new Thread(thread).start(); // OR ANYWHERE ELSE
                } else {
                    //TO DO something
                }
            }

        });
    }


    private boolean nextImage() {
        if ((index + 1) < imagesArray.size()) {
            index++;
            File file = imagesArray.get(index);
            if (file.isAbsolute()) {
                this.imageView.setImage(new Image("file:" + file.getPath()));
            } else {
                this.imageView.setImage(new Image(file.getPath()));
            }
            return true;
        }
        return false;
    }

    private boolean prevImage() {
        if ((index - 1) >= 0) {
            index--;
            File file = imagesArray.get(index);
            if (file.isAbsolute()) {
                this.imageView.setImage(new Image("file:" + file.getPath()));
            } else {
                this.imageView.setImage(new Image(file.getPath()));
            }
            return true;
        }
        return false;
    }
}
