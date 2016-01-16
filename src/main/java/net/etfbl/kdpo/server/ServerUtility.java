package net.etfbl.kdpo.server;

import javafx.scene.image.Image;

import java.net.Socket;
import java.util.HashMap;

/**
 * Created by User on 12/15/2015.
 *
 * Klasa za sve statičke funkcije ili objekte koje možda budemo imali ili ne imali
 */
public class ServerUtility {

	public static String SERVER_IP = "localhost";
	public static int SERVER_PORT = 10000;
    public static HashMap<String, User> users;
    public static HashMap<String, Socket> username_socket;
    public static KeyGen keyGen;

    static {
        users = new HashMap<>();
        username_socket = new HashMap<>();
        keyGen = new KeyGen();
    }

    public static void saveData() {
    }

    public static void loadData() {
    }

    public static void saveImage(String from, String to) {
    }

    public static Image[] loadImage() {
        Image[] images = null;
        return images;
    }

    public static boolean registerUser(User user){
		if (checkIfUsernameIsAvailable(user.getUsername())) {
			users.put(user.getUsername(), user);
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkIfUsernameIsAvailable(String username) {
		return !users.containsKey(username);
	}

}
