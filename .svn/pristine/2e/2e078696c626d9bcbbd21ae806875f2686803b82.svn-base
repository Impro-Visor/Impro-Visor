/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.lickgen.transformations;

import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.Duration;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.lickgen.LickGen;
import imp.lickgen.NoteConverter;
import imp.util.ErrorLog;
import polya.*;
import java.util.*;
/**
 * Class that applies the functions of the Transformational Grammar
 * 
 * @author Alex Putman
 */
public class Evaluate implements Function1{
    
public Polylist frame;
String op;
Object val;
public boolean trace;

/**
 * 
 * @param frame         the association list used for variables
 */
Evaluate(Polylist frame)
{
    this(frame, null, null);
}

/**
 * 
 * @param frame         the association list used for variables
 * @param op            the operator to be applied to each element of the
 *                      evaluated list
 */
Evaluate(Polylist frame, String op)
{
    this(frame, op, null);
}

/**
 * 
 * @param frame         the association list used for variables
 * @param op            the operator to be applied to each element of the
 *                      evaluated list
 * @param val           the value associated with the operator to be applied
 */
Evaluate(Polylist frame, String op, Object val)
{
    this.trace = false;
    this.frame = frame;
    this.op = op;
    this.val = val;
}

/**
 * get the value of a variable from the frame
 * @param var       The variable name
 * @return 
 */
public Object getVar(Object var)
{
    return frame.assoc(var).second();
}

/**
 * Set a variable name associated to the value to the frame
 * @param var           variable name
 * @param value         variable value
 */
public void setVar(Object var, Object value)
{
    this.frame = frame.cons(Polylist.list(var,value));
}

/**
 * Set a variable name associated to a NoteChordPair to the frame
 * @param var           variable name
 * @param note          note of variable   
 * @param chord         chord of note
 */
public void setNoteVar(Object var, Note note, Chord chord)
{
    NoteChordPair pair = new NoteChordPair(note,chord);
    setVar(var, pair);
}

/**
 * Checks if an object is a constant
 */  
public boolean isConstant(Object obj)
{
    if(!isVariable(obj) && !isFunction(obj))
        return true;
    return false;
}

/**
 * Checks if an object is a variable
 */  
public boolean isVariable(Object obj)
{
    
    return (frame.assoc(obj)!=null);
}

/**
 * checks if an object is a function
 * @param obj
 * @return 
 */
public boolean isFunction(Object obj)
{
    if(obj instanceof Polylist 
       && !((Polylist)obj).isEmpty() 
       && Operators.fromGrammarName(((Polylist)obj).first().toString())!=null)
        return true;
    return false;
}

/**
 * returns the variable frame 
 * @return 
 */
public Polylist getFrame()
{
    return frame;
}

/**
 * Used to map a function to a Polylist
 * @param x         Polylist to map function to
 * @return          result
 */
public Object apply(Object x) {
    return evaluate(x);
}

/**
 * Enum that holds the string values that represent each operator in the grammar
 * and their corresponding string representation in the java implementation.
 * Allows for the use of switch case.
 */ 
public enum Operators {
  // Idiomatic Java names. You could ignore those if you really want,
  // and overload the constructor to have a parameterless one which calls
  // name() if you really want.
    
  // "//" denotes that that function is in the documentation panel
  PRINT_NOTE("print-note"), // only for debugging, not program
  AND("and"),//
  OR("or"),//
  NOT("not"),//
  IF("if"),//
  EQUALS("="),//
  ABSOLUTE_VALUE("abs"),//
  GR(">"),//
  GR_EQ(">="),//
  LT("<"),//
  LT_EQ("<="),//
  MEMBER("member"),//
  CHORD_EQUALS("chord="),//
  CHORD_FAMILY("chord-family"),
  TRIPLET("triplet?"),//
  QUINTUPLET("quintuplet?"),//
  REST("rest?"),//
  DURATION("duration"),//
  DURATION_EQ("duration="),//
  DURATION_ADDITION("duration+"),//
  DURATION_SUBTRACTION("duration-"),//
  DURATION_GR("duration>"),//
  DURATION_GR_EQ("duration>="),//
  DURATION_LT("duration<"),//
  DURATION_LT_EQ("duration<="),//
  PITCH_ADDITION("pitch+"),//
  PITCH_SUBTRACTION("pitch-"),//
  PITCH_GR("pitch>"),//
  PITCH_GR_EQ("pitch>="),//
  PITCH_LT("pitch<"),//
  PITCH_LT_EQ("pitch<="),//
  NOTE_CATEGORY("note-category"),//
  RELATIVE_PITCH("relative-pitch"),//
  ABSOLUTE_PITCH("absolute-pitch"),//
  SCALE_DURATION("scale-duration"),//
  NOTE_DURATION_ADDITION("add-duration"),//
  NOTE_DURATION_SUBTRACTION("subtract-duration"),//
  SET_DURATION("set-duration"),//
  SET_RELATIVE_PITCH("set-relative-pitch"),//
  TRANSPOSE_DIATONIC("transpose-diatonic"),//
  TRANSPOSE_CHROMATIC("transpose-chromatic"),//
  MAKE_REST("make-rest"),//
  GET_NOTE("get-note"); // Only used in transformation.java

  public static final Map<String, Operators> nameToValueMap;

  static {
    // Really I'd use an immutable map from Guava...
    nameToValueMap = new HashMap<String, Operators>();
    for (Operators op : EnumSet.allOf(Operators.class)) {
      nameToValueMap.put(op.grammarName, op);
    }
  }

  public final String grammarName;

  private Operators(String grammarName) {
    this.grammarName = grammarName;
  }

  public String getGrammarName() {
    return grammarName;
  }

  public static Operators fromGrammarName(String grammarName) {
    return nameToValueMap.get(grammarName);
  }
}


/**
 * Functions for mapping
 */ 


/**
 * Functions that are applied in the transformations
 */    
public Object evaluate(Object sent)
{
    Polylist statement;
    //System.out.println(sent.toString());
    if(sent == null)
        return null;
    if(isConstant(sent) && op == null)
        return sent;
    else if(isVariable(sent) && op == null)
        return getVar(sent);
    if(op != null)
        statement = new Polylist(sent,new Polylist());
    else
        statement = (Polylist) sent;
    Object operator;
    Polylist args;
    if(op == null)
    {
        operator = statement.first();
        args = statement.rest();
    }
    else
    {
        if(val == null)
            args = statement;
        else
            args = statement.cons(val);
        operator = op;
    }
    if(trace)
    {
        System.out.println("\t\t\t\t\tOperator: " + operator.toString() + 
                "\n\t\t\t\t\tArgs: " + args.toString());
    }
    Polylist evaledArgs = args.map(new Evaluate(frame)).flatten();
    if(evaledArgs.member(null) && 
            Operators.fromGrammarName(operator.toString()) != Operators.OR && 
            Operators.fromGrammarName(operator.toString()) != Operators.MEMBER)
        return null;
    Object returnVal = null;
    
    if(operator instanceof String)
    {
        Object firstArg;
        Object secondArg;
        switch(Operators.fromGrammarName(operator.toString()))
        {
            case AND:
                returnVal = and(evaledArgs);
                break;
                
            case OR:
                returnVal = or(evaledArgs);
                break;
                
            case NOT:
                returnVal = not(evaledArgs);
                break;
                
            case IF:
                returnVal = if_statement(evaledArgs);
                break;
                
            case EQUALS:
                returnVal = equals(evaledArgs);
                break;
                
            case ABSOLUTE_VALUE:
                returnVal = absolute_value(evaledArgs);
                break;
                
            case GR:
                returnVal = greater_than(evaledArgs);
                break;
                
            case GR_EQ:
                returnVal = greater_than_equals(evaledArgs);
                break;
                
            case LT:
                returnVal = less_than(evaledArgs);
                break;
                
            case LT_EQ:
                returnVal = less_than_equals(evaledArgs);
                break;
                
            case MEMBER:
                returnVal = member(evaledArgs);
                break;
                
            case CHORD_EQUALS:
                returnVal = chord_equals(evaledArgs);
                break;
                
            case CHORD_FAMILY:
                returnVal = chord_family(evaledArgs);
                break;
                
            case TRIPLET:
                returnVal = triplet(evaledArgs);
                break;
                
            case QUINTUPLET:
                returnVal = quintuplet(evaledArgs);
                break;
                
            case REST:
                returnVal = rest(evaledArgs);
                break;
            
            case DURATION:
                returnVal = duration(evaledArgs);
                break;
            
            case DURATION_ADDITION:
                returnVal = duration_addition(evaledArgs);
                break;
                
            case DURATION_SUBTRACTION:
                returnVal = duration_subtraction(evaledArgs);
                break;
            
            case DURATION_EQ:
                returnVal = duration_eq(evaledArgs);
                break;
                
            case DURATION_GR:
                returnVal = duration_gr(evaledArgs);
                break;
                
            case DURATION_GR_EQ:
                returnVal = duration_gr_eq(evaledArgs);
                break;
                
            case DURATION_LT:
                returnVal = duration_lt(evaledArgs);
                break;
                
            case DURATION_LT_EQ:
                returnVal = duration_lt_eq(evaledArgs);
                break;
                
            case NOTE_CATEGORY:
                returnVal = note_category(evaledArgs);
                break;
                
            case RELATIVE_PITCH:
                returnVal = relative_pitch(evaledArgs);
                break;
                
            case ABSOLUTE_PITCH:
                returnVal = absolute_pitch(evaledArgs);
                break;
            
            case PITCH_ADDITION:
                returnVal = pitch_addition(evaledArgs);
                break;
                
            case PITCH_SUBTRACTION:
                returnVal = pitch_subtraction(evaledArgs);
                break;
                
            case PITCH_GR:
                returnVal = pitch_gr(evaledArgs);
                break;
                
             case PITCH_GR_EQ:
                returnVal = pitch_gr_eq(evaledArgs);
                break;
                
             case PITCH_LT:
                returnVal = pitch_lt(evaledArgs);
                break;
                
             case PITCH_LT_EQ:
                returnVal = pitch_lt_eq(evaledArgs);
                break;
                
            case SCALE_DURATION:
                double firstNum = readNumber(evaledArgs.first().toString());
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.SCALE_DURATION.getGrammarName(), 
                                                                   firstNum)).flatten();
                else
                {
                    returnVal = scale_duration((NoteChordPair)evaledArgs.rest().first(),(double)firstNum);
                }
                break;
            case NOTE_DURATION_ADDITION:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.NOTE_DURATION_ADDITION.getGrammarName(), 
                                                                   firstArg)).flatten();
                else
                {
                    returnVal = note_duration_addition((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case NOTE_DURATION_SUBTRACTION:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.NOTE_DURATION_SUBTRACTION.getGrammarName(), 
                                                                   firstArg)).flatten();
                else
                {
                    returnVal = note_duration_subtraction((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case SET_DURATION:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.SET_DURATION.getGrammarName(), 
                                                                   firstArg)).flatten();
                else
                {
                    returnVal = set_duration((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case SET_RELATIVE_PITCH:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.SET_RELATIVE_PITCH.getGrammarName(), 
                                                                   firstArg)).flatten();
                else
                {
                    returnVal = set_relative_pitch((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case TRANSPOSE_DIATONIC:
                firstArg = evaledArgs.first().toString();
                
                
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.TRANSPOSE_DIATONIC.getGrammarName(), 
                                                                   firstArg)).flatten();
                else
                {
                    returnVal = transpose_diatonic((NoteChordPair)evaledArgs.second(),firstArg.toString());
                }
                break;
            case TRANSPOSE_CHROMATIC:
                firstArg = evaledArgs.first().toString();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(frame, 
                                                                   Operators.TRANSPOSE_CHROMATIC.getGrammarName(), 
                                                                   firstArg)).flatten();
                else
                {
                    returnVal = transpose_chromatic((NoteChordPair)evaledArgs.rest().first(),readNumber(firstArg.toString()));
                }
                break;
            case MAKE_REST:
                if(evaledArgs.length() != 1)
                    returnVal = evaledArgs.map(new Evaluate(frame, 
                                                            Operators.MAKE_REST.getGrammarName())).flatten();
                else
                {
                    returnVal = make_rest((NoteChordPair)evaledArgs.first());
                }
                break;
            case GET_NOTE:
                if(evaledArgs.length() > 1)
                    returnVal = evaledArgs.map(new Evaluate(frame, 
                                                            Operators.GET_NOTE.getGrammarName())).flatten();
                else
                {
                    returnVal = new Polylist(get_note(evaledArgs), new Polylist());
                }
                break;
            default:
        }
    }
    else
        returnVal = null;
    if(trace)
    {
        if(returnVal == null)
            System.out.println("\t\t\t\t\tReturned: NULL");
        else
            System.out.println("\t\t\t\t\tReturned: " + returnVal.toString());
    }
    return returnVal;
}

/*
******************************************************************************** 
START OF FUNCTIONS FOR TRANSFORMATIONAL GRAMMAR
********************************************************************************
*/

/**
 * No argument can return false
 */ 
public Boolean and(Polylist evaledArgs)
{
    if(evaledArgs.member(Boolean.FALSE))
        return false;
    return true;
}
/**
 * Atleast one argument must return true
 */
public Boolean or(Polylist evaledArgs)
{
    if(evaledArgs.member(Boolean.TRUE))
        return true;
    return false;
}
/**
 * Returns the opposite of the boolean value given
 */
public Boolean not(Polylist evaledArgs)
{
    Boolean firstVal = (Boolean) evaledArgs.first();
    return !firstVal;
}
/**
 * If the first argument returns true, return the second argument, else the
 * third argument
 */
public Object if_statement(Polylist evaledArgs)
{
    if(evaledArgs.length() < 3)
        ErrorLog.log(ErrorLog.WARNING, "Not enough arguments for if statement "
                + "in transformational grammar");
    if(evaledArgs.length() > 3)
        ErrorLog.log(ErrorLog.WARNING, "Too many arguments for if statement "
                + "in transformational grammar");
    
    if((Boolean)evaledArgs.first())
        return evaledArgs.second();
    else
        return evaledArgs.third();
}
/**
 * Returns true if the first argument equals the second argument
 */
public Boolean equals(Polylist evaledArgs)
{
    if(evaledArgs.length() < 2)
        ErrorLog.log(ErrorLog.WARNING, "Not enough arguments for = statement "
                + "in transformational grammar");
    if(evaledArgs.length() > 2)
        ErrorLog.log(ErrorLog.WARNING, "Too many arguments for = statement "
                + "in transformational grammar");
    
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    
    if(firstArg == null || secondArg == null)
        return false;
    if(firstArg.toString().matches("(-?)([\\d]*)(\\.?)([\\d])*") 
       && secondArg.toString().matches("(-?)([\\d]*)(\\.?)([\\d])*"))
    {
        double firstNum = Double.parseDouble(firstArg.toString());
        double secondNum = Double.parseDouble(secondArg.toString());
        return (firstNum == secondNum);
    }
    else
        return firstArg.equals(secondArg);
}
/**
 * returns the absolute value of a number in double form
 */ 
public double absolute_value(Polylist evaledArgs)
{
    double firstArg = readNumber(evaledArgs.first().toString());
    return Math.abs(firstArg);
}

public boolean greater_than(Polylist evaledArgs)
{
    double firstArg = readNumber(evaledArgs.first().toString());
    double secondArg = readNumber(evaledArgs.second().toString());
    return (firstArg > secondArg);
}

public boolean greater_than_equals(Polylist evaledArgs)
{
    double firstArg = readNumber(evaledArgs.first().toString());
    double secondArg = readNumber(evaledArgs.second().toString());
    return (firstArg >= secondArg);
}

public boolean less_than(Polylist evaledArgs)
{
    double firstArg = readNumber(evaledArgs.first().toString());
    double secondArg = readNumber(evaledArgs.second().toString());
    return (firstArg < secondArg);
}

public boolean less_than_equals(Polylist evaledArgs)
{
    double firstArg = readNumber(evaledArgs.first().toString());
    double secondArg = readNumber(evaledArgs.second().toString());
    return (firstArg <= secondArg);
}
/**
 * if the first object is contained in the Polylist second argument, return true
 */ 
public Boolean member(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    if(firstArg == null)
        return null;
    Polylist secondArg = evaledArgs.rest();
    if(secondArg.member(firstArg))
    {
        return true;
    }
    else 
    {
        if(firstArg.toString().matches("(\\d)*(\\.)?(\\d)*"))
        {
            return secondArg.member(Double.parseDouble(firstArg.toString()));
        }
        return false;
    }
}
/**
 * returns true if the given note or duration string is a triplet
 */ 
public Boolean triplet(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    
    String dur;
    
    if(firstArg instanceof NoteChordPair)
        dur = Note.getDurationString(((NoteChordPair)firstArg).note.getRhythmValue());
    else
        dur = firstArg.toString();
    
    return dur.contains("/3");
}
/**
 * if the first object is contained in the Polylist second argument, return true
 */ 
public Boolean quintuplet(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    
    String dur;
    
    if(firstArg instanceof NoteChordPair)
        dur = Note.getDurationString(((NoteChordPair)firstArg).note.getRhythmValue());
    else
        dur = firstArg.toString();
    
    return dur.contains("/5");
}
/**
 * if the first object is contained in the Polylist second argument, return true
 */ 
public Boolean rest(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    
    String dur;
    
    if(firstArg instanceof NoteChordPair)
        return ((NoteChordPair)firstArg).note.isRest();
    else
        return null;
    
}
/**
 * return true if both notes-chords given have the same chord
 */ 
public boolean chord_equals(Polylist evaledArgs)
{
    NoteChordPair pair1 = (NoteChordPair) evaledArgs.first();
    NoteChordPair pair2 = (NoteChordPair) evaledArgs.second();
    return pair1.chord.getChordSymbol().equals(pair2.chord.getChordSymbol());
}
/**
 * return the chord family of a note
 */ 
public String chord_family(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    Chord chord = pair.chord;
    String family = chord.getFamily();
    if(family == null)
        return "none";
    else
        return family;
}
/**
 * return the duration of a note in lead sheet notation
 */ 
public String duration(Polylist evaledArgs)
{
    Note note = ((NoteChordPair) evaledArgs.first()).note.copy();
    return Note.getDurationString(note.getRhythmValue());
}
/**
 * return the string duration of the duration addition of both args
 */ 
public String duration_addition(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    
    int firstDur = 0;
    int secondDur = 0;
    
    if(firstArg instanceof NoteChordPair)
        firstDur = ((NoteChordPair)firstArg).note.getRhythmValue();
    else
        firstDur = Duration.getDuration0(firstArg.toString());
    
    if(secondArg instanceof NoteChordPair)
        secondDur = ((NoteChordPair)secondArg).note.getRhythmValue();
    else
        secondDur = Duration.getDuration0(secondArg.toString());
    
    return Note.getDurationString(firstDur + secondDur);
}
/**
 * return the string duration of the second arg subtracted from the first arg
 */ 
public String duration_subtraction(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    
    int firstDur = 0;
    int secondDur = 0;
    
    if(firstArg instanceof NoteChordPair)
        firstDur = ((NoteChordPair)firstArg).note.getRhythmValue();
    else
        firstDur = Duration.getDuration0(firstArg.toString());
    
    if(secondArg instanceof NoteChordPair)
        secondDur = ((NoteChordPair)secondArg).note.getRhythmValue();
    else
        secondDur = Duration.getDuration0(secondArg.toString());
    
    int sub = firstDur - secondDur;
    if (sub < 0)
        return null;
    else
        return Note.getDurationString(sub);
}

private int getDuration(Object ob)
{
    if(ob instanceof NoteChordPair)
        return ((NoteChordPair)ob).note.getRhythmValue();
    else
        return Duration.getDuration0(ob.toString());
}
/**
 * returns true if two notes are equal in duration
 */
public Boolean duration_eq(Polylist evaledArgs)
{
    int dur1 = getDuration(evaledArgs.first());
    int dur2 = getDuration(evaledArgs.second());

    if(dur1 == dur2)
        return true;
    else 
        return false;
}
/**
 * return true if the duration of arg1 is greater than the duration of arg2
 */ 
public Boolean duration_gr(Polylist evaledArgs)
{
    int subtraction = getDuration(evaledArgs.first()) - getDuration(evaledArgs.second());
    
    if(subtraction > 0)
        return true;
    else   
        return false;
}
/**
 * return true if the duration of arg1 is greater than or equal to 
 * the duration of arg2
 */ 
public Boolean duration_gr_eq(Polylist evaledArgs)
{
    int subtraction = getDuration(evaledArgs.first()) - getDuration(evaledArgs.second());
    
    if(subtraction >= 0)
        return true;
    else   
        return false;
}
/**
 * return true if the duration of arg1 is less than the duration of arg2
 */ 
public Boolean duration_lt(Polylist evaledArgs)
{
    int subtraction = getDuration(evaledArgs.first()) - getDuration(evaledArgs.second());
    
    if(subtraction < 0)
        return true;
    else   
        return false;
}
/**
 * return true if the duration of arg1 is less than or equal to 
 * the duration of arg2
 */ 
public Boolean duration_lt_eq(Polylist evaledArgs)
{
    int subtraction = getDuration(evaledArgs.first()) - getDuration(evaledArgs.second());
    
    if(subtraction <= 0)
        return true;
    else   
        return false;
}
/**
 * return the note category of a note in a NoteChordPair
 */ 
public String note_category(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    int classify = LickGen.classifyNote(pair.note, pair.chord);
    switch(classify)
    {
        case LickGen.CHORD:
            return "C";
        case LickGen.COLOR:
            return "L";
        case LickGen.NOTE:
            return "X";
        default:
            return "R";
    }
}
/**
 * return the relative pitch of a note in a NoteChordPair
 */ 
public Object relative_pitch(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    Note note = pair.note;
    Chord chord = pair.chord;

    Polylist relNoteList = NoteConverter.noteToRelativePitch(note, chord);
    if(relNoteList.second().equals("0"))
        return null;
    String relPitch = relNoteList.second().toString();
    if(relPitch.matches("\\d*"))
        return Integer.parseInt(relPitch);
    else
        return relPitch;
}
/**
 * return the absolute pitch of a note in a NoteChordPair
 */ 
public String absolute_pitch(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    if(pair == null)
        return null;
    Note note = pair.note.copy();
    note.setRhythmValue(0);
    return note.toLeadsheet();
}
/**
 * Takes in 2 of the following: NoteChordPairs or a String relative pitch.
 * 
 * If at least one is a relative pitch, then the result will be both converted 
 * to relative pitches and added together.
 * 
 * Else the result will be both absolute pitches added together
 */ 
public Object pitch_addition(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    if(firstArg == null || secondArg == null)
        return null;
    // Choose the first chord of the set of notes incase neither args are notes
    // if the first arg is not a note
    
    if(firstArg.toString().matches("[b#]?(-?)(\\d)+"))
    {
        if(secondArg instanceof NoteChordPair)
        {
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            Chord chord = ((NoteChordPair)secondArg).chord;
            Polylist relNote = NoteConverter.noteToRelativePitch(secondNote, chord);
            if(relNote.second().equals("0"))
                return null;
            String relPitch = relNote.second().toString();
            return addRelPitch(relPitch, firstArg.toString());
        }
        else
        {
            return addRelPitch(firstArg.toString(), secondArg.toString());
        }
    }
    else if(firstArg instanceof NoteChordPair)
    {
        if(secondArg.toString().matches("[b#]?(-?)(\\d)+"))
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Chord chord = ((NoteChordPair)firstArg).chord;
            Polylist relNote = NoteConverter.noteToRelativePitch(firstNote, chord);
            if(relNote.second().equals("0"))
                return null;
            String relPitch = relNote.second().toString();
            return addRelPitch(relPitch, secondArg.toString());
        }
        else
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            return (firstNote.getPitch()+secondNote.getPitch())/2.0;
       }
    }
    else
        return null;
}
/**
 * Takes in 2 of the following: NoteChordPairs or String absolute pitch
 * or String relative pitch.
 * 
 * If at least one is a relative pitch, then the result will be both converted 
 * to relative pitches and subtracted.
 * 
 * Else the result will be both absolute pitches subtracted.
 */ 
    public Object pitch_subtraction(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    if(firstArg == null || secondArg == null)
        return null;
    // Choose the first chord of the set of notes incase neither args are notes
    // if the first arg is not a note
    
    if(firstArg.toString().matches("[b#]?(-?)(\\d)+"))
    {
        if(secondArg instanceof NoteChordPair)
        {
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            Chord chord = ((NoteChordPair)secondArg).chord;
            Polylist relNote = NoteConverter.noteToRelativePitch(secondNote, chord);
            if(relNote.second().equals("0"))
                return null;
            String relPitch = relNote.second().toString();
            StringBuilder minusPitch = new StringBuilder();
            while(relPitch.charAt(0) == 'b' || relPitch.charAt(0) == '#')
            {
                if(relPitch.charAt(0) == 'b')
                {
                    relPitch = relPitch.substring(1);
                    minusPitch.append("#");
                }
                else if(relPitch.charAt(0) == '#')
                {
                    relPitch = relPitch.substring(1);
                    minusPitch.append("b");
                }
            }
            int value = Integer.parseInt(relPitch);
            value = -1*value + 1;
            if(value == 0)
                value = 1;
            minusPitch.append(value);
            return addRelPitch(firstArg.toString(), minusPitch.toString());
        }
        else
        {
            String relPitch = secondArg.toString();
            StringBuilder minusPitch = new StringBuilder();
            while(relPitch.charAt(0) == 'b' || relPitch.charAt(0) == '#')
            {
                if(relPitch.charAt(0) == 'b')
                {
                    relPitch = relPitch.substring(1);
                    minusPitch.append("#");
                }
                else if(relPitch.charAt(0) == '#')
                {
                    relPitch = relPitch.substring(1);
                    minusPitch.append("b");
                }
            }
            
            int value = Integer.parseInt(relPitch);
            value = -1*value + 1;
            if(value == 0)
                value = 1;
            minusPitch.append(value);
            return addRelPitch(firstArg.toString(), minusPitch.toString());
        }
    }
    else if(firstArg instanceof NoteChordPair)
    {
        
        if(secondArg.toString().matches("[b#]?(-?)(\\d)+"))
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Chord chord = ((NoteChordPair)firstArg).chord;
            Polylist relNote = NoteConverter.noteToRelativePitch(firstNote, chord);
            if(relNote.second().equals("0"))
                return null;
            String relPitch = secondArg.toString();
            StringBuilder minusPitch = new StringBuilder();
            while(relPitch.charAt(0) == 'b' || relPitch.charAt(0) == '#')
            {
                if(relPitch.charAt(0) == 'b')
                {
                    relPitch = relPitch.substring(1);
                    minusPitch.append("#");
                }
                else if(relPitch.charAt(0) == '#')
                {
                    relPitch = relPitch.substring(1);
                    minusPitch.append("b");
                }
            }
            
            int value = Integer.parseInt(relPitch);
            value = -1*value + 1;
            if(value == 0)
                value = 1;
            minusPitch.append(value);
            return addRelPitch(relNote.second().toString(), minusPitch.toString());
        }
        else if(secondArg instanceof NoteChordPair)
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            return (firstNote.getPitch()-secondNote.getPitch())/2.0;
        }
        else
            return null;
    }
    else
    {
        return null;
    }
}
/**
 * returns if the first note or relative pitch given is higher than the second
 */ 
public Boolean pitch_gr(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return false;
    else if(subtraction.charAt(subtraction.length()-1) == '1')
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == '#')
            return true;
        else
            return false;
    }
    else
        return true;
}
/**
 * returns if the first note or relative pitch given is higher than or equal to 
 * the second
 */ 
public Boolean pitch_gr_eq(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return false;
    else if(subtraction.charAt(subtraction.length()-1) == '1')
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == 'b')
            return false;
        else
            return true;
    }
    else
        return true;
}
/**
 * returns if the first note or relative pitch given is lower than the second
 */ 
public Boolean pitch_lt(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return true;
    else if(subtraction.charAt(subtraction.length()-1) == '1')
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == 'b')
            return true;
        else
            return false;
    }
    else
        return false;
}
/**
 * returns if the first note or relative pitch given is lower than or equal to 
 * the second
 */ 
public Boolean pitch_lt_eq(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return true;
    else if(subtraction.charAt(subtraction.length()-1) == '1')
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == '#')
            return false;
        else
            return true;
    }
    else
        return false;
}
/**
 * returns a NoteChordPair with its note's duration multiplied by scale
 */ 
public NoteChordPair scale_duration(NoteChordPair pair, double scale)
{
    Note note = pair.note.copy();
    int dur = (int) (note.getRhythmValue()*scale);
    String durString = Note.getDurationString(dur);
    note.setRhythmValue(Duration.getDuration0(durString));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with its note's duration set to duration
 */ 
public NoteChordPair set_duration(NoteChordPair pair, String duration)
{
    Note note = pair.note.copy();
    note.setRhythmValue(Duration.getDuration0(duration));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with its note's duration added with duration
 */ 
public NoteChordPair note_duration_addition(NoteChordPair pair, String duration)
{
    Note note = pair.note.copy();
    int dur = note.getRhythmValue()+Duration.getDuration0(duration);
    String durString = Note.getDurationString(dur);
    note.setRhythmValue(Duration.getDuration0(durString));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with duration subtracted from pair's note's duration
 */ 
public NoteChordPair note_duration_subtraction(NoteChordPair pair, String duration)
{
    Note note = pair.note.copy();
    int dur = note.getRhythmValue()-Duration.getDuration0(duration);
    String durString = Note.getDurationString(dur);
    note.setRhythmValue(Duration.getDuration0(durString));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with the note created from a relative pitch
 */ 
public NoteChordPair set_relative_pitch(NoteChordPair pair, String relPitch)
{
    Note note = pair.note.copy();
    Polylist transposeNoteList = Polylist.PolylistFromString("X " + 
                                                             relPitch + 
                                                             " " + 
                                                             Note.getDurationString(note.getRhythmValue()));
    Chord chord = pair.chord;
    Note newNote = LickGen.makeRelativeNote(transposeNoteList, chord);
    return new NoteChordPair(newNote, chord);
}
/**
 * returns a NoteChordPair with the note pitch transposed diatonically 
 */ 
public NoteChordPair transpose_diatonic(NoteChordPair pair, String relPitch)
{
    if(pair.chord.isNOCHORD())
        return null;
    if(pair.note.isRest())
        return null;
    
    String returns = relative_pitch(new Polylist(pair, new Polylist())).toString();
    if(returns == null)
    {
        return null;
    }
    String initArg = returns;
    
    String totalSum = addRelPitch(returns, relPitch);
    int shiftOctaves = 0;
    
    String insert = "";
    while(totalSum.charAt(0) == 'b' || totalSum.charAt(0) == '#')
    {
        insert += totalSum.charAt(0);
        totalSum = totalSum.substring(1);
    }
    int origNumber = Integer.parseInt(totalSum);
    int number = 0;
    if(origNumber<0)
    {
        shiftOctaves += ((origNumber / 7) - 1);
        number = origNumber % 7;
        number += 8;
    }
    else
    {
        shiftOctaves += ((origNumber - 1) / 7);
        number = (origNumber - 1)%7 + 1;
    }
    totalSum = insert + number; 
    
    Polylist transposeNoteList = Polylist.PolylistFromString("X " + totalSum + " 4");
    Polylist transposeNoteListInit = Polylist.PolylistFromString("X " + initArg + " 4");
    Chord chord = pair.chord;
    Note fakeNote = LickGen.makeRelativeNote(transposeNoteList, chord);
    Note fakeInitNote = LickGen.makeRelativeNote(transposeNoteListInit, chord);
    Note note = pair.note.copy();
    int newPitch = fakeNote.getPitch()-fakeInitNote.getPitch();
    newPitch += Note.OCTAVE*shiftOctaves;
    note.shiftPitch(newPitch, 0);
    return new NoteChordPair(note, chord);
}
/**
 * returns a NoteChordPair with the note transposed chromatically 
 */ 
public NoteChordPair transpose_chromatic(NoteChordPair pair, double pitches)
{
    pitches *= 2;
    Note note = pair.note.copy();
    if(note.isRest())
        return null;
    note.shiftPitch((int)pitches, 0);
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with the note set to a rest
 */ 
public NoteChordPair make_rest(NoteChordPair pair)
{
    Note note = pair.note.copy();
    Note newNote = Note.makeRest(note.getRhythmValue());
    return new NoteChordPair(newNote, pair.chord);
}
/**
 * returns a NoteChordPair with the note transposed chromatically 
 */ 
public Note get_note(Polylist evaledArgs)
{
    Note note = ((NoteChordPair)evaledArgs.first()).note.copy();
    return note;
}

/*
******************************************************************************** 
END OF FUNCTIONS FOR TRANSFORMATIONAL GRAMMAR
********************************************************************************
*/

/**
 * Returns the double of a number in fraction form
 * @param num       String of a number in fraction form
 * @return 
 */
public double readNumber(String num)
{
    if(num.indexOf("/") == -1)
        return Double.parseDouble(num);
    String[] halves = num.split("/");
    return Arith.divide(Double.parseDouble(halves[0]), 
                        Double.parseDouble(halves[1])).doubleValue();
}

/**
 * Adds two relative pitches 
 * @param num1          Relative Pitch 1
 * @param num2          Relative Pitch 2
 * @return 
 */
public String addRelPitch(String num1, String num2)
{
    String augment1 = "";
    while(num1.charAt(0) == '#' || num1.charAt(0) == 'b')
    {
        augment1 += num1.charAt(0);
        num1 = num1.substring(1);
    }
    int numVal1 = Integer.parseInt(num1);
    
    String augment2 = "";
    while(num2.charAt(0) == '#' || num2.charAt(0) == 'b')
    {
        augment2 += num2.charAt(0);
        num2 = num2.substring(1);
    }
    int numVal2 = Integer.parseInt(num2);
    
    int addTotal = numVal2 + numVal1;
    if(numVal1 > 0)
        addTotal --;
    if(numVal2 > 0)
        addTotal --;
    String returnString;
    if(addTotal >= 0)
        addTotal ++;
    int flats = 0;
    int sharps = 0;
    for(char c1 : augment1.toCharArray())
    {
        if(c1 == 'b')
            flats++;
        else if(c1 == '#')
            sharps++;
    }
    for(char c2 : augment2.toCharArray())
    {
        if(c2 == 'b')
            flats++;
        else if(c2 == '#')
            sharps++;
    }
    int newFlats = flats - sharps;
    int newSharps = sharps - flats;
    String augments = "";
    while(newFlats > 0)
    {
        augments+= "b";
        newFlats--;
    }
    while(newSharps > 0)
    {
        augments+= "#";
        newSharps--;
    }
    returnString = augments + addTotal;
    return returnString;
}

/**
 * Returns the absolute Relative Pitch difference between two notes, not modding
 * the results
 * @param n1        First note that the second note subtracts from
 * @param n2        Second note
 * @param chord     Chord of n1
 * @return          the relative pitch difference
 */
public String absoluteRelPitchDiff(Note n1, Note n2, Chord chord)
{
    
    String rel1 = NoteConverter.noteToRelativePitch(n1, chord).second().toString();
    String rel2 = NoteConverter.noteToRelativePitch(n2, chord).second().toString();
    
    if(pitch_gr_eq(Polylist.PolylistFromString(rel1 + " " + rel2)) == null)
        return null;
    while(pitch_gr_eq(Polylist.PolylistFromString(rel1 + " " + rel2)) 
          && !(n1.getPitch() >= n2.getPitch())) 
    {
        rel2 = pitch_addition(Polylist.PolylistFromString(rel2 + " 8")).toString();
    }
    
    if(pitch_lt_eq(Polylist.PolylistFromString(rel1 + " " + rel2))  == null)
    {
        return null;
    }
    while(pitch_lt_eq(Polylist.PolylistFromString(rel1 + " " + rel2)) 
          && !(n1.getPitch() <= n2.getPitch()))
    {
        rel1 = pitch_addition(Polylist.PolylistFromString(rel1 + " 8")).toString();
    }
    String returning = pitch_subtraction(Polylist.PolylistFromString(rel1 + " " + rel2)).toString();
            
    return returning;
}
/**
 * A data-structure that holds a note and its chord. 
 */
public class NoteChordPair{
    public final Note note;
    public final Chord chord;
    
    public NoteChordPair(Note note, Chord chord)
    {
        this.note = note;
        this.chord = chord;
    }
    
    public String toString(){
        return "NoteChordPair";
    }
}
}