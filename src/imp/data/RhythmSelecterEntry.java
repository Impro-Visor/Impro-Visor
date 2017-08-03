/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import static imp.Constants.DEFAULT_BEATS_PER_BAR;
import imp.com.CommandManager;
import imp.data.advice.AdviceForMelody;
import imp.generalCluster.DataPoint;
import imp.gui.Notate;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import polya.Polylist;

/**
 *
 * @author Lukas Gnirke & Cai Glencross
 */
public class RhythmSelecterEntry implements Runnable{
    private boolean isSelected;
    private boolean added;
    private final boolean session;
    private boolean delete;
    private final Polylist ruleStringPL;
    private ImageIcon rhythmRepresentation;
    private boolean hide;
    private final String realMelody;
    private boolean alreadyReturned;
    private DataPoint dataPoint;
    private Notate rhythmNotate;
    private final int[] metre;
    private final int numMeasures;
    private final JScrollPane scrollPane;
    private final Future<Notate> futureRhythmNotate;
    private boolean abort;
    
    public RhythmSelecterEntry(ImageIcon rhythmRep, boolean session, String realMelody, Polylist ruleStringPL, 
            Future<Notate> futureRhythmNotate, int[] metre, int numMeasures, JScrollPane scrollPane){
        //this.rhythmRep = rhythmRep;
        this.rhythmRepresentation = rhythmRep;
        //this.button = button;
        this.added = false;
        this.isSelected = false;
        this.session = session;
        this.delete = false;
        this.ruleStringPL = ruleStringPL;
        this.hide = false;
        this.realMelody = realMelody;
        this.dataPoint = null;
        this.metre = metre;
        //this.rhythmNotate = rhythmNotate;
        this.numMeasures = numMeasures;
        this.scrollPane = scrollPane;
        this.futureRhythmNotate = futureRhythmNotate;
        this.abort = false;
    }
    
    public ImageIcon getRhythmRepresentation(){
        return this.rhythmRepresentation;
    }
    
    public String getRealMelody(){
        return this.realMelody;
    }

    
    public boolean isSession(){
        return this.session;
    }
    
    public boolean isSelected(){
        return this.isSelected;
    }
    
    public void setDelete(boolean d){
        this.delete = true;
    }
    
    public Polylist getRuleStringPL(){
        return this.ruleStringPL;
    }

    public void setAdded(boolean b) {
        this.added = b;
    }

    public void setChecked(boolean b) {
        this.isSelected = false;
    }
    
    public boolean isAdded(){
        return this.added;
    }

    public boolean isDeleted() {
        return this.delete;
    }
    
    public void hide(){
        System.out.println("rse hidden....");
        this.hide = true;
    }
    
    public boolean isHidden(){
        return this.hide;
    }
    
    public void markNotReturned(){
        this.alreadyReturned = false;
    }
    
    public void markAsReturned(){
        this.alreadyReturned = true;
    }
    
    public boolean hasBeenReturned(){
        return this.alreadyReturned;
    }

    public void addDataPoint(DataPoint d) {
        this.dataPoint = d;
    }
    
    public DataPoint getDataPoint(){
        if(this.dataPoint == null){
            System.out.println("This rhythmSelecterEntry doesn't have a dataPoint assigned to it!");
        }
        return this.dataPoint;   
    }

    @Override
    public void run() {
        try {
            this.rhythmNotate = futureRhythmNotate.get();
    
            synchronized(this.rhythmNotate){

                if(!abort){rhythmNotate.adjustLayout(Polylist.list((long) (numMeasures / DEFAULT_BEATS_PER_BAR)));}
                
                if(!abort){
                    BufferedImage rhythmScreenshot = createImageRepresentation(this.realMelody);
                    Image resizedRhythmPic = rhythmScreenshot.getScaledInstance((int) (rhythmScreenshot.getWidth() / 2.25), 
                        (int) (rhythmScreenshot.getHeight() / 2.25), Image.SCALE_SMOOTH);
                    this.rhythmRepresentation = new ImageIcon(resizedRhythmPic);                  
}
            }
            if(!abort){scrollPane.repaint();}

        } catch (InterruptedException ex) {
            System.out.println("Retrieval of rhythmNotate interrupted...");
        } catch (ExecutionException ex) {
            Logger.getLogger(RhythmSelecterEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
        
    /**
     * Creates an image of param userRhythm pasted onto the invisible notate.
     * 
     * @param userRhythm - the rhythm you want an image of (in "real melody" format)
     * @return the image of the rhythm
     */
    public BufferedImage createImageRepresentation(String userRhythm){
        //prepare the rhythm so that it can be pasted into the invisible notate
        String[] userRhythmNoteStrings = userRhythm.split(" ");
        Polylist noteSymbolPolylist = new Polylist();
        Polylist pitchNoteSymbolPolylist = new Polylist();
        for (int i = 0; i < userRhythmNoteStrings.length; i++){
            NoteSymbol noteSymbol = NoteSymbol.makeNoteSymbol(userRhythmNoteStrings[i]);
            noteSymbolPolylist = noteSymbolPolylist.addToEnd(noteSymbol);
            pitchNoteSymbolPolylist = pitchNoteSymbolPolylist.addToEnd(NoteSymbol.makeNoteSymbol("c4"));
        }
        
        Polylist notePolylistToWrite = NoteSymbol.newPitchesForNotes(noteSymbolPolylist, pitchNoteSymbolPolylist);
        //System.out.println("Creating image for: " + notePolylistToWrite);
        
        MelodyPart melodyPartToWrite = new MelodyPart(notePolylistToWrite.toStringSansParens());

        
        //Create the advice object that will do the pasting
        AdviceForMelody advice = new AdviceForMelody("RhythmPreview", notePolylistToWrite, "c", Key.getKey("c"),
                        metre, 0);//make a new advice for melody object
   
        
        advice.setNewPart(melodyPartToWrite);//new part of advice object is the part that gets pasted to the leadsheet
        
        //Insert the rhythm into the invisible notate
        advice.insertInPart(rhythmNotate.getScore().getPart(0), 0, new CommandManager(), rhythmNotate);//insert melodyPartToWrite into the notate score
        rhythmNotate.getCurrentStave().setSelection(rhythmNotate.getCurrentStave().getMelodyPart().size() - 1);
        rhythmNotate.repaint();//refresh the notate page
      
        //Take a screenshot of the invisible notate and create a graphics object       
//        BufferedImage notateScreenshot = new BufferedImage(rhythmNotate.getSize().width, rhythmNotate.getSize().height-100, 
//                BufferedImage.TYPE_INT_ARGB);

          BufferedImage notateScreenshot = new BufferedImage(rhythmNotate.getScoreTab().getSize().width, rhythmNotate.getScoreTab().getSize().height-100, 
                BufferedImage.TYPE_INT_ARGB);  
        
        Graphics g = notateScreenshot.createGraphics();
        rhythmNotate.getScoreTab().paint(g);
        rhythmNotate.getScoreTab().paint(g);

        g.dispose(); 
        g.dispose();
        

        int croppedXStart = 77;//(75 / notateScreenshotWidth);
        int croppedYStart = 125;// * (290 / notateScreenshotWidth);
        int croppedWidth = 966;
        int croppedHeight = 74;     
        
        //Create the image as a cropped version of the screenshot
        BufferedImage dest = notateScreenshot.getSubimage(croppedXStart, croppedYStart, croppedWidth, croppedHeight);
        
        return dest;
    }
    
    public void setRhythmNotate(Notate n){
        this.rhythmNotate = n;
    }
    
    public boolean rhythmNotateSet(){
        if(this.rhythmNotate == null){
            return false;
        }
        return true;
    }

    public boolean finishedCreatingFutureNotate() {
        return this.futureRhythmNotate.isDone();
    }
    
    public void abort(){
        this.abort = true;
    }
    
    
}
