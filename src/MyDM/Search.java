package MyDM;

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
    public static TwitterException exception;
    public LinkedBlockingQueue resultsQueue;
    public String s;
    public int numtweets = 0;

    public Search(String s, LinkedBlockingQueue resultsQueue) {
        this.resultsQueue = resultsQueue;
        this.s = s;
    }

    @Override
    public void run() {
        while (DataController.isSearching()) {


            long lowestTweetId = Long.MAX_VALUE;
            Query query = new Query(s);
            query.setCount(100);
            query.setGeoCode(geoLocation, radius, Query.KILOMETERS);
            query.setSince("2014-07-01");
            QueryResult queryResult;
            try {
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
                //If the rate limit is exceeded:
                if (e.exceededRateLimitation()) {
                    exception = e;
                    int wait = e.getRateLimitStatus().getSecondsUntilReset();
                    if (GUI.ISGUI) GUI.log("Exceeded Rate Limit: " + wait + "secs");
                    DataController.searching = false;
                    DataController.getExec().shutdownNow();
                } else {
                    return;
                }
            }
        }
    }
}

