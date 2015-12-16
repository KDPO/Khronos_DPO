package net.etfbl.kdpo.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Stijak on 16.12.2015..
 */
public class User {
    private String name;
    private String key;
    private HashSet<String> blockedUsersList;
    private boolean blocked;

    public User(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
