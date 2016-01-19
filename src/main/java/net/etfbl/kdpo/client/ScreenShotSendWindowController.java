package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stijak on 12.01.2016.
 */
public class ScreenShotSendWindowController {
	@FXML
	private Button btnCancel;

	@FXML
	private ComboBox<String> dropDownList;

	@FXML
	private ProgressIndicator progressBar;

	@FXML
	private HBox hBoxImageContainer;

	@FXML
	private Button btnSend;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Label lblErrorText;

	@FXML
	private VBox vBox;

	private ImageView imageView;
	private Stage stage;
	private double x = 0;
	private double y = 0;
	private File imageFile;

	@FXML
	void initialize() {
		ObservableList<String> data = FXCollections.observableArrayList();
		//data.addAll("Marko", "Nenad", "Nemanja", "Duško", "Vladan");
		dropDownList.setItems(data);
		progressBar.setVisible(false);
		lblErrorText.setVisible(false);
		imageView = new ImageView();
		//imageView.setPreserveRatio(true);
		ImageViewPane ivp = new ImageViewPane(imageView);
		hBoxImageContainer.getChildren().add(ivp);
		ivp.prefHeightProperty().bind(hBoxImageContainer.heightProperty());
		ivp.prefWidthProperty().bind(hBoxImageContainer.widthProperty());
		new Thread(this::createImage).start();
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(2000);
					String response = ClientServicesThread.displayUsers();
					String[] users = response.split("#");
					//preskacemo prvi jer je on kontrola USERS
					for (int i = 1; i < users.length; ++i)
						data.add(users[i]);
					//Thread.sleep(1000);
				} catch (Exception e) {
					Platform.runLater(() -> {
						lblErrorText.setText("Connection problem!");
						lblErrorText.setVisible(true);
					});
				}
				return null;
			}
		};
		if (ClientServicesThread.socket != null) {
			progressBar.visibleProperty().bind(task.runningProperty());
			new Thread(task).start();
		} else {
			Main.showNotification("Not Connected!");
		}
		// za pomijeranje prozora
		anchorPane.setOnMousePressed(event -> {
			this.x = anchorPane.getScene().getWindow().getX() - event.getScreenX();
			this.y = anchorPane.getScene().getWindow().getY() - event.getScreenY();
		});

		anchorPane.setOnMouseDragged(event -> {
			stage.setX(event.getScreenX() + this.x);
			stage.setY(event.getScreenY() + this.y);
		});

		btnSend.setOnMouseClicked(event -> {
			if (ClientServicesThread.socket != null) {
				sendSS();
			} else {
				Main.showNotification("Not Connected!");
			}
		});

		btnCancel.setOnMouseClicked(event -> {
			task.cancel();
			stage.close();
		});

		dropDownList.setOnAction(event -> {
			if (lblErrorText.isVisible()) {
				lblErrorText.setVisible(false);
			}
		});
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private void createImage() {
		try {
			Robot ro = new Robot();
			Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			BufferedImage im = ro.createScreenCapture(rect);
			WritableImage image = new WritableImage(width, height);
			SwingFXUtils.toFXImage(im, image);
			imageView.setImage(image);

			String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "License" + File.separator + "tmp.png";
			imageFile = new File(path);
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
			ImageIO.write(renderedImage, "png", imageFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendSS() {
		String user = dropDownList.getValue();
		if (user == null) {
			lblErrorText.setText("You need to select some user!");
			lblErrorText.setVisible(true);
		} else {
			// slanje slike serveru
			ClientServicesThread.out.println("SCREENSHOT#" + dropDownList.getSelectionModel().getSelectedItem());
			try {
				ClientServicesThread.sendImage(ClientServicesThread.socket, imageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
