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
    private String tweetlocation, voterlocation;
    public ArrayList<Tweet> tweets;
    public HashMap<Long, Voter> voters;

    public static void main(String[] args) {
        DataRefactor dataRefactor = new DataRefactor("var/tweets.bin","var/voters.bin");
        dataRefactor.updateAllVoter();
        dataRefactor.save();
    }

    public DataRefactor(String tweetlocation, String voterlocation) {
        this.tweetlocation = tweetlocation;
        this.voterlocation = voterlocation;
        tweets = readTweets(tweetlocation);
        voters = readVoters(voterlocation);
    }

    ArrayList<Tweet> readTweets(String location) {
        ReadObjects ro = null;
        try {
            ro = new ReadObjects(location);
            ro.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Tweet> ret = (ArrayList<Tweet>) ro.o;
        if (ret!=null) {
            StringUtils.log("Tweets: "+ ret.size(),"blue");
            return ret;
        }
        System.out.println(location+" has no tweets");
        return null;
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

    HashMap<Long,Voter> createVoters(){
        if (tweets == null) throw new IllegalArgumentException("Cannot create voters from null tweet array");
        HashMap<Long,Voter> voters = new HashMap<>();
        for (Tweet t : tweets){
            Long userid = t.getUser().getId();
            if (!voters.containsKey(userid)) voters.put(userid,new Voter(t.getUser(),t));
            else {
                voters.get(userid).tweets.add(t);
            }
        }

        Iterator it = voters.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Long,Voter> entry = (Map.Entry<Long, Voter>) it.next();
            entry.getValue().update();
        }

        return voters;
    }

    ArrayList<Tweet> createTweets(){

        if (voters.isEmpty()||voters==null) return null;

        ArrayList<Tweet> tweets = new ArrayList<>();
        Iterator it = voters.entrySet().iterator();

        while (it.hasNext()){
            Map.Entry<Long,Voter> entry = (Map.Entry<Long, Voter>) it.next();
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
        save(tweets, tweetlocation);
        save(voters, voterlocation);
    }

    public void save(ArrayList<Tweet> tweets,String location){
        try {
            new SaveObjects(tweets,location).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(HashMap<Long,Voter> voters,String location){
        try {
            new SaveObjects(voters,location).run();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void resetVoterScans() {
        Iterator<Map.Entry<Long,Voter>> it = voters.entrySet().iterator();
        while (it.hasNext()){
            Voter v = it.next().getValue();
            v.scannedall = false;
            v.numOfScans = 0;
        }
    }

    public void resetVoterScans(String s) {
        Iterator<Map.Entry<Long, Voter>> it = voters.entrySet().iterator();

        while (it.hasNext()) {
            Voter v = it.next().getValue();
            if (v.name.trim().equals(s)) v.scannedall = false;
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

    //    public void combineData(String folder, String genernalname){
//        int i = 1;
//        String c;
//        boolean hasnext = true;
//        String s;
//        ArrayList<String> files = new ArrayList<>();
//
//        while (hasnext){
//            File file;
//
//            if (i==1) s = folder+"/"+genernalname+".bin";
//            else {
//                c = Integer.toString(i);
//                s = folder + "/" + genernalname + " " + c + ".bin";
//            }
//
//            file = new File(s);
//            if (file.exists()) files.add(s);
//            else hasnext = false;
//
//            i++;
//        }
//
//        System.out.println(files);
//
//        ArrayList<ReadObjects> roarray= new ArrayList<>();
//        ExecutorService exec = null;
//        for (String file : files){
//            exec = Executors.newFixedThreadPool(files.size());
//            ReadObjects ro = null;
//            try {
//                ro = new ReadObjects(file);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            roarray.add(ro);
////FIX            exec.execute(ro);
//        }
//
//        exec.shutdown();
//        while (!exec.isTerminated()){}
//
//        datas = new ArrayList<>(files.size());
//
//        for (ReadObjects ro : roarray){
//            ArrayList<Tweet> temp = (ArrayList<Tweet>) ro.o;
//            System.out.println(temp.size());
//            datas.add(temp);
//        }
//        exec.shutdownNow();
//    }
}
