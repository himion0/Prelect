package MyDM;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        System.out.println("Conservatives: " + round(100 * conservatives / size,4) + "%");
        System.out.println("Labour: " + round(100 * labour / size,4) + "%");
        System.out.println("Green: " + round(100 * green / size,4) + "%");
        System.out.println("Lib-Dem: " + round(100 * libdem / size,4) + "%");
        System.out.println("UKIP: " + round(100 * ukip / size,4) + "%");
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
