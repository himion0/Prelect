package Twitterr;

import twitter4j.Status;

import java.util.*;

/**
 * Created by H on 01/11/14.
 */
public class Tweet {
    Status status;
    //To be turned into appropriate objects;
    Voter user;
    String text;
    String location;
    String language;
    String mediatype;
    //To be turned into an array of users.
    int retweets,favourites;
    public Long id;
    Date date;
    //ArrayList of Users
    ArrayList<String> hashtags,mentions;
    boolean isretweet;

    ArrayList toArray;

    public Tweet(Status s){
        status = s;
        text = s.getText().replace("\n","").replace("\r","");
        user = new Voter(s.getUser());
        id = s.getId();
        date = s.getCreatedAt();
        retweets = s.getRetweetCount();
        isretweet = s.isRetweet();
    }

    public Tweet(ArrayList<String> a){
        toArray = a;
        text = a.get(0);
        user = new Voter(a.get(1),a.get(2));
        id = Long.valueOf(a.get(3));
        date = new Date(a.get(4));
        retweets = Integer.valueOf(a.get(5));
    }

    //Change when columns added
    public ArrayList toArray() {
        if (toArray==null){
            toArray = new ArrayList();
            toArray.add(text);
            toArray.add(user.name);
            toArray.add(user.screenName);
            toArray.add(id);
            toArray.add(date);
            toArray.add(retweets);
            return toArray;
        } else {
            return toArray;
        }
    }

    public ArrayList<String> toStringArray(){
        if (toArray==null) {
            toArray();
            return toStringArray();
        }
        else {
            ArrayList<String> a = new ArrayList<>();
            for (Object o : toArray){
                a.add(String.valueOf(o));
            }
            return a;
        }
    }
}
