package net.etfbl.kdpo.client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    private ImageView imageView;
    private Stage stage;

    @FXML
    void initialize() {
        imageView = new ImageView();
        ImageViewPane ivp = new ImageViewPane(imageView);
        hBoxImageContainer.getChildren().add(ivp);
        ivp.prefHeightProperty().bind(hBoxImageContainer.heightProperty());
        ivp.prefWidthProperty().bind(hBoxImageContainer.widthProperty());
        btnCancel.setOnMouseClicked(event -> stage.close());
        new Thread(this::createImage).start();
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
}
