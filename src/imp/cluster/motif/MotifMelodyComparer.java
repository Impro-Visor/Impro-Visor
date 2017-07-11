/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package imp.cluster.motif;

import polya.Polylist;

/**
 *
 * @author Joseph Yaconelli
 */
public class MotifMelodyComparer {
    
    
    
    // edit distance adapted from http://www.programcreek.com/2013/12/edit-distance-in-java/
    /**
     * Calculates the Levenshtein (Edit) Distance between two Abstract Melodies
     * @param melody1 First melody to compare
     * @param melody2 Second melody to compare
     * @return The minimum number of edits (deletes, insertions, and exchanges) to convert one to the other
     */
    public static int levenshteinDistance(Polylist melody1, Polylist melody2) {
        
        // ensure no nested lists
        melody1 = melody1.flatten();
        melody2 = melody2.flatten();
        
	int len1 = melody1.length();
	int len2 = melody2.length();
 
	// len1+1, len2+1, because finally return dp[len1][len2]
	int[][] dp = new int[len1 + 1][len2 + 1];
 
	for (int i = 0; i <= len1; i++) {
		dp[i][0] = i;
	}
 
	for (int j = 0; j <= len2; j++) {
		dp[0][j] = j;
	}
 
	//iterate though, and check last string
	for (int i = 0; i < len1; i++) {
            String c1 = String.valueOf(melody1.nth(i));
            for (int j = 0; j < len2; j++) {
                    String c2 = String.valueOf(melody2.nth(j));

                    //if last two strings equal
                    if (c1 == null ? c2 == null : c1.equalsIgnoreCase(c2)) {
                            //update dp value for +1 length
                            dp[i + 1][j + 1] = dp[i][j];
                    } else {
                            int replace = dp[i][j] + 1;
                            int insert = dp[i][j + 1] + 1;
                            int delete = dp[i + 1][j] + 1;

                            int min = replace > insert ? insert : replace;
                            min = delete > min ? min : delete;
                            dp[i + 1][j + 1] = min;
                    }
            }
	}
 
	return dp[len1][len2];
    }
    
    /**
     * Calculates the Levenshtein (Edit) Distance between the Abstract Melodies of two Motifs
     * @param motif1 first motif to compare
     * @param motif2 second motif to compare
     * @return The minimum number of edits to convert one abstract melody to the other
     */
    public static int levenshteinDistance(Motif motif1, Motif motif2){
        return(levenshteinDistance(motif1.grammarRule().flatten(), motif2.grammarRule().flatten()));
    }
    
    /**
     * Calculates normalized [0, 1] distance between two Motifs.
     * <br>Warning! This is <b>NOT</b> an O(1) call. Uses call to {@link levenshteinDistance}.
     * @param motif1 first motif to compare
     * @param motif2 second motif to compare
     * @return the normalized distance between two Motifs
     * @see levenshteinDistance(Motif m1, Motif m2)
     */
    public static double distance(Motif motif1, Motif motif2){
         
        double d;
         
        int maxDistance = Math.max(motif1.grammarRule().flatten().length(), motif2.grammarRule().flatten().length());
        int levenshtein = levenshteinDistance(motif1, motif2);
         
        // calculate percent difference between two motifs
        d = (double) levenshtein / (double) maxDistance;
        
        return d;
        
     }

    
}
