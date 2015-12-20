package net.etfbl.kdpo.server;

import org.omg.PortableInterceptor.ServerRequestInfo;

import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Created by User on 12/15/2015.
 */
public class KeyGen {
    private HashMap<String, Boolean> keyList;


    public boolean checkKey() {
        return false;
    }

    public boolean setKeyAsUsed() {
        return false;
    }

    public void generateNewKeys(int numberOfKeys) {
    }

    public String getKey() {
        return null;
    }
}
