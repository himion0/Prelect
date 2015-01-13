package Twitterr;

import java.io.Serializable;

/**
 * Created by H on 01/11/14.
 */
public class Voter implements Serializable{
    String name, screenName, fullname, address;

    public Voter(twitter4j.User u){
       screenName=u.getScreenName();
        name = u.getName();
    }

    public Voter(String s, String d) {
        name = s;
        screenName = d;
    }
}
