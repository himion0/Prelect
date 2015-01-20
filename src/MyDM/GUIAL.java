package MyDM;

import utils.TextFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Harry Coultas Blum on 19/11/14.
 */

//This class deals with the button presses of the previous
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
                new Thread(da).start();
                gui.searchtwitter.setText("Stop");
            } else {
                gui.searchtwitter.setText("Search Twitter");
            }
        } else if (e.getSource() == gui.keywordsbut) {
            log("\n------Loading keywords-----");
            log("[THIS IS NOT IMPLEMENTED]");
        } else if (e.getSource() == gui.savetotext){
            if (!DataController.data.isEmpty()){
                if (isValidFile(GUI.keywords.getText())&&isValidFile(GUI.datafilelocation.getText())){

                    log("\n------Saving to Textfile-----");
                    da.keywordfile=GUI.keywords.getText();
                    da.savetoText(GUI.datafilelocation.getText());
                    log("------Finished Saving-----");
                }
            }
        }
    }

    boolean isValidFile(String s) {
        boolean bol = s.matches("[\\w\\(\\)\\|]+\\.txt")&&TextFile.fileExists(s);
        if (!bol) log("ERROR: "+s+" is not a valid file");
        return bol;
    }

    public void log(String s){
        gui.log.setText(gui.log.getText()+s+"\n");
        gui.repaint();
    }
}