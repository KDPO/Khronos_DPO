package net.etfbl.kdpo.server;

import javafx.scene.image.Image;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
		try {
			// loggedIn označava da je korisnik prihvaćen
			boolean loggedIn = false;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			String fromClient;
			while (!loggedIn) {
				fromClient = in.readLine();
				// očekivani oblik poruke bez space, | znači "ili"
				// TOACTIVATE # username # key
				// provjera autentičnosti
				if (fromClient.startsWith("TOACTIVATE")) {
					boolean isKeyValid = ServerUtility.keyGen.checkKey(fromClient.split("#")[2]);
					if (isKeyValid) {
						thisUser = new User(fromClient.split("#")[1], fromClient.split("#")[2]);
						boolean isActivationSuccessful = ServerUtility.registerUser(thisUser);
						if (isActivationSuccessful) {
							out.println("ACTIVATION#OK");
							setUserAsActive();
							//postavlja kljuc na iskoristen
							ServerUtility.keyGen.setKeyAsUsed(fromClient.split("#")[2]);
							loggedIn = true;
						} else {
							out.println("ACTIVATION#NOK#NOK#OK");
							thisUser = null; // not necessary
						}
					} else {
						if (ServerUtility.checkIfUsernameIsAvailable(fromClient.split("#")[1])) {
							out.println("ACTIVATION#NOK#OK#NOK");
						} else {
							out.println("ACTIVATION#NOK#NOK#NOK");
						}
						thisUser = null;
					}
				}
				// očekivani oblik poruke
				// ACTIVATED # username
				//  postavljanje korisnika kao aktivnog i priprema za rad
				if (fromClient.startsWith("ACTIVATED")) {
					thisUser = ServerUtility.users.get(fromClient.split("#")[1]);
					setUserAsActive();
					loggedIn = true;
					//out.println("ACTIVATION#OK");
				}
				else if(fromClient.equals("EXIT")){
					//setUserAsInactive();
					out.close();
					in.close();
					socket.close();
				}
			}
			// TODO provjera da li mu treba poslati slike
			if(!thisUser.getImageQueue().isEmpty()) {
				for(String path : thisUser.getImageQueue()) {
					try {
						File file = new File(path);
						String name = file.getName();
						String from = name.split(" ")[0];
						boolean filef = file.exists();
						out.println("SCREENSHOT#" + from);
						try {
							Thread.sleep(4000);
						} catch (Exception e) {}
						sendImage(socket, file);
						//imageFile.delete();
						Files.delete(Paths.get(file.getAbsolutePath()));
						thisUser.getImageQueue().remove(path);

					} catch (Exception ex) {
						// greška u pisanju fajla čitanju slanju negdje
						ex.printStackTrace();
					}
				}
			}

			// opsluživanje klijenta
			// realno, jedina stvar je da prima sliku, i onda proslijedi dalje, ili sačuva
			// a nije, treba i da omogućuje blokadu korisnika, to će se provjeravati prilikom prosljeđivanja slike
			String[] fromClientArray;
			while (loggedIn) {
				fromClient = in.readLine();
				System.out.println(fromClient);
				if (fromClient.startsWith("CONTROL")) {
					fromClientArray = fromClient.split("#");
					if ("BLOCKUSER".equals(fromClientArray[1])) {
						thisUser.blockUser(fromClientArray[2]);
					}
					if ("UNBLOCKUSER".equals(fromClientArray[1])) {
						thisUser.unblockUser(fromClientArray[2]);
					}
					if ("BLOCKALL".equals(fromClientArray[1])) {
						thisUser.blockAll();
					}
					if ("UNBLOCKALL".equals(fromClientArray[1])) {
						thisUser.unblockAll();
					}
				}
				if (fromClient.startsWith("SCREENSHOT")) {
					File imageFile = receiveImage(socket, thisUser.getUsername(), fromClient.split("#")[1]);
					if (ServerUtility.username_socket.containsKey(fromClient.split("#")[1])) {
						try {
							(new PrintWriter(new OutputStreamWriter(ServerUtility.username_socket.get(fromClient.split("#")[1]).getOutputStream()),true)).println("SCREENSHOT#" + thisUser.getUsername());
							try {
								Thread.sleep(2000);
							} catch (Exception e) {}
							sendImage(ServerUtility.username_socket.get(fromClient.split("#")[1]), imageFile);
							//imageFile.delete();
							Files.delete(Paths.get(imageFile.getAbsolutePath()));
						} catch (Exception ex) {
							// greška u pisanju fajla čitanju slanju negdje
							ex.printStackTrace();
							ServerUtility.users.get(fromClient.split("#")[1]).addImageToImageQueue(imageFile.getPath());
						}
					} else {
						ServerUtility.users.get(fromClient.split("#")[1]).addImageToImageQueue(imageFile.getPath());
					}
				} else if (fromClient.equals("USERS")) {
					//vraca listu korisnika kojima je moguce poslati SS, nije moguce sebi poslati SS
					String users = ServerUtility.getUsers(thisUser.getUsername());
					if (users != null) {
						out.println("USERS#" + users);
					} else {
						out.println("USERS");
					}
				}
				else if(fromClient.equals("EXIT")){
					setUserAsInactive();
					loggedIn=false;
					out.close();
					in.close();
					socket.close();
				}
			}

		} catch (IOException e) {
			// ovo znači da je pukla veza, server  treba da izbaci klijenta, iz aktivnih
			e.printStackTrace();
			setUserAsInactive();
			//asdf
		} catch (Exception e) {

		}
	}


	/**
	 * postavlja u serverske tabele da je korisnik aktivan, printwriter i još štošta, koristi thisUser objekat
	 */

	private void setUserAsActive() {
		ServerUtility.username_socket.put(thisUser.getUsername(), socket);
	}

	/**
	 * briše što je uradila metoda setUserAsInactive, koristi thisUser objekat
	 */
	private void setUserAsInactive() {
		// pukla veza prije aktivacije, nema šta da se briše
		if (thisUser != null) {
			ServerUtility.username_socket.remove(thisUser.getUsername());
		}
	}

	public File receiveImage(Socket socket, String userSender, String userReceiver) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		//String path = System.getProperty("user.home") + File.separator + "Khronos_DPO" + File.separator + "Server" + File.separator + fileName;
		String pathString = new StringBuilder().append(System.getProperty("user.home")).append(File.separator).append("Khronos_DPO").append(File.separator).append("Server").append(File.separator).append(userSender).append(" ").append(userReceiver).append(" ").append(df.format(Calendar.getInstance().getTime())).toString();
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


	public void sendImage(Socket socket, File imageFile) throws IOException {
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

	public static void sendImageToUser(User user, String imageID) {

	}

	public static void saveImage(Image image) {
		//malo je bezveze ovo pisati
		//jer nisu metode, već funkcionalnosti koje će obaljati run
	}

	public static void saveImage(String userSender, String userReceiver, String imageName, byte[] data) throws IOException {
		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		String pathString = new StringBuilder().append(System.getProperty("user.home")).append(File.separator).append("Khronos_DPO").append(File.separator).append("Server").append(File.separator).append(userSender).append(" ").append(userReceiver).append(" ").append(imageName).append(" ").append(df.format(Calendar.getInstance().getTime())).toString();
		//File outputFile = new File(pathString);
		//outputFile.createNewFile();
		//OutputStream os = new FileOutputStream(outputFile);
		//os.write(data);

		// pokušaj sa Path
		Path path = Paths.get(pathString);
		Files.write(path, data, StandardOpenOption.CREATE_NEW);
	}
}

/* Ključne riječi upotrebbljene u klijent server komunikaciji,
	za sad

	klijent -> server
	TOACTIVATE # username # key - nalazi se na početku poruke kada se klijent aktivira
	ACTIVATED # username - klijent je aktiviran, i traži konekciju sa serverom
	IMAGE#RECEIVE#image name#user receiver - klijent će poslati serveru sliku
	CONTROL#BLOCKUSER#username
	CONTROL#UNBLOCKUSER#username
	CONTROL#BLOCKALL
	CONTROL#UNBLOCKALL
	USERS - korisnik trazi listu korisnika kojima moze slati sliku

	server -> klijent
	ACTIVATION#OK
	ACTIVATION#NOK#TIP#Poruka - da li je aktivacija prošla ili ne TIP = 1 invalid key ; TIP = 2 username taken
	IMAGE#RECEIVE#image name#user sender - server će poslati klijentu sliku
	USERS#user1#user2...
 */