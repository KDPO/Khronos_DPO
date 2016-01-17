package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.*;
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
							// TODO prikaz obavještenje da je stigla slika , napraviti funciju?
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

	/**
	 * Pokreće thread. Ova funkcija se poziva u mainu unutar drugog threada
	 * sout treba izbrisati kasnije
	 * @throws IOException ako se desila greška pri konekciji
	 */
	public static void startClientServicesThread() throws IOException {
		socket = new Socket(SERVER_IP, SERVER_PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		// get username
		String username = null;
		try {
			String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "License" + File.separator + "license.txt";
			File file = new File(path);
			if (file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				username = reader.readLine().split("<.*?>")[1];
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println("ACTIVATED#" + username);
		System.out.println(in.readLine());
		clientServicesThread = new ClientServicesThread();
		clientServicesThread.start();
	}
}
