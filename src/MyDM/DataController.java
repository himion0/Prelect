package MyDM;

import Twitterr.Search;
import twitter4j.Status;
import utils.ReadObjects;
import utils.SaveObjects;
import utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DataController implements Runnable {
    public static ArrayList<String> searchstrings;
    private static ArrayList<Search> searches;
    public static ArrayList<Status> data;
    long timetaken;
    public String keywordfile;
    private static ArrayList columns =  new ArrayList<>(
            Arrays.asList("Tweet-Text","Screen-Name","UserID","TweetID","Date","Retweet-Count","Location"));

    public DataController(String keywordfile) {
        data = new ArrayList<>();
        this.keywordfile = keywordfile;
        try {
            searchstrings = new TextFile(keywordfile).lines;
        } catch (IOException e) {
            e.printStackTrace();
        }
        searches = new ArrayList<>();
    }

    //Thread managing object
    static ExecutorService searchExec = Executors.newFixedThreadPool(searchstrings.size());

    public void run() {

        timetaken = System.currentTimeMillis();
        LinkedBlockingQueue<Status> resultsQueue = new LinkedBlockingQueue<>();

        for (String s : searchstrings) {
            Search ser = new Search(s, resultsQueue);
            searches.add(ser);
            searchExec.execute(ser);
        }

        //Stop any new threads from being executed
        searchExec.shutdown();

        boolean foundsomething = false;
        while(!searchExec.isTerminated()) {
            if(resultsQueue.peek() == null) {
                continue;
            }

            ArrayList<Status> tmpResults = new ArrayList<>();
            resultsQueue.drainTo(tmpResults);

            for(Status result : tmpResults) {
                if(!data.contains(result)) {
                    data.add(result);
                    foundsomething = true;
                    GUI.log("New Tweet Found");
                }
            }
        }
        if (foundsomething) try { save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GUI.searchtwitter.setText("Search Twitter");
        GUI.log("\n-------Finished Search---------");
    }

    ArrayList<String> statustoArray(Status s) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(s.getText());
        ar.add(s.getUser().getScreenName());
        ar.add(s.getUser().getName());
        ar.add(String.valueOf(s.getId()));
        ar.add(String.valueOf(s.getCreatedAt()));
        ar.add(String.valueOf(s.getRetweetCount()));
        ar.add(String.valueOf(s.getPlace().getName()));
        return ar;
    }

    public void savetoText(String s) {
        if (data.isEmpty()) GUI.log("Data Empty: cannot save");
        else {
            TextFile rtext = null;
            try {
                rtext = new TextFile(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<ArrayList> ar = new ArrayList<>(data.size());
            for (Status t : data) ar.add(statustoArray(t));
            try {
                rtext.saveChanges(columns, ar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        try {
            data = new ReadObjects("var/Data.bin").o;
            GUI.log("Loaded " + data.size() + " Tweets from: var/Data.bin");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        new Thread(new SaveObjects(data,"var/Data.bin")).run();
    }
}