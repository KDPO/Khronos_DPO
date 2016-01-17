package net.etfbl.kdpo.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

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
        Main.primaryStage = primaryStage;
        if (checkActivation())
            showMainWindow(primaryStage);
        else if (showActivationWindow(new Stage()))
            showMainWindow(primaryStage);
        else Platform.exit();
    }

    public static void main(String[] args) {
        launch();//args);
    }

    private void showMainWindow(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root, 800, 480));
        stage.setMinWidth(600);
        stage.setMinHeight(360);
        stage.show();
		Thread startCST = new Thread(() -> {
			try {
				ClientServicesThread.startClientServicesThread();
				System.out.println("Services started");
			} catch (IOException e) {
				// ako nije uspjela konekcija
				e.printStackTrace();
			}
		});
		startCST.start();
    }

    private boolean showActivationWindow(Stage stage) throws Exception {
        stage.getIcons().add(new Image("/images/khronos.png"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/activation.fxml"));
        Parent root = loader.load();
        ActivationWindowController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root, 600, 400));
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
        return controller.isActivated();
    }

    public static void showNotification(String message) {
        //dodati controlsfx-8.40.10.jar u projekat (pitajte kako :D )
        try {
            Notifications notifications = Notifications.create();
            notifications.onAction(event -> {
                //MainController.mainController.showImageViewController(MainController.screenshotAlbum.getImages(), 0);
                System.out.println("You clicked me!");
            });
            notifications.hideAfter(Duration.seconds(10)).title("Khronos DPO").text(message).position(Pos.BOTTOM_RIGHT).darkStyle().showInformation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // vraća hash funkciju generisanu pomoću username, computer name i key
    public static String getHashCode(String username, String key) {
        try {
            String string = username + System.getProperty("user.name") + key;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(string.getBytes(Charset.forName("UTF-8")));
            StringBuffer sb = new StringBuffer();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // provjerava aktivaciju tako što koristi zapamćeni username i key i hash funkciju
    public static boolean checkActivation() {
        try {
            String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "License" + File.separator + "license.txt";
            File file = new File(path);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String username = reader.readLine().split("<.*?>")[1];
                String key = reader.readLine().split("<.*?>")[1];
                String hash = reader.readLine().split("<.*?>")[1];
                reader.close();
                return hash.equals(Main.getHashCode(username, key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
