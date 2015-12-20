package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 12/15/2015.
 */

// kontejnerska klasa
public class VirtualAlbum implements Serializable {
    private String name;
    private Date creationDate;
    private ArrayList<File> images;
    private String description;

    public VirtualAlbum(String name, String description) {
    }

    public VirtualAlbum(String name, String description, ArrayList<File> images) {
    }

    public void removeImage(File file) {
    }

    public void addImage(File file) {
    }

    public void addImages(ArrayList<File> files) {
    }

    public void setStatusMessage(String message) {

    }
}

