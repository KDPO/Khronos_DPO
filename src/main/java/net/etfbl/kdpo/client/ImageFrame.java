package net.etfbl.kdpo.client;

import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Created by User on 12/15/2015.
 */
public class ImageFrame extends Pane {
    private Image image;
    private String name;
    private CheckBox checkBox;
    private ImageView imageView;


    public ImageFrame(String name, Image image) {
        this.name = name;   // ne znam zašto je ovo :D
                            // ime slike koje će se prikazati dole ispod. možda na hover
        // this.
    }


    public ImageFrame() {
        this.setBackground(new Background(new BackgroundFill(new Color(0.5, 1, 1, 0.5), null, null)));
        this.setPrefSize(200, 200);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

