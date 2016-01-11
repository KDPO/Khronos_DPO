package net.etfbl.kdpo.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Stijak on 12.12.2015..
 */
public class Server {

	public static String SERVER_IP = "localhos";
	public static int SERVER_PORT = 10000;

	public static void main(String[] args){

		try {
			ServerSocket ss = new ServerSocket(SERVER_PORT);
			ServerAcceptThread sat = new ServerAcceptThread(ss);
			sat.start();
		} catch (IOException e) {
			//TO DO something
		}
	}

}
