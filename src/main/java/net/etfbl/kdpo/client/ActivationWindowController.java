package net.etfbl.kdpo.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * Created by Stijak on 16.12.2015..
 */
public class ActivationWindowController {
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtKey1;
    @FXML
    private TextField txtKey2;
    @FXML
    private TextField txtKey3;
    @FXML
    private TextField txtKey4;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblKey;
    @FXML
    private Label lblError;
    @FXML
    private Label lblMinus1;
    @FXML
    private Label lblMinus2;
    @FXML
    private Label lblMinus3;
    @FXML
    private Label lblKDPO;
    @FXML
    private Label lblActivation;
    @FXML
    private ImageView icon;
    @FXML
    private Button btnOK;
    @FXML
    private Button btnCancel;

    @FXML
    void initialize() {
    }

    public void setConnectionTrue() {

    }

    public void sendActivationData() {

    }

    public void showError() {

    }
 
}
