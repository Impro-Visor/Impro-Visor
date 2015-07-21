/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.Constants;

/**
 *
 * @author muddCS15
 */
public class RhythmGenerator {
    
    private int slots;

    public RhythmGenerator(int slots) {
        this.slots = slots;
    }
    
    //for now, just fill with quarter notes
    public MelodyPart rhythm(int duration) {
        MelodyPart rhythm = new MelodyPart();
        for(int slotsLeft = slots; slotsLeft > 0; slotsLeft -= duration){
            rhythm.addNote(new Note(Constants.C4, duration));
        }
        return rhythm;
    }
    
}
