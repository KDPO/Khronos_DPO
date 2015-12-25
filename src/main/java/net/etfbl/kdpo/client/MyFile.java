package net.etfbl.kdpo.client;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * @author Vladan
 */
public class MyFile extends File {
    private static FileSystemView fsView;

    static {
        fsView = FileSystemView.getFileSystemView();
    }

    public MyFile(String f) {
        super(f);
    }

    @Override
    public String toString() {
        if (!super.getName().equals("")) {
            return super.getName();
        }
        return fsView.getSystemDisplayName(this);
    }
}