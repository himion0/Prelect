package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by H on 17/02/15.
 */
public abstract class StringUtils {
    static final String ANSI_RESET = "\u001B[0m";

    //Colours:
    static final String ANSI_BLACK = "\u001B[30m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_PURPLE = "\u001B[35m";
    static final String ANSI_CYAN = "\u001B[36m";
    static final String ANSI_WHITE = "\u001B[37m";

    public static String changeColor(String s,String color){
        String ret;
        switch (color.toLowerCase()) {
            case "black":
                ret = ANSI_BLACK;
                break;
            case "red":
                ret = ANSI_RED;
                break;
            case "green":
                ret = ANSI_GREEN;
                break;
            case "yellow":
                ret = ANSI_YELLOW;
                break;
            case "blue":
                ret = ANSI_BLUE;
                break;
            case "purple":
                ret = ANSI_PURPLE;
                break;
            case "cyan":
                ret = ANSI_CYAN;
                break;
            case "white":
                ret = ANSI_WHITE;
                break;
            default:
                throw new IllegalArgumentException("Illegal Color");
        }
        return ret+s+ANSI_RESET;
    }


    public static void log(String s){
        if (s==null) throw new IllegalArgumentException("Can't log null string");
        else {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dates = changeColor(dateFormat.format(date) + ": ", "blue");
            System.out.println(dates + s);
        }
    }

    public static void log(String s,String color){
        if (s==null) throw new IllegalArgumentException("Can't log null string");
        else {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dates = changeColor(dateFormat.format(date) + ": ", "blue");
            System.out.println(dates + changeColor(s,color));
        }
    }
}
