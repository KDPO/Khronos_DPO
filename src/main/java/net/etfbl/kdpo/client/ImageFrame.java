package net.etfbl.kdpo.client;

import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Stijak on 19.12.2015..
 */
public class ImageFrame extends AnchorPane {
    private ImageView imageView;
    private Label label;
    private CheckBox checkBox;
    private File file;

    public ImageFrame(File file) {
        this.file = file;
        this.label = new Label(file.getName().substring(0, file.getName().indexOf(".")));
        try {
            FileInputStream iStream = new FileInputStream(file);
            Image image = new Image(iStream, 500, 500, true, true);
            imageView = new ImageView(image);
            iStream.close();
            image = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkBox = new CheckBox();
        createImageFrame();
    }

    private void createImageFrame() {
        imageView.setPreserveRatio(true);
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER);
        imageView.setFitHeight(210);
        label.setFont(new Font(16));
        label.setTextFill(Paint.valueOf("ffffff"));
        label.setAlignment(Pos.CENTER);
        label.setBackground(new Background(new BackgroundImage(new Image("/images/pozadina.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        label.setVisible(false);
        checkBox.setVisible(true);
        this.setAnchor(checkBox, -1, 5, 5, -1);
        this.setAnchor(label, 0, -1, 0, 0);
        this.setAnchor(hBox, 0, 0, 0, 0);
        this.getChildren().addAll(hBox, checkBox, label);
        new Thread(this::setEffect).start();
        this.getStyleClass().add("image-frame");
    }

    private void setAnchor(Node node, double left, double top, double right, double bottom) {
        if (left >= 0)
            AnchorPane.setLeftAnchor(node, left);
        if (top >= 0)
            AnchorPane.setTopAnchor(node, top);
        if (right >= 0)
            AnchorPane.setRightAnchor(node, right);
        if (bottom >= 0)
            AnchorPane.setBottomAnchor(node, bottom);
    }

    public void setEffect() {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
            else
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
        });

        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
            else
                this.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
        });

        this.checkBox.visibleProperty().bind(this.hoverProperty().or(this.checkBox.selectedProperty()));
        this.label.visibleProperty().bind(this.hoverProperty().or(this.checkBox.selectedProperty()));
    }

    public File getFile() {
        return file;
    }

    public Image getImage() {
        return imageView.getImage();
    }

    public String getName() {
        return label.getText();
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setImage(File file) {
        this.imageView.setImage(new Image(file.getAbsolutePath()));
    }

    @Override
    public boolean equals(Object o) {
        return this.file.equals(o);
    }

}