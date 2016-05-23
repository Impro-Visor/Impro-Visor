/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014-2016 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.themeWeaver;
import imp.data.MelodyPart;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import static imp.Constants.BEAT;
import imp.data.Part;
import imp.data.Unit;
import polya.Polylist;
import polya.PolylistEnum;

/**
 *
 * @author Nava Dallal
 */
public class Theme {
    
 MelodyPart melody; 

 String name;
 
 int ThemeLength;
 
 int serial;
 
 int numDiscriminators = 0;
 
 String discriminator[] = new String[maxDiscriminators];
 
 static int maxDiscriminators = 2;
 
 int discriminatorOffset[] = new int[maxDiscriminators];
 
 //construct a Theme from a MelodyPart
 public Theme(MelodyPart melody)
 {
     this.melody = melody;
     this.ThemeLength = melody.size() / BEAT;
 }
 
 private static LinkedHashMap<String, Theme> allThemes = new LinkedHashMap<String, Theme>();
 private static ArrayList<Theme> orderedThemes = null;
 
//create Theme from name and MelodyPart
     public static Theme makeTheme(String name, MelodyPart theme) {
         Theme newTheme = new Theme(theme);
         newTheme.name = name;
         newTheme.ThemeLength = theme.size() /BEAT;
        // newTheme.ThemeLength = theme.getUnitList();
//         for (int i = 0; i < theme.getUnitList().size(); i++){
//             Note note = (Note)theme.getUnitList().get(i);
        //     note.getNoteLength();
         
         
         return newTheme;
     } 
     
     //get name of a Theme
     String getName() { return name; } 
     
 // get the string of notes in a theme    
     Polylist getNotes()
  {
  return Polylist.list(melodyToString(melody));
  }
   
     //Convert a MelodyPart to a String
     public static String melodyToString(MelodyPart melody){
         Part.PartIterator i = melody.iterator(); //iterate over lick
                    String theme = ""; //set theme as empty to start
                    
                    while (i.hasNext()) //while you can still iterate through the lick
                    {
                        Unit unit = i.next();
                        if (unit != null) //if next isn't empty
                        {
                            theme += unit.toLeadsheet() + " "; //add it to the theme
                        }
                    }
                    return theme;
     } 
     
     //Convert a Theme to a Polylist
     public Polylist ThemetoPolylist(Theme theme){
        return Polylist.list("theme", Polylist.list("name", theme.name), Polylist.list("notes", theme.melodyToString(theme.melody)));
     } 
     
     //construct a Theme from a Polylist
     public Theme(Polylist list) {
         Polylist nameList = (Polylist)list.second(); //get Polylist of the name
         Polylist nameTheme = nameList.rest(); //just get the name
         PolylistEnum nameElements = nameTheme.elements(); //set the name of the Theme
         
         String nameString = "";
          while (nameElements.hasMoreElements()) { //while there are more notes
           Object current =  nameElements.nextElement();//get next note
             String currentString = current.toString(); //convert it to String
             nameString += currentString + " "; //add the note to the melodyString
           //  System.out.println(nameString);
         }  
          this.name = nameString;
         
         Polylist melodyList = (Polylist)list.last(); //get polylist of the melody
         Polylist melodyNotes = (Polylist)melodyList.rest(); //get the notes in a polylist
         
         PolylistEnum melodyElements = melodyNotes.elements(); //get the notes as elements
         
         //To get the notes of the theme in a string:
         String melodyString = "";
         while (melodyElements.hasMoreElements()) { //while there are more notes
           Object current =  melodyElements.nextElement();//get next note
             String currentString = current.toString(); //convert it to String
             melodyString += currentString + " "; //add the note to the melodyString
            // System.out.println(melodyString);
         }  
         MelodyPart melody = new MelodyPart(melodyString); //create a MelodyPart of the string
         this.melody = melody; //set the melody to the melody of the theme
     }
     
     
     
    public void showForm(java.io.PrintStream out) {
        out.println(toString());
    }
    
 @Override
    public String toString()
    {
        return "(theme "
                + "(name " + getName()
                + ")(notes " + melodyToString(melody)
                + "))";
    }
 
}
