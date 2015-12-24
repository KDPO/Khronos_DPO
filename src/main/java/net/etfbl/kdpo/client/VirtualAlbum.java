package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Stijak on 24.12.2015.
 */

// kontejnerska klasa
public class VirtualAlbum implements Serializable {
    private String name;
    private Date creationDate;
    private ObservableList<File> images;
    private String description;

    public VirtualAlbum(String name, String description) {
        this.name = name;
        this.description = description;
        this.images = FXCollections.observableArrayList();
    }

    public String getName() {
        return name;
    }

    public ObservableList<File> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return this.name;
    }
}

