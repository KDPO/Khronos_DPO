package net.etfbl.kdpo.client;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.HashMap;

/**
 * @author Vladan
 */
public class MyFile extends File {
    private static FileSystemView fsView;
    private static HashMap<String, String> drivesName;

    static {
        fsView = FileSystemView.getFileSystemView();
        drivesName = new HashMap<>();
        for (File file : File.listRoots())
            if (file.isDirectory())
                drivesName.put(file.toString(), fsView.getSystemDisplayName(file));
    }

    public MyFile(String f) {
        super(f);
    }

    public MyFile(String parent, String string) {
        super(parent, string);
    }

    @Override
    public String toString() {
        if (!super.getName().equals("")) {
            return super.getName();
        }
        //zbog ovoga je sporo
        //return fsView.getSystemDisplayName(this);
        return drivesName.get(super.toString());
    }
}