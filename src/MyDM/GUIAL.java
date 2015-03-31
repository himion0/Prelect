package MyDM;

import utils.TextFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//This class deals with the button presses of GUI classs
public class GUIAL implements ActionListener {
    //Whether to keep searching through the API:
    GUI gui;
    DataController da;

    GUIAL(GUI gui, DataController data) {
        this.gui = gui;
        da = data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gui.searchtwitter) {
            String text = gui.searchtwitter.getText();
            if (text.equals("Search Twitter")){
//                new Thread(da).start(); CHECK
                gui.searchtwitter.setText("Stop");
            } else {
                gui.searchtwitter.setText("Search Twitter");
                da.getExec().shutdownNow();
            }
        } else if (e.getSource() == gui.keywordsbut) {
            log("\n------Loading keywords-----");
            log("[THIS IS NOT IMPLEMENTED]");
        } else if (e.getSource() == gui.savetotext){
            if (!DataController.tweets.isEmpty()){
                if (isValidFile(GUI.keywords.getText())&&isValidFile(GUI.datafilelocation.getText())){
                    log("Saving "+da.tweets.size()+" Tweets");
                    da.keywordfile=GUI.keywords.getText();
                    log("------Finished Saving-----");
                }
            }
        }
    }

    boolean isValidFile(String s) {
        boolean bol = s.matches("[\\w\\(\\)\\|]+\\.txt")&& TextFile.fileExists(s);
        if (!bol) log("ERROR: "+s+" is not a valid file");
        return bol;
    }

    public void log(String s){
        gui.log(s);
    }
}