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

import java.awt.*;
import java.awt.image.BufferedImage;

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
                    String response = ClientServicesThread.displayUsers();
                    String[] users = response.split("#");
                    //preskacemo prvi jer je on kontrola USERS
                    for (int i = 1; i < users.length; ++i)
                        data.add(users[i]);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        lblErrorText.setText("Connection problem!");
                        lblErrorText.setVisible(true);
                    });
                }
                return null;
            }
        };
        progressBar.visibleProperty().bind(task.runningProperty());
        dropDownList.disableProperty().bind(task.runningProperty());
        new Thread(task).start();
        // za pomijeranje prozora
        anchorPane.setOnMousePressed(event -> {
            this.x = anchorPane.getScene().getWindow().getX() - event.getScreenX();
            this.y = anchorPane.getScene().getWindow().getY() - event.getScreenY();
        });

        anchorPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + this.x);
            stage.setY(event.getScreenY() + this.y);
        });

        btnSend.setOnMouseClicked(event -> sendSS());

        btnCancel.setOnMouseClicked(event -> {
            task.cancel();
            stage.close();
        });

        dropDownList.setOnAction(event -> {
            if (lblErrorText.isVisible())
                lblErrorText.setVisible(false);
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

        }
    }
}
