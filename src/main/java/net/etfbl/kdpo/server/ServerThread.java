package net.etfbl.kdpo.server;

import javafx.scene.image.Image;

import java.io.*;
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
		try {
			// loggedIn označava da je korisnik prihvaćen
			boolean loggedIn = false;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);

			String fromClient;
			while(!loggedIn) {
				fromClient = in.readLine();
				// očekivani oblik poruke bez space, | znači "ili"
				// TOACTIVATE # username # key
				// provjera autentičnosti
				if (fromClient.startsWith("TOACTIVATE")) {
					boolean isKeyValid = ServerUtility.keyGen.checkKey(fromClient.split("#")[2]);

					// TODO provjera postoji li korisnicko ime
					if (isKeyValid) {
						out.println("ACTIVATION#OK");
						thisUser = new User(fromClient.split("#")[1], fromClient.split("#")[2]);

						loggedIn = true;
					} else {
						out.println("ACTIVATION#NOK");
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

			// opsluživanje klijenta
			// realno, jedina stvar je da prima sliku, i onda proslijedi dalje, ili sačuva
			// a nije, treba i da omogućuje blokadu korisnika, to će se provjeravati prilikom prosljeđivanja slike
			while(loggedIn) {
				fromClient = in.readLine();
			}

		} catch (IOException e) {
			// ovo znači da je pukla veza, server  treba da izbaci klijenta, iz aktivnih
			e.printStackTrace();
			setUserAsActive();
		}
	}



	/** postavlja u serverske tabele da je korisnik aktivan, printwriter i još štošta, koristi thisUser objekat
	 */
	private void setUserAsActive() {

	}

	/** briše što je uradila metoda setUserAsInactive, koristi thisUser objekat
	 */
	private void setUserAsInactive(){
		// pukla veza prije aktivacije, nema šta da se briše
		if(thisUser != null) {

		}
	}

	public static void sendImageToUser(User user, String imageID) {

	}

	public static void saveImage(Image image) {
		//malo je bezveze ovo pisati
		//jer nisu metode, već funkcionalnosti koje će obaljati run
	}
}

/* Ključne riječi upotrebbljene u klijent server komunikaciji,
	za sad

	klijent -> server
	TOACTIVATE # username # key - nalazi se na početku poruke kada se klijent aktivira
	ACTIVATED # username - klijent je aktiviran, i traži konekciju sa serverom
	IMAGE#RECEIVE#image name#user receiver - klijent će poslati serveru sliku
	CONTOL#BLOCKUSER#username
	CONTROL#UNBLOCKUSER#username
	CONTROL#BLOCKALL

	server -> klijent
	ACTIVATION#OK
	ACTIVATION#NOK - da li je aktivacija prošla ili ne
	IMAGE#RECEIVE#image name#user sender - server će poslati klijentu sliku
 */