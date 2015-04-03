package mining;

import twitter4j.Status;
import twitter4j.User;
import utils.SentimentAnalysis;
import utils.StringUtils;

import java.io.Serializable;

/**
 * Created by H on 13/02/15.
 */

public class Tweet implements Serializable {
    //Prevents issues with compiler rejecting older versions of the class
    private static final long serialVersionUID = 1982319238;
    public Status status;
    public int sentiment = -1;
    public String query;

    public Tweet(Status s, String q){
        status = s;
        query = q;
    }

    public int getSentiment(){
        if (sentiment==-1) {
            SentimentAnalysis sa = new SentimentAnalysis(status.getText());
            sa.run();
            sentiment = sa.result+1;
            if (sentiment == 1 || sentiment == 5) {
                StringUtils.log("Extreme Sentiment(" + String.valueOf(sentiment) + "):\n" + status.getText(), "red");
            }
        }
        return sentiment;
    }

    boolean contains(String s){
        return query.contains(s);
    }

    public long getID(){return status.getId();}

    public String getUserName(){
        return status.getUser().getName();
    }

    public User getUser(){ return status.getUser();}

    public String getTweetLocation(){return status.getPlace().getName();}
}

