package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 12/15/2015.
 */
public class FullScreenController {

    @FXML
    private HBox hBox;

    // da li ovo radi???
    @FXML
    void onKeyTyped(KeyEvent event) {
        if (event.getCode().equals(KeyCode.DOWN) || (event.getCode().equals(KeyCode.RIGHT))) {
            nextImage();
        } else if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.LEFT)) {
            prevImage();
        }

    }

    /* Pogledaj kako sam ja napravio promjenu slika, isto se može i ovdje uraditi. Ja sam za to
    da se slike vrte u krug umjesto da "postoji" kraj. Nema potrebe da se koristi ArrayList<?>
    kad već postoji ObservableList<?>. Moraš paziti na index slike tako da prebaci na pravu
    u FullScreen-u. Slobodno izbaci VirtuelniAlbum, koristi samo slike (fajlove).

    Napomena: Ovaj dolje Thread ti neće raditi (bacaće izuzetak) zato što nije UI thread. Moraš koristiti
    Platform.runLater(new Runnable(){ ... });. Preporučio bih ti da ovdje koristiš Task<?> sa Thread-om
    pa taman možeš i implementirati progress bar.
     */


    @FXML
    void initialize() {
        images = FXCollections.observableArrayList();
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        ivp = new ImageViewPane(imageView);
        hBox.getChildren().add(ivp);
        ivp.prefHeightProperty().bind(hBox.heightProperty());
        ivp.prefWidthProperty().bind(hBox.widthProperty());
    }

    private ImageViewPane ivp;
    private VirtualAlbum virtualAlbum;
    private ObservableList<File> images;
    private ImageView imageView;
    private ArrayList<File> imagesArray;
    private int index;
    private Parent oldRoot;

    private Stage stage;
    private Scene scene;
    private static boolean slideShowOn;
    private Runnable thread;


    public void setVirtualAlbum(VirtualAlbum virtualAlbum) {
        this.virtualAlbum = virtualAlbum;
        images = virtualAlbum.getImages();
        imagesArray = new ArrayList<>(images);
        showImage();
    }

    public void setSceneAndStage(Scene scene, Stage stage) {
        this.oldRoot = scene.getRoot();
        this.scene = scene;
        this.stage = stage;
        scene.getWindow().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.DOWN) || (event.getCode().equals(KeyCode.RIGHT))) {
                nextImage();
            } else if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.LEFT)) {
                prevImage();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                this.stage.getScene().setRoot(oldRoot);
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
                                    // if (!fsc.nextImage()) {
                                    //    slideShowOn = false;
                                    // }
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

    // Don't repeat yourself Nenad! :P
    private void showImage() {
        File file = imagesArray.get(index);
        if (file.isAbsolute()) {
            this.imageView.setImage(new Image("file:" + file.getPath()));
        } else {
            this.imageView.setImage(new Image(file.getPath()));
        }
        ivp.setMaxHeight(imageView.getImage().getHeight());
    }

    private void nextImage() {
        if ((index + 1) < imagesArray.size()) {
            index++;
            showImage();
        }
    }

    private void prevImage() {
        if ((index - 1) >= 0) {
            index--;
            showImage();
        }
    }
}
