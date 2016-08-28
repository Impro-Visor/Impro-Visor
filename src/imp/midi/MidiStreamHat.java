/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.midi;

import imp.data.Note;
import imp.data.Rest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author cssummer16
 */
public class MidiStreamHat {
    
    private BlockingQueue<Note> melodyQueue;
    private long lastSlot;
    private int lastPitch;
    private int slotResolution;
    private int slotRestAbsorption;
    
    public MidiStreamHat() {
        melodyQueue = new LinkedBlockingQueue<>();
        this.lastSlot = 0;
        this.slotResolution = 10;
        this.slotRestAbsorption = 30;
    }
    
    public BlockingQueue<Note> getMelodyQueue() {
        return melodyQueue;
    }
    
    public long snapToResolution(long slot)
    {
        if(slot % slotResolution > (slotResolution / 2))
            return slot / slotResolution + 1;
        else
            return slot / slotResolution;
    }
    
    public void setStart(long slot) {
        //System.out.println("Start slot: " + slot);
        this.lastSlot = snapToResolution(slot);
        this.lastPitch = -1;
    }
    
    public void noteOn(int pitch, long slot) {
            //System.out.println("Slot: " + slot);
            int lastSlotDuration = (int) (slot - (lastSlot * slotResolution));
            //System.out.println("Duration of lastNote: " + lastSlotDuration);
            
            slot = snapToResolution(slot);
            
            if(!(lastPitch == -1 && lastSlotDuration < slotRestAbsorption)) {
                
                int duration = (int) (slot - lastSlot);
                submitNote(duration);
            }
            this.lastPitch = pitch;
            this.lastSlot = slot;
    }
    
    public void noteOff(int pitch, long slot) {
        if(pitch == lastPitch) {
            slot = snapToResolution(slot);
            int lastDuration = (int) (slot - lastSlot);
            //System.out.println("Duration of lastNote: " + (int) (slot - lastSlot));
            submitNote(lastDuration);
            this.lastPitch = -1;
            this.lastSlot = slot;
        }
    }
    
    public void submitNote(int duration) {
        Note lastNote;
        if(lastPitch == -1)
            lastNote = new Rest(duration * slotResolution);
        else
            lastNote = new Note(lastPitch, duration * slotResolution);
        melodyQueue.add(lastNote);
    }
    
    public void flush(long slot){
        noteOff(lastPitch, slot);
    }
}
