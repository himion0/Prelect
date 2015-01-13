package MyDM;

import Twitterr.Search;
import twitter4j.Status;
import utils.ReadObjects;
import utils.SaveObjects;
import utils.TextFile;
import java.io.IOException;
import java.util.*;

public class Data {
    public static ArrayList<String> searchstrings;
    private static ArrayList<Search> searches;
    public static ArrayList<Status> data;
    long timetaken;
    public String keywordfile;
    private static ArrayList columns =  new ArrayList<>(
            Arrays.asList("Tweet-Text","Screen-Name","UserID","TweetID","Date","Retweet-Count"));

    public Data(String keywordfile) {
        data = new ArrayList<>();
        load();
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
            threads[i].run();
            i++;
            searches.add(ser);
        }

        for (Thread t : threads) t.join();

        importSearch();

        timetaken -= System.currentTimeMillis();
        GUI.log("Total time: " + -1 * timetaken + "ms");
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
            GUI.log("Found " + s.results.size() + " results for: " + s.s);
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
        System.out.println("Loading...");
        try {
            ReadObjects ro = new ReadObjects("var/Data.bin");
            if (ro.o!=null){
                data = (ArrayList<Status>) ro.o;
            }
            GUI.log("Loaded "+ data.size()+ " Tweets");
        } catch (Exception e){
        }

    }

    public void save() throws IOException {
        new Thread(new SaveObjects(data,"var/Data.bin")).run();
    }

    public int indexofTweet(Long id){
        for (Status t : data){
            if (t.getId()==id) return data.indexOf(t);
        }
        return -1;
    }

    //Adds the tweet to the database
    void add(Status s){
        data.add(s);
    }

    //Parses a single lined string into an array
    private ArrayList<String> stringtoArray(String s){
        s =  reverseString(s);

        //Check if columns array already exists:
        if (columns!=null) {
            ArrayList<String> sarray = new ArrayList<>(columns.size());
            String element = "";
            int numofele = 0;
            for (Character c : s.toCharArray()){
                if (c=='|'&&numofele<columns.size()-1){
                    sarray.add(0,reverseString(element));
                    element = "";
                    numofele++;
                } else {
                    element += c;
                }
            }
            sarray.add(0,reverseString(element));
            return sarray;
        }
        //Parse Column
        else {
            ArrayList<String> sarray = new ArrayList<>();
            for (String s1 : s.split("\\|")) {
                sarray.add(0, reverseString(s1));
            }
            return sarray;
        }
    }

    String reverseString(String s){
        return new StringBuffer(s).reverse().toString();
    }

    //Parses the database from the instances file variable
//
//    public void loadFromText(String s) throws IOException {
//        TextFile rtext = new TextFile(s);
//        columns = stringtoArray(rtext.lines.get(0));
//        rtext.lines.remove(0);
//        if (rtext.lines.size()!=0) {
//            for (String s : rtext.lines) {
//                if (stringtoArray(s)!=null) datafilelocation.add(new Tweet(stringtoArray(s)));
//            }
//        }
//        System.out.println("Loaded [" + datafilelocation.size() + "] tweets from text file: " + rtext.toString());
//    }
}