package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
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


    @FXML
    private Label lblStatus;

	@FXML
	private Label lblEndOfSlideShow;

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
        lblStatus.setTextFill(Paint.valueOf("green"));
        lblStatus.setContentDisplay(ContentDisplay.TOP);
		progressBar.setOpacity(0.0);
		lblEndOfSlideShow.setTextFill(Paint.valueOf("green"));
		lblEndOfSlideShow.setOpacity(0.0);
		lblEndOfSlideShow.setText("End of Slide Show");
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

    public void setImages(ObservableList<File> images) {
        this.images = images;
        imagesArray = new ArrayList<>(images);
        showImage();
        updateStatusLabel();
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
					lblStatus.setOpacity(0.0);
					updateProgressBar();
					progressBar.setOpacity(0.3);
                    FullScreenController fsc = this;
					Task<Integer> task = new Task<Integer>() {
						@Override
						protected Integer call() throws Exception {
							try {
								while (slideShowOn) {
									Thread.sleep(3500);
									if(!slideShowOn) {
										return null;
									}
									fsc.nextImage();
									if (index == (images.size()-1 )) {
										slideShowOn = false;
									}
								}
								Thread.sleep(3500);
								imageView.setImage(null);
								lblEndOfSlideShow.setOpacity(1.0);
								progressBar.setOpacity(0.0);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							return null;
						}
					};

					Thread th = new Thread(task);
					th.setDaemon(true);
					th.start();
                } else {
					updateStatusLabel();
					lblStatus.setOpacity(1.0);
					progressBar.setOpacity(0.0);
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
		if(imageView.getImage() == null) {
			lblEndOfSlideShow.setOpacity(0.0);
			lblStatus.setOpacity(1.0);
			showImage();
			updateStatusLabel();
		}
        if ((index + 1) < imagesArray.size()) {
            index++;
            showImage();
			if (slideShowOn) {
				updateProgressBar();
			} else {
				updateStatusLabel();
			}
		}
    }

    private void prevImage() {
		if(imageView.getImage() == null) {
			lblEndOfSlideShow.setOpacity(0.0);
			lblStatus.setOpacity(1.0);
		}
        if ((index - 1) >= 0) {
            index--;
            showImage();
			updateStatusLabel();
        }
    }

	private void updateProgressBar() {
		progressBar.setProgress((index+1)/(double)imagesArray.size());
	}

    private void updateStatusLabel() {
        lblStatus.setText( imagesArray.get(index).getName() + " "  + (index+1) + "/" + imagesArray.size());
    }
    /* Maybe Later
    private void startTimer() {
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return null;
            }
        }
    }
    */

    @FXML
    private void lblStatusMouseEnteredFunction(MouseEvent event) {
        lblStatus.setOpacity(1.0);
    }

    @FXML
    private void lblStatusMouseExitedFunction(MouseEvent event) {
        lblStatus.setOpacity(0.0);
    }

	@FXML
	private ProgressBar progressBar;

}
