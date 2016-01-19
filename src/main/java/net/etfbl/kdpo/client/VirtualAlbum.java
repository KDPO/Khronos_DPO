package net.etfbl.kdpo.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Stijak on 24.12.2015.
 */

// kontejnerska klasa
public class VirtualAlbum implements Serializable {
    private static final String DATE_FORMAT = "dd.MM.yyyy.";
    private static DateFormat DATE;

    static {
        DATE = new SimpleDateFormat(DATE_FORMAT);
    }

    private String name;
    private String creationDate;
    private ObservableList<File> images;
    private String description;
    private boolean temporary;

    static final long serialVersionUID = 1L;

    public VirtualAlbum() {
        this.images = FXCollections.observableArrayList();
    }

    public VirtualAlbum(String name, String description) {
        this.creationDate = DATE.format(Calendar.getInstance().getTime());
        this.name = name;
        this.description = description;
        this.images = FXCollections.observableArrayList();
        this.temporary = false;
    }

    public VirtualAlbum(String name, String description, boolean temporary) {
        this(name, description);
        this.temporary = temporary;
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

    public boolean isTemporary() {
        return temporary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualAlbum) {
            return name.equals(((VirtualAlbum) obj).getName());
        }
        return false;
    }

    public void setImages(ObservableList<File> images) {
        this.images.addAll(images);
    }

    public void addImage(File image) {
        images.add(image);
    }

    public void setName(String name) {
        this.name = name;
    }

    //metode za serijalizaciju jer nije moguce serijalizovati ObservableList automatski
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(name);
        oos.writeObject(creationDate);
        oos.writeObject(description);
        oos.writeBoolean(temporary);
        ArrayList<File> list = new ArrayList<>(images);
        oos.writeObject(list);
    }

    private void readObject(ObjectInputStream ois) throws IOException {
        try {
            name = (String) ois.readObject();
            creationDate = (String) ois.readObject();
            description = (String) ois.readObject();
            temporary = ois.readBoolean();
            ArrayList<File> list = (ArrayList<File>) ois.readObject();
            for (int i = 0; i < list.size(); ++i) {
                if (!list.get(i).exists()) {
                    list.remove(i);
                }
            }
            images = FXCollections.observableArrayList(list);
        } catch (ClassNotFoundException ex) {
            //System.out.println("Greska u read");
        }
    }
}

