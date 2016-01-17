package net.etfbl.kdpo.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Stijak on 16.12.2015..
 */
public class User implements Serializable {
    private String username;
    private String key;
    private HashSet<String> blockedUsersList;
    private boolean allBlocked;
    private ArrayList<String> imageQueue;

	{
		blockedUsersList = new HashSet<>();
		imageQueue = new ArrayList<>();
	}

    public User(String username, String key) {
        this.username = username;
        this.key = key;
    }

    public String getUsername(){
        return username;
    }

    public boolean areBlockedAll() {
        return allBlocked;
    }

    public boolean isUserBlocked(User user) {
        return  isUserBlocked(user.getUsername());
    }

    public boolean isUserBlocked(String username) {
        return allBlocked || blockedUsersList.contains(username);
    }

    public void blockUser(String userName) {
		blockedUsersList.add(userName);
    }

    public void unblockUser(String userName) {
		blockedUsersList.remove(userName);
    }

    public void blockAll() {
		allBlocked = true;
    }

    public void unblockAll() {
		allBlocked = false;
    }

	/**
	 *  Dodaje identifikator slike u korisnikov red slika, koje će mu biti poslane nakon što se prijavi
	 *  i zatim obrisane
	 * @param ImageID prijedlog "userSender UserReceiver yyyyMMddhhmmss"
	 * @return true ako je uspješno izvršeno
	 */
    public boolean addImageToImageQueue(String ImageID){
		return imageQueue.add(ImageID);
    }

	public ArrayList<String> getImageQueue() {
		return imageQueue;
	}

	public String getKey() {
		return key;
    }
}
