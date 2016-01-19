package net.etfbl.kdpo.server;

import javafx.scene.image.Image;
import net.etfbl.kdpo.client.VirtualAlbum;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by User on 12/15/2015.
 * <p>
 * Klasa za sve statičke funkcije ili objekte koje možda budemo imali ili ne imali
 */
public class ServerUtility {

	public static String SERVER_IP = "localhost";
	public static int SERVER_PORT = 10000;
	public static HashMap<String, User> users;
	public static HashMap<String, Socket> username_socket;
	public static KeyGen keyGen;

	static {
		username_socket = new HashMap<>();
		loadData();
	}

	public static void saveData() {

		String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "Server";
		File outputPath = new File(path);
		if (!outputPath.exists()) {
			outputPath.mkdir();
		}
		String file = path + File.separator + "server.ser";
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(users);
			oos.writeObject(keyGen);
			oos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void loadData() {
		String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "Server";
		File folderPath = new File(path);
		if (!folderPath.exists()) {
			folderPath.mkdir();
		}
		String filePath = path + File.separator + "server.ser";
		File file = new File(filePath);
		Path pathFilePath = Paths.get(filePath);
		//if(file.exists()) {
		if(Files.exists(pathFilePath)) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				users = (HashMap<String, User>) ois.readObject();
				keyGen = (KeyGen) ois.readObject();
				ois.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			users = new HashMap<>();
			keyGen = new KeyGen();
		}
	}

	public static void saveImage(String from, String to) {
	}

	public static Image[] loadImage() {
		Image[] images = null;
		return images;
	}

	public static boolean registerUser(User user) {
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

	public static String getUsers(String user){
		Set<String> usersSet=users.keySet();
		String result="";
		for(String s:usersSet){
			if(!s.equals(user)) {
				result += s + "#";
			}
		}
		if(result.equals("")){
			return null;
		}
		return result.substring(0, result.length()-1);
	}

}
