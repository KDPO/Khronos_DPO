package net.etfbl.kdpo.client;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
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
    private boolean temporary;

    public VirtualAlbum(){
        this.images=FXCollections.observableArrayList();
    }

    public VirtualAlbum(String name, String description) {
        this.name = name;
        this.description = description;
        this.images = FXCollections.observableArrayList();
        temporary = false;
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
        if(obj instanceof VirtualAlbum ){
            return name.equals(((VirtualAlbum) obj).getName());
        }
        return false;
    }

    public void setImages(ObservableList<File> images){
        this.images.addAll(images);
    }

    //metode za serijalizaciju jer nije moguce serijalizovati ObservableList automatski
    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.writeObject(name);
        oos.writeObject(creationDate);
        oos.writeObject(description);
        oos.writeBoolean(temporary);
        if(images!=null) {
            oos.writeInt(images.size());
            for (File f : images) {
                oos.writeObject(f);
            }
        }
        else {
            oos.writeInt(0);
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException{
        try {
            images=FXCollections.observableArrayList();
            name = (String) ois.readObject();
            creationDate=(Date) ois.readObject();
            description=(String) ois.readObject();
            temporary=ois.readBoolean();
            int n=ois.readInt();
            for(int i=0;i<n;++i){
                images.add((File)ois.readObject());
            }
        }catch (ClassNotFoundException ex){
            //System.out.println("Greska u read");
        }
    }
}

