package net.etfbl.kdpo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by User on 12/15/2015.
 */

//Čeka na konekciju
public class ServerAcceptThread extends Thread {
    private ServerSocket ss;

    public ServerAcceptThread(ServerSocket ss) {
        this.ss = ss;
    }

    @Override
    public void run() {
        super.run();
        try {
            while(true) { // potreban pametniji uslov, serverUtilitiy možda
                Socket socket = ss.accept();
				(new ServerThread(socket)).start();
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
