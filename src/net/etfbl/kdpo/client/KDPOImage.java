package net.etfbl.kdpo.client;

import javafx.scene.image.Image;

/**
 * Created by User on 12/15/2015.
 *
 * ne sjećam se koja nam je ideja bila sa ovim
 */
public class KDPOImage extends Image {
    private String name;
    private String imagePath;

    public KDPOImage(String name, String path) {
        super("file://" + path);
        this.name = name;
    }
}
