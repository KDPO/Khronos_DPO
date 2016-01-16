package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by User on 12/15/2015.
 */
public class ClientServicesThread extends Thread {
    public static Socket socket;
    public static BufferedReader in; // staticki kako se ne bi pravili novi objekti već koristili postojeći
    public static PrintWriter out; // ukoliko ih ima
    public static String SERVER_IP = "localhost";
    public static int SERVER_PORT = 10000;
    public static ClientServicesThread clientServicesThread; // zlu ne trebalo

    // ako je konektovan, čeka server da mu pošalje sliku
    // ako nije konektovan, i ne postoji
    public void run() {
        super.run();
        try {
            // beskonačna petlja, prekida se prilikom pucanja konekcije
            // možda ubacimo u settings dugme connect, disconnect, connect automaticaly i slično
            while (true) {
                String fromServer = in.readLine();

                if (fromServer.startsWith("IMAGE")) {
                    if (fromServer.split("#")[1].equals("RECEIVE")) {
                        receiveImage(fromServer.split("#")[2], socket);
                        Platform.runLater(() -> {
                            // TODO prikaz obavještenje da je stigla slika
                        });
                    }
                }
            }
        } catch (IOException e) {

        } catch (NullPointerException e) {

        }

    }

    public void saveImage(Image image) {
    }

    public void displayUsers() {
    }

    public static void sendImage(File image, Socket socket) {
        // koristiti object , ili preko file?
    }

    public static void receiveImage(String imageName, Socket socket) {

    }
}
