package net.etfbl.kdpo.client;

import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

	private static String users;

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
						File imageFile = receiveImage(socket, fromServer.split("#")[2]);
						MainController.screenshotAlbum.addImage(imageFile);
						Platform.runLater(() -> {
								// TODO prikaz obavještenje da je stigla slika , napraviti funciju?
								//	Main.showNotification("Stigla slika");
						});
					}
				} else if (fromServer.startsWith("USERS")) {
					users = fromServer;
				}
			}
		} catch (IOException e) {
			System.out.println("Services ended");

		} catch (NullPointerException e) {

		}

	}

	public void saveImage(Image image) {
	}

	public static String displayUsers() {
		out.println("USERS");
		while (users == null) {
		}
		String result = users;
		users = null;
		return result;
	}

	public File receiveImage(Socket socket, String userSender) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		//String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "Server" + File.separator + fileName;
		String pathString = new StringBuilder().append(System.getProperty("user.home")).append(File.separator).append("Khronos_DPO").append(File.separator).append("Screenshots").append(File.separator).append(userSender).append(" ").append(df.format(Calendar.getInstance().getTime())).toString();
		long lengthOfFile = Long.parseLong(in.readLine());
		System.out.println("duzina fajla" + lengthOfFile);

		long controlLength = 0, flag = 0;
		byte[] buffer = new byte[2 * 1024 * 1024];
		File file = new File(pathString);
		OutputStream fos = new FileOutputStream(file);
		InputStream is = socket.getInputStream();
		while (true) {
			controlLength = is.read(buffer);
			fos.write(buffer, 0, (int) controlLength);
			flag += controlLength;
			System.out.println("primljeno" + controlLength);
			if (flag >= lengthOfFile) {
				break;
			}
		}
		fos.close();
		return file;
	}

	public static void sendImage(Socket socket, File imageFile) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		//long length = Files.size(imagePath);
		long length = imageFile.length();
		out.println(length + "");
		System.out.println("duzina fajla za slanje: " + length);
		byte buffer[] = new byte[2 * 1024 * 1024];
		InputStream fis = new FileInputStream(imageFile);
		OutputStream os = socket.getOutputStream();
		int lengthThatIsRead = 0;
		long flag = 0;
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
		}
		while ((lengthThatIsRead = fis.read(buffer)) > 0) {
			System.out.println("saljem " + lengthThatIsRead);
			os.write(buffer, 0, lengthThatIsRead);
			flag += lengthThatIsRead;
			if (flag >= length) {
				break;
			}
		}
		fis.close();

	}

	/**
	 * Pokreće thread. Ova funkcija se poziva u mainu unutar drugog threada
	 * sout treba izbrisati kasnije
	 *
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
