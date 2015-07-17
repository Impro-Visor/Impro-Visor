/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

package jm.gui.cpn;

import  jm.music.data.Phrase;
import  jm.music.data.Note;
import  jm.music.data.Score;
import  jm.music.data.Part;
import  jm.util.Write;
import  java.io.*;
import  java.util.*;

public class StavePhraseProperties extends Properties {

    private static String KEY_SIGNATURE   = "STAVE_KEY";
    
    private static String STAVE_TYPE      = "STAVE_TYPE";    
    
    private static String STAVE_TITLE     = "STAVE_TITLE";    
    
    private static String STAVE_METRE     = "STAVE_METRE";    

    private static String PHRASE_NUMERATOR = "PHRASE_NUM";

    private static String PHRASE_DENOMINATOR = "PHRASE_DEN";
    
    private static String PHRASE_TEMPO      = "PHRASE_TEMPO";

    private static String PHRASE_TITLE      = "PHRASE_TITLE";
    private static String PHRASE_INSTRUMENT = "PHRASE_INSTRUMENT";

    private static String LAST_NOTE_RHYTHM = "LAST_NOTE_RHYTHM";
    private static String LAST_NOTE_DUR    = "LAST_NOTE_DUR";
    
    private static String FINAL_REST_RHYTHM = "FINAL_REST_RHYTHM";
    private static String FINAL_REST_DUR    = "FINAL_REST_DUR";

    private static String OTHER_NOTES_TOTAL_RHYTHM 
                                = "OTHER_NOTES_TOTAL_RHYTHM";
    private static String OTHER_NOTES_TOTAL_DUR 
                                = "OTHER_NOTES_TOTAL_DUR";


    private static String GRAND_STAVE    = "GRAND_STAVE";
    private static String TREBLE_STAVE   = "TREBLE_STAVE";
    private static String BASS_STAVE     = "BASS_STAVE";
    private static String PIANO_STAVE    = "PIANO_STAVE";



    
    private static String FILE_NAME_SUFFIX = "pj";    
    
    public StavePhraseProperties (String midiFileName)
            throws FileNotFoundException,
                   IOException {         
      FileInputStream theStream 
        = new FileInputStream(midiFileName + FILE_NAME_SUFFIX);
      load((InputStream) theStream);  
    }    

    public StavePhraseProperties(
            Stave  stave,
            Phrase phrase) {  
        System.out.println("1"); 
        setSavedProperty( 
            KEY_SIGNATURE,  stave.getKeySignature());
        System.out.println("2");
        setSavedProperty(STAVE_TYPE,  getStaveType(stave));
    System.out.println("3");
        setSavedProperty( 
            STAVE_TITLE,  stave.getTitle());
  System.out.println("4");                   
        setSavedProperty( 
            STAVE_METRE,  stave.getMetre());
System.out.println("5");
        setSavedProperty( 
            PHRASE_NUMERATOR,  phrase.getNumerator());
System.out.println("6");
        setSavedProperty( 
            PHRASE_DENOMINATOR,  phrase.getDenominator());
System.out.println("7");
        setSavedProperty( 
            PHRASE_INSTRUMENT,  phrase.getInstrument());
System.out.println("8");
        setSavedProperty( 
            PHRASE_TEMPO,  phrase.getTempo());
    System.out.println("9");
        setSavedProperty( 
            PHRASE_TITLE,  phrase.getTitle());
        
        int n;
        n = findLastNonRest(phrase);
        System.out.println("10");
        if ( n >= 0) {
            setSavedProperty( 
                LAST_NOTE_RHYTHM,
                phrase.getNote(n).getRhythmValue()
            );                
            setSavedProperty( 
                LAST_NOTE_DUR,
                phrase.getNote(n).getDuration()
            );                
        }                
        else {
            setSavedProperty(LAST_NOTE_RHYTHM, 0.0 );
            setSavedProperty(LAST_NOTE_DUR, 0.0);
        }            
        
        setSavedProperty( 
                FINAL_REST_RHYTHM,
                getFinalRestRhythm(phrase)
        );                
        
        setSavedProperty( 
                FINAL_REST_DUR,
                getFinalRestDuration(phrase)
        );                

        setSavedProperty( 
                OTHER_NOTES_TOTAL_RHYTHM,
                getOtherNotesTotalRhythm(phrase)
        );                

        setSavedProperty( 
                OTHER_NOTES_TOTAL_DUR,
                getOtherNotesTotalDuration(phrase)
        );     
    }    


    private void setSavedProperty(String label, String value){
        if (label == null) label = "";
        if (value == null) value = "";
        setProperty(label, value);             
    }       

    private void setSavedProperty(String label, int value){
        setSavedProperty(label, Integer.toString(value));        
    }        

    private void setSavedProperty(String label, double value){
        setSavedProperty(label, Double.toString(value));        
    }        
        
    private String getStaveType( Stave stave) {
        if( stave instanceof TrebleStave) {
            return TREBLE_STAVE;
        }                       
        else if( stave instanceof GrandStave) {
            return GRAND_STAVE;
        }                        
        else if( stave instanceof BassStave) {
            return BASS_STAVE;
        }                        
        else if( stave instanceof PianoStave) {
            return PIANO_STAVE;
        }                        
        return GRAND_STAVE;                  
    }        
    
    public void writeToFile(String midiFileName) 
                throws FileNotFoundException {
      try {
        FileOutputStream theStream 
            = new FileOutputStream(midiFileName + FILE_NAME_SUFFIX);
        store((OutputStream) theStream, 
            "Stave and Phrase Properties for " +
            midiFileName );  
      }
      catch ( IOException e ) {
        System.out.println( 
            "Error Writing MIDI Properties File " + 
            midiFileName + " " + e.getMessage());
      }                                         
    }
    
    public void updateStave( Stave stave ) {
        
        stave.setKeySignature(
            (new Integer(getProperty(KEY_SIGNATURE)))
                 .intValue());                    
                 
        stave.setTitle(getProperty(STAVE_TITLE));
        
        stave.setMetre(
            (new Double(getProperty(STAVE_METRE)))
                 .doubleValue());                    
    }                
    
    public void updatePhrase( Phrase phrase ) {
        phrase.setNumerator(
            (new Integer(getProperty(PHRASE_NUMERATOR)))
                 .intValue());                    
                 
        phrase.setDenominator(
            (new Integer(getProperty(PHRASE_DENOMINATOR)))
                 .intValue());                    
                 
        phrase.setTitle(getProperty(PHRASE_TITLE));          
        
        phrase.setTempo(
            (new Double(getProperty(PHRASE_TEMPO)))
                 .doubleValue());                    
        
        try {
            phrase.setInstrument( 
                (new Integer(getProperty(PHRASE_INSTRUMENT)))
                     .intValue());
        }
        catch (Throwable e) {
            phrase.setInstrument( 0 );
        }                                 
                                        
        int lastNotePos = findLastNonRest(phrase);
        if (lastNotePos >= 0) {
            phrase.getNote(lastNotePos)
                .setRhythmValue(                    
                    (new Double(getProperty(LAST_NOTE_RHYTHM)))
                            .doubleValue());                    
            phrase.getNote(lastNotePos)
                .setDuration(                    
                    (new Double(getProperty(LAST_NOTE_DUR)))
                            .doubleValue());                    
        }
        
        if ( new Double(getProperty(FINAL_REST_RHYTHM)).doubleValue()                    
               > 0.00001 ) {
            adjustFinalRestRhythm(                   
                phrase, 
                new Double(getProperty(FINAL_REST_RHYTHM))
                    .doubleValue(),
                new Double(getProperty(FINAL_REST_DUR))
                    .doubleValue()
            );                    
        }                    
        
        adjustOtherNotesTotalRhythm(                   
                phrase, 
                new Double(getProperty(OTHER_NOTES_TOTAL_RHYTHM))
                    .doubleValue()
        );
                                        
        adjustOtherNotesTotalDuration(                   
                phrase, 
                new Double(getProperty(OTHER_NOTES_TOTAL_DUR))
                    .doubleValue()
        );

        Score   s = new Score();
        Part    p = new Part();
        s.addPart(p);
        p.addPhrase(phrase);                     
    }                
    
    public boolean isTrebleStave() {
        return getProperty(STAVE_TYPE).equals(TREBLE_STAVE);             
    }        
    
    public boolean isBassStave() {
        return getProperty(STAVE_TYPE).equals(BASS_STAVE);             
    }        

    public boolean isGrandStave() {
        return getProperty(STAVE_TYPE).equals(GRAND_STAVE);             
    }        
    
    public boolean isPianoStave() {
        return getProperty(STAVE_TYPE).equals(PIANO_STAVE);             
    }   
    
    private static int findLastNonRest(Phrase  phrase) {
        int answer;        
        answer = phrase.size() - 1;
        while ( (answer >= 0) && 
                (phrase.getNote(answer).getPitch()
                     == Note.REST) ) {
            --answer;
        }
        return answer;            
    }    
    
    private static void adjustFinalRestRhythm(
                     Phrase  phrase,
                     double  rhythm,
                     double  duration ) {
        
        double restPresent;                                
        restPresent = getFinalRestRhythm(phrase);
        Note restToAdd;
        if ( rhythm - restPresent > 0.001 ) {
            restToAdd = new Note();
            restToAdd.setFrequency(Note.REST);                                                
            restToAdd.setRhythmValue(
                            rhythm - restPresent);                                                
            restToAdd.setDuration(
                        (rhythm - restPresent) *
                        (duration/rhythm)) ;                                                
            phrase.addNote(restToAdd);
        }                                        
    }    

    private static void adjustOtherNotesTotalRhythm(                   
                Phrase phrase, 
                double totalRhythmWanted ) {

        double totalRhythmFound;
        totalRhythmFound 
            =  getOtherNotesTotalRhythm(phrase);
        if ( totalRhythmFound > 0.0 ) {
            double adjustmentFactor;
            adjustmentFactor = 
                totalRhythmWanted 
                / totalRhythmFound;
            int n;
            n = findLastNonRest(phrase);                
            for(int i = 0; i < n; ++i ) {
                phrase.getNote(i).setRhythmValue(
                    phrase.getNote(i).getRhythmValue()
                    * adjustmentFactor );
            }                
        }                                              
    }                
    
    

    private static void adjustOtherNotesTotalDuration(                   
                Phrase phrase, 
                double totalDurationWanted ) {

        double totalDurationFound;
        totalDurationFound 
            =  getOtherNotesTotalDuration(phrase);
        if ( totalDurationFound > 0.0 ) {
            double adjustmentFactor;
            adjustmentFactor = 
                totalDurationWanted 
                / totalDurationFound;
            int n;
            n = findLastNonRest(phrase);                
            for(int i = 0; i < n; ++i ) {
                if ( phrase.getNote(i).getPitch()
                        != Note.REST ) {
                    phrase.getNote(i).setDuration(
                        phrase.getNote(i).getDuration()
                         * adjustmentFactor );
                }                                             
                else {
                    phrase.getNote(i).setDuration(
                        phrase.getNote(i)
                        .getRhythmValue()
                        );
                }                                            
            }                
        }                                              
    }                
    
    private static double getFinalRestRhythm(                   
                        Phrase phrase ) {
        int i, n;
        double answer;
        answer = 0.0;
        n = phrase.size();
        i = findLastNonRest(phrase) + 1;                                    
        while(i < n) {
            answer = answer + phrase.getNote(i)
                                .getRhythmValue();
            ++i;                                            
        }
        return answer;                
    }                             
    
    
    private static double getFinalRestDuration(                   
                        Phrase phrase ) {
        int i, n;
        double answer;
        answer = 0.0;
        n = phrase.size();
        i = findLastNonRest(phrase) + 1;                                    
        while(i < n) {
            answer = answer + phrase.getNote(i)
                                .getDuration();
            ++i;                                            
        }
        return answer;                
    }                             
    
    private static double getOtherNotesTotalRhythm(                   
                        Phrase phrase ) {
        int i, n;
        double answer;
        answer = 0.0;
        i = 0;
        n = findLastNonRest(phrase);                                    
        while(i < n) {
            answer = answer + phrase.getNote(i)
                                    .getRhythmValue();
            ++i;                                            
        }
        return answer;                
    }                             
    
    
    private static double getOtherNotesTotalDuration(                   
                        Phrase phrase ) {
        int i, n;
        double answer;
        answer = 0.0;
        i = 0;
        n = findLastNonRest(phrase);                                    
        while(i < n) {
            if ( phrase.getNote(i).getPitch()
                    != Note.REST ) {
                answer = answer + phrase.getNote(i)
                                    .getDuration();
            }                                    
            ++i;                                            
        }
        return answer;                
    }                             
    
    
}
