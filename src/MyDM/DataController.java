package MyDM;

import twitter4j.Status;
import utils.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataController {
    ExecutorService searchExec;
    static ArrayList<String> searchstrings;
    private static ArrayList<Search> searches;
    static ArrayList<Tweet> tweets;
    static HashMap<Long,Voter> voters;
    public String keywordfile;
    public static AtomicBoolean searching = new AtomicBoolean(true);
    LinkedBlockingQueue<Tweet> resultsQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        //Windowless application
        System.setProperty("java.awt.headless", "true");
        final DataController dc = new DataController();
        //Anonymous shutdown hook for the program
        //Runs at the end of the program to save progress to a text file
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log("Shutting Down", "blue");
                    dc.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
        int wait = 0;
        while (true) {
            if (wait>0) {
                log("Exceeded Rate Limit","cyan");
                log("Restarting at " +minutes(wait), "cyan");
                try {
                    Thread.sleep(wait * 1000 + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wait = 0;
            } else {
                log("SEARCHING", "blue");
                dc.run();
                wait = Search.getRateLimit();
            }
        }
    }

    public DataController() {
        SentimentAnalysis.load();
        //Try load tweets:
        searchstrings = new TextFile("keywords.txt").lines;
        DataRefactor dr = new DataRefactor("var/tweets.bin","var/voters.bin");
        tweets = dr.tweets;
        voters = dr.voters;
        if (tweets==null) tweets = new ArrayList<>();
        if (voters==null) voters = new HashMap<>();
        searches = new ArrayList<>();
    }

    public static boolean contains(Status s){
        for (Tweet t : tweets) if (t.getID() == s.getId()) return true;
        return false;
    }

    public static boolean contains(Tweet tweet){
        for (Tweet t : tweets) if (t.getID()==tweet.getID()) return true;
        return false;
    }

    public static boolean contains(Voter voter){
        if (voter==null||voters.isEmpty()) return false;
        Iterator it = voters.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Long,Voter> pair = (Map.Entry) it.next();
            if (pair.getKey()==voter.id) return true;
            it.remove();
        }
        return false;
    }

    public void run() {
        searching.set(true);
        Search.resetCounters();
        searchExec = Executors.newFixedThreadPool(searchstrings.size());

        if (searches.isEmpty()) {
            for (String s : searchstrings) {
                Search ser = new Search(s, resultsQueue);
                searches.add(ser);
                searchExec.execute(ser);
            }
        } else {
            for (Search s : searches) searchExec.execute(s);
        }

        //Stop any new threads from being executed
        searchExec.shutdown();

        boolean foundsomething = false;
        int newtweets = 0;
        int newvoters = 0;

        //Contains users with new tweets
        ArrayList<Long> updatedvoters = new ArrayList<>();

        //Will run until all the new Tweets have been added to the tweets
        while (!searchExec.isTerminated()) {
            if (resultsQueue.peek() == null) {
                continue;
            }

            ArrayList<Tweet> tmpResults = new ArrayList<>();
            resultsQueue.drainTo(tmpResults);

            //Check if the Status object is already contained
            for (Tweet result : tmpResults) {
                if (!contains(result)) {
                    foundsomething = true;
                    newtweets++;
                    result.getSentiment();
                    tweets.add(result);
                    Long voterid = result.status.getUser().getId();

                    if (!updatedvoters.contains(voterid)) updatedvoters.add(voterid);

                    if (voters.containsKey(voterid)) voters.get(voterid).addTweet(result);
                    else {
                        newvoters++;
                        voters.put(voterid, new Voter(result.status.getUser(), result));
                    }
                }
            }
        }

        //Log Results
        if (Search.numofq.get() != 0) {
            log("Number of Queries: " + Search.numofq, "blue");
            log("Searched " + Search.count, "blue");

            //Try saving the tweets to var/Data.bin
            if (foundsomething) {
                log("Found: " + newtweets + " tweets", "green");
                log("Found: " + newvoters + " voters", "green");
                log("Voters Updated: " + updatedvoters.size(), "green");
            } else log("Didn't find anything new", "red");
        }

        //Scan Timelines
        Voter.ratelimited = false;
        int votersscanned = 0;
        Iterator it = voters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Voter> entry = (Map.Entry) it.next();
            if (entry.getValue().scannedall) {
                votersscanned++;
            } else {
                if (entry.getValue().getMoreTweets(this)) votersscanned++;
                entry.getValue().update();
            }
        }
        double p = (double) votersscanned / (double) voters.size();
        log("Full Scanned Voters: " + round(p * 100, 3) + '%', "green");

        for (Long l : updatedvoters) voters.get(l).update();
        //Save
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> statustoArray(Tweet s) {
        ArrayList<String> ar = new ArrayList<>();
        String query = String.valueOf(s.query);
        if (query.contains("Conservatives") ||query.contains("Cameron")){
            query = "Conservatives";
        }else if (query.contains("Labour")||query.contains("Miliband")){
            query = "Labour";
        }else if (query.contains("GreenParty")||query.contains("natalie")|| query.contains("Natalie")){
            query = "Green";
        }else if (query.contains("LibDems")||query.contains("nick_clegg")){
            query = "LibDems";
        }else if (query.contains("Farage")||query.contains("UKIP")){
            query = "UKIP";
        }
        ar.add(query);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ar.add(sdf.format(s.status.getCreatedAt()));
        ar.add(String.valueOf(s.status.getPlace().getName()));
        return ar;
    }

    private ArrayList<String> votertoArray(Voter v){
        ArrayList<String> ar = new ArrayList<>();
        ar.add(String.valueOf(v.id));
        ar.add(v.name);
        ar.add(v.screenName);
        for (double d : v.partysetiment) {
            if (d == -1) ar.add("0");
            else ar.add(String.valueOf(round(d, 4)+1));
        }
        ar.add(v.location);
        return ar;
    }

    //Save to bin files and makes text files
    public void save() throws IOException {
        if (tweets.size()==0){
            log("tweets is empty, not saving...","red");
        } else {
            new Thread(new SaveObjects(tweets, "var/tweets.bin")).run();
            TextFile rtext = new TextFile("tweets.txt");
            ArrayList<ArrayList> arr = new ArrayList<>(tweets.size());
            for (Tweet t : tweets) arr.add(statustoArray(t));
            ArrayList columns =  new ArrayList<>(
                    Arrays.asList(
                            "Query",
                            "Date",
                            "Location"
                    )
            );
            rtext.saveChanges(columns, arr);
            log("Saved tweets.bin: " + tweets.size());
        }
        if (voters.size()==0){
            log("voters is empty, not saving...","red");
        }
        else {
            new Thread(new SaveObjects(voters, "var/voters.bin")).run();
            TextFile rtext = new TextFile("voters.txt");
            ArrayList<ArrayList> arr = new ArrayList<>(voters.size());
            Iterator it = voters.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<Long,Voter> pair = (Map.Entry) it.next();
                arr.add(votertoArray(pair.getValue()));
            }
            ArrayList columns =  new ArrayList<>(
                    Arrays.asList(
                            "ID",
                            "Name",
                            "Screen Name",
                            "Conservative",
                            "Labour",
                            "Green",
                            "Lib-Dem",
                            "UKIP",
                            "Location",
                            "Tweets"
                    )
            );
            rtext.saveChanges(columns, arr);
            log("Saved voters.bin: " + voters.size());
        }
    }

    //Logging to console / GUI with date and color:
    public static void log(String string,String color){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dates = StringUtils.changeColor(dateFormat.format(date) + ": ", "blue");
        String s = dates+StringUtils.changeColor(string,color);
        if (GUI.ISGUI)GUI.log(s);
        else System.out.println(s);
    }

    public static void log(String string){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String dates = StringUtils.changeColor(dateFormat.format(date) + ": ", "blue");
        if (GUI.ISGUI)GUI.log(dates+string);
        else System.out.println(dates+string);
    }

    //Returns the thread managing object:
    protected ExecutorService getExec(){ return searchExec; }

    private static String minutes(int i){
        Date d =  new Date(System.currentTimeMillis()+i*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(d);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}