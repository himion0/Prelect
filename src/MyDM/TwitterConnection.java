package MyDM;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


/*
 * Could add several of these to manipulate the rate limit
 */
public class TwitterConnection {

    private final static String CONSUMER_KEY = "giR9jOuW3brC2Uo1zuBesI0vF";
    private final static String CONSUMER_KEY_SECRET =
            "YfzyH0sSgY98yup50XZMVL9n0U5PFV6gYP2rqh7HRcHoTdK7DI";
    private final static String SAVED_ACCESS_TOKEN = "1350978980-bpNx1UMydKqLdzhULgnYZ0zBi8r9HGY49BXiYk8";
    private final static String SAVED_ACCESS_TOKEN_SECRET = "RT3VjEE3V7DC48qfRJDkoyiwFl1KDYITVY7NvAS3NaI6U";
    final Twitter twitter;


    public TwitterConnection() {
        System.out.println("Connecting to Twitter");
        //Set up account:
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
        twitter.setOAuthAccessToken(new AccessToken(SAVED_ACCESS_TOKEN,
                SAVED_ACCESS_TOKEN_SECRET));
        System.out.println("Success");
    }

    public Twitter getTwitter(){
        return twitter;
    }

}