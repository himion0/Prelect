package utils;

import java.io.*;

/**
 * Created by H on 01/11/14.
 */
public final class SaveObjects implements Runnable {
    Object o;
    String savelocation;

    public SaveObjects(Object o, String s) throws IOException {
        savelocation = s;
        this.o=o;
    }

    @Override
    public void run() {
        //Creates file if it doesn't exist
        File f = new File(savelocation);
        if(!f.exists()) try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(savelocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(o);
            os.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

