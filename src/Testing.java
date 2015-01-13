import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by H on 01/11/14.
 */
public class Testing {
    private static ArrayList columns;


    public static void main(String[] args) throws IOException {
        File file = new File("testslkfjalsdkfj");

    }

    static String reverseString(String s){
        return new StringBuffer(s).reverse().toString();
    }

    static private ArrayList<String> stringtoArray(String s){
        //Check to see if it is a valid:

        //Parsestring:
        s =  reverseString(s);

        //Check if columns array already exists:
        if (columns!=null) {
            ArrayList<String> sarray = new ArrayList<>(columns.size());
            String element = "";
            int numofele = 0;
            for (Character c : s.toCharArray()){
                if (c=='|'&&numofele<columns.size()-1){
                    sarray.add(0,reverseString(element));
                    element = "";
                    numofele++;
                } else {
                    element += c;
                }
            }
            sarray.add(0,reverseString(element));
            return sarray;
        }
        //Parse Column
        else {
            ArrayList<String> sarray = new ArrayList<>();
            for (String s1 : s.split("\\|")) {
                sarray.add(0, reverseString(s1));
            }
            return sarray;
        }
    }
}

