package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

//This class deals with TextFiles to be used by the program
public class TextFile {
    private PrintWriter writer;
    private final String filename;
    public ArrayList<String> lines;
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    //Loads the specified textfile. If the readlocation doesn't exist, it will
    //create it
    public TextFile(String filename) throws IOException {
        File file = new File(filename);
        this.filename = filename;
        if(file.exists()&&!file.isDirectory()){
            readFile();
        } else {
            file.createNewFile();
        }

    }

    @Override
    public String toString(){
        return filename;
    }

    /*
    * Reads each line from the text file and adds it to the lines ArrayList
    *
    * TODO:
    * Implement ignoring comment character (e.g. '#')
    * */
    void readFile() throws IOException {
        System.out.println("Reading File: "+filename);
        Path path = Paths.get(filename);
        lines = new ArrayList<>();
        Scanner scanner =  new Scanner(path, ENCODING.name());
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            lines.add(s);
        }

    }


    public void saveChanges(ArrayList columns, ArrayList<ArrayList> data) throws IOException {
        System.out.println("Saving to File: "+filename);
        writer = new PrintWriter(new FileWriter(filename, false));
        String s = "";
        s += formatarray(columns);

        for (ArrayList a : data){
            s += formatarray(a);
        }
        writer.write(s);
        writer.close();
    }

    private String formatarray(ArrayList a){
        String s = "";
        for (Object o : a){
            s += o.toString().replace("\n","").replace("\r","")+"|";
        }
        return  s.substring(0,s.length()-1)+"\n";
    }

    public static boolean fileExists(String readlocation){
        File f = new File(readlocation);
        return f.exists();
    }
}
