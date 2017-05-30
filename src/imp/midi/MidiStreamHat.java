/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2015-2017 Robert Keller and Harvey Mudd College
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
