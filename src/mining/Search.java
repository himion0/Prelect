package mining;

import twitter4j.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Searches the twitter API:
 *
 * IMPORTANT: Change geoLocation location to other locations
 */

public class Search implements Runnable {
    private static final Twitter twitter = new TwitterConnection().twitter;
    private static GeoLocation geoLocation = new GeoLocation(55.16807, -4.41317);
    private static double radius = 543.26;
    LinkedBlockingQueue resultsQueue;
    public String s;
    static AtomicInteger count, numofq, ratelimit;
    Query query;


    public Search(String s, LinkedBlockingQueue resultsQueue) {
        this.resultsQueue = resultsQueue;
        this.s = s;
    }

    @Override
    public void run() {
        long maxID = Long.MAX_VALUE;
        query = new Query(s);
        query.setCount(100);
        query.setMaxId(maxID);
        query.setGeoCode(geoLocation, radius, Query.KILOMETERS);
        while (DataController.searching.get()) {
            QueryResult queryResult = null;
            try {
                queryResult = twitter.search(query);
                numofq.set(numofq.incrementAndGet());
                //If something goes wrong:
            } catch (TwitterException e) {
                //If the rate limit is exceeded:
                if (e.exceededRateLimitation()) {
                    ratelimit.set(e.getRateLimitStatus().getSecondsUntilReset());
                    DataController.searching.set(false);
                } else e.printStackTrace();
            }
            if (queryResult!=null){
                for (Status status : queryResult.getTweets()) {
                    count.set(count.incrementAndGet());
                    resultsQueue.add(new Tweet(status, s));
                    if (status.getId() < maxID) {
                        maxID = status.getId();
                        query.setMaxId(maxID);
                    }
                }
            }
        }
    }

    public static int getRateLimit(){

        return ratelimit==null ? 0 : ratelimit.get();
    }

    public static void resetCounters(){
        numofq = new AtomicInteger(0);
        count = new AtomicInteger(0);
        ratelimit = new AtomicInteger(0);
    }
}

