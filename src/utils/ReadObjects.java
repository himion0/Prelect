package utils;

import MyDM.DataController;
import twitter4j.Status;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by H on 01/11/14.
 */

public class ReadObjects implements Runnable {
    public ArrayList<Status> o;

    public ReadObjects(String file) throws ClassNotFoundException {
        File f = new File(file);
        if(!f.exists()) try {
            new SaveObjects(new DataController("keywords.txt"),"var/Data.bin");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Reading Object File: "+file);
        try {
            FileInputStream filestream = new FileInputStream(file);
            ObjectInput input = new ObjectInputStream(filestream);
            o = (ArrayList<Status>) input.readObject();
            input.close();
            filestream.close();
            System.out.println("Read object of type: "+o.getClass().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
