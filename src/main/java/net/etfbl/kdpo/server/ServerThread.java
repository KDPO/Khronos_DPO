package net.etfbl.kdpo.server;

import javafx.scene.image.Image;

import java.net.Socket;

/**
 * Created by Stijak on 12.12.2015..
 */
public class ServerThread extends Thread {

    private Socket socket;
    private User thisUser;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
    }

    public static void sendImageToUser(User user, String imageID){

    }

    public static void saveImage(Image image) {
        //malo je bezveze ovo pisati
        //jer nisu metode, već funkcionalnosti koje će obaljati run
    }
}
