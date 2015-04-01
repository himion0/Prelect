package MyDM;

import twitter4j.*;
import utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by H on 01/11/14.
 */
public class Voter implements Serializable{
    //Prevents issues with compiler rejecting older versions of the class
    private static final long serialVersionUID = 1982319231;
    String name, screenName, location;
    public long id;
    //Conservative, Labour, Green, Liberal Democrat, UKIP
    //A party sentiment means that there aren't any tweets based on the voter that tells us about there sentiment towards that
    //party.
    double[] partysentiment = {0,0,0,0,0};
    boolean scannedall = false;
    static boolean ratelimited = false;
    int numOfScans = 0;
    static RateLimitStatus rateLimitStatus;
    static Twitter twitter = new TwitterConnection().twitter;

    ArrayList<Tweet> tweets = new ArrayList<>();

    public Voter(User u,Tweet tweet){
        screenName=u.getScreenName();
        name = u.getName();
        id = u.getId();
        location = tweet.getTweetLocation();
        tweets.add(tweet);
    }

    public void addTweet(Tweet t) {
        if (!DataController.contains(t)){
            DataController.tweets.add(t);
        }
        tweets.add(t);
    }

    //Finds the mode location
    private void updateLocation(){
        HashMap<String, Integer> modelocation = new HashMap<>();

        for (Tweet t : tweets){
            String location = t.status.getPlace().getName();
            if (modelocation.containsKey(location)){
                modelocation.replace(location,modelocation.get(location)+1);
            } else {
                modelocation.put(location,1);
            }
        }

        int max = 0;
        String maxkey ="empty";
        Iterator i = modelocation.entrySet().iterator();

        while (i.hasNext()){
            Map.Entry<String, Integer> e = (Map.Entry) i.next();
            if (e.getValue() > max){
                max = e.getValue();
                maxkey = e.getKey();
            }
        }

        location = maxkey;
    }

    //Works out the sentiments of the users based on the collected tweets that they have
    private void updateSentiment() {
        int[] count = {0, 0, 0, 0, 0};
        HashMap<String, Integer> modelocation = new HashMap<>();

        partysentiment = new double[]{0, 0, 0, 0, 0};

        for (Tweet t : tweets){

            //Check if they've tweeted from there before
            String location = t.status.getPlace().getName();
            if (modelocation.containsKey(location)){
                modelocation.replace(location,modelocation.get(location)+1);
            } else {
                modelocation.put(location,1);
            }

            //Check sentiment:
            double sentiment = 0;
            if (t.contains("Conservatives")||t.contains("Cameron")){
                count[0]++;
                sentiment = t.getSentiment()+1;
                if (sentiment==5) {
                    partysentiment[0] += 5.6;
                } else if (sentiment==1) {
                    partysentiment[0] += 0.4;
                } else if (sentiment==2){
                    partysentiment[0] += 1.8;
                } else if (sentiment==4){
                    partysentiment[0] += 3.2;
                } else {
                    partysentiment[0] += sentiment;
                }
            }else if (t.contains("Labour")||t.contains("Miliband")){
                count[1]++;
                sentiment = t.getSentiment()+1;
                if (sentiment==5) {
                    partysentiment[1] += 5.6;
                } else if (sentiment==1) {
                    partysentiment[1] += 0.4;
                } else if (sentiment==2){
                    partysentiment[1] += 1.8;
                } else if (sentiment==4){
                    partysentiment[1] += 3.2;
                } else {
                    partysentiment[1] += sentiment;
                }
            }else if (t.contains("GreenParty")||t.contains("natalie")||t.contains("Natalie")){
                count[2]++;
                sentiment = t.getSentiment()+1;
                if (sentiment==5) {
                    partysentiment[2] += 5.6;
                } else if (sentiment==1) {
                    partysentiment[2] += 0.4;
                } else if (sentiment==2){
                    partysentiment[2] += 1.8;
                } else if (sentiment==4){
                    partysentiment[2] += 3.2;
                } else {
                    partysentiment[2] += sentiment;
                }
            }else if (t.contains("LibDems")||t.contains("nick_clegg")){
                count[3]++;
                sentiment = t.getSentiment()+1;
                if (sentiment==5) {
                    partysentiment[3] += 5.6;
                } else if (sentiment==1) {
                    partysentiment[3] += 0.4;
                } else if (sentiment==2){
                    partysentiment[3] += 1.8;
                } else if (sentiment==4){
                    partysentiment[3] += 3.2;
                } else {
                    partysentiment[3] += sentiment;
                }
            }else if (t.contains("Farage")||t.contains("UKIP")){
                count[4]++;
                sentiment = t.getSentiment()+1;
                if (sentiment==5) {
                    partysentiment[4] += 5.6;
                } else if (sentiment==1) {
                    partysentiment[4] += 0.4;
                } else if (sentiment==2){
                    partysentiment[4] += 1.8;
                } else if (sentiment==4){
                    partysentiment[4] += 3.2;
                } else {
                    partysentiment[4] += sentiment;
                }
            }
        }

        //Calculate sentiment average
        for (int i = 0; i <count.length;i++) {
            if (count[i]==0) partysentiment[i] = 0;
            else {
                double value = partysentiment[i] / count[i];
                if (value>5) value = 5;
                else if (0<value&&value<1) value = 1;
                partysentiment[i] = value;
            }
        }
    }

    public void update(){
        updateSentiment();
        updateLocation();
    }

    public boolean getMoreTweets(DataController dc){
        if (!ratelimited){
            StringUtils.log("Timeline Search: "+name+" - "+id,"purple");
            final Twitter twitter = new TwitterConnection().twitter;
            int count = 0;
            int i = 0;
            int size = -1;
            numOfScans++;
            while (size!=0 && !ratelimited) {
                i++;
                Paging page = new Paging(i, 200);//page number, number per page
                try {
                    ResponseList<Status> statuses = twitter.getUserTimeline(id, page);
                    size = statuses.size();
                    Iterator<Status> it = statuses.iterator();
                    while (it.hasNext()){
                        Status s = it.next();
                        //If we already have the tweet
                        if (dc.contains(s))break;

                        //If the status is from Great Britain
                        if (s.getPlace()!=null&&s.getPlace().getCountryCode().equals("GB")) {
                            boolean found = false;
                            //Look through hashtags
                            for (HashtagEntity hashtagEntity: s.getHashtagEntities()){
                                String h = hashtagEntity.getText();
                                if (DataController.searchstrings.contains("#"+h)){
                                    addTweet(new Tweet(s, "#" + h));
                                    found = true;
                                    count++;
                                    break;
                                }
                            }

                            //Look through mentions
                            if (!found) {
                                for (UserMentionEntity ume : s.getUserMentionEntities()) {
                                    String u = ume.getText();
                                    if (DataController.searchstrings.contains("@" + u)) {
                                        addTweet(new Tweet(s, "@" + u));
                                        count++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    scannedall = true;
                } catch (TwitterException e) {
                    rateLimitStatus = e.getRateLimitStatus();
                    StringUtils.log("Rate Limited","cyan");
                    ratelimited = true;
                    if (numOfScans>2) {
                        StringUtils.log("Blocked: " + name, "red");
                        scannedall = true;
                    } else StringUtils.log("Didn't finish: "+"("+numOfScans+")"+name,"red");
                }
            }
            if (count!=0) StringUtils.log("FOUND " + count + " EXTRA","green");
        }
        return scannedall;
    }

    public static RateLimitStatus getRateLimitStatus(){
        try {
            return twitter.getUserTimeline().getRateLimitStatus();
        } catch (TwitterException e) {
            return e.getRateLimitStatus();
        }
    }
}
