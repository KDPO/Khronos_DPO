package net.etfbl.kdpo.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by User on 12/15/2015.
 */
public class KeyGen implements Serializable {
    private HashMap<String, Boolean> keyList;

    private static final int KEY_LENGTH = 16;

    public KeyGen() {
        keyList = new HashMap<>();
    }

    //provjerava da li je Key zauzet i validan
    public boolean checkKey(String key) {
        boolean valid = false;
        if (keyList.containsKey(key)) {
            if (keyList.get(key).equals(false)) {
                valid = true;
            }
        }
        return valid;
    }

    //metoda koja postavlja kljuc za koristen i vraca da li je uspjesno izvrsena
    public boolean setKeyAsUsed(String key) {
        boolean flag = false;
        if (keyList.containsKey(key)) {
            if (keyList.get(key).equals(false)) {
                keyList.replace(key, true);
                flag = true;
            }
        }
        return flag;
    }

    //metoda koja generise i ubacuje u listu kljuceva
    public void generateNewKeys(int numberOfKeys) {
        for (int i = 0; i < numberOfKeys; ) {
            String key = generateKey();
            if (!keyList.containsKey(key)) {
                keyList.put(key, false);
                ++i;
            }
        }
    }

    //metoda koja generise jedan kljuc
    private String generateKey() {
        char[] key = new char[KEY_LENGTH];
        Random random = new Random();

        key[0] = (char) (random.nextInt(4) + 2 + '0');
        key[1] = (char) (random.nextInt(9) + '0');
        if(random.nextBoolean()) {
            key[2] = (char) (random.nextInt(26) + 'A');
        }else{
            key[2] = (char) (random.nextInt(26) + 'a');
        }
        if(random.nextBoolean()) {
            key[3] = (char) (random.nextInt(26) + 'A');
        }else{
            key[3] = (char) (random.nextInt(26) + 'a');
        }

        key[4] = (char) (random.nextInt(9) + '0');
        if(random.nextBoolean()) {
            key[5] = (char) (random.nextInt(26) + 'A');
        }else{
            key[5] = (char) (random.nextInt(26) + 'a');
        }
        if(random.nextBoolean()) {
            key[6] = (char) (random.nextInt(26) + 'A');
        }else{
            key[6] = (char) (random.nextInt(26) + 'a');
        }
        key[7] = (char) (random.nextInt(3) + 6 + '0');

        key[8] = (char) (random.nextInt(9) + '0');
        if(random.nextBoolean()) {
            key[9] = (char) (random.nextInt(26) + 'A');
        }else{
            key[9] = (char) (random.nextInt(26) + 'a');
        }
        key[10] = (char) (random.nextInt(4) + '0');
        if(random.nextBoolean()) {
            key[11] = (char) (random.nextInt(26) + 'A');
        }else{
            key[11] = (char) (random.nextInt(26) + 'a');
        }

        key[12] = (char) (random.nextInt(5) + 4 + '0');
        key[13] = (char) (random.nextInt(2) + '0');
        key[14] = (char) (random.nextInt(9) + '0');
        if(random.nextBoolean()) {
            key[15] = (char) (random.nextInt(26) + 'A');
        }else{
            key[15] = (char) (random.nextInt(26) + 'a');
        }

        return String.valueOf(key);
    }

    //metoda koja vraca jedan slobodan kljuc iz liste kljuceva
    public String getKey() {
        String key = null;
        for (Map.Entry<String, Boolean> entry : keyList.entrySet()) {
            if (entry.getValue().equals(false)) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }
}