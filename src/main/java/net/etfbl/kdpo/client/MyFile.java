/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.etfbl.kdpo.client;
import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Vladan
 */
public class MyFile extends File{
    public MyFile(String f){
        super(f);
    }
    @Override
    public String toString(){
        if(!super.getName().equals("")){
            return super.getName();
        }
        return FileSystemView.getFileSystemView().getSystemDisplayName(this);
        //return super.toString();
    }
}