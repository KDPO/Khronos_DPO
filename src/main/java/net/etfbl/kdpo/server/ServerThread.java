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
				}
			}

			// TODO provjera da li mu treba poslati slike

			// opsluživanje klijenta
			// realno, jedina stvar je da prima sliku, i onda proslijedi dalje, ili sačuva
			// a nije, treba i da omogućuje blokadu korisnika, to će se provjeravati prilikom prosljeđivanja slike
			String[] fromClientArray;
			while (loggedIn) {
				fromClient = in.readLine();

				if(fromClient.startsWith("CONTROL")) {
					fromClientArray = fromClient.split("#");
					if("BLOCKUSER".equals(fromClientArray[1])){
						thisUser.blockUser(fromClientArray[2]);
					}
					if("UNBLOCKUSER".equals(fromClientArray[1])){
						thisUser.unblockUser(fromClientArray[2]);
					}
					if("BLOCKALL".equals(fromClientArray[1])){
						thisUser.blockAll();
					}
					if("UNBLOCKALL".equals(fromClientArray[1])){
						thisUser.unblockAll();
					}
				}
			}

		} catch (IOException e) {
			// ovo znači da je pukla veza, server  treba da izbaci klijenta, iz aktivnih
			e.printStackTrace();
			setUserAsInactive();
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

	server -> klijent
	ACTIVATION#OK
	ACTIVATION#NOK#TIP#Poruka - da li je aktivacija prošla ili ne TIP = 1 invalid key ; TIP = 2 username taken
	IMAGE#RECEIVE#image name#user sender - server će poslati klijentu sliku
 */