/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;

import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.util.ErrorLog;
import java.util.logging.Level;
import java.util.logging.Logger;
import lstm.architecture.NetworkMeatPacker;
import lstm.architecture.poex.GenerativeProductModel;
import lstm.encoding.EncodingParameters;
import lstm.io.leadsheet.DataPartIO;
import lstm.io.leadsheet.LeadSheetDataSequence;

/**
 *
 * @author cssummer16
 */
public class LSTMGen {
    GenerativeProductModel model;
    String params_path;
    boolean loaded;
    
    public LSTMGen() {
        int outputSize = EncodingParameters.noteEncoder.getNoteLength();
        int beatSize = 9;
        int featureVectorSize = 100;
        int lowerBound = 48;
        int upperBound = 84+1;
        model = new GenerativeProductModel(outputSize, beatSize, featureVectorSize, lowerBound, upperBound);
        
        params_path = null;
        loaded = false;
    }
    
    public void setLoadPath(String path) {
        params_path = path;
        loaded = false;
    }
    
    public void load() throws InvalidParametersException {
        if(params_path == null){
            throw new RuntimeException("Load called without providing parameters!");
        }
        NetworkMeatPacker packer = new NetworkMeatPacker();
        String[] notFound = packer.pack(params_path, model);
        if(notFound.length > 0){
            System.err.println(notFound.length + " files were not able to be matched to the architecture!");
            for (String fileName : notFound) {
                System.err.println("\t" + fileName);
            }
            throw new InvalidParametersException(notFound);
        }
        loaded = true;
    }
    
    public void loadFromPath(String path) throws InvalidParametersException {
        setLoadPath(path);
        load();
    }
    
    public void reload() throws InvalidParametersException{
        if(!loaded) {
            load();
        } else {
            NetworkMeatPacker packer = new NetworkMeatPacker();
            String[] notFound = packer.refresh(params_path, model, "initialstate");
            if (notFound.length > 0) {
                System.err.println(notFound.length + " files were not able to be matched to the architecture!");
                for (String fileName : notFound) {
                    System.err.println("\t" + fileName);
                }
                throw new InvalidParametersException(notFound);
            }
        }
    }
    
    public void generate_into(ChordPart chords, MelodyPart dest, int offset) {
        try {
            reload();
        } catch (InvalidParametersException ex) {
            Logger.getLogger(LSTMGen.class.getName()).log(Level.WARNING, null, ex);
            ErrorLog.log(ErrorLog.WARNING, "Could not load LSTM parameters!", true);
            return;
        }
        
        LeadSheetDataSequence chordSequence = DataPartIO.readChords(chords, offset);
        LeadSheetDataSequence outputSequence = chordSequence.copy();
        outputSequence.clearMelody();
        
        while(chordSequence.hasNext()) { //iterate through time steps in input data
            outputSequence.pushStep(null, null, model.step(chordSequence.retrieve()));
        }
        
        DataPartIO.addToMelodyPart(outputSequence, dest);
    }
    
    public MelodyPart generate(ChordPart chords) {
        return generate(chords, 0);
    }
    public MelodyPart generate(ChordPart chords, int offset) {
        MelodyPart dest = new MelodyPart();
        generate_into(chords, dest, offset);
        return dest;
    }
    
    public MelodyPart generateTrading(ChordPart chords, boolean generateStart, int tradeQuantum) {
        return generateTrading(chords, 0, generateStart, tradeQuantum);
    }
    public MelodyPart generateTrading(ChordPart chords, int offset, boolean generateStart, int tradeQuantum) {
        int chordLength = chords.size();
        System.out.println(chordLength + " " + tradeQuantum);
        
        MelodyPart accum_part = new MelodyPart();
        for(int start_pos = generateStart ? 0 : tradeQuantum; start_pos+tradeQuantum <= chordLength; start_pos += 2*tradeQuantum) {
            if(!generateStart)
                accum_part.addNote(Note.makeRest(tradeQuantum));
            ChordPart extracted = chords.extract(start_pos, start_pos+tradeQuantum-1);
            System.out.println("Extracted " + extracted.getSize());
            generate_into(extracted, accum_part, offset+start_pos);
            if(generateStart)
                accum_part.addNote(Note.makeRest(tradeQuantum));
        }
        
        return accum_part;
    }
}
