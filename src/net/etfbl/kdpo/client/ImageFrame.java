package net.etfbl.kdpo.client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by User on 12/15/2015.
 */
public class ImageFrame extends ImageView {
    private Image image;
    private String name;

    public ImageFrame(String name, Image image) {
        super(image);
        this.name = name;  // ne znam zašto je ovo :D
    }

}
