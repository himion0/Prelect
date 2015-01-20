package MyDM;

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
    public static ExecutorService searchExec;
    public static ArrayList<String> searchstrings;
    private static ArrayList<Search> searches;
    public static ArrayList<Status> data;
    long timetaken;
    public String keywordfile;

    //Hold state of the radio button to continue searching:
    public static boolean searching = true;

    public static boolean isSearching(){
        return searching;
    }

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

    public void run() {
        searching = true;
        //Thread managing object
        searchExec = Executors.newFixedThreadPool(searchstrings.size());

        //Records the current time:
        timetaken = System.currentTimeMillis();

        //Thread safe queue to allow multiple intputs of data:
        LinkedBlockingQueue<Status> resultsQueue = new LinkedBlockingQueue<>();
        System.out.println("Querying API (" + searchstrings + ")");
        for (String s : searchstrings) {
            Search ser = new Search(s, resultsQueue);
            searches.add(ser);
            searchExec.execute(ser);
        }

        //Stop any new threads from being executed
        searchExec.shutdown();

        boolean foundsomething = false;
        int newtweets = 0;

        //Will run until all the new Tweets have been
        //added to the data
        while(!searchExec.isTerminated()) {
            if(resultsQueue.peek() == null) {
                continue;
            }

            ArrayList<Status> tmpResults = new ArrayList<>();
            resultsQueue.drainTo(tmpResults);

            //Check if the Status object is already contained
            for(Status result : tmpResults) {
                if(!data.contains(result)) {
                    foundsomething = true;
                    System.out.println("Found New Tweet");
                    newtweets++;
                    data.add(result);
                }
            }
        }

        //Try saving the data to var/Data.bin
        if (foundsomething) try { save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (GUI.ISGUI) {

            System.out.println(GUI.ISGUI);
            GUI.log("Found " + newtweets + " new tweets");
            GUI.log("\nTime taken: " + String.valueOf(System.currentTimeMillis() - timetaken) + "ms");
            GUI.log("-------Finished Search---------");
            //Reset Button
            GUI.searchtwitter.setText("Search Twitter");
        }
    }

    private static ArrayList<String> statustoArray(Status s) {
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

    public static void savetoText(String s) {
        //If the data is empty:
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

    public void load() {
        try {
            data = new ReadObjects("var/Data.bin").o;
            log("Loaded " + data.size() + " Tweets from: var/Data.bin");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        new Thread(new SaveObjects(data,"var/Data.bin")).run();
    }

    public static void log(String s){
        if (GUI.ISGUI)GUI.log(s);
        else System.out.println(s);
    }

    public static ExecutorService getExec(){return searchExec;}

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.awt.headless", "true");
        final DataController dc = new DataController(args[0]);
        dc.load();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dc.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        while (true) {
            dc.run();
            if (!searching) {
                int wait = Search.exception.getRateLimitStatus().getSecondsUntilReset();
                System.out.println("Exceeded Rate Limit: "+wait+"secs");
                Thread.sleep(wait*1000);
            }
        }
    }
}