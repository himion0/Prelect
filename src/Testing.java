import Twitterr.TwitterConnection;
import twitter4j.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by H on 01/11/14.
 */
public class Testing {
    private static ArrayList columns;

    Testing(){
        final Twitter twitter = new TwitterConnection().getTwitter();
        final GeoLocation geoLocation = new GeoLocation(55.16807, -4.41317);
        int found = 0;
        final double radius = 523.26;
        ArrayList<Status> results = new ArrayList<>();
        int num = 200;
        int querycount = 100;
        final String QUERY = "#Labour";

        long lowestTweetId = Long.MAX_VALUE;
        Query query = new Query(QUERY);
        query.setCount(querycount);
        query.setGeoCode(geoLocation, radius, Query.KILOMETERS);
        query.setSince("2014-07-01");
        QueryResult queryResult;
        try {
            System.out.println("Querying API (" + QUERY+ ")");
            queryResult = twitter.search(query);
            for (Status status : queryResult.getTweets()) {
                results.add(status);
                found++;
                if (status.getId() < lowestTweetId) {
                    lowestTweetId = status.getId();
                    query.setMaxId(lowestTweetId);
                }
            }
            //If something goes wrong:
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Testing();
    }
}

