package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * Created by H on 01/11/14.
 */

public class ReadObjects implements Runnable{
    public Object o;
    private String file;

    public ReadObjects(String file) throws ClassNotFoundException {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            FileInputStream filestream = new FileInputStream(file);
            ObjectInput input = new ObjectInputStream(filestream);
            o = input.readObject();
            input.close();
            filestream.close();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
