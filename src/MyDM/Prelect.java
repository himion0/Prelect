package MyDM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by H on 23/03/15.
 */
public class Prelect {

    public static void main(String[] args) {
        //Read Data:
        DataRefactor dr = new DataRefactor("var/tweets.bin","var/voters.bin");

        //Assign voters

        //Iterate through the voters and find the index of the sentiment with the largest value
        HashMap<Long,Voter> voters = dr.voters;
        Iterator<Map.Entry<Long,Voter>> iterator = voters.entrySet().iterator();
        ArrayList<Integer> arr = new ArrayList<>(voters.size());
        while (iterator.hasNext()){
            Map.Entry<Long,Voter> entry = iterator.next();
            double[] sentiments = entry.getValue().partysetiment;
            double max = Double.MIN_VALUE;
            int maxindex = -1;

            for (int i = 0; i<sentiments.length; i++){
                if (sentiments[i]>max){
                    max = sentiments[i];
                    maxindex = i;
                }
            }

            arr.add(maxindex);
        }

        //Find the number of voters for each party:
        int conservatives = 0, labour = 0, green = 0, libdem = 0, ukip = 0;

        for (Integer i : arr){
            switch (i){
                case 0:
                    conservatives++;
                    break;
                case 1:
                    labour++;
                    break;
                case 2:
                    green++;
                    break;
                case 3:
                    libdem++;
                    break;
                case 4:
                    ukip++;
                    break;
                default:
                    System.out.println("Doesn't fit case: "+i);
                    break;
            }
        }


        System.out.println(voters.size());
        double size = voters.size();
        System.out.println("Conservatives: " + ("" + 100 * conservatives / size).substring(0, 5)+"%");
        System.out.println("Labour: " + ("" + 100 * labour / size).substring(0, 5)+"%");
        System.out.println("Green: " + ("" + 100 * green / size).substring(0, 5)+"%");
        System.out.println("Lib-Dem: " + ("" + 100 * libdem / size).substring(0, 5) + "%");
        System.out.println("UKIP: " + ("" + 100 * ukip / size).substring(0, 5)+"%");
    }
}
