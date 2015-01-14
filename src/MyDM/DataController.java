package MyDM;

import Twitterr.Search;
import twitter4j.Status;
import utils.ReadObjects;
import utils.SaveObjects;
import utils.TextFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DataController {
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

    void search() throws InterruptedException {
        timetaken = System.currentTimeMillis();
        Thread[] threads = new Thread[searchstrings.size()];
        int i = 0;
        for (String s : searchstrings) {
            Search ser = new Search(s,100);
            threads[i] = new Thread(ser);
            threads[i].start();
            i++;
            searches.add(ser);
        }

        for (Thread t : threads) t.join();
        importSearch();

        timetaken -= System.currentTimeMillis();
        GUI.log("Total time: " + -1*timetaken + "ms");
        GUI.log("Tweets in database: " + data.size());

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void importSearch() {
        boolean found;
        GUI.log("\n-------Importing Search-------\n");
        int newtweets = 0;
        for (Search s : searches) {
            found = false;
            GUI.log("Found " + s.numtweets + " results for: " + s.s);
            for (Status status : s.results) {
                //Find duplicates (Could be made more efficient)
                if (data!=null) {
                    for (Status stat : data) {
                        if (stat.getId() == status.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        data.add(status);
                        newtweets++;
                    }
                }

            }
        }
        GUI.log("\nTweets Added:" + newtweets + "\n");
    }

    ArrayList<String> statustoArray(Status s){
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

    String reverseString(String s){
        return new StringBuffer(s).reverse().toString();
    }
}