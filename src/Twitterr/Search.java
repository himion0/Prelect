package Twitterr;

import MyDM.GUI;
import twitter4j.*;
import twitter4j.Query;
import java.util.ArrayList;

/**
 * Created by H on 28/10/14.
 */
public class Search implements Runnable {
    private static final Twitter twitter = new TwitterConnection().twitter;
    private static GeoLocation geo = new GeoLocation(55.16807,-4.41317);
    public ArrayList<Status> results;
    public String s;
    private int num;

    static {
        GUI.log("\n---------Searching---------\n");
    }

    public Search(String s,int num){
        this.s=s;
        this.num=num;
        results = new ArrayList<>();
        GUI.log(s);
    }

    @Override
    public void run() {
        int numtweets = 0;
        long lowestTweetId = Long.MAX_VALUE;
        while (numtweets<num) {
            Query query = new Query(s);
            query.setCount(100);
            query.setGeoCode(geo, 523.26, Query.KILOMETERS);
            QueryResult queryResult;
            try {
                queryResult = twitter.search(query);

            } catch (TwitterException e) {
                break;
            }

            numtweets += queryResult.getTweets().size();
            for (Status status : queryResult.getTweets()) {
                results.add(status);
                if (status.getId() < lowestTweetId) {
                    lowestTweetId = status.getId();
                    query.setMaxId(lowestTweetId);
                }
            }

        }
    }
}
