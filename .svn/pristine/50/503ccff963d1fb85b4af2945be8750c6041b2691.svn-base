package imp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author muddCS15
 */
public class VoicingDistanceCalculator {
    public static int calculateDistance(int[] voicing1, int[] voicing2)
    {
        int sum1=0,sum2=0;
        for(int i:voicing1)
        {
            double currentMinimum=100;//can't really have much more than that as distance between two notes
            for(int j: voicing2)
                if(Math.abs(i-j)<currentMinimum)
                    currentMinimum=Math.abs(i-j);
            sum1+=currentMinimum;
        }
         for(int i:voicing2)
        {
            double currentMinimum=100;//can't really have much more than that as distance between two notes
            for(int j: voicing1)
                if(Math.abs(i-j)<currentMinimum)
                    currentMinimum=Math.abs(i-j);
            sum2+=currentMinimum;
        }
         if(sum1<sum2)
             return sum2;
        return sum1;
    }
    public static int calculateNotesChanged(int[] voicing1, int[] voicing2)
    {
        int sumChanged=0;
         for(int i:voicing1)
        {
            
            int changed=1;
            for(int j: voicing2)
                if(i-j==0)
                    changed=0;
            sumChanged+=changed;
        }
        return sumChanged;
    }
}
