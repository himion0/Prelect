package MyDM;

import utils.TextFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Harry Coultas Blum on 19/11/14.
 */

//This class deals with the button presses of the previous
class GUIAL implements ActionListener {
    GUI gui;
    Data da;

    GUIAL(GUI gui, Data data) {
        this.gui = gui;
        da = data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gui.searchtwitter) {
            try {
                da.search();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == gui.keywordsbut) {
            log("\n------Loading keywords-----");
            log("[THIS IS NOT IMPLEMENTED]");
        } else if (e.getSource() == gui.savetotext){
            if (!da.data.isEmpty()){
                if (isValidFile(gui.keywords.getText())&&isValidFile(gui.datafilelocation.getText())){

                    log("\n------Saving to Textfile-----");
                    da.keywordfile=gui.keywords.getText();
                    da.savetoText(gui.datafilelocation.getText());
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
    }
}