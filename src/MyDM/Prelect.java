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
        HashMap<String, ArrayList<Integer>> locations  = new HashMap<>();

        while (iterator.hasNext()){
            Voter v = iterator.next().getValue();
            double[] sentiments = v.partysentiment;
            double max = Double.MIN_VALUE;
            int maxindex = -1;

            if (locations.containsKey(v.location)){
                for (int i = 0; i < sentiments.length; i++) {
                    if (sentiments[i] > max) {
                        max = sentiments[i];
                        maxindex = i;
                    }
                }
                locations.get(v.location).add(maxindex);
            } else {
                for (int i = 0; i < sentiments.length; i++) {
                    if (sentiments[i] > max) {
                        max = sentiments[i];
                        maxindex = i;
                    }
                }
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(maxindex);
                locations.put(v.location, arr);
            }
        }

        Iterator<Map.Entry<String, ArrayList<Integer>>> it = locations.entrySet().iterator();

        ArrayList<String> html = new ArrayList<>(locations.size());
        html.add(" <table class=\"table table-bordered partypercent table-hover\">\n" +
                "                <thead>\n" +
                "                <tr>\n" +
                "                    <td>Locations</td>\n" +
                "                    <td>Conservatives</td>\n" +
                "                    <td>Labour</td>\n" +
                "                    <td>Green</td>\n" +
                "                    <td>Lib-Dem</td>\n" +
                "                    <td>UKIP</td>\n" +
                "                    <td>Number of Voters\n"+
                "                </tr>\n" +
                "                </thead>");

        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Integer>> e = it.next();
            ArrayList<Integer> arr = e.getValue();
            String key = e.getKey();
            //Find the number of voters for each party:
            int conservatives = 0, labour = 0, green = 0, libdem = 0, ukip = 0;
            for (Integer i : arr) {
                switch (i) {
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
                        System.out.println("Doesn't fit case: " + i);
                        break;
                }
            }
            double size = arr.size();
            if (size>1) {
                String c = round(100 * conservatives / size, 4) + "%";
                String l = round(100 * labour / size, 4) + "%";
                String g = round(100 * green / size, 4) + "%";
                String ld = round(100 * libdem / size, 4) + "%";
                String u = round(100 * ukip / size, 4) + "%";

                html.add("                  <tr>\n" +
                        "                      <td>" + key + "</td>\n" +
                        "                      <td>" + c + "</td>\n" +
                        "                      <td>" + l + "</td>\n" +
                        "                      <td>" + g + "</td>\n" +
                        "                      <td>" + ld + "</td>\n" +
                        "                      <td>" + u + "</td>\n" +
                        "                      <td>" + arr.size() + "</td>\n" +
                        "                  </tr>\n");
            }
        }

        html.add("                  </tbody>\n" +
                "                     </table>");
        for (String s : html){
            System.out.println(s);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
