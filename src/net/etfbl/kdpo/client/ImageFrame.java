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
        this.name = name;   // ne znam zašto je ovo :D
                            // ime slike koje će se prikazati dole ispod. možda na hover
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

