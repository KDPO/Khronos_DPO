package net.etfbl.kdpo.client;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Created by Stijak on 17.12.2015..
 */
public class ImageFrame extends AnchorPane {
    private String name;
    private CheckBox checkBox;
    private ImageView imageView;
    private Label label;
    private Image image;

    public ImageFrame(Image image, String name) {
        this.image = image;
        this.name = name;
        createImageFrame();
    }

    public ImageFrame(Image image, Double height, String name) {
        this.image = image;
        this.name = name;
        createImageFrame();
    }

    private void createImageFrame() {
        imageView = new ImageView(this.image);
        checkBox = new CheckBox();
        label = new Label(this.name);
        HBox hBox = new HBox(imageView);

        AnchorPane.setTopAnchor(checkBox, 5.0);
        AnchorPane.setRightAnchor(checkBox, 5.0);

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);

        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);

        imageView.setPreserveRatio(true);
        hBox.alignmentProperty().setValue(Pos.CENTER);
        //imageView.fitHeightProperty().bind(hBox.heightProperty());
        label.alignmentProperty().setValue(Pos.CENTER);
        label.textFillProperty().setValue(Paint.valueOf("#ffffff"));
        label.setPrefHeight(50);
        label.setFont(new Font("Verdana", 20));
        label.setBackground(new Background(new BackgroundImage(new Image("/images/pozadina.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        label.setVisible(false);
        checkBox.setVisible(false);
        imageView.setFitHeight(200);
        this.getChildren().addAll(hBox, checkBox, label);
        setEffect();
    }

    public void setEffect() {
        this.setOnMouseEntered((MouseEvent) -> {
            checkBox.setVisible(true);
            label.setVisible(true);
        });

        this.setOnMouseExited((MouseEvent) -> {
            checkBox.setVisible(false);
            label.setVisible(false);
        });
    }

    public String getName() {
        return name;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public Image getImage() {
        return image;
    }

    public Label getLabel() {
        return label;
    }
}
