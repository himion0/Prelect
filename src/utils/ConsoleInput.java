package utils;

/**
 * Created by H on 28/10/14.
 */
class ConsoleInput {
    private String input;
    private String message;
    private boolean result;

    //Yes or No
    public ConsoleInput(String message){
        this.message = message;
    }

    void start(){
        System.out.println(message);
        input = System.console().readLine();
        input.trim().toLowerCase();
        if (yes()) result = true;
        else if (no()) result = false;
    }

    public boolean setNewmessage(String newmessage){
        this.message = newmessage;
        start();
        return result;
    }

    boolean yes(){
        return input=="y"||input=="yes";
    }

    boolean no(){
        return input=="n"||input=="no";
    }
}
