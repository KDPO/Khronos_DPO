package net.etfbl.kdpo.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Stijak on 12.12.2015..
 */

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image("/images/khronos.png"));
        primaryStage.setTitle("Khronos DPO");
        //koristi se za onemogucenje mjenjanja pozadinskog prozora iz popup
        Main.primaryStage=primaryStage;
        //showActivationWindow(primaryStage);
        showMainWindow(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();//args);
    }

    public boolean checkConnection() {
        return false;
    }

    public boolean checkActivation() {
        return false;
    }

    private void showMainWindow(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root, 600, 360));
        stage.setMinWidth(600);
        stage.setMinHeight(360);
    }

    private void showActivationWindow(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/activation.fxml"));
        Parent root = loader.load();
        ActivationWindowController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root, 600, 400));
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
    }


}
