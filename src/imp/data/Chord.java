/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College
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

package imp.data;

import imp.style.Style;
import imp.midi.MidiSequence;
import imp.midi.MidiSynth;
import imp.style.stylePatterns.ChordPattern;
import imp.data.advice.Advisor;
import imp.Constants;
import imp.roadmap.brickdictionary.ChordBlock;
import static imp.data.Chord.flushChordBuffer;
import imp.util.Preferences;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import polya.Polylist;
import polya.PolylistEnum;

/**
 * The Chord class represents a chord in a chord progression.
 * Right now the Chord only stores its name and its rhythmValue.
 * It has a function called getPitches that returns a ArrayList
 * containing the MIDI numbers of all the pitches in the chord.
 * Currently it is just a bunch of if statements with very limited 
 * knowledge (but with enough information to play "Half Nelson")
 * and in the future it will likely call Prolog or wherever we
 * put the actual music theory knowledge.  Perhaps the Chord
 * object itself will store the actual pitches in the future,
 * but at the moment it does not do that.
 * @see         Unit
 * @see         Part
 * @see         ChordPart
 * @author      Stephen Jones
 */
public class Chord implements Constants, Unit, Serializable {

    /**
     * the maximum number of bars to be printed on an output line
     */
    public static int maxBarsOnLine = 4;

    /**
     * the maximum number of notes in a chord voicing
     */
    public static int maxNotes = Integer.parseInt(Preferences.getPreference(Preferences.MAX_NOTES_IN_VOICING));
    
    /**
     * the default chord name
     */
    public static final String DEFAULT_NAME = NOCHORD;

    /**
     * the ChordSymbol for this chord (which contains the name)
     */

    private ChordSymbol symbol;
    
    
    private Polylist voicing = null;
    

    /**
     * an int containing the rhythm value
     */
    private int rhythmValue;
    
    private int[] metre = new int[2];
    
    /**
     * Use this in case name can be ill-formed.
     */
    
    public static Chord makeChord(String name)
      {
      return makeChord(name, DEFAULT_RHYTHM_VALUE);
      }

    public static Chord makeChord(String name, int rhythmValue)
      {
      ChordSymbol symbol = ChordSymbol.makeChordSymbol(name);
      if( symbol == null )
        {
        return null;
        }
      return new Chord(symbol, rhythmValue);
      }

    /**
     * Creates a Chord with the specified name and rhythmValue.
     * @param name          a String containing the chord name
     * @param rhythmValue   an int containing the rhythm value
     */
    public Chord(String name, int rhythmValue) {
        this(ChordSymbol.makeChordSymbol(name), rhythmValue);
    }
    
    /**
     * Creates a Chord from a ChordSymbol and rhythmValue
     * @param symbol a ChordSymbol identifying this chord
     * @param rhythmValue   an int containing the rhythm value
     */
    public Chord(ChordSymbol symbol, int rhythmValue) {
        this.symbol = symbol;
        this.rhythmValue = rhythmValue;
    }
    
    public Chord(ChordBlock block)
      {
        this.symbol = ChordSymbol.makeChordSymbol(block.getName());
        rhythmValue = polya.Arith.long2int(block.getDuration());
      }
    
    /**
     * Creates a Chord with the specified name.
     * @param name      a String containing the chord name
     */
    public Chord(String name) {
        this(name, DEFAULT_RHYTHM_VALUE);
    }

 	/**
 	 * Creates a Chord with the specified rhythm value
 	 * @param rhythmValue   an int containing the rhythmValue
 	 */
	public Chord(int rhythmValue) {
		this(DEFAULT_NAME, rhythmValue);
	}

    /**
     * Sets the Chord's ChordSymbol
     * @param symbol the new ChordSymbol of this Chord
     */
    public void setChordSymbol(ChordSymbol symbol) {
        this.symbol = symbol;
    }
    
    /**
     * Sets the Chord's rhythm value.
     * @param rhythmValue       the rhythm value to set
     */
    public void setRhythmValue(int rhythmValue) {
        this.rhythmValue = rhythmValue;
        //System.out.println("setting rhythm value to " + rhythmValue + " for chord " + this);
    }

    /**
     * Increments the Chord's rhythm value.
     * @param rhythmValue       the rhythm value by which to increment
     */
    public void incRhythmValue(int rhythmValue) {
        this.rhythmValue += rhythmValue;
    }
    
    /**
     * Attempts to toggle enharmonics on a chord by
     * transposing chords up one semitone, then down one semitone
     * and comparing the resulting Chord Symbols (if they changed, the
     * new one is used).  If there was no change, the same is attempted
     * using transposition down first, then up.
     */
    public boolean toggleEnharmonic() {
        ChordSymbol up = symbol.transpose(1).transpose(-1);
        if(!up.getName().equals(symbol.getName())) {
            setChordSymbol(up);
            return true;
        }
        
        ChordSymbol down = symbol.transpose(-1).transpose(1);
        if(!down.getName().equals(symbol.getName())) {
            setChordSymbol(down);
            return true;
        }
        
        return false;
    }

    /**
     * Transpose this chord by changing the ChordSymbol
     * @param semitones the number of semitones by which to transpose
     */
    public void transpose(int semitones) {
        setChordSymbol(symbol.transpose(semitones));
    }

    /**
     * Returns the Chord's rhythm value.
     * @return int      the Chord's rhythm value
     */
    public int getRhythmValue() {
        return rhythmValue;
    }

    /**
     * Returns the Chord's name.
     * @return String   the Chord's name
     */
    public String getName() {
        return symbol.getName();
    }
    
    /**
     * Returns the Chord's ChordForm.
     * @return String   the Chord's name
     */
    public ChordForm getChordForm() {
    	String root = symbol.getRootString();
        return symbol.getChordForm();
    }

    /**
     * Returns the Chord's Family
     * @return String   the Chord's name
     */
    public String getFamily() {
        return symbol.getFamily();
    }
    
    public Polylist getSpell()
    {
        if (getName().equals(NOCHORD))
            return null;
        
    	String root = symbol.getRootString();
	ChordForm chordForm = getChordForm();
	Key key = chordForm.getKey(root);

	return chordForm.getSpell(root, key);
    }

    public Polylist getColor()
    {
        if (getName().equals(NOCHORD))
            return null;
        
	String root = symbol.getRootString();
	ChordForm chordForm = symbol.getChordForm();
	Key key = chordForm.getKey(root);

	return chordForm.getColor(root, key);
    }
    
    public Polylist getFirstScale()
    {
        if (getName().equals(NOCHORD))
            return null;

        String root = symbol.getRootString();
	ChordForm chordForm = symbol.getChordForm();

	return chordForm.getFirstScaleTones(root);
    }
    
    public Polylist getScales()
    {
        if (getName().equals(NOCHORD))
            return null;
	ChordForm chordForm = symbol.getChordForm();
        if( chordForm == null )
          {
          return Polylist.nil; // This has happened, but shouldn't
          }
	Polylist scales = chordForm.getScales();
        Polylist transScales = new Polylist();
	
        while (scales.nonEmpty())
        {
            Polylist scale = (Polylist)scales.first();
            Polylist transScale = new Polylist();
            NoteSymbol tonic = NoteSymbol.makeNoteSymbol((String)scale.first());
            tonic = tonic.transpose(PitchClass.findRise(symbol.getRootString()));
            
            transScale = transScale.cons(Advisor.concatListWithSpaces(scale.rest()));
            transScale = transScale.cons(tonic.getPitchString());
            transScales = transScales.cons(transScale);
            
            scales = scales.rest();
        }
        
        transScales = transScales.reverse();
	return transScales;
    }
    
    public String isValidScale(String type)
    {
        if (getName().equals(NOCHORD))
            return null;
        
        ChordForm cf = symbol.getChordForm();
        Polylist scales = cf.getScales();
        
        while (scales.nonEmpty())
        {
            if (type.equals(((Polylist)scales.first()).second()))
                return (String)((Polylist)scales.first()).first();
            
            scales = scales.rest();
        }
        
        return null;
    }
    
    public Polylist getPriority()
    {
        if (getName().equals(NOCHORD))
        {
          return Polylist.nil;
        }
         
    	String root = symbol.getRootString();
	ChordForm chordForm = symbol.getChordForm();
        if( chordForm == null )
        {
          return Polylist.nil;
        }
        
	Key key = chordForm.getKey(root);

	return chordForm.getPriority(root, key);
    }

    /**
     * Sets the Chord's name.
     * @param name a String of the Chord's name
     */
    public void setName(String name) {
        this.symbol = ChordSymbol.makeChordSymbol(name);
    }

    /**
     * Returns the Chord's root.
     * @return the Chord's root
     */
    public String getRoot() {
        return symbol.getRoot().toString();        
    }
    
    
     /**
     * Returns the Chord's root.
     * @return the Chord's root
     */
    public PitchClass getRootPitchClass() {
    return PitchClass.getPitchClass(getRoot());        
    }
    
   
    /**
     * Return the type of note relative to a this chord
     * used, e.g. in coloration.
     */
    
    public int getTypeIndex(Note note)
    {
        return symbol.getTypeIndex(note);
    }

   /**
     * Return the type of note relative to a this and the next note and chord.
     * Intended for use in bass lines, this classification prefers approach tone
     * over color tone, but that can be changed.
     */
    
    public int classify(Note note, Note nextNote, Chord nextChord)
    {
      int singleClassification = symbol.getTypeIndex(note);
      
      if( singleClassification == CHORD_TONE )
      {
        return CHORD_TONE;
      }
      if( nextNote == null || nextChord == null )
        {
         return singleClassification;
        }
      switch( nextChord.getTypeIndex(nextNote) )
          {
            case CHORD_TONE:
            case COLOR_TONE:
              int delta = note.getPitch() - nextNote.getPitch();
              if( delta == 1 || delta == -1 )
                {
                return APPROACH_TONE;
                }
                break;
            default: break;
            }
      return singleClassification;
      }

    /**
     * Returns a copy of the Chord.
     * @return Chord    a copy of the Chord
     */
    public Chord copy() {
        return new Chord(symbol, rhythmValue);
    }
    
    /**
     * Returns a String representation of the Chord.
     * @return String   a String representation of the Chord
     */
    @Override
    public String toString() {
        return "CHORD: " +
               "[Name = " + getName() +
               "][Voicing = " + voicing +
               "][RhythmValue = " + rhythmValue + "]";
    }


    /**
     * Adds the Chord at the specified time on the specified Track and channel
     * in the specified Sequence, then returns the time that a sequential
     * Chord should be added.
     * @param seq       the Sequence to add the Chord to
     * @param time      the time to start the Chord at
     * @param ch        the channel to put the Chord on
     * @return long      the time that a sequential Chord should start
     */
    public long render(MidiSequence seq, 
                       long time, 
                       int ch, 
                       Style style, 
                       Chord prev, 
                       int rhythmValue, 
                       int transposition, 
                       int endLimitIndex)
                throws InvalidMidiDataException {

        Track track = seq.getChordTrack();
        
        int dynamic = MAX_VOLUME;
        
        ChordSymbol prevSym = null;
        if(prev != null)
            prevSym = prev.symbol;

        // set the note offtime so we can turn all the notes in the chord
        // off at once
        long offTime = Math.min(endLimitIndex, time + rhythmValue) * seq.getResolution() / BEAT;
        
        //System.out.println("Chord: time = " + time + " rhythmValue = " + rhythmValue + " endLimitIndex = " + endLimitIndex + " offTime = " + offTime);

        MidiEvent evt;

        Polylist chordTones = Advisor.getChordTones(symbol);
        if(chordTones == null)
            return offTime;

        int numNotes = chordTones.length();
        
        if( numNotes > maxNotes ) {
            numNotes = maxNotes;
        }

        int LIMIT = 5;
        
        int MIN = 4;

        PitchClass bass = symbol.getBass();

        // Include bass pitch in voicing if fewer than MIN notes in chord.
        
        Polylist usedTones = numNotes < MIN ? chordTones : NoteSymbol.enhDrop(chordTones, bass);

        usedTones = usedTones.prefix(LIMIT);

        PolylistEnum tones = usedTones.elements();

        int pitch = Key.makeNote(bass.toString(), C2, 0).getPitch();
        //Trace.log(2, "\nrendering chord " + this + ", tones = " + usedTones + " transposition = " + transposition);

        int actualPitch = pitch + transposition;
        //Trace.log(2, "bass " + actualPitch);
        
        if( voicing == null )
          {
          voicing = ChordPattern.findVoicing(symbol, style.getChordBase(), style);
          setVoicing(voicing);
          }
        
         if(voicing != null) {
            PolylistEnum notes = voicing.elements();
            while( notes.hasMoreElements() ) {
                NoteSymbol ns = (NoteSymbol)notes.nextElement();
                pitch = ns.getMIDI();
                evt = MidiSynth.createNoteOnEvent(ch, pitch, dynamic, time);
                track.add(evt);
                //Trace.log(2, "adding to track " + track + " note on " + " channel = " + ch + " pitch = " + pitch + " time = " + time);

                evt = MidiSynth.createNoteOffEvent(ch, pitch, dynamic, offTime);
                track.add(evt);
                //Trace.log(2, "adding to track " + track + " note off " + " channel = " + ch + " pitch = " + pitch + " time = " + offTime);
            }
            evt = MidiSynth.createNoteOnEvent(ch, actualPitch, dynamic, time);
            track.add(evt);
            //Trace.log(2, "adding to track " + track + " note on " + " channel = " + ch + " pitch = " + pitch + " time = " + time);
            evt = MidiSynth.createNoteOffEvent(ch, actualPitch, dynamic, offTime);
            track.add(evt);
            //Trace.log(2, "adding to track " + track + " note off " + " channel = " + ch + " pitch = " + pitch + " time = " + offTime);
        }

        return offTime;
    }
    
    
    public void setVoicing(Polylist voicing)
    {
    //System.out.println("setting voicing " +  this);

    this.voicing = voicing;
    }
    
    public Polylist getVoicing()
    {
      return voicing;
    }
    
    /**
     * Writes the Chord to the passed BufferedWriter.
     * @param out       the BufferedWriter to write the Chord to
     */
    public void save(BufferedWriter out) throws IOException {
		out.write(getName() + ' ' + rhythmValue);
		out.newLine();
    }
    
private static ArrayList<String> names;
private static ArrayList<Integer> durations;
static int accumulation;
static int barsOnLine;

    public String toLeadsheet()  // FIX: This is temporary, used by trace, not leadsheet
      {
      return getName();
      }

    public static void initSaveToLeadsheet()
      {
      names = new ArrayList<String>();
      durations = new ArrayList<Integer>();
      accumulation = 0;
      barsOnLine = 0;      
      }


    public  static void flushChordBuffer(BufferedWriter out, int[] metre, boolean lineBreaks, boolean finalBar) throws IOException
      {
      int duration;
      String name;
      int residual = 0;	// residual duration of any long chord

      if( !durations.isEmpty() )
        {
        int spaceRemaining = metre[0]*(WHOLE/metre[1]);

        // Find gcd of durations

        Iterator<Integer> e = durations.iterator();

        // initialize gcd with duration of first chord
        // or the entire space, whichever is less

        int gcd = e.next().intValue();

        if( gcd >= spaceRemaining )
          {
          // first chord is long
          residual = gcd - spaceRemaining;
          gcd = spaceRemaining;
          }

        spaceRemaining -= gcd;

//System.out.println("-------------------------------------------------");
//System.out.println("gcd = " + gcd + " space = " + spaceRemaining + " residual = " + residual);

        // stop when a duration as long as a bar or more is encountered

        while( e.hasNext() && residual == 0 )
          {

          duration = e.next().intValue();
//System.out.println("duration = " + " space = " + spaceRemaining);
          if( duration >= spaceRemaining )
            {
//System.out.println("residual exists: duration = " + " space = " + spaceRemaining);
            residual = duration - spaceRemaining;
            duration = spaceRemaining;
            }

          spaceRemaining -= duration;

//System.out.println(" space = " + spaceRemaining);

          while( duration != gcd )
            {
            if( duration < gcd )
              {
              gcd -= duration;
              }
            else
              {
              duration -= gcd;
              }
            }
          }

//System.out.println("\nfinal gcd = " + gcd + " space = " + spaceRemaining + " residual = " + residual);

        // Now output the chords, up to but not past the long chord

        e = durations.iterator();
        Iterator<String> f = names.iterator();

        spaceRemaining = metre[0]*(WHOLE/metre[1]);

        while( e.hasNext() )
          {
          name = (String)f.next();			// chord name
          
          if( name != null )	// handle extended duration if necessary
            {
            out.write(name + " ");
            }
          else
            {
            out.write(SLASHSTRING + " ");
            }

          duration = e.next().intValue();	// chord duration

//System.out.println("\nchord = " + name + " duration = " + duration + " space = " + spaceRemaining);

          if( duration > spaceRemaining )
            {
            duration = spaceRemaining;
            }

          spaceRemaining -= duration;

          accumulation -= duration;	// caution: accumulation is static

          if( duration > gcd )
	    while( duration > gcd )
	      {
	      out.write(SLASHSTRING + " ");
	      duration -= gcd;
	      }
          }
        }

      names = new ArrayList<String>();
      durations = new ArrayList<Integer>();

      // carry any residual value of a long chord to the next measure

      if( residual > 0 )
        {
	// residual value
        names.add(null);	// extended duration, don't repeat name
                                // else chord will be struck again.
        durations.add(residual);
//System.out.println("\nresidual added back: " + name + " duration = " + residual);
        }

      if( finalBar ) out.write(BARSTRING + " ");

      barsOnLine++;
      if( lineBreaks && barsOnLine >= maxBarsOnLine )
        {
        out.newLine();
        barsOnLine = 0;
        }
      }

    /**
     * Writes the Chord to the passed BufferedWriter in Leadsheet notation.
     * @param out       the BufferedWriter to write the Chord to
     */
    public void saveLeadsheet(BufferedWriter out, int[] metre) throws IOException
      {
      saveLeadsheet(out, metre, true); // use linebreaks
      }

    public void saveLeadsheet(BufferedWriter out, int[] metre, boolean lineBreaks) throws IOException {
        names.add(getName());
        accumulation += rhythmValue;
        durations.add(new Integer(rhythmValue));
	int beatValue = WHOLE / metre[1];
        int beatsPerMeasure = beatValue*metre[0];
//System.out.println("metre = " + metre[0] + "/" + metre[1] + ", beatsPerMeasure = " + beatsPerMeasure);
        while( accumulation >= beatsPerMeasure )
          {
          // end measure and flush buffer
          flushChordBuffer(out, metre, lineBreaks, true);
          }
    }
    
    /**
     * Reads the BufferedReader and creates a new Chord.
     * @param in        the BufferedReader to read the Chord from
     * @return Chord    the Chord read from the BufferedReader
     */
    public static Chord open(BufferedReader in) throws IOException {
    	String str = in.readLine();
    	if(str.equals(""))
    		return null;
    	
        int index = 0;
        
        String name = "";
        while(str.charAt(index) != ' ') {
        	name += str.charAt(index);
        	index++;
        }
        
        index++;
        String rv = "";
        while(index < str.length()) {
        	rv += str.charAt(index);
        	index++;
        }
        
        return new Chord(name, Integer.decode(rv));
    }

    /**
     * Returns the ChordSymbol for this Chord
     * @return the ChordSymbol for this Chord
     */
    public ChordSymbol getChordSymbol()
      {
      return symbol;
      }
    
    
    /**
     * Returns the quality (e.g. M7, m7, o, etc. as a String) for this Chord
     * If the quality is just a major triad, then quality is the empty string.
     */
    public String getQuality()
      {
        return symbol.getQuality();
      }
    
    public boolean isNOCHORD()
      {
        return symbol.isNOCHORD();
      }
    
    public int getRootSemitones()
      {
        return symbol.getRoot().getSemitones();
      }
}
