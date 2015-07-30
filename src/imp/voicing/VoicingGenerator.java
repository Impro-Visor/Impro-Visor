//jmusic for synthesis
package imp.voicing;

import java.util.ArrayList;

/**
 * This class is the actual voicing calculator, takes very specific parameters that can be randomized using hand manager and stored in automatic voicing settings.
 * Instructions: Initialize with priorities in constructor. 
 * Chord notes, hand bounds, and number of notes per hand should be set with each new chord.
 * The number of notes/hand, and hand bounds should have some degree of randomness.
 * Call calculate to actually calculate chord tones, and then get the integer array of midi notes to be played.
 *
 * The way this class works: all available notes are individually weighted, and array lists are created containing n duplicates of each note in each hand's range where n is the weight of the note.
 * Notes are picked randomly from the list.
 * Once a note is picked, the notes around it and exactly an octave (and multiple octaves) above and below are multiplied by the multiplier settings and new array lists are generated.
 * The process is repeated until the array list is empty or the number of desired notes is reached.
 * @author Daniel Scanteianu
 */
public class VoicingGenerator {

    public VoicingGenerator() {
    }

    /**
     * 
     * @param colorPriority-the weight the color notes should have. Make it between 0 and a big-ish number
     * @param maxPriority-the maximum weight the priority notes should have. should be greater than the max number of priority notes*the priority multiplier.
     * @param previousVoicingMultiplier- amount to multiply the weight of the notes in the previous voicing's weightings by. Default: 1. Make greater than 1 for voice leading.
     * @param halfStepAwayMultiplier-amount to multiply the weight of the notes half a step away from the previous chord Default: 1. Make greater than 1 for voice leading.
     * @param fullStepAwayMultiplier-amount to multiply the weight of the notes a full step away from the previous chord. Default: 1. Make greater than 1 for voice leading.
     * @param priorityMultiplier - amount of weight to remove from notes as priority decreases. Default 0 for equal probability.
     * @param repeatMultiplier - the amount to reduce(or increase) the priority of notes already selected for the chord in other octaves. for reduction, make between 0 and 1. Default 1.
     */
        public VoicingGenerator(int leftColorPriority,int rightColorPriority, int maxPriority, double previousVoicingMultiplier, double halfStepAwayMultiplier, double fullStepAwayMultiplier, double priorityMultiplier, double repeatMultiplier, double halfStepReducer, double fullStepReducer, boolean invertM9) {
        this.leftColorPriority = leftColorPriority;
        this.rightColorPriority=rightColorPriority;
        this.maxPriority = maxPriority;
        this.previousVoicingMultiplier = previousVoicingMultiplier;
        this.halfStepAwayMultiplier = halfStepAwayMultiplier;
        this.fullStepAwayMultiplier = fullStepAwayMultiplier;
        this.priorityMultiplier = priorityMultiplier;
        this.repeatMultiplier = repeatMultiplier;
        this.halfStepReducer = halfStepReducer;
        this.fullStepReducer = fullStepReducer;
        this.invertM9=invertM9;
    }
        
    public void getVoicingSettings(AutomaticVoicingSettings avs)
    {
        setFullStepAwayMultiplier(avs.getFullStepAwayMultiplier());
        setHalfStepAwayMultiplier(avs.getHalfStepAwayMultiplier());
        setMaxPriority(avs.getMaxPriority());
        setLeftColorPriority(avs.getLeftColorPriority());
        setRightColorPriority(avs.getRightColorPriority());
        setPreviousVoicingMultiplier(avs.getPreviousVoicingMultiplier());
        setRepeatMultiplier(avs.getRepeatMultiplier());
        setHalfStepReducer(avs.getHalfStepReducer());
        setFullStepReducer(avs.getFullStepReducer());
        setInvertM9(avs.getInvertM9());
        setVoiceAll(avs.getVoiceAll());
        setRootless(avs.getRootless());
        setMinInterval(avs.getMinInterval());
    }
    
    public void getHandSettings(HandManager hm)
    {
        setLowerLeftBound(hm.getLeftHandLowestNote());
        setUpperLeftBound(hm.getLeftHandLowestNote() + hm.getLeftHandSpread());
        setLowerRightBound(hm.getRightHandLowestNote());
        setUpperRightBound(hm.getRightHandLowestNote() + hm.getRightHandSpread());
        setNumNotesLeft(hm.getNumLeftNotes());
        setNumNotesRight(hm.getNumRightNotes());
    }
    /**
     * generates a voicing based on current parameters and stores it in the chord array accessible by get chord. 
     */
    public void calculate()
    {
        allLeftValues=new ArrayList<Integer>();
        allRightValues=new ArrayList<Integer>();
        leftHand=new ArrayList<Integer>();
        rightHand=new ArrayList<Integer>();
        
        int noteToAdd;
        int start=0;
        
        if(voiceAll)
        {
            //System.out.println("Driving VAN");
            //enable only chord notes
            for(int i=0; i<allMidiValues.length; i++)
            {
                allMidiValues[i]=0;
            }
            for(int p=0; p<priority.length; p++)
            {
                setupNote(priority[p], (int)(maxPriority*10-p*10*priorityMultiplier));
            }
            if(rootless)
                setupNote(root,0);
            
            //do usual calculations, modded to ensure all notes happen
            for(int i=0; i<priority.length; i++)
            {
                setupAllLeftValues();   
                if(!allLeftValues.isEmpty())
                {
                    if(i<priority.length)
                    {
                        noteToAdd=allLeftValues.get((int)(Math.random()*allLeftValues.size()));
                        leftHand.add(noteToAdd);
                        allMidiValues[noteToAdd]=0;
                        if(allMidiValues[noteToAdd+1]*halfStepReducer>0)
                            allMidiValues[noteToAdd+1]*=halfStepReducer;
                        if(allMidiValues[noteToAdd-1]*halfStepReducer>0)
                            allMidiValues[noteToAdd-1]*=halfStepReducer;
                        if(allMidiValues[noteToAdd+2]*halfStepReducer>0)
                            allMidiValues[noteToAdd+2]*=halfStepReducer;
                        if(allMidiValues[noteToAdd-2]*halfStepReducer>0)
                            allMidiValues[noteToAdd-2]*=halfStepReducer;
                        multiplyNotes(noteToAdd,0);

                    }
                }
                setupAllRightValues();
                if(!allRightValues.isEmpty())
                {
                    if(i<priority.length)
                    {
                        noteToAdd=allRightValues.get((int)(Math.random()*allRightValues.size()));
                        rightHand.add(noteToAdd);
                        allMidiValues[noteToAdd]=0;
                        if(allMidiValues[noteToAdd+1]*halfStepReducer>0)
                            allMidiValues[noteToAdd+1]*=halfStepReducer;
                        if(allMidiValues[noteToAdd-1]*halfStepReducer>0)
                            allMidiValues[noteToAdd-1]*=halfStepReducer;
                        if(allMidiValues[noteToAdd+2]*halfStepReducer>0)
                            allMidiValues[noteToAdd+2]*=halfStepReducer;
                        if(allMidiValues[noteToAdd-2]*halfStepReducer>0)
                            allMidiValues[noteToAdd-2]*=halfStepReducer;
                        multiplyNotes(noteToAdd,0);

                    }

                }
                
            }
            start=priority.length;
        }
        //begin normal algorithm
        initAllMidiValues();
        if(rootless)
                setupNote(root,0);
        for(int i:leftHand)
            allMidiValues[i]=0;
        for(int i:rightHand)
            allMidiValues[i]=0;
        if(previousVoicing!=null)
            weightPreviousVoicing();
        VoicingDebug.println((numNotesLeft+numNotesRight)+" #notes exp.");
        VoicingDebug.println(lowerRightBound+" lower right bound");
        for(int i = start; i<numNotesLeft || i<numNotesRight; i++)
        {
            setupAllLeftValues();   
            if(!allLeftValues.isEmpty())
            {
                if(i<numNotesLeft)
                {
                    noteToAdd=allLeftValues.get((int)(Math.random()*allLeftValues.size()));
                    leftHand.add(noteToAdd);
                    allMidiValues[noteToAdd]=0;
                    allMidiValues[noteToAdd+1]*=halfStepReducer;
                    allMidiValues[noteToAdd-1]*=halfStepReducer;
                    allMidiValues[noteToAdd+2]*=fullStepReducer;
                    allMidiValues[noteToAdd-2]*=fullStepReducer;
                    multiplyNotes(noteToAdd,repeatMultiplier);
                    for(int j=0; j<minInterval; j++)
                    {
                        allMidiValues[noteToAdd+i]=0;
                        allMidiValues[noteToAdd-i]=0;
                }
            }
            else
                VoicingDebug.println("LH EMPTY: Req:"+this.numNotesLeft+" act:");
                
            }
            setupAllRightValues();
            if(!allRightValues.isEmpty())
            {
                if(i<numNotesRight)
                {
                    noteToAdd=allRightValues.get((int)(Math.random()*allRightValues.size()));
                    rightHand.add(noteToAdd);
                    allMidiValues[noteToAdd]=0;
                    allMidiValues[noteToAdd+1]*=halfStepReducer;
                    allMidiValues[noteToAdd-1]*=halfStepReducer;
                    allMidiValues[noteToAdd+2]*=fullStepReducer;
                    allMidiValues[noteToAdd-2]*=fullStepReducer;
                    multiplyNotes(noteToAdd,repeatMultiplier);
                    for(int j=0; j<minInterval; j++)
                    {
                        allMidiValues[noteToAdd+i]=0;
                        allMidiValues[noteToAdd-i]=0;
                    }
                }
            
            }
            else
                VoicingDebug.println("RH EMPTY");
           // System.out.println("calculate called");
            if(invertM9)
            {
                invertM9th(leftHand, leftHand);
                invertM9th(leftHand, rightHand);
                invertM9th(rightHand, rightHand);
            }
            
            
        }
        
        
    }

    public double getHalfStepReducer() {
        return halfStepReducer;
    }

    public void setHalfStepReducer(double halfStepReducer) {
        this.halfStepReducer = halfStepReducer;
    }

    public double getFullStepReducer() {
        return fullStepReducer;
    }

    public void setFullStepReducer(double fullStepReducer) {
        this.fullStepReducer = fullStepReducer;
    }
    /**
     * this is for voice leading, makes it likelier to hit notes in or near the last voicing.
     */
    private void weightPreviousVoicing()
    {
        for(int n: previousVoicing)
        {
            allMidiValues[n]=(int) (allMidiValues[n]*previousVoicingMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n+1]=(int) (allMidiValues[n+1]*halfStepAwayMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n-1]=(int) (allMidiValues[n-1]*halfStepAwayMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n-2]=(int) (allMidiValues[n-2]*fullStepAwayMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n+2]=(int) (allMidiValues[n+2]*fullStepAwayMultiplier);
        }
    }
    /**
     * sets up the midi values for a new chord 
     */
    private void initAllMidiValues()
    {
        //start with everything at zero
        for(int i=0; i<allMidiValues.length; i++)
        {
            allMidiValues[i]=0;
        }
        for(int i=0; i<color.length; i++)
        {
            setupNote(color[i],leftColorPriority*10);
        }
        for(int i=0; i<color.length; i++)
        {
            setupNote(color[i],rightColorPriority*10,lowerRightBound);
        }
        for(int p=0; p<priority.length; p++)
        {
            setupNote(priority[p], (int)(maxPriority*10-p*10*priorityMultiplier));
        }
    }
    /**
     * sets up left array list
     */
    private void setupAllLeftValues() {
       allLeftValues=new ArrayList<Integer>();
       for(int i=lowerLeftBound; i<=upperLeftBound; i++)
       {
           for(int j=0; j<allMidiValues[i]; j++)
           {
               allLeftValues.add(i);
           }
       }
    }
    /**
     * sets up right array list
     */
    private void setupAllRightValues() {
       allRightValues=new ArrayList<Integer>();
       for(int i=lowerRightBound; i<=upperRightBound; i++)
       {
           for(int j=0; j<allMidiValues[i]; j++)
           {
               allRightValues.add(i);
           }
       }
    }
    /**
     * Sets up all of a certain note to a certain value in all octaves
     * @param midiValue the note (gets converted to mod12)
     * @param priority  the value to set up the note to
     */
    private void setupNote(int midiValue, int priority)
    {
        midiValue=midiValue%12;
        for(int i=midiValue; i<allMidiValues.length; i+=12)
        {
            allMidiValues[i]=priority;
        }
    }
    /**
     * Sets up all of a certain note to a certain value in all octaves above the note start
     * @param midiValue the note (gets converted to mod12)
     * @param priority  the value to set up the note to
     * @param start the note from which to start setting up the note
     */
    private void setupNote(int midiValue, int priority, int start)
    {
        midiValue=midiValue%12;
        for(int i=start; i<allMidiValues.length; i++)
        {
            if(i%12==midiValue)
                allMidiValues[i]=priority;
        }
    }
    /**
     * checks 2 lists for a minor 9th between them, and flips the interval to a maj. 7th. the lists may be the same list.
     * @param list1
     * @param list2 
     */
    private void invertM9th(ArrayList<Integer> list1, ArrayList<Integer> list2)
    {   
//        System.out.println("invm9");
        ArrayList<Integer> list3=new ArrayList<Integer>();
        ArrayList<Integer> list4;
//        for(int i: list1)
//            System.out.print(i+" ");
//        System.out.println("list1orig");
//        for(int j: list2)
//            System.out.print(j+" ");
//        System.out.println("list2orig");
        for(int i:list1)
        {
            boolean added=false;
            list4=new ArrayList<Integer>();
            for(int j:list2)
            {
                //System.out.println("invoked, i:"+ i+", j:"+j);
                if(j-i==13)
                {
                    if(added)
                        list3.remove(list3.size()-1);
                    list3.add(i+1);
                    added=true;
                    list4.add(j-1);
                    VoicingDebug.println("Inverted m9 i"+i+" "+j);
                }
                else{
                    list4.add(j);
                    if(!added)
                    {
                        list3.add(i);
                        added=true;
                    }
                }
            }
            
            list2=list4;
        }
        list1=list3;
        
//        for(int i: list1)
//            System.out.print(i+" ");
//        System.out.println("list1");
//        for(int j: list2)
//            System.out.print(j+" ");
//        System.out.println("list2");
    }
    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public int[] getPriority() {
        return priority;
    }

    public void setPriority(int[] priority) {
        this.priority = priority;
    }
    public int getLowerLeftBound() {
        return lowerLeftBound;
    }

    public void setLowerLeftBound(int lowerLeftBound) {
        this.lowerLeftBound = lowerLeftBound;
    }

    public int getUpperLeftBound() {
        return upperLeftBound;
    }

    public void setUpperLeftBound(int upperLeftBound) {
        this.upperLeftBound = upperLeftBound;
    }

    public int getLowerRightBound() {
        return lowerRightBound;
    }

    public void setLowerRightBound(int lowerRightBound) {
        this.lowerRightBound = lowerRightBound;
    }

    public int getUpperRightBound() {
        return upperRightBound;
    }

    public void setUpperRightBound(int upperRightBound) {
        this.upperRightBound = upperRightBound;
    }

    public int getNumNotesLeft() {
        return numNotesLeft;
    }

    public void setNumNotesLeft(int numNotesLeft) {
        this.numNotesLeft = numNotesLeft;
    }

    public int getNumNotesRight() {
        return numNotesRight;
    }

    public void setNumNotesRight(int numNotesRight) {
        this.numNotesRight = numNotesRight;
    }

    public int getLeftColorPriority() {
        return leftColorPriority;
    }

    public void setLeftColorPriority(int leftColorPriority) {
        this.leftColorPriority = leftColorPriority;
    }

    public int getRightColorPriority() {
        return rightColorPriority;
    }

    public void setRightColorPriority(int rightColorPriority) {
        this.rightColorPriority = rightColorPriority;
    }

   
    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public int[] getPreviousVoicing() {
        return previousVoicing;
    }

    public void setPreviousVoicing(int[] previousVoicing) {
        this.previousVoicing = previousVoicing;
    }

    public double getPreviousVoicingMultiplier() {
        return previousVoicingMultiplier;
    }

    public void setPreviousVoicingMultiplier(double previousVoicingMultiplier) {
        this.previousVoicingMultiplier = previousVoicingMultiplier;
    }

    public double getHalfStepAwayMultiplier() {
        return halfStepAwayMultiplier;
    }

    public void setHalfStepAwayMultiplier(double halfStepAwayMultiplier) {
        this.halfStepAwayMultiplier = halfStepAwayMultiplier;
    }

    public double getFullStepAwayMultiplier() {
        return fullStepAwayMultiplier;
    }

    public void setFullStepAwayMultiplier(double fullStepAwayMultiplier) {
        this.fullStepAwayMultiplier = fullStepAwayMultiplier;
    }

    public double getPriorityMultiplier() {
        return priorityMultiplier;
    }

    public void setPriorityMultiplier(double priorityMultiplier) {
        this.priorityMultiplier = priorityMultiplier;
    }

    public double getRepeatMultiplier() {
        return repeatMultiplier;
    }

    public void setRepeatMultiplier(double repeatMultiplier) {
        this.repeatMultiplier = repeatMultiplier;
    }
    private void multiplyNotes(int midiValue, double multiplier)
    {
        midiValue=midiValue%12;
        for(int i=midiValue; i<allMidiValues.length; i+=12)
        {
            allMidiValues[i]=(int)(allMidiValues[i]*multiplier);
        }
    }
    /**
     *  generates array with notes in LH
     * @return int array
     */
    public int[] getLeftHand()
    {
        int[] leftArray=new int[leftHand.size()];
        for(int i=0; i<leftHand.size(); i++)
        {
            leftArray[i]=leftHand.get(i);
        }
        return leftArray;
    }
    /**
     * generates array with notes in RH
     * @return int array
     */
    public int[] getRightHand()
    {
        int[] rightArray=new int[rightHand.size()];
        for(int i=0; i<rightHand.size(); i++)
        {
            rightArray[i]=rightHand.get(i);
        }
        return rightArray;
    }
    /**
     * generates int array with all notes in chord.
     * @return int array with chord.
     */
    public int[] getChord()
    {
        int[] chord=new int[rightHand.size()+leftHand.size()];
        ArrayList<Integer> chordList=new ArrayList<Integer>();
        chordList.addAll(leftHand);
        chordList.addAll(rightHand);
        for(int i=0; i<chordList.size();i++)
        {
            chord[i]=chordList.get(i);
        }
        return chord;
        
    }
     public int getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(int minInterval) {
        this.minInterval = minInterval;
    }
    public boolean isVoiceAll() {
        return voiceAll;
    }
    public boolean getVoiceAll() {
        return voiceAll;
    }
    public void setVoiceAll(boolean voiceAll) {
        this.voiceAll = voiceAll;
    }
    private boolean invertM9;
    public boolean isInvertM9() {
        return invertM9;
    }
    
    public boolean getInvertM9() {
        return invertM9;
    }

    public void setInvertM9(boolean invertM9) {
        this.invertM9 = invertM9;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public boolean isRootless() {
        return rootless;
    }

    public void setRootless(boolean rootless) {
        this.rootless = rootless;
    }
    
    private int allMidiValues[]= new int[128];
    private int root;
    private int color[];
    private int priority[];
    private ArrayList<Integer> leftHand;
    private ArrayList<Integer> rightHand;
    private ArrayList<Integer> allLeftValues;
    private ArrayList<Integer> allRightValues;
    private int lowerLeftBound;
    private int upperLeftBound;
    private int lowerRightBound;
    private int upperRightBound;
    private int numNotesLeft;
    private int numNotesRight;
    private int leftColorPriority;//priority of any color note
    private int rightColorPriority;
    private int maxPriority;//max priority a note in the priority array can have
    private int previousVoicing[];
    private double previousVoicingMultiplier;// multiplier for notes used in previous voicing
    private double halfStepAwayMultiplier;
    private double fullStepAwayMultiplier;
    private double priorityMultiplier;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
    private double repeatMultiplier;
    private double halfStepReducer;
    private double fullStepReducer;
    private boolean voiceAll;
    private boolean rootless;
    private int minInterval;

    
    

}
