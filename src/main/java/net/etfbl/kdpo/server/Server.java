package net.etfbl.kdpo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Stijak on 12.12.2015..
 */
public class Server {



	public static void main(String[] args){

		try {
			ServerSocket ss = new ServerSocket(ServerUtility.SERVER_PORT);
			ServerAcceptThread sat = new ServerAcceptThread(ss);
			sat.start();

			boolean notEnd = true;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Server started...");
			while(notEnd) {
				String command = in.readLine();
				if("get key".equals(command)) {
					System.out.println(ServerUtility.keyGen.getKey());
				}
				if("shut down".equals(command)) {
					ServerUtility.saveData();
					notEnd=false;
				}
				if(command.startsWith("generate keys")) {
					ServerUtility.keyGen.generateNewKeys(Integer.parseInt(command.split(" ")[2]));
				}
			}
			System.exit(0);
		} catch (IOException e) {
			//TODO something
		}
	}

}

