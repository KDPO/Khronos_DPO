package net.etfbl.kdpo.server;

import java.net.ServerSocket;

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
    }
}
