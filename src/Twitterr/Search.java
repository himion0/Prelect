package Twitterr;

import MyDM.GUI;
import twitter4j.*;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Searches the twitter API:
 *
 * IMPORTANT: Change geoLocation location for other locations
 */

public class Search implements Runnable {
    private static final Twitter twitter = new TwitterConnection().twitter;
    private static GeoLocation geoLocation = new GeoLocation(55.16807, -4.41317);
    private static double radius = 523.26;
    public LinkedBlockingQueue resultsQueue;
    public String s;
    private int num;
    public int numtweets = 0;

    static {
        GUI.log("\n---------Searching---------");
    }

    public Search(String s, LinkedBlockingQueue resultsQueue) {
        this.resultsQueue = resultsQueue;
        this.s = s;
        GUI.log(s);
    }

    @Override
    public void run() {
        long lowestTweetId = Long.MAX_VALUE;
        Query query = new Query(s);
        query.setCount(100);
        query.setGeoCode(geoLocation, radius, Query.KILOMETERS);
        query.setSince("2014-07-01");
        QueryResult queryResult = null;
        try {
            System.out.println("Querying API (" + s + ")");
            queryResult = twitter.search(query);
            for (Status status : queryResult.getTweets()) {
                resultsQueue.add(status);
                numtweets++;
                if (status.getId() < lowestTweetId) {
                    lowestTweetId = status.getId();
                    query.setMaxId(lowestTweetId);
                }
            }
            //If something goes wrong:
        } catch (TwitterException e) {
            e.printStackTrace();
            GUI.log(e.getErrorMessage());
            //If the rate limit is exceeded:
            if (e.exceededRateLimitation()) {
                int wait = e.getRateLimitStatus().getSecondsUntilReset();
                GUI.log("Exceeded Rate Limit: "+wait+"secs");
            } else {
                return;
            }
        }
    }
}

