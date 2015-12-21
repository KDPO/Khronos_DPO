package net.etfbl.kdpo.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by Stijak on 12.12.2015..
 */

public class Main extends Application {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.getIcons().add(new Image("/images/khronos.png"));
        primaryStage.setTitle("Khronos DPO");
        Scene scene = new Scene(root, 600, 360);
        scene.getStylesheets().add("/css/main.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showNewWindow(String path) {
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
}
