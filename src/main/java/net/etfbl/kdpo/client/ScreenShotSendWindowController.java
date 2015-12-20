package net.etfbl.kdpo.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.etfbl.kdpo.server.User;

/**
 * Created by Stijak on 16.12.2015..
 */
public class ScreenShotSendWindowController {
    @FXML
    private ImageView imageView;

    @FXML
    private ListView<String> users;

    @FXML
    private Button btnSend;

    @FXML
    private Button btnCancel;

    @FXML
    void initialize() {
    }

    public void sendImage(Image image) {
    }

    public void getUsers() {
    }

    public void setStatusMessage(String message) {
    }
}
