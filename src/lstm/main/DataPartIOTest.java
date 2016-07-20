/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;

import static imp.ImproVisor.getVocabDirectory;
import imp.com.LoadAdviceCommand;
import imp.data.Chord;
import imp.data.ChordForm;
import imp.data.ChordPart;
import imp.data.ChordSymbol;
import imp.data.Key;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.advice.Advisor;
import imp.util.Preferences;
import lstm.io.leadsheet.DataPartIO;
import lstm.io.leadsheet.LeadSheetDataSequence;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author cssummer16
 */
public class DataPartIOTest {
    public static void main(String[] args) {
        // Setup
        Preferences.loadPreferences();
        Advisor advisor = new Advisor();
        
        String ruleFilePath = Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE);
        String ruleFileName;
        if (ruleFilePath.lastIndexOf(File.separator) == -1) {
            ruleFileName = ruleFilePath;
        } else {
            ruleFileName = ruleFilePath.substring(ruleFilePath.lastIndexOf(File.separator), ruleFilePath.length());
        }
        File ruleFile = new File("../Impro-Visor/improvisor811/vocab", ruleFileName);
        
        LoadAdviceCommand loadAdvice = new LoadAdviceCommand(ruleFile, advisor, null, true, false);
        loadAdvice.setLoadDialogText("Loading Vocabulary ...");
        loadAdvice.execute();
        synchronized (loadAdvice) {
            while (!loadAdvice.hasLoaded()) {
                try {
                    loadAdvice.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        loadAdvice.hideLoadDialog();
        
        // Actually do things
        ArrayList<Chord> toPrint = new ArrayList<Chord>();
        toPrint.add(new Chord("C"));
        toPrint.add(new Chord("Db"));
        toPrint.add(new Chord("Dm7/C"));
        for(Chord c : toPrint) {
            ChordSymbol cs = c.getChordSymbol();
            ChordForm cf = c.getChordForm();
            System.out.println(c);
            System.out.println("Spell " + c.getSpell());
            System.out.println("Root " + c.getRoot());
            System.out.println("Quality " + c.getQuality());
            System.out.println("ChordSymbol Root " + cs.getRoot());
            System.out.println("ChordSymbol Bass " + cs.getBass());
            System.out.println("ChordSymbol isSlash " + cs.isSlashChord());
            System.out.println("ChordForm colorMIDI " + cf.getColorMIDIarray(c.getRoot()));
            System.out.println("ChordForm scaleMIDI " + cf.getScaleMIDIarray(c.getRoot()));
            System.out.println("ChordForm spellMIDI " + cf.getSpellMIDIarray(c.getRoot()));
            System.out.println("ChordForm spellVec " + Arrays.toString(cf.getSpellVector("c", Key.Ckey)));
        }
        
        ChordPart cpart = new ChordPart();
        MelodyPart mpart = new MelodyPart();
        cpart.addChord("Dm9", 480);
        cpart.addChord("G13", 480);
        cpart.addChord("CM7", 2*480);
        mpart.addNote(new Note(80, 240));
        mpart.addNote(new Note(81, 240));
        mpart.addNote(new Note(82, 480));
        mpart.addNote(new Note(83, 480));
        mpart.addNote(new Note(84, 480));
        
        LeadSheetDataSequence seq = DataPartIO.readParts(cpart, mpart);
        LeadSheetDataSequence seq2 = seq.copy();
        
        System.out.println("Melody:");
        while(seq.hasMelodyLeft()){
            System.out.println(seq.pollMelody());
        }
        System.out.println("Chords:");
        while(seq.hasChordsLeft()){
            System.out.println(seq.pollChords());
        }
        
        MelodyPart mpart2 = DataPartIO.getMelodyPart(seq2);
        mpart2.printNotesAtSlots();
    }
}
