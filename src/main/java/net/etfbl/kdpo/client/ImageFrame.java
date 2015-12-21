package net.etfbl.kdpo.client;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by Stijak on 19.12.2015..
 */
public class ImageFrame extends AnchorPane {
    private ImageView imageView;
    private Label label;
    private CheckBox checkBox;

    public ImageFrame(File file) {
        this.label = new Label(file.getName());
        this.imageView = new ImageView(new Image(file.getPath()));
        checkBox = new CheckBox();
        createImageFrame();
    }

    private void createImageFrame() {
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER);
        imageView.setPreserveRatio(true);
        //imageView.fitHeightProperty().bind(hBox.heightProperty());
        label.setFont(new Font(16));
        label.setTextFill(Paint.valueOf("ffffff"));
        label.setAlignment(Pos.CENTER);
        label.setBackground(new Background(new BackgroundImage(new Image("/images/pozadina.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        this.setAnchor(checkBox, -1, 5, 5, -1);
        this.setAnchor(label, 0, -1, 0, 0);
        this.setAnchor(hBox, 0, 0, 0, 0);
        imageView.setFitHeight(210);
        this.getChildren().addAll(hBox, checkBox, label);
        setEffect();
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
        this.setOnMouseEntered((MouseEvent) -> {
            this.checkBox.setVisible(true);
            this.label.setVisible(true);
            if (!checkBox.isSelected())
                this.setStyle("-fx-border-color: #2c2c2c;");
        });

        this.setOnMouseExited((MouseEvent) -> {
            if (!checkBox.isSelected()) {
                this.checkBox.setVisible(false);
                this.label.setVisible(false);
                this.setStyle("-fx-border-color: transparent;");
            }
        });

        checkBox.setOnMouseClicked((MouseEvent) -> {
            if (checkBox.isSelected())
                this.setStyle("-fx-border-color: #f98026;");
            if (!checkBox.isSelected())
                this.setStyle("-fx-border-color: #2c2c2c;");
        });
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
}