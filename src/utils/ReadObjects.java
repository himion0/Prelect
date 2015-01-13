package utils;

import MyDM.Data;

import java.io.*;

/**
 * Created by H on 01/11/14.
 */
public class ReadObjects implements Runnable {
    public Object o;
    String readlocation;

    public ReadObjects(String file){
        File f = new File(file);
        if(!f.exists()) try {
            new SaveObjects(new Data("keywords.txt"),"var/Data.bin");
        } catch (IOException e) {
            e.printStackTrace();
        }

        readlocation = file;
        System.out.println("Reading Object File: "+readlocation);
        try {
            FileInputStream filestream = new FileInputStream(readlocation);
            ObjectInput input = new ObjectInputStream(filestream);
            o = input.readObject();
            input.close();
            filestream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
