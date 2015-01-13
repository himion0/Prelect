package MyDM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Harry Coultas Blum on 19/11/14.
 * The purpose of this class is to initilaise the Graphical User Interface for
 * the 'Tweet Miner' application.
 */

public class GUI extends JFrame {
    JTextField keywords, datafilelocation;
    JButton searchtwitter;
    JButton keywordsbut, savetotext;
    int blockwidth=400,blockheight=230;
    Data data;
    JTextArea log;
    JRadioButton stop;

    static GUIAL al;
    JPanel keywordpanel;

    public GUI() {
        setLayout(new GridLayout(1,2));
        setTitle("Tweet Miner");
        JPanel filepanel = new JPanel(new GridLayout(2,2));
        JPanel controlpanel = new JPanel(new GridLayout(2,1));
        keywordpanel = new JPanel(new GridLayout(2,1));

        setResizable(false);

        //Logging Area:
        log = new JTextArea();
        log.setLineWrap(true);
        log.setEditable(false);
        JScrollPane logpane = new JScrollPane(log);

        //Control Panel:
        keywords = new JTextField("keywords.txt");
        keywords.requestFocusInWindow();
        keywordsbut = new JButton("Load Keywords");
        datafilelocation = new JTextField("UKDMdata.txt");
        savetotext = new JButton("Save to Text File");

        filepanel.add(keywords);
        filepanel.add(keywordsbut);
        filepanel.add(datafilelocation);
        filepanel.add(savetotext);
        searchtwitter = new JButton("Search Twitter");

        controlpanel.add(filepanel);
        controlpanel.add(searchtwitter);

        //The Keywords Area:
        JTextField addkeyword = new JTextField();
        JTextArea keytext = new JTextArea();
        keytext.setLineWrap(true);
        keytext.setEditable(false);
        JScrollPane keytextpane = new JScrollPane(keytext);
        keywordpanel.add(addkeyword);
        keywordpanel.add(keytextpane);

        JTextArea datatext = new JTextArea();
        datatext.setLineWrap(true);

        add(controlpanel);
        add(logpane);
        data = new Data("keywords.txt");
        al = new GUIAL(this, data);
        keywordsbut.addActionListener(al);
        savetotext.addActionListener(al);
        searchtwitter.addActionListener(al);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        refreshsize();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //This is for eventual implementation of several areas that have yet to be shown
    //[NOT FULLY IMPLEMENTED]
    private void refreshsize(){
        GridLayout g = (GridLayout) getContentPane().getLayout();
        setSize(g.getColumns()*blockwidth,g.getRows()*blockheight);
        g.setHgap(5);
        g.setVgap(5);
    }

    //Sets the grid layout
    //[NOT IMPLEMENTED]
    void setGrid(int rows, int colums){
        GridLayout g = (GridLayout) getContentPane().getLayout();
        g.setRows(rows);
        g.setColumns(colums);
    }

    //Writes the given string on a new line in logging are on the right
    public static void log(String s){
        al.log(s);
    }

    //Runs the GUI:
    public static void main(String[] args) {
        new GUI();
    }
}


