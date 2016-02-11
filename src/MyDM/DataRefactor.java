package MyDM;

import utils.ReadObjects;
import utils.SaveObjects;
import utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Created by H on 02/02/15.
 *  Primary use of this class is to combine SentStatus files from a given folder name
 *  e.g. I used it to recover lost SentStatus and combine backups. Be careful however that you don't include SentStatuss
 *  in the folder that you don't want combined.
 *  It also gives the ability to create a set of voters from a given set of tweets.
 */
public class DataRefactor {
    private String voterlocation;
    public ArrayList<Tweet> tweets;
    public HashMap<Long, Voter> voters;

    public static void main(String[] args) {
        DataRefactor dataRefactor = new DataRefactor("var/voters.bin");
        dataRefactor.updateAllVoter();
        dataRefactor.save();
    }

    public DataRefactor(String voterlocation) {
        this.voterlocation = voterlocation;
        voters = readVoters(voterlocation);
        tweets = createTweets();
    }

    HashMap<Long, Voter> readVoters(String location){
        ReadObjects ro = null;
        try {
            ro = new ReadObjects(location);
            ro.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<Long, Voter> voters = (HashMap<Long, Voter>) ro.o;
        if (voters==null){
            System.out.println("Voters was Empty: ");
            voters = new HashMap<>();
        }
        StringUtils.log("Voters: " + voters.size(), "blue");
        return voters;
    }

    HashMap<Long, Voter> createVoters(){
        if (tweets == null) throw new IllegalArgumentException("Cannot create voters from null tweet array");
        HashMap<Long, Voter> voters = new HashMap<>();
        for (Tweet t : tweets){
            Long userid = t.getUser().getId();
            if (!voters.containsKey(userid)) voters.put(userid,new Voter(t.getUser(),t));
            else {
                voters.get(userid).tweets.add(t);
            }
        }

        Iterator it = voters.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Long, Voter> entry = (Map.Entry<Long, Voter>) it.next();
            entry.getValue().update();
        }

        return voters;
    }

    ArrayList<Tweet> createTweets(){

        if (voters.isEmpty()||voters==null) return null;

        ArrayList<Tweet> tweets = new ArrayList<>();
        Iterator it = voters.entrySet().iterator();

        while (it.hasNext()){
            Map.Entry<Long, Voter> entry = (Map.Entry<Long, Voter>) it.next();
            for (Tweet t : entry.getValue().tweets) {
                boolean found = false;
                for (Tweet newtweet : tweets) {
                    if (t.getID() == newtweet.getID()) {
                        found = true;
                        break;
                    }
                }
                if (!found) tweets.add(t);
            }
        }

        if (tweets.isEmpty()||tweets==null)return null;
        else return tweets;
    }

    public void save(){
        try {
            new SaveObjects(voters, voterlocation).run();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetVoterScans() {
        Iterator<Map.Entry<Long, Voter>> it = voters.entrySet().iterator();
        while (it.hasNext()){
            Voter v = it.next().getValue();
            v.scannedall = false;
            v.numOfScans = 0;
        }
    }

    public void updateAllVoter(){
        Iterator<Map.Entry<Long, Voter>> it = voters.entrySet().iterator();
        while (it.hasNext()) {
            Voter v = it.next().getValue();
            v.update();
            for (double d : v.partysentiment){
                System.out.print( d + ", ");
            }
            System.out.println();
        }
    }
}
