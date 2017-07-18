/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College.
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
package imp.trading.tradingResponseModes;

import imp.Constants;
import imp.ImproVisor;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.DataPoint;
import imp.cluster.motif.MotifCluster;
import imp.cluster.motif.MotifClusterManager;
import imp.com.CommandManager;
import imp.data.MelodyPart;
import imp.gui.Notate;
import imp.lickgen.Grammar;
import imp.lickgen.LickGen;
import imp.lickgen.LickgenFrame;
import imp.lickgen.NoteConverter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import polya.Polylist;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An active trading style that learns the Motifs played by the user and incorporates them into a solo in response.
 * @author Joseph Yaconelli
 */
public class MemorizeMotifsTRM extends BlockResponseMode {

    private static final String MotifStartSymbol = "UseMotif";

    int beat = (Constants.WHOLE / 4); // assumes 4/4 time -- need to generalize
    int measure = (4 * beat); // assumes 4/4 time -- need to generalize
    int windowSize = 1;
    static Notate notate;
    String[] abstractMelodies;
    Grammar _grammar;
    
    private static final boolean TESTING = false;
    
    static String allRest = Polylist.list("slope", "0", "0", "R").toString();
    
//    FetchMelodiesThread fetchMelodiesThread;
//    CreateDataPointsThread createDataPointsThread;
//    AddToClustersThread addToClustersThread;
//    AddToGrammarThread addToGrammarThread;
//    ConcurrentLinkedQueue melodiesToDataPointsQueue;
//    ConcurrentLinkedQueue dataPointsToClustersQueue;
//    ConcurrentLinkedQueue    clustersToGrammarQueue;
    
    private int _initGrammarSize;
    
    private String _grammarSaveName;
   
    public MemorizeMotifsTRM(String message, Notate note) {
        super(message);
        notate = note;
        MotifClusterManager.reset();

        initGrammar();

        // name to save new Motif grammar to
        _grammarSaveName = notate.getGrammarFileName().
                                    replaceFirst(notate.getGrammarName(),
                                                "UserMotifTrade_".concat(
                                                        (new SimpleDateFormat("dd-MM-yy_HH-mm-ss"))
                                                                .format(Calendar.getInstance().getTime())));
        
    }

    
    
    // multithreaded version not implemented right now, but the code is all there for it to work (though there exists at least 1 race condition)
    
    /**
     * Called on the beginning of trading. Loads grammar file and converts it into proper format.
     */
    @Override
    public void onStartTrading(){
        
        
        initGrammar();
        
        
     /*  
        // this is all code for the multi-threaded version of this. Currently Unused.
        
        melodiesToDataPointsQueue = new ConcurrentLinkedQueue();
        dataPointsToClustersQueue = new ConcurrentLinkedQueue();
        clustersToGrammarQueue = new ConcurrentLinkedQueue();
        
        fetchMelodiesThread = new FetchMelodiesThread(melodiesToDataPointsQueue,
                responseInfo,
                createDataPointsThread,
                notate);
        
        createDataPointsThread = new CreateDataPointsThread(dataPointsToClustersQueue,
                responseInfo,
                fetchMelodiesThread);
        
        addToClustersThread = new AddToClustersThread(clustersToGrammarQueue,
                responseInfo,
                createDataPointsThread);
        
        addToGrammarThread = new AddToGrammarThread(responseInfo, addToClustersThread, _grammar);
        
        
        // start threads
        
        fetchMelodiesThread.start();
        createDataPointsThread.start();
        addToClustersThread.start();
        addToGrammarThread.start();
        */
    }
    
    /**
     * Uses a generated motif grammar to create response
     * @return MelodyPart of generated solo response
     */
    @Override
    public MelodyPart generateResponse() {

        
        // TODO: change windowSize to get from user specified
        MelodyPart[] parts = responseInfo.chopResponse(responseInfo.getResponse(), 0, windowSize);
        
        abstractMelodies = new String[parts.length];
        
        
        if(TESTING){
            for(MelodyPart p : parts){
                System.out.println("Part: " + p);
                System.out.println(p.getBars());
            }
        }

        
        DataPoint d;
        for(int i = 0; i < parts.length; i++){
            d = getDataPointForUser(parts[i], parts[0].getEndTime() + windowSize*measure*i);
            MotifClusterManager.addMotif(d);
            if (TESTING) System.err.println("Got out");

        }
        if (TESTING) System.err.println("Done adding data points");

        
        Collection<MotifCluster> motifs = MotifClusterManager.getMotifClusters();
        if (TESTING) System.err.println("got clusters");

        motifs.forEach((MotifCluster mc) -> {
            (new Thread(){
                
                @Override
                public void run(){
                    Polylist r, p;
                    r = (Polylist) mc.getMotif().grammarRule().second();
                    
                    Polylist tempRule = mc.getMotif().grammarRule();

                    // HACK: doesn't add rules that contain empty list (formatting error in abstract melody creation makes bad rules
                    Polylist flattened = tempRule.flatten();
                    boolean containsJunk = flattened.member("ENDTIED") || flattened.member("STARTTIED") || flattened.member("ENDT") || flattened.member("STARTT");
                    if(flattened.member(Polylist.nil) || containsJunk){
                        return;
                    }

                    Polylist checkAllRest = (Polylist) ((Polylist) tempRule.third()).first();
                    

                    if(checkAllRest.toString().equalsIgnoreCase(allRest)){
                        return;
                    }
                    
                    Polylist finalRule = Polylist.list("rule", Polylist.list(MotifStartSymbol), tempRule.third(), tempRule.last());
                    Polylist currentRules;
                    
                    currentRules = _grammar.getRules();
                    boolean ruleExists = false;

                    if(TESTING) System.err.println("In grammar creation...");

                    for(Polylist R = currentRules; R.nonEmpty(); R = R.rest()){
                        if(((Polylist) R.first()).prefix(3).equals(finalRule.prefix(3))){
                            R.setFirst(((Polylist) R.first()).prefix(3).append(Polylist.list(finalRule.last())));
                            ruleExists = true;
                        }
                    }
                    if(!ruleExists) {
                        _grammar.addRule(finalRule);
                        if(TESTING) System.out.println("Part to compare: " + (String) ((Polylist)finalRule.second()).first());
                    }


                }
            }).start();
        });
        
        
//        synchronized(_grammar){
            if(TESTING) System.err.println("In synchronized");
            
            
            if(TESTING) _grammarSaveName = "/home/cssummer17/impro-visor-version-9.2-files/test_grammar_output.grammar";
            if(TESTING) System.err.println("About to save grammar to: " + _grammarSaveName);
            
            _grammar.saveGrammar(_grammarSaveName);
            
            
            if(TESTING) System.err.println("Grammar saved. About to generate solo...");
            
            responseInfo.genMotifSolo(_grammar);
            
            if(TESTING) System.err.println("Solo generated. Returning solo...");
            
            MelodyPart response = responseInfo.getResponse().quantizeMelody(notate.getQuantizationQuanta(),
                                                                            notate.getQuantizationSwing(),
                                                                            notate.getQuantizationRestAbsorption());
            
            
            return response;
//        }
    }

    /**
     * Converts melody to a data point
     * @param melody Melody to convert
     * @param nextSection the index of this melody
     * @return the {@link DataPoint} version of the {@link MelodyPart}
     */
    public synchronized static DataPoint getDataPointForUser(MelodyPart melody, int nextSection){
      
        LickGen lg = new LickGen(ImproVisor.getGrammarFile().getAbsolutePath(), notate, null);
        LickgenFrame lgf = new LickgenFrame(notate, lg, new CommandManager());
        String abstractMel = lgf.addMeasureToAbstractMelody(nextSection, 4, false, false);
        String exactMelody = lgf.getExactMelody(4, abstractMel, nextSection);
        
        try{
            abstractMel = abstractMel.substring(1, abstractMel.length() - 1);
        } catch (NullPointerException e){
            if (TESTING) System.err.println("Empty melody played");
        }
        
        String relativePitch = NoteConverter.melPartToRelativePitch(melody, notate.getChordProg());
        
        Polylist temp = Polylist.PolylistFromString(abstractMel);

        Polylist rule;
        

        if(temp.member(Polylist.list())){
            rule = Polylist.list("Seg4").append((Polylist) temp.first());
        } else{
            rule = Polylist.list("Seg4").append(temp);
        }

        String ruleString = Polylist.list("rule", Polylist.list("Seg4"), Polylist.PolylistFromString(abstractMel))
                + "(Xnotation "
                + relativePitch
                + ") (Brick-type null) Head "
                + exactMelody
                + " CHORDS Fm7 G7b9";
        
        String i = "0";

        
        return CreateGrammar.processRule(rule, ruleString, i);
    }
    
    
    /**
     * Initializes grammar. Sets grammar, sets grammar length, converts to proper format, and adds start rule.
     * @see MemorizeMotifsTRM#convertGrammar(imp.lickgen.Grammar) convertGrammar(Grammar g)
     * 
     */
    private void initGrammar(){
        String grammarName = notate.getGrammarFileName();

        if(TESTING) {
            grammarName = notate.getGrammarFileName().replaceFirst(notate.getGrammarName(), "chord");
            System.out.println("Grammar Name: " + grammarName);
        }
        
        _grammar = convertGrammar(new Grammar(grammarName));
        
        _grammar.addRule(Polylist.list("rule", Polylist.list("P"), Polylist.list(MotifStartSymbol, "P"), 10));

        _initGrammarSize = _grammar.getRules().flatten().length();
    }
    
    
    /**
     * Convert the selected grammar to work as a base.
     * @param g the grammar to change
     * @return the new grammar with only terminals.
     */
    private Grammar convertGrammar(Grammar g) {
        Polylist rules = Polylist.list();
        int Qs = -1;
        
        
        // extract parameters and terminals from loaded grammar
        for(Polylist p = g.getRules(); p.nonEmpty(); p = p.rest()){
            Polylist temp = (Polylist) p.first();
            
            boolean isParameter = ((String) temp.first()).equalsIgnoreCase("parameter");
            boolean isRule = ((String) temp.first()).equalsIgnoreCase("rule");
            
            boolean containsQ;
            try{
                containsQ = ((String)((Polylist) temp.second()).first()).contains("Q");
            } catch (Exception e){
                containsQ = false;
            }
            
            boolean isMotif;
            try{
                isMotif = ((String)((Polylist) temp.second()).first()).contains(MotifStartSymbol);
            } catch (Exception e){
                isMotif = false;
            }
            
            if(isParameter || (isRule && containsQ) || (isRule && isMotif)){
                rules = rules.addToEnd(p.first());  
                
                try{
                    Qs = Integer.parseInt(((String)((Polylist) temp.second()).first()).substring(1));
                } catch (NumberFormatException | ClassCastException e) {
                    
                }
            }
        }
        
        // add passage from M_X to preexisting terminals
        for(int i = 0; i <= Qs; i++){
            rules = rules.addToEnd(Polylist.list("rule", Polylist.list("M_X"), Polylist.list("Q".concat(String.valueOf(i))), 1));
        }
        
        // add initial start state as long as Q rules exist
        if(Qs >= 0){
            rules = rules.addToEnd(Polylist.list("rule", Polylist.list("P"), Polylist.list("M_X", "P"), 1));
        }
        
        // set start symbol
        rules = rules.addToEnd(Polylist.list("startsymbol", "P"));
        
        // create new grammar
        Grammar grammar = new Grammar(rules);
        
        return grammar;
        
    }
    
}