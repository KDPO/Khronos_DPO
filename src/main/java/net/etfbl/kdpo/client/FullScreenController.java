package net.etfbl.kdpo.client;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

/**
 *   FullScreenControler
 *   can only be caled from ImageViewController :)
 */
public class FullScreenController {
	@FXML
	private HBox hBox;

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label lblStatus;

	@FXML
	private Label lblEndOfSlideShow;

	@FXML
	private AnchorPane anchorPane;


	@FXML
	void initialize() {
		images = FXCollections.observableArrayList();
		imageView = new ImageView();
		imageView.setPreserveRatio(true);
		ivp = new ImageViewPane(imageView);
		System.out.println(hBox);
		System.out.println(lblStatus);
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


	public FullScreenController() {}


	private Stage previousStage;
	private Stage myStage;
	private Scene myScene;

	private ImageViewController imageViewController;

	private PauseTransition hidelabel;
	private boolean noActivity;

	private ImageViewPane ivp;
	private ObservableList<File> images;
	private ImageView imageView;
	private int index;
	private static boolean slideShowOn;

	public void setPreviousStageStage(Stage previousStage) {
		this.previousStage = previousStage;
	}

	public void setRoot(Parent root) {
		myScene = new Scene(root);
		myStage = new Stage();
		myStage.setScene(myScene);
		registerListeners();
	}

	public void show() {
		myStage.setFullScreen(true);
		myStage.setFullScreenExitHint("");
		myStage.show();
	}

	public void setImageViewController(ImageViewController imageViewController){
		this.imageViewController = imageViewController;
	}

	public void setImages(ObservableList<File> images, int index) {
		this.index = index;
		this.images = images;
		showImage();
		updateStatusLabel();
	}

	public void registerListeners() {
		myScene.getWindow().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode().equals(KeyCode.DOWN) || (event.getCode().equals(KeyCode.RIGHT))) {
				nextImage();
			} else if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.LEFT)) {
				prevImage();
			} else if (event.getCode().equals(KeyCode.ESCAPE)) {
				escape();
			}  else if (event.getCode().equals(KeyCode.SPACE)) {
				slideShow();
			}
		});
		myScene.getWindow().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> nextImage());
		myScene.getWindow().addEventHandler(ScrollEvent.SCROLL, event -> {
			double value = event.getDeltaY();
			if (value < 0) {
				nextImage();
			} else {
				prevImage();
			}
		});
		PauseTransition idle = new PauseTransition(Duration.seconds(2));
		idle.setOnFinished(e -> myScene.setCursor(Cursor.NONE));
		myScene.getWindow().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			myScene.setCursor(Cursor.DEFAULT);
			lblStatus.setOpacity(1.0);
			idle.playFromStart();
			hideLblStatusAfterSeconds(4);
		});
	}

	private void escape() {
		slideShowOn = false;
		myScene.setCursor(Cursor.DEFAULT);
		myStage.close();
		imageViewController.setINDEX(index);
		previousStage.show();
	}

	private void slideShow() {
		slideShowOn = !slideShowOn;
		if (slideShowOn) {
			System.out.println("SlideShow ONN slideshowon=" + slideShowOn);
			lblStatus.setOpacity(0.0);
			updateProgressBar();
			progressBar.setOpacity(0.3);
			System.out.println("Creating task");
			Task<Integer> task = new Task<Integer>() {
				@Override
				protected Integer call() throws Exception {
					try {
						while (slideShowOn) {
							System.out.println("starting slideshow");
							Thread.sleep(3500);
							if (!slideShowOn) {
								progressBar.setOpacity(0.0);
								slideShowOn = false;
								return null;
							}
							nextImage();
							if (index == (images.size() - 1)) {
								Thread.sleep(3500);
								if (!slideShowOn) {
									progressBar.setOpacity(0.0);
									slideShowOn = false;
									return null;
								}
								imageView.setImage(null);
								lblEndOfSlideShow.setOpacity(1.0);
								progressBar.setOpacity(0.0);
								slideShowOn = false;
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}
			};

			Thread thread = new Thread(task);
			thread.setDaemon(true);
			thread.start();
		} else {
			System.out.println("Slideshow off slideshowon=" + slideShowOn);
			updateStatusLabel();
			lblStatus.setOpacity(1.0);
			progressBar.setOpacity(0.0);
		}
	}

	private void showImage() {
		System.out.println("show image");
		File file = images.get(index);
		if (file.isAbsolute()) {
			this.imageView.setImage(new Image("file:" + file.getPath()));
		} else {
			this.imageView.setImage(new Image(file.getPath()));
		}
		ivp.setMaxHeight(imageView.getImage().getHeight());
		noActivity = false;
	}

	private void nextImage() {
		if(imageView.getImage() == null) {
			lblEndOfSlideShow.setOpacity(0.0);
			lblStatus.setOpacity(1.0);
			showImage();
			updateStatusLabel();
			hideLblStatusAfterSeconds(2);
		}
		if ((index + 1) < images.size()) {
			System.out.println("prije show image");
			index++;
			showImage();
			if (slideShowOn) {
				updateProgressBar();
			} else {
				updateStatusLabel();
				lblStatus.setOpacity(1.0);
				hidelabel.pause();
				hideLblStatusAfterSeconds(2);
			}
		}
	}

	private void prevImage() {
		if(imageView.getImage() == null) {
			lblEndOfSlideShow.setOpacity(0.0);
			lblStatus.setOpacity(1.0);
			updateStatusLabel();
			hideLblStatusAfterSeconds(2);
		}
		if ((index - 1) >= 0) {
			index--;
			showImage();
			updateStatusLabel();
			lblStatus.setOpacity(1.0);
			hidelabel.pause();
			hideLblStatusAfterSeconds(2);
		}
	}

	private void hideLblStatusAfterSeconds(int seconds) {
		PauseTransition hideLabel = new PauseTransition(Duration.seconds(seconds));
		hideLabel.setOnFinished( e -> {
			if(noActivity) {
				lblStatus.setOpacity(0.0);
			}
		});
		noActivity = true;
		this.hidelabel = hideLabel;
		hideLabel.playFromStart();
	}

	private void updateProgressBar() {
		progressBar.setProgress((index+1)/(double)images.size());
	}

	private void updateStatusLabel() {
		lblStatus.setText(String.format("%s\t%d/%d", images.get(index).getName(), index + 1, images.size()));
	}

}
