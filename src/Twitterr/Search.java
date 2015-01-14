package Twitterr;

import MyDM.GUI;
import twitter4j.*;

import java.util.ArrayList;

/**
 * Searches the twitter API:
 *
 * IMPORTANT: Change geoLocation location
 */

public class Search implements Runnable {
    private static final Twitter twitter = new TwitterConnection().twitter;
    private static GeoLocation geoLocation = new GeoLocation(55.16807, -4.41317);
    private static double radius = 523.26;
    public ArrayList<Status> results;
    public String s;
    private int num;
    public int numtweets = 0;

    static {
        GUI.log("\n---------Searching---------\n");
    }

    public Search(String s, int num) {
        this.s = s;
        this.num = num;
        results = new ArrayList<>();
        GUI.log(s);
    }

    @Override
    public void run() {
        long lowestTweetId = Long.MAX_VALUE;
        while (numtweets < num) {
            Query query = new Query(s);
            query.setCount(100);
            query.setGeoCode(geoLocation, radius, Query.KILOMETERS);
            QueryResult queryResult = null;
            try {
                System.out.println("Querying API (" + s + ")");
                queryResult = twitter.search(query);

                //If something goes wrong:
            } catch (TwitterException e) {
                e.printStackTrace();
                GUI.log(e.getErrorMessage());
                //If the rate limit is exceeded:
                if (e.exceededRateLimitation()) {
                    GUI.log("Exceeded Rate Limit");
                    int wait = e.getRateLimitStatus().getSecondsUntilReset();
                    GUI.log("Waiting " + wait + "secs");
                    try {
                        Thread.sleep(wait * 1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    return;
                }
            }
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
