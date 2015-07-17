/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.data.MidiChannelInfo.ChannelInfo;
import imp.util.ErrorLog;
import imp.util.LeadsheetFileView;
import imp.util.LeadsheetFilter;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import polya.Polylist;

/**
 * @author Robert Keller, from original code by Jim Herold
 * 
 * Use of public access to elements in MIDIBeast should
 * be changed to use proper methods.
 */

@SuppressWarnings("serial")

public class ExtractionEditor extends javax.swing.JDialog implements Constants
{
public static final int DRUM_CHANNEL = 9;
public static final String DRUM_NAMES = "DRUMS";

Notate notate;
StyleEditor styleEditor;
RepresentativeBassRules repBassRules;
ArrayList<RepresentativeBassRules.BassPattern> selectedBassRules;

RepresentativeDrumRules repDrumRules;
ArrayList<RepresentativeDrumRules.DrumPattern> selectedDrumRules;

RepresentativeChordRules repChordRules;
ArrayList<RepresentativeChordRules.ChordPattern> selectedChordRules;

/**
 * Models for the raw and selected JLists
 */
DefaultListModel rawRulesModelBass;
DefaultListModel selectedRulesModelBass;
DefaultListModel rawRulesModelChord;
DefaultListModel selectedRulesModelChord;
DefaultListModel rawRulesModelDrum;
DefaultListModel selectedRulesModelDrum;

/**
 * minimum duration (in slots) for a note not to be counted as a rest.
 */
private int minDuration = 0;


public static final int BASS = 0;
public static final int DRUM = 1;
public static final int CHORD = 2;


//for chord extract
private ChannelInfo[] channelInfo;
private JFileChooser chordFileChooser = new JFileChooser();

/**
 * Creates new form ExtractionEditor
 */

public ExtractionEditor(java.awt.Frame parent, 
                        boolean modal, 
                        StyleEditor p,  
                        int minDuration)
  {
    super(parent, modal);
    this.styleEditor = p;
    this.notate = p.getNotate();
    this.minDuration = minDuration;
    
    rawRulesModelBass       = new DefaultListModel();
    selectedRulesModelBass  = new DefaultListModel();
    rawRulesModelChord      = new DefaultListModel();
    selectedRulesModelChord = new DefaultListModel();
    rawRulesModelDrum       = new DefaultListModel();
    selectedRulesModelDrum  = new DefaultListModel();

    initComponents();
    initComponents2();
    setSize(900, 425);

    SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
    numberOfClustersSpinnerBass.setModel(model);

    setPotentialParts();
    
    
    //for chord extraction
    MidiChannelInfo midiChannelInfo = new MidiChannelInfo(MIDIBeast.midiFileName);
    channelInfo = midiChannelInfo.getChannelInfo();
    bassChannelName.setModel(new javax.swing.DefaultComboBoxModel(channelInfo));
    chordChannelName.setModel(new javax.swing.DefaultComboBoxModel(channelInfo));
    
    File styleExtractDir = ImproVisor.getStyleExtractDirectory();
    LeadsheetFileView styView = new LeadsheetFileView();
    chordFileChooser.setCurrentDirectory(styleExtractDir);
    chordFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chordFileChooser.setDialogTitle("Open Leadsheet");
    chordFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chordFileChooser.resetChoosableFileFilters();
    chordFileChooser.addChoosableFileFilter(new LeadsheetFilter());
    chordFileChooser.setFileView(styView);
    
    useLeadsheet.setSelected(MIDIBeast.useLeadsheet);
    
    int bassChannel = MIDIBeast.getBassChannel();
    int chordChannel = MIDIBeast.getChordChannel();
    for (int j = 0; j < channelInfo.length; j++)
    {
        if( channelInfo[j].getChannelNum() == bassChannel )
        {
            bassChannelName.setSelectedItem(channelInfo[j]);
        }
        if( channelInfo[j].getChannelNum() == chordChannel )
        {
            chordChannelName.setSelectedItem(channelInfo[j]);
        }
    }
  }

public void setBass()
  {
    bassPanel.setBackground(Color.orange);
    setBassDefaults();
    repBassRules = MIDIBeast.getRepBassRules();
    setBassSelectedRules();
    setBassRawRules();
  }

public void setChords()
  {
    chordPanel.setBackground(Color.green);
    setChordDefaults();
    repChordRules = MIDIBeast.getRepChordRules();
    setChordRawRules();
    setChordSelectedRules();
  }

public void setDrums()
  {
    drumPanel.setBackground(Color.yellow);
    setDrumDefaults();
    repDrumRules = MIDIBeast.getRepDrumRules();
    setDrumRawRules();
    setDrumSelectedRules();
  }

public void setPotentialParts()
  {
    ArrayList<String> potentialInstruments = new ArrayList<String>();

    for( int i = 0; i < MIDIBeast.allParts.size(); i++ )
      {
        if( MIDIBeast.allParts.get(i).getChannel() == DRUM_CHANNEL )
          {
            potentialInstruments.add(DRUM_NAMES);
          }
        else
          {
            potentialInstruments.add(MIDIBeast.getInstrumentForPart(i));
          }
      }
    potentialInstrumentsJListBass.setListData(potentialInstruments.toArray());
    potentialInstrumentsJListBass.setSelectedIndex(0);
    potentialInstrumentsJListChord.setListData(potentialInstruments.toArray());
    potentialInstrumentsJListChord.setSelectedIndex(0);

  }

public void setBassDefaults()
  {
    startBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.bassPart.getPhrase(0).getStartTime())));
    endBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.bassPart.getPhrase(0).getEndTime())));
  }

public void setChordDefaults()
  {
    startBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.chordPart.getPhrase(0).getStartTime())));
    endBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.chordPart.getPhrase(0).getEndTime())));
  }

public void setDrumDefaults()
  {
    startBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.drumPart.getPhrase(0).getStartTime())));
    endBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.drumPart.getPhrase(0).getEndTime())));
  }

public void setBassRawRules()
  {
    rawRulesModelBass.clear();
    ArrayList<RepresentativeBassRules.Section> sections = repBassRules.getSections();
    ArrayList<Object> rawRules = new ArrayList<Object>();
    //Add Clustered Rules
    for( int i = 0; i < sections.size(); i++ )
      {
        RepresentativeBassRules.Section currentSection = sections.get(i);
        // FIX: For some reason, getSlotCount() seems to return a value 
        // doubly multiplied by BEAT.
        int length = (currentSection.getSlotCount() / BEAT) / BEAT;
        ArrayList<RepresentativeBassRules.Cluster> clusters = currentSection.getClusters();
        for( int j = 0; j < clusters.size(); j++ )
          {
            RepresentativeBassRules.Cluster currentCluster = clusters.get(j);
            rawRules.add("---- Cluster " + j + " of length " + length + " patterns ----");
            for( int k = 0; k < currentCluster.size(); k++ )
              {
                rawRules.add(repBassRules.makeBassPatternObj(currentCluster.getStringRule(k), 1));
              }
          }
      }
    // Copy the rules to the model
    for( Object rawRule : rawRules )
      {
        rawRulesModelBass.addElement(rawRule);
      }
    rawRulesJListBass.setModel(rawRulesModelBass);
    rawRulesJListBass.setSelectedIndex(0);
  }

public void setChordRawRules()
  {
    rawRulesModelChord.clear();
    ArrayList<RepresentativeChordRules.Section> sections = repChordRules.getSections();
    ArrayList<Object> rawRules = new ArrayList<Object>();

    for( int i = 0; i < sections.size(); i++ )
      {
        RepresentativeChordRules.Section currentSection = sections.get(i);
        // FIX: For some reason, getSlotCount() seems to return a value 
        // doubly multiplied by BEAT.
        int length = (currentSection.getSlotCount() / BEAT) / BEAT;
        ArrayList<RepresentativeChordRules.Cluster> clusters = currentSection.getClusters();
        for( int j = 0; j < clusters.size(); j++ )
          {
            RepresentativeChordRules.Cluster currentCluster = clusters.get(j);
            rawRules.add("---- Cluster " + j + " of length " + length + " patterns ----");
            for( int k = 0; k < currentCluster.size(); k++ )
              {
                rawRules.add(repChordRules.makeChordPattern(currentCluster.getStringRule(k), 1));
              }
          }
      }

    ArrayList<String> duplicates = repChordRules.getDuplicates();
    if( duplicates.size() > 0 )
      {
        rawRules.add("Duplicates:");
        for( int i = 0; i < duplicates.size(); i++ )
          {
            rawRules.add("    " + duplicates.get(i));
          }
      }

    for( Object rawRule : rawRules )
      {
        rawRulesModelChord.addElement(rawRule);
      }
    rawRulesJListChord.setModel(rawRulesModelChord);
    rawRulesJListChord.setSelectedIndex(0);
  }

public void setDrumRawRules()
  {
    rawRulesModelDrum.clear();
    ArrayList<RepresentativeDrumRules.Cluster> clusters = repDrumRules.getClusters();
    ArrayList<Object> rawRules = new ArrayList<Object>();

    if( clusters != null )
      {
        for( int i = 1; i < clusters.size(); i++ )
          {
            RepresentativeDrumRules.Cluster cluster = clusters.get(i);

            // Need a check here for cluster emptiness.
            String[] clusterRules = cluster.getRules();
            if( clusterRules.length > 1 )
              {
                //System.out.println("clusterRules " + i + " = " + clusterRules);
                rawRules.add("---- Cluster " + i + " " + clusterRules[0]);
                for( int j = 1; j < clusterRules.length; j++ )
                  {
                    rawRules.add(makeDrumPattern(clusterRules[j] + "(weight 1))"));
                  }
              }
          }
      }

    ArrayList<String> duplicates = MIDIBeast.getRepDrumRules().getDuplicates();
    if( duplicates.size() > 0 )
      {
        rawRules.add("Duplicates");
        for( int i = 0; i < duplicates.size(); i++ )
          {
            rawRules.add(makeDrumPattern(duplicates.get(i) + "(weight 1))"));
          }
      }

    for( Object rawRule : rawRules )
      {
        rawRulesModelDrum.addElement(rawRule);
      }
    
    rawRulesJListBass.setModel(rawRulesModelDrum);
    rawRulesJListBass.setSelectedIndex(0);
  }

private RepresentativeDrumRules.DrumPattern makeDrumPattern(String string)
  {
    String[] split = string.split("\n");
    RepresentativeDrumRules.DrumPattern drumPattern = repDrumRules.makeDrumPattern();
    for( int i = 1; i < split.length - 1; i++ )
      {
        RepresentativeDrumRules.DrumRule drumRule = repDrumRules.makeDrumRule();
        int instrumentNumber = Integer.parseInt(split[i].substring(split[i].indexOf('m') + 2, split[i].indexOf('m') + 4));
        drumRule.setInstrumentNumber(instrumentNumber);
        int startIndex = split[i].indexOf('m') + 2;
        int endIndex = split[i].indexOf(')');
        String elements = split[i].substring(startIndex, endIndex);
        String[] split2 = elements.split(" ");
        // Start at 1 rather than 0, to skip over the drum number
        for( int j = 1; j < split2.length; j++ )
          {
            drumRule.addElement(split2[j]);
          }
        String weightString = split[split.length - 1];

        drumPattern.setWeight(1);
        //System.out.println("adding drumPattern " + drumPattern);
        drumPattern.addRule(drumRule);
      }
    return drumPattern;
  }


public void setBassSelectedRules()
  {
   selectedRulesModelBass.clear();
   selectedBassRules = repBassRules.getBassRules();
   for( RepresentativeBassRules.BassPattern selectedRule: selectedBassRules )
      {
      selectedRulesModelBass.addElement(selectedRule);
      }
    selectedRulesJListBass.setModel(selectedRulesModelBass);
    selectedRulesJListBass.setSelectedIndex(selectedBassRules.size()-1);
  }

public void setChordSelectedRules()
  {
   selectedRulesModelChord.clear();
   selectedChordRules = repChordRules.getChordRules();
   for( RepresentativeChordRules.ChordPattern selectedRule: selectedChordRules )
      {
      selectedRulesModelChord.addElement(selectedRule);
      }
    selectedRulesJListChord.setModel(selectedRulesModelChord);
    selectedRulesJListChord.setSelectedIndex(selectedChordRules.size()-1);
  }

public void setDrumSelectedRules()
  {
   selectedRulesModelDrum.clear();
   selectedDrumRules = repDrumRules.getRepresentativePatterns();
   for( RepresentativeDrumRules.DrumPattern selectedRule: selectedDrumRules )
      {
      selectedRulesModelDrum.addElement(selectedRule);
      }
    selectedRulesJListDrum.setModel(selectedRulesModelDrum);
    selectedRulesJListDrum.setSelectedIndex(selectedDrumRules.size()-1);
  }

private void initComponents2()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    errorDialog = new javax.swing.JDialog();
    errorMessage = new javax.swing.JLabel();
    errorButton = new javax.swing.JButton();

    errorDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    errorDialog.setTitle("Error");
    errorDialog.setBackground(java.awt.Color.white);
    errorMessage.setForeground(new java.awt.Color(255, 0, 51));
    errorMessage.setText("jLabel1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
    errorDialog.getContentPane().add(errorMessage, gridBagConstraints);

    errorButton.setText("OK");
    errorButton.addActionListener(new java.awt.event.ActionListener()
    {

    public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        errorButtonActionPerformed(evt);
      }
    });
  }

private void errorButtonActionPerformed(java.awt.event.ActionEvent evt)
  {//GEN-FIRST:event_errorButtonActionPerformed
    errorDialog.setVisible(false);
  }//GEN-LAST:event_errorButtonActionPerformed


private void checkForAndThrowErrors()
  {
    double endBeat;
    double startBeat;
    try
      {
        endBeat = Double.parseDouble(endBeatTextFieldBass.getText());
        startBeat = Double.parseDouble(startBeatTextFieldBass.getText());
      }
    catch( Exception e )
      {
        errorMessage.setText("ERROR: Malformed Start/End Beat.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
        return;
      }

    if( endBeat < 0 || startBeat < 0 )
      {
        errorMessage.setText("ERROR: Start/End Beats must be positive.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
      }
    else if( startBeat > endBeat )
      {
        errorMessage.setText("ERROR: Start beat must be less than end beat.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
      }
    else if( endBeat < startBeat )
      {
        errorMessage.setText("ERROR: End beat must be greater than start beat.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
      }
  }


/**
 * Plays a selected rule. In this case, the rules themselves are stored
 * in the JList (in contrast to playRawRule()).
 */

public void playSelectedRule(int type)
  {
    Polylist rule = null;
    int duration = 0;
    Object selected;
        switch( type )
          {
            case BASS:
                try
                  {
                    selected = selectedRulesJListBass.getSelectedValue();
                    RepresentativeBassRules.BassPattern selectedBassRule 
                            = (RepresentativeBassRules.BassPattern) selected;
                    duration = selectedBassRule.getDuration();
                    rule = Notate.parseListFromString(selectedBassRule.toString());
                    break;
                  }
                catch( Exception e )
                  {
                    e.printStackTrace();
                  }
                break;

            case CHORD:
                selected = selectedRulesJListChord.getSelectedValue();
                RepresentativeChordRules.ChordPattern selectedChordRule 
                        = (RepresentativeChordRules.ChordPattern) selected;
                duration = selectedChordRule.getDuration();
                rule = Notate.parseListFromString(selectedChordRule.toString());
                break;
            
            case DRUM:
                selected = selectedRulesJListDrum.getSelectedValue();
                RepresentativeDrumRules.DrumPattern selectedDrumPattern 
                        = (RepresentativeDrumRules.DrumPattern) selected;
                duration = selectedDrumPattern.getDuration();
                rule = Notate.parseListFromString(selectedDrumPattern.toString());
                break;
          }

        if( rule.isEmpty() )
          {
            ErrorLog.log(ErrorLog.WARNING, "Internal Error:"
                    + "Extraction Editor: Empty Rule");
            return;
          }

        //System.out.println("rule for style = " + rule);
        Style tempStyle = Style.makeStyle(rule);
        tempStyle.setSwing(styleEditor.getSwingValue());
        tempStyle.setAccompanimentSwing(styleEditor.getAccompanimentSwingValue());
        tempStyle.setName("extractionPattern");
        Style.setStyle("extractionPattern", tempStyle);
        // This is necessary so that the StyleListModel menu in notate is reset.
        // Without it, the contents will be emptied.
        notate.reloadStyles();
        ChordPart c = new ChordPart();
        String chord = styleEditor.getChord();
        boolean muteChord = styleEditor.isChordMuted();
        c.addChord(chord, new Double(duration).intValue());
        c.setStyle(tempStyle);

        Score s = new Score(c);
        s.setBassVolume(styleEditor.getVolume());
        if( type == CHORD )
          {
            notate.setChordVolume(styleEditor.getVolume());
          }
        else
          {
            notate.setChordVolume(0);
          }
        notate.setDrumVolume(styleEditor.getVolume());
        s.setTempo(styleEditor.getTempo());
        //s.setVolumes(notate.getMidiSynth());

        new PlayScoreCommand(s,
                             0,
                             true,
                             notate.getMidiSynth(),
                             ImproVisor.getCurrentWindow(),
                             0,
                             notate.getTransposition()).execute();
  }


/**
 * Plays a raw rule. In this case, Strings are stored in the JList and
 * rules must be created from them (in contrast to playSelectedRule()).
 */

public void playRawRule(int type)
  {
    Object rawOb;
    RepPattern repPattern;
    Polylist rule = null;
    int duration = 0;
        switch( type )
          {
            case BASS:
                {
                rawOb = rawRulesJListBass.getSelectedValue();
                if( rawOb instanceof RepresentativeBassRules.BassPattern )
                {
                repPattern = (RepPattern)rawOb;

                RepresentativeBassRules.BassPattern selectedBassRule 
                        = (RepresentativeBassRules.BassPattern) repPattern;
                duration = selectedBassRule.getDuration();
                rule = Notate.parseListFromString(selectedBassRule.toString());
                break;
                }
                }

            case CHORD:
                {
                // There should be some criterion here to mask out lines that
                // don't correspond to rules. The old way, checking for
                // parens at the start and end, is no longer relevant.

                rawOb = rawRulesJListChord.getSelectedValue();
                if( rawOb instanceof RepresentativeChordRules.ChordPattern )
                {
                repPattern = (RepPattern)rawOb;
  
                RepresentativeChordRules.ChordPattern selectedChordRule 
                        = (RepresentativeChordRules.ChordPattern) repPattern;
                duration = selectedChordRule.getDuration();
                rule = Notate.parseListFromString(selectedChordRule.toString());
                break;
                }
                }
                
            case DRUM:
                rawOb = rawRulesJListDrum.getSelectedValue();
                if( rawOb instanceof RepresentativeDrumRules.DrumPattern )
                {
                repPattern = (RepPattern)rawOb;
                  
                RepresentativeDrumRules.DrumPattern selectedDrumPattern 
                        = (RepresentativeDrumRules.DrumPattern) repPattern;
                duration = selectedDrumPattern.getDuration();
                rule = Notate.parseListFromString(selectedDrumPattern.toString());
                break;
                }
          }

        //System.out.println("rule for style = " + rule);
        Style tempStyle = Style.makeStyle(rule);
        tempStyle.setSwing(styleEditor.getSwingValue());
        tempStyle.setAccompanimentSwing(styleEditor.getAccompanimentSwingValue());
        tempStyle.setName("extractionPattern");
        Style.setStyle("extractionPattern", tempStyle);
        // This is necessary so that the StyleListModel menu in notate is reset.
        // Without it, the contents will be emptied.
        notate.reloadStyles();
        ChordPart c = new ChordPart();
        String chord = styleEditor.getChord();
        boolean muteChord = styleEditor.isChordMuted();
        c.addChord(chord, new Double(duration).intValue());
        c.setStyle(tempStyle);

        Score s = new Score(c);
        s.setBassVolume(styleEditor.getVolume());
        if( type == CHORD )
          {
            notate.setChordVolume(styleEditor.getVolume());
          }
        else
          {
            notate.setChordVolume(0);
          }
        notate.setDrumVolume(styleEditor.getVolume());
        s.setTempo(styleEditor.getTempo());
        //s.setVolumes(notate.getMidiSynth());

        new PlayScoreCommand(s,
                             0,
                             true,
                             notate.getMidiSynth(),
                             ImproVisor.getCurrentWindow(),
                             0,
                             notate.getTransposition()).execute();      
  }

public void playRawRule(int type, String raw)

  {
    Object rawOb;
    RepPattern repPattern;
    Polylist rule = null;
    int duration = 0;
        switch( type )
          {
            case BASS:
                {
                rawOb = rawRulesJListBass.getSelectedValue();
                if( rawOb instanceof RepresentativeBassRules.BassPattern )
                {
                repPattern = (RepPattern)rawOb;

                RepresentativeBassRules.BassPattern selectedBassRule 
                        = (RepresentativeBassRules.BassPattern) repPattern;
                duration = selectedBassRule.getDuration();
                rule = Notate.parseListFromString(selectedBassRule.toString());
                break;
                }
                }

            case CHORD:
                {
                // There should be some criterion here to mask out lines that
                // don't correspond to rules. The old way, checking for
                // parens at the start and end, is no longer relevant.

                rawOb = rawRulesJListChord.getSelectedValue();
                if( rawOb instanceof RepresentativeChordRules.ChordPattern )
                {
                repPattern = (RepPattern)rawOb;
  
                RepresentativeChordRules.ChordPattern selectedChordRule 
                        = (RepresentativeChordRules.ChordPattern) repPattern;
                duration = selectedChordRule.getDuration();
                rule = Notate.parseListFromString(selectedChordRule.toString());
                break;
                }
                }
                
            case DRUM:
                rawOb = rawRulesJListDrum.getSelectedValue();
                if( rawOb instanceof RepresentativeDrumRules.DrumPattern )
                {
                repPattern = (RepPattern)rawOb;
                  
                RepresentativeDrumRules.DrumPattern selectedDrumPattern 
                        = (RepresentativeDrumRules.DrumPattern) repPattern;
                duration = selectedDrumPattern.getDuration();
                rule = Notate.parseListFromString(selectedDrumPattern.toString());
                break;
                }
          }

        //System.out.println("rule for style = " + rule);
        Style tempStyle = Style.makeStyle(rule);
        tempStyle.setSwing(styleEditor.getSwingValue());
        tempStyle.setAccompanimentSwing(styleEditor.getAccompanimentSwingValue());
        tempStyle.setName("extractionPattern");
        Style.setStyle("extractionPattern", tempStyle);
        // This is necessary so that the StyleListModel menu in notate is reset.
        // Without it, the contents will be emptied.
        notate.reloadStyles();
        ChordPart c = new ChordPart();
        String chord = styleEditor.getChord();
        boolean muteChord = styleEditor.isChordMuted();
        c.addChord(chord, new Double(duration).intValue());
        c.setStyle(tempStyle);

        Score s = new Score(c);
        s.setBassVolume(styleEditor.getVolume());
        if( type == CHORD )
          {
            notate.setChordVolume(styleEditor.getVolume());
          }
        else
          {
            notate.setChordVolume(0);
          }
        notate.setDrumVolume(styleEditor.getVolume());
        s.setTempo(styleEditor.getTempo());
        //s.setVolumes(notate.getMidiSynth());

        new PlayScoreCommand(s,
                             0,
                             true,
                             notate.getMidiSynth(),
                             ImproVisor.getCurrentWindow(),
                             0,
                             notate.getTransposition()).execute();          
  }

/**
 * This method is called from within the constructor to initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is always
 * regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        extractionTabbedPane = new javax.swing.JTabbedPane();
        bassPanel = new javax.swing.JPanel();
        widePatternScrollPaneBass = new javax.swing.JScrollPane();
        widePatternTextFieldBass = new javax.swing.JTextField();
        rawPatternsPanelBass = new javax.swing.JScrollPane();
        rawRulesJListBass = new javax.swing.JList();
        selectedPatternsPanelBass = new javax.swing.JScrollPane();
        selectedRulesJListBass = new javax.swing.JList();
        optionPanelBass = new javax.swing.JPanel();
        maximumClustersLabelBass = new javax.swing.JLabel();
        startBeatLabelBass = new javax.swing.JLabel();
        startBeatTextFieldBass = new javax.swing.JTextField();
        endBeatLabelBass = new javax.swing.JLabel();
        endBeatTextFieldBass = new javax.swing.JTextField();
        numberOfClustersSpinnerBass = new javax.swing.JSpinner();
        noteResolutionComboBoxBass = new javax.swing.JComboBox();
        potentialInstrumentsScrollPaneBass = new javax.swing.JScrollPane();
        potentialInstrumentsJListBass = new javax.swing.JList();
        selectPatternBtnBass = new javax.swing.JButton();
        leftPlayPatternBtnBass = new javax.swing.JButton();
        removePatternBtnBass = new javax.swing.JButton();
        rightPlayPatternBtnBass = new javax.swing.JButton();
        copySelectionsBtnBass = new javax.swing.JButton();
        reExtractBtnBass = new javax.swing.JButton();
        nextTabBtnBass = new javax.swing.JButton();
        chordPanel = new javax.swing.JPanel();
        widePatternScrollPaneChord = new javax.swing.JScrollPane();
        widePatternTextFieldChord = new javax.swing.JTextField();
        rawPatternsPanelChord = new javax.swing.JScrollPane();
        rawRulesJListChord = new javax.swing.JList();
        selectedPatternsPanelChord = new javax.swing.JScrollPane();
        selectedRulesJListChord = new javax.swing.JList();
        optionPanelChord = new javax.swing.JPanel();
        maximumClustersLabelChord = new javax.swing.JLabel();
        startBeatLabelChord = new javax.swing.JLabel();
        startBeatTextFieldChord = new javax.swing.JTextField();
        endBeatLabelChord = new javax.swing.JLabel();
        endBeatTextFieldChord = new javax.swing.JTextField();
        numberOfClustersSpinnerChord = new javax.swing.JSpinner();
        noteResolutionComboBoxChord = new javax.swing.JComboBox();
        potentialInstrumentsScrollPaneChord = new javax.swing.JScrollPane();
        potentialInstrumentsJListChord = new javax.swing.JList();
        selectPatternBtnChord = new javax.swing.JButton();
        leftPlayPatternBtnChord = new javax.swing.JButton();
        removePatternBtnChord = new javax.swing.JButton();
        rightPlayPatternBtnChord = new javax.swing.JButton();
        copySelectionsBtnChord = new javax.swing.JButton();
        reExtractBtnChord = new javax.swing.JButton();
        nextTabBtn = new javax.swing.JButton();
        drumPanel = new javax.swing.JPanel();
        widePatternScrollPaneDrum = new javax.swing.JScrollPane();
        widePatternTextFieldDrum = new javax.swing.JTextField();
        rawPatternsPanelDrum = new javax.swing.JScrollPane();
        rawRulesJListDrum = new javax.swing.JList();
        selectedPatternsPanelDrum = new javax.swing.JScrollPane();
        selectedRulesJListDrum = new javax.swing.JList();
        optionPanelDrum = new javax.swing.JPanel();
        maximumClustersLabelDrum = new javax.swing.JLabel();
        startBeatLabelDrum = new javax.swing.JLabel();
        startBeatTextFieldDrum = new javax.swing.JTextField();
        endBeatLabelDrum = new javax.swing.JLabel();
        endBeatTextFieldDrum = new javax.swing.JTextField();
        numberOfClustersSpinnerDrum = new javax.swing.JSpinner();
        doubleDrumLengthDrum = new javax.swing.JCheckBox();
        noteResolutionComboBoxDrum = new javax.swing.JComboBox();
        selectPatternBtnDrum = new javax.swing.JButton();
        leftPlayPatternBtnDrum = new javax.swing.JButton();
        removePatternBtnDrum = new javax.swing.JButton();
        rightPlayPatternBtnDrum = new javax.swing.JButton();
        copySelectionsBtnDrum = new javax.swing.JButton();
        reExtractBtnDrum = new javax.swing.JButton();
        closeWindowBtn = new javax.swing.JButton();
        extractChords = new javax.swing.JButton();
        useLeadsheet = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        bassChannelName = new javax.swing.JComboBox();
        chordChannelName = new javax.swing.JComboBox();
        chordResolutionComboBox = new javax.swing.JComboBox();
        extractionEditorMenuBar = new javax.swing.JMenuBar();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();
        menuServingAsLabel = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Style Pattern Extraction");
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        extractionTabbedPane.setMinimumSize(new java.awt.Dimension(1300, 800));
        extractionTabbedPane.setPreferredSize(new java.awt.Dimension(1300, 800));

        bassPanel.setLayout(new java.awt.GridBagLayout());

        widePatternScrollPaneBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Most Recent Bass Pattern"));
        widePatternScrollPaneBass.setMinimumSize(new java.awt.Dimension(31, 100));

        widePatternTextFieldBass.setEditable(false);
        widePatternTextFieldBass.setBorder(null);
        widePatternTextFieldBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widePatternTextFieldBassActionPerformed(evt);
            }
        });
        widePatternScrollPaneBass.setViewportView(widePatternTextFieldBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        bassPanel.add(widePatternScrollPaneBass, gridBagConstraints);

        rawPatternsPanelBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Raw Patterns"));
        rawPatternsPanelBass.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelBass.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListBass.setModel(rawRulesModelBass);
        rawRulesJListBass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawPatternsMouseClickedBass(evt);
            }
        });
        rawPatternsPanelBass.setViewportView(rawRulesJListBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bassPanel.add(rawPatternsPanelBass, gridBagConstraints);

        selectedPatternsPanelBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Patterns"));
        selectedPatternsPanelBass.setMinimumSize(new java.awt.Dimension(300, 200));
        selectedPatternsPanelBass.setPreferredSize(new java.awt.Dimension(300, 200));

        selectedRulesJListBass.setModel(selectedRulesModelBass);
        selectedRulesJListBass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedPatternsMouseClickedBass(evt);
            }
        });
        selectedPatternsPanelBass.setViewportView(selectedRulesJListBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bassPanel.add(selectedPatternsPanelBass, gridBagConstraints);

        optionPanelBass.setBackground(java.awt.Color.orange);
        optionPanelBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Re-Extraction Settings"));
        optionPanelBass.setToolTipText("Extract patterns again, possibly using different parameters.");
        optionPanelBass.setLayout(new java.awt.GridBagLayout());

        maximumClustersLabelBass.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maximumClustersLabelBass.setText("Maximum Clusters: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelBass.add(maximumClustersLabelBass, gridBagConstraints);

        startBeatLabelBass.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        startBeatLabelBass.setText("Start after beats:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelBass.add(startBeatLabelBass, gridBagConstraints);

        startBeatTextFieldBass.setText("8");
        startBeatTextFieldBass.setToolTipText("The starting beat from which patterns will be extracted");
        startBeatTextFieldBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBeatTextFieldBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        optionPanelBass.add(startBeatTextFieldBass, gridBagConstraints);

        endBeatLabelBass.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        endBeatLabelBass.setText("End Beat: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelBass.add(endBeatLabelBass, gridBagConstraints);

        endBeatTextFieldBass.setText(" ");
        endBeatTextFieldBass.setToolTipText("The ending beat from which patterns will be extracted");
        endBeatTextFieldBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endBeatTextFieldBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        optionPanelBass.add(endBeatTextFieldBass, gridBagConstraints);

        numberOfClustersSpinnerBass.setModel(new javax.swing.SpinnerNumberModel(4, 1, 99, 1));
        numberOfClustersSpinnerBass.setToolTipText("The number of clusters sought in pattern extraction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        optionPanelBass.add(numberOfClustersSpinnerBass, gridBagConstraints);

        noteResolutionComboBoxBass.setMaximumRowCount(16);
        noteResolutionComboBoxBass.setModel(NoteResolutionComboBoxModel.getNoteResolutionComboBoxModel());
        noteResolutionComboBoxBass.setSelectedIndex(NoteResolutionComboBoxModel.getSelectedIndex());
        noteResolutionComboBoxBass.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[NoteResolutionComboBoxModel.getSelectedIndex()]);
        noteResolutionComboBoxBass.setToolTipText("Sets the resolution with which MIDI tracks are converted to Impro-Visor notes. Select the highest number of slots that gives satisfactory results. Low numbers take more memory and may fail.");
        noteResolutionComboBoxBass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        noteResolutionComboBoxBass.setMinimumSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBoxBass.setPreferredSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBoxBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteResolutionComboBoxBassimportMidiNoteResolutionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        optionPanelBass.add(noteResolutionComboBoxBass, gridBagConstraints);

        potentialInstrumentsScrollPaneBass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Instrument to Extract", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        potentialInstrumentsJListBass.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        potentialInstrumentsJListBass.setToolTipText("Select MIDI instrument for re-estraction");
        potentialInstrumentsScrollPaneBass.setViewportView(potentialInstrumentsJListBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        optionPanelBass.add(potentialInstrumentsScrollPaneBass, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.4;
        bassPanel.add(optionPanelBass, gridBagConstraints);

        selectPatternBtnBass.setText("Include Pattern in Selections");
        selectPatternBtnBass.setToolTipText("Moves the selected pattern into the right list for inclusion in the style.");
        selectPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPatternBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        bassPanel.add(selectPatternBtnBass, gridBagConstraints);

        leftPlayPatternBtnBass.setText("Play Pattern");
        leftPlayPatternBtnBass.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        leftPlayPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftPlayPatternBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        bassPanel.add(leftPlayPatternBtnBass, gridBagConstraints);

        removePatternBtnBass.setText("Discard Pattern");
        removePatternBtnBass.setToolTipText("Removes the selected pattern from further consideration for the style.");
        removePatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePatternBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        bassPanel.add(removePatternBtnBass, gridBagConstraints);

        rightPlayPatternBtnBass.setText("Play Pattern");
        rightPlayPatternBtnBass.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        rightPlayPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightPlayPatternBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        bassPanel.add(rightPlayPatternBtnBass, gridBagConstraints);

        copySelectionsBtnBass.setText("Copy Selections to Style Editor");
        copySelectionsBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySelectionsBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        bassPanel.add(copySelectionsBtnBass, gridBagConstraints);

        reExtractBtnBass.setToolTipText("Extract the patterns for this window using new parameters.");
        reExtractBtnBass.setLabel("Re-Extract  Bass Patterns");
        reExtractBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reExtractBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        bassPanel.add(reExtractBtnBass, gridBagConstraints);

        nextTabBtnBass.setText("Next Tab");
        nextTabBtnBass.setToolTipText("Move to the next Tab.");
        nextTabBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextTabBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        bassPanel.add(nextTabBtnBass, gridBagConstraints);

        extractionTabbedPane.addTab("Bass Patterns", bassPanel);

        chordPanel.setLayout(new java.awt.GridBagLayout());

        widePatternScrollPaneChord.setBorder(javax.swing.BorderFactory.createTitledBorder("Most Recent Chord Pattern"));
        widePatternScrollPaneChord.setMinimumSize(new java.awt.Dimension(23, 100));

        widePatternTextFieldChord.setEditable(false);
        widePatternTextFieldChord.setBorder(null);
        widePatternTextFieldChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widePatternTextFieldChordActionPerformed(evt);
            }
        });
        widePatternScrollPaneChord.setViewportView(widePatternTextFieldChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        chordPanel.add(widePatternScrollPaneChord, gridBagConstraints);

        rawPatternsPanelChord.setBorder(javax.swing.BorderFactory.createTitledBorder("Raw Patterns"));
        rawPatternsPanelChord.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelChord.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListChord.setModel(rawRulesModelChord);
        rawRulesJListChord.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListMouseClickedChord(evt);
            }
        });
        rawPatternsPanelChord.setViewportView(rawRulesJListChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chordPanel.add(rawPatternsPanelChord, gridBagConstraints);

        selectedPatternsPanelChord.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Patterns"));
        selectedPatternsPanelChord.setMinimumSize(new java.awt.Dimension(300, 200));
        selectedPatternsPanelChord.setPreferredSize(new java.awt.Dimension(300, 200));

        selectedRulesJListChord.setModel(selectedRulesModelChord);
        selectedRulesJListChord.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedRulesJListChordMouseClickedChord(evt);
            }
        });
        selectedPatternsPanelChord.setViewportView(selectedRulesJListChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chordPanel.add(selectedPatternsPanelChord, gridBagConstraints);

        optionPanelChord.setBackground(java.awt.Color.green);
        optionPanelChord.setBorder(javax.swing.BorderFactory.createTitledBorder("Re-Extraction Settings"));
        optionPanelChord.setToolTipText("Extract patterns again, possibly using different parameters.");
        optionPanelChord.setLayout(new java.awt.GridBagLayout());

        maximumClustersLabelChord.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maximumClustersLabelChord.setText("Maximum Clusters: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelChord.add(maximumClustersLabelChord, gridBagConstraints);

        startBeatLabelChord.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        startBeatLabelChord.setText("Start after beats:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.25;
        optionPanelChord.add(startBeatLabelChord, gridBagConstraints);

        startBeatTextFieldChord.setText("8");
        startBeatTextFieldChord.setToolTipText("The starting beat from which patterns will be extracted");
        startBeatTextFieldChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBeatTextFieldChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        optionPanelChord.add(startBeatTextFieldChord, gridBagConstraints);

        endBeatLabelChord.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        endBeatLabelChord.setText("End Beat: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.25;
        optionPanelChord.add(endBeatLabelChord, gridBagConstraints);

        endBeatTextFieldChord.setText(" ");
        endBeatTextFieldChord.setToolTipText("The ending beat from which patterns will be extracted");
        endBeatTextFieldChord.setMinimumSize(new java.awt.Dimension(22, 28));
        endBeatTextFieldChord.setPreferredSize(new java.awt.Dimension(22, 28));
        endBeatTextFieldChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endBeatTextFieldChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        optionPanelChord.add(endBeatTextFieldChord, gridBagConstraints);

        numberOfClustersSpinnerChord.setModel(new javax.swing.SpinnerNumberModel(4, 1, 99, 1));
        numberOfClustersSpinnerChord.setToolTipText("The number of clusters sought in pattern extraction");
        numberOfClustersSpinnerChord.setMinimumSize(new java.awt.Dimension(30, 28));
        numberOfClustersSpinnerChord.setPreferredSize(new java.awt.Dimension(30, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelChord.add(numberOfClustersSpinnerChord, gridBagConstraints);

        noteResolutionComboBoxChord.setMaximumRowCount(16);
        noteResolutionComboBoxChord.setModel(NoteResolutionComboBoxModel.getNoteResolutionComboBoxModel());
        noteResolutionComboBoxChord.setSelectedIndex(NoteResolutionComboBoxModel.getSelectedIndex());
        noteResolutionComboBoxChord.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[NoteResolutionComboBoxModel.getSelectedIndex()]);
        noteResolutionComboBoxChord.setToolTipText("Sets the resolution with which MIDI tracks are converted to Impro-Visor notes. Select the highest number of slots that gives satisfactory results. Low numbers take more memory and may fail.");
        noteResolutionComboBoxChord.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        noteResolutionComboBoxChord.setMinimumSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBoxChord.setPreferredSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBoxChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteResolutionComboBoxChordimportMidiNoteResolutionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        optionPanelChord.add(noteResolutionComboBoxChord, gridBagConstraints);

        potentialInstrumentsScrollPaneChord.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Instrument to Extract", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        potentialInstrumentsJListChord.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        potentialInstrumentsJListChord.setToolTipText("Select MIDI instrument for re-estraction");
        potentialInstrumentsScrollPaneChord.setViewportView(potentialInstrumentsJListChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        optionPanelChord.add(potentialInstrumentsScrollPaneChord, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.4;
        chordPanel.add(optionPanelChord, gridBagConstraints);

        selectPatternBtnChord.setText("Include Pattern in Selections");
        selectPatternBtnChord.setToolTipText("Include Pattern in Selections");
        selectPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPatternBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        chordPanel.add(selectPatternBtnChord, gridBagConstraints);

        leftPlayPatternBtnChord.setText("Play Pattern");
        leftPlayPatternBtnChord.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        leftPlayPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftPlayPatternBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        chordPanel.add(leftPlayPatternBtnChord, gridBagConstraints);

        removePatternBtnChord.setText("Discard Pattern");
        removePatternBtnChord.setToolTipText("Removes the selected pattern from further consideration for the style.");
        removePatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePatternBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        chordPanel.add(removePatternBtnChord, gridBagConstraints);

        rightPlayPatternBtnChord.setText("Play Pattern");
        rightPlayPatternBtnChord.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        rightPlayPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightPlayPatternBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        chordPanel.add(rightPlayPatternBtnChord, gridBagConstraints);

        copySelectionsBtnChord.setText("Copy Selections to Style Editor");
        copySelectionsBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySelectionsBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        chordPanel.add(copySelectionsBtnChord, gridBagConstraints);

        reExtractBtnChord.setToolTipText("Extract the patterns for this window using new parameters.");
        reExtractBtnChord.setLabel("Re-Extract  Chord Patterns");
        reExtractBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reExtractBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        chordPanel.add(reExtractBtnChord, gridBagConstraints);

        nextTabBtn.setText("Next Tab");
        nextTabBtn.setToolTipText("Move to the next Tab.");
        nextTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextTabBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        chordPanel.add(nextTabBtn, gridBagConstraints);

        extractionTabbedPane.addTab("Chord Patterns", chordPanel);

        drumPanel.setLayout(new java.awt.GridBagLayout());

        widePatternScrollPaneDrum.setBorder(javax.swing.BorderFactory.createTitledBorder("Most Recent Drum Pattern"));
        widePatternScrollPaneDrum.setMinimumSize(new java.awt.Dimension(31, 100));

        widePatternTextFieldDrum.setEditable(false);
        widePatternTextFieldDrum.setBorder(null);
        widePatternTextFieldDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widePatternTextFieldDrumActionPerformed(evt);
            }
        });
        widePatternScrollPaneDrum.setViewportView(widePatternTextFieldDrum);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        drumPanel.add(widePatternScrollPaneDrum, gridBagConstraints);

        rawPatternsPanelDrum.setBorder(javax.swing.BorderFactory.createTitledBorder("Raw Patterns"));
        rawPatternsPanelDrum.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelDrum.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListDrum.setModel(rawRulesModelDrum);
        rawRulesJListDrum.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawPatternsMouseClickedDrum(evt);
            }
        });
        rawPatternsPanelDrum.setViewportView(rawRulesJListDrum);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        drumPanel.add(rawPatternsPanelDrum, gridBagConstraints);

        selectedPatternsPanelDrum.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Patterns"));
        selectedPatternsPanelDrum.setMinimumSize(new java.awt.Dimension(300, 200));
        selectedPatternsPanelDrum.setPreferredSize(new java.awt.Dimension(300, 200));

        selectedRulesJListDrum.setModel(selectedRulesModelDrum);
        selectedRulesJListDrum.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedPatternsMouseClickedDrum(evt);
            }
        });
        selectedPatternsPanelDrum.setViewportView(selectedRulesJListDrum);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        drumPanel.add(selectedPatternsPanelDrum, gridBagConstraints);

        optionPanelDrum.setBackground(java.awt.Color.yellow);
        optionPanelDrum.setBorder(javax.swing.BorderFactory.createTitledBorder("Re-Extraction Settings"));
        optionPanelDrum.setToolTipText("Extract patterns again, possibly using different parameters.");
        optionPanelDrum.setLayout(new java.awt.GridBagLayout());

        maximumClustersLabelDrum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maximumClustersLabelDrum.setText("Maximum Clusters: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelDrum.add(maximumClustersLabelDrum, gridBagConstraints);

        startBeatLabelDrum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        startBeatLabelDrum.setText("Start after beats:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelDrum.add(startBeatLabelDrum, gridBagConstraints);

        startBeatTextFieldDrum.setText("8");
        startBeatTextFieldDrum.setToolTipText("The starting beat from which patterns will be extracted");
        startBeatTextFieldDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBeatTextFieldDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        optionPanelDrum.add(startBeatTextFieldDrum, gridBagConstraints);

        endBeatLabelDrum.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        endBeatLabelDrum.setText("End Beat: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionPanelDrum.add(endBeatLabelDrum, gridBagConstraints);

        endBeatTextFieldDrum.setText(" ");
        endBeatTextFieldDrum.setToolTipText("The ending beat from which patterns will be extracted");
        endBeatTextFieldDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endBeatTextFieldDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        optionPanelDrum.add(endBeatTextFieldDrum, gridBagConstraints);

        numberOfClustersSpinnerDrum.setModel(new javax.swing.SpinnerNumberModel(4, 1, 99, 1));
        numberOfClustersSpinnerDrum.setToolTipText("The number of clusters sought in pattern extraction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        optionPanelDrum.add(numberOfClustersSpinnerDrum, gridBagConstraints);

        doubleDrumLengthDrum.setText("Double Drum Length");
        doubleDrumLengthDrum.setToolTipText("Change the length of extracted drum pattern to be double what it was.");
        doubleDrumLengthDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleDrumLengthDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.weightx = 1.0;
        optionPanelDrum.add(doubleDrumLengthDrum, gridBagConstraints);

        noteResolutionComboBoxDrum.setMaximumRowCount(16);
        noteResolutionComboBoxDrum.setModel(NoteResolutionComboBoxModel.getNoteResolutionComboBoxModel());
        noteResolutionComboBoxDrum.setSelectedIndex(NoteResolutionComboBoxModel.getSelectedIndex());
        noteResolutionComboBoxDrum.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[NoteResolutionComboBoxModel.getSelectedIndex()]);
        noteResolutionComboBoxDrum.setToolTipText("Sets the resolution with which MIDI tracks are converted to Impro-Visor notes. Select the highest number of slots that gives satisfactory results. Low numbers take more memory and may fail.");
        noteResolutionComboBoxDrum.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        noteResolutionComboBoxDrum.setMinimumSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBoxDrum.setPreferredSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBoxDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteResolutionComboBoxDrumimportMidiNoteResolutionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        optionPanelDrum.add(noteResolutionComboBoxDrum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.4;
        drumPanel.add(optionPanelDrum, gridBagConstraints);

        selectPatternBtnDrum.setText("Include Pattern in Selections");
        selectPatternBtnDrum.setToolTipText("Moves the selected pattern into the right list for inclusion in the style.");
        selectPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPatternBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        drumPanel.add(selectPatternBtnDrum, gridBagConstraints);

        leftPlayPatternBtnDrum.setText("Play Pattern");
        leftPlayPatternBtnDrum.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        leftPlayPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftPlayPatternBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        drumPanel.add(leftPlayPatternBtnDrum, gridBagConstraints);

        removePatternBtnDrum.setText("Discard Pattern");
        removePatternBtnDrum.setToolTipText("Removes the selected pattern from further consideration for the style.");
        removePatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePatternBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        drumPanel.add(removePatternBtnDrum, gridBagConstraints);

        rightPlayPatternBtnDrum.setText("Play Pattern");
        rightPlayPatternBtnDrum.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        rightPlayPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightPlayPatternBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        drumPanel.add(rightPlayPatternBtnDrum, gridBagConstraints);

        copySelectionsBtnDrum.setText("Copy Selections to Style Editor");
        copySelectionsBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySelectionsBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        drumPanel.add(copySelectionsBtnDrum, gridBagConstraints);

        reExtractBtnDrum.setToolTipText("Extract the patterns for this window using new parameters.");
        reExtractBtnDrum.setLabel("Re-Extract  Drum Patterns");
        reExtractBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reExtractBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        drumPanel.add(reExtractBtnDrum, gridBagConstraints);

        closeWindowBtn.setText("Dismiss this Window");
        closeWindowBtn.setToolTipText("Close the window. Any patterns to be included should be copied to the Style Editor before closing.");
        closeWindowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        drumPanel.add(closeWindowBtn, gridBagConstraints);

        extractionTabbedPane.addTab("Drum Patterns", drumPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        getContentPane().add(extractionTabbedPane, gridBagConstraints);

        extractChords.setText("Extract Chords");
        extractChords.setToolTipText("Extracts the chords for the given chord resolution and chord/bass channel number.");
        extractChords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractChordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(extractChords, gridBagConstraints);

        useLeadsheet.setText("Use Leadsheet");
        useLeadsheet.setToolTipText("If ticked, will prompt leadsheet file when Extract Chords button is pressed. Otherwise, Extract Chords will extract chords from the midi file given the corresponding bass and chord channel info.");
        useLeadsheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLeadsheetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(useLeadsheet, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Channel #"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        bassChannelName.setToolTipText("Sets the bass channel number for chord extraction.");
        bassChannelName.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bass", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        bassChannelName.setMaximumSize(null);
        bassChannelName.setMinimumSize(null);
        bassChannelName.setPreferredSize(null);
        bassChannelName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassChannelNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(bassChannelName, gridBagConstraints);

        chordChannelName.setToolTipText("Sets the chord/accompaniment channel number for chord extraction.");
        chordChannelName.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chord", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        chordChannelName.setMaximumSize(null);
        chordChannelName.setMinimumSize(null);
        chordChannelName.setPreferredSize(null);
        chordChannelName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordChannelNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(chordChannelName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        getContentPane().add(jPanel1, gridBagConstraints);

        chordResolutionComboBox.setMaximumRowCount(16);
        chordResolutionComboBox.setModel(ChordResolutionComboBoxModel.getChordResolutionComboBoxModel());
        chordResolutionComboBox.setSelectedIndex(ChordResolutionComboBoxModel.getSelectedIndex());
        chordResolutionComboBox.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[ChordResolutionComboBoxModel.getSelectedIndex()]);
        chordResolutionComboBox.setToolTipText("Sets the resolution with which the MIDI channels are converted to Impro-Visor chords. Quarter note and half note resolution recommended.");
        chordResolutionComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chord Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        chordResolutionComboBox.setMinimumSize(new java.awt.Dimension(300, 60));
        chordResolutionComboBox.setPreferredSize(new java.awt.Dimension(300, 75));
        chordResolutionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordResolutionComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(chordResolutionComboBox, gridBagConstraints);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window");
        windowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                windowMenuMenuSelected(evt);
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window");
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows");
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(windowMenuSeparator);

        extractionEditorMenuBar.add(windowMenu);

        menuServingAsLabel.setText("(Tempo and Volume are set in the Style Editor)");
        extractionEditorMenuBar.add(menuServingAsLabel);

        setJMenuBar(extractionEditorMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void leftPlayPatternBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leftPlayPatternBtnBassActionPerformed
  {//GEN-HEADEREND:event_leftPlayPatternBtnBassActionPerformed
      playRawRule(BASS);
  }//GEN-LAST:event_leftPlayPatternBtnBassActionPerformed

private void rightPlayPatternBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rightPlayPatternBtnBassActionPerformed
  {//GEN-HEADEREND:event_rightPlayPatternBtnBassActionPerformed
      playSelectedRule(BASS);
  }//GEN-LAST:event_rightPlayPatternBtnBassActionPerformed

private void nextTabBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextTabBtnBassActionPerformed
  {//GEN-HEADEREND:event_nextTabBtnBassActionPerformed
  extractionTabbedPane.setSelectedIndex(1);      
  }//GEN-LAST:event_nextTabBtnBassActionPerformed

private void copySelectionsBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copySelectionsBtnBassActionPerformed
  {//GEN-HEADEREND:event_copySelectionsBtnBassActionPerformed
      MIDIBeast.selectedBassRules = selectedBassRules;
      styleEditor.loadBassPatterns(MIDIBeast.getRepBassRules().getBassRules());
  }//GEN-LAST:event_copySelectionsBtnBassActionPerformed

private void startBeatTextFieldBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startBeatTextFieldBassActionPerformed
  {//GEN-HEADEREND:event_startBeatTextFieldBassActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_startBeatTextFieldBassActionPerformed

private void selectPatternBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectPatternBtnBassActionPerformed
  {//GEN-HEADEREND:event_selectPatternBtnBassActionPerformed
      Object ob = rawRulesJListBass.getSelectedValue();
      //System.out.println("ob = " + ob + " class = " + ob.getClass());
      if( ob instanceof RepPattern )
        {
          //System.out.println("selected rule " + incompleteRule);

          RepPattern repPattern = (RepPattern) ob;

          int index = rawRulesJListBass.getSelectedIndex();

          // There should be some criterion here to mask out lines that
          // don't correspond to rules. The old way, checking for
          // parens at the start and end, is no longer relevant.

          RepresentativeBassRules.BassPattern selectedBassRule = (RepresentativeBassRules.BassPattern) repPattern;

          selectedBassRules.add(selectedBassRule);
          setBassSelectedRules();

          rawRulesModelBass.removeElement(ob);
          rawRulesJListBass.setSelectedIndex(Math.max(0, index - 1));
        }
  }//GEN-LAST:event_selectPatternBtnBassActionPerformed

private void selectedPatternsMouseClickedBass(java.awt.event.MouseEvent evt)//GEN-FIRST:event_selectedPatternsMouseClickedBass
  {//GEN-HEADEREND:event_selectedPatternsMouseClickedBass
      Object selectedOb = selectedRulesJListBass.getSelectedValue();
      if( selectedOb instanceof RepPattern )
        {
        widePatternTextFieldBass.setText(selectedOb.toString());
        playSelectedRule(BASS);
        }
  }//GEN-LAST:event_selectedPatternsMouseClickedBass

private void rawPatternsMouseClickedBass(java.awt.event.MouseEvent evt)//GEN-FIRST:event_rawPatternsMouseClickedBass
  {//GEN-HEADEREND:event_rawPatternsMouseClickedBass
      Object selectedOb = rawRulesJListBass.getSelectedValue();
      if( selectedOb instanceof RepPattern )
        {
        widePatternTextFieldBass.setText(selectedOb.toString());
        playRawRule(BASS);
        }
  }//GEN-LAST:event_rawPatternsMouseClickedBass

private void removePatternBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removePatternBtnBassActionPerformed
  {//GEN-HEADEREND:event_removePatternBtnBassActionPerformed
      int indexOfRuleToBeRemoved = selectedRulesJListBass.getSelectedIndex();
      selectedBassRules.remove(indexOfRuleToBeRemoved);
      selectedRulesJListBass.setListData(selectedBassRules.toArray());
      selectedRulesJListBass.setSelectedIndex(Math.max(0, indexOfRuleToBeRemoved - 1));
  }//GEN-LAST:event_removePatternBtnBassActionPerformed

private void reExtractBtnBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reExtractBtnBassActionPerformed
  {//GEN-HEADEREND:event_reExtractBtnBassActionPerformed
      checkForAndThrowErrors();

      double endBeat = Double.parseDouble(endBeatTextFieldBass.getText());
      double startBeat = Double.parseDouble(startBeatTextFieldBass.getText());

      Integer maxNumberOfClusters = (Integer) numberOfClustersSpinnerBass.getValue();
      int selectedIndex;
      jm.music.data.Part selectedPart;

      selectedIndex = potentialInstrumentsJListBass.getSelectedIndex();
      selectedPart = MIDIBeast.allParts.get(selectedIndex); //Implement part selection

      repBassRules = new RepresentativeBassRules(startBeat,
                                                 endBeat,
                                                 maxNumberOfClusters,
                                                 selectedPart);
      MIDIBeast.setRepBassRules(repBassRules);
      setBassRawRules();
      setBassSelectedRules();
  }//GEN-LAST:event_reExtractBtnBassActionPerformed

private void endBeatTextFieldBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_endBeatTextFieldBassActionPerformed
  {//GEN-HEADEREND:event_endBeatTextFieldBassActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_endBeatTextFieldBassActionPerformed

private void widePatternTextFieldBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_widePatternTextFieldBassActionPerformed
  {//GEN-HEADEREND:event_widePatternTextFieldBassActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_widePatternTextFieldBassActionPerformed

private void noteResolutionComboBoxBassimportMidiNoteResolutionChanged(java.awt.event.ActionEvent evt)//GEN-FIRST:event_noteResolutionComboBoxBassimportMidiNoteResolutionChanged
  {//GEN-HEADEREND:event_noteResolutionComboBoxBassimportMidiNoteResolutionChanged
    int newResolution = ((NoteResolutionInfo)noteResolutionComboBoxBass.getSelectedItem()).getSlots();
    MIDIBeast.setResolution(newResolution);
    NoteResolutionComboBoxModel.setSelectedIndex(noteResolutionComboBoxBass.getSelectedIndex());
  }//GEN-LAST:event_noteResolutionComboBoxBassimportMidiNoteResolutionChanged

private void rawRulesJListMouseClickedChord(java.awt.event.MouseEvent evt)//GEN-FIRST:event_rawRulesJListMouseClickedChord
  {//GEN-HEADEREND:event_rawRulesJListMouseClickedChord
     Object selectedOb = rawRulesJListChord.getSelectedValue();
      if( selectedOb instanceof RepPattern )
        {
        widePatternTextFieldChord.setText(selectedOb.toString());
        playRawRule(CHORD);
        }
  }//GEN-LAST:event_rawRulesJListMouseClickedChord

private void selectedRulesJListChordMouseClickedChord(java.awt.event.MouseEvent evt)//GEN-FIRST:event_selectedRulesJListChordMouseClickedChord
  {//GEN-HEADEREND:event_selectedRulesJListChordMouseClickedChord
      Object selectedOb = selectedRulesJListChord.getSelectedValue();
      if( selectedOb instanceof RepPattern )
        {
        widePatternTextFieldChord.setText(selectedOb.toString());
        playSelectedRule(CHORD);
        }
  }//GEN-LAST:event_selectedRulesJListChordMouseClickedChord

private void startBeatTextFieldChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startBeatTextFieldChordActionPerformed
  {//GEN-HEADEREND:event_startBeatTextFieldChordActionPerformed

  }//GEN-LAST:event_startBeatTextFieldChordActionPerformed

private void endBeatTextFieldChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_endBeatTextFieldChordActionPerformed
  {//GEN-HEADEREND:event_endBeatTextFieldChordActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_endBeatTextFieldChordActionPerformed

private void reExtractBtnChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reExtractBtnChordActionPerformed
  {//GEN-HEADEREND:event_reExtractBtnChordActionPerformed
      checkForAndThrowErrors();

      double endBeat = Double.parseDouble(endBeatTextFieldBass.getText());
      double startBeat = Double.parseDouble(startBeatTextFieldBass.getText());

      Integer maxNumberOfClusters = (Integer) numberOfClustersSpinnerBass.getValue();
      int selectedIndex;
      jm.music.data.Part selectedPart;

      selectedIndex = potentialInstrumentsJListBass.getSelectedIndex();
      selectedPart = MIDIBeast.allParts.get(selectedIndex); //Implement part selection

      repChordRules =
              new RepresentativeChordRules(startBeat,
                                           endBeat,
                                           maxNumberOfClusters,
                                           selectedPart,
                                           minDuration);
      MIDIBeast.setRepChordRules(repChordRules);
      setChordRawRules();
      setChordSelectedRules();
  }//GEN-LAST:event_reExtractBtnChordActionPerformed

private void noteResolutionComboBoxChordimportMidiNoteResolutionChanged(java.awt.event.ActionEvent evt)//GEN-FIRST:event_noteResolutionComboBoxChordimportMidiNoteResolutionChanged
  {//GEN-HEADEREND:event_noteResolutionComboBoxChordimportMidiNoteResolutionChanged
    // TODO add your handling code here:
  }//GEN-LAST:event_noteResolutionComboBoxChordimportMidiNoteResolutionChanged

private void selectPatternBtnChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectPatternBtnChordActionPerformed
  {//GEN-HEADEREND:event_selectPatternBtnChordActionPerformed
      Object ob = rawRulesJListChord.getSelectedValue();
      //System.out.println("ob = " + ob + " class = " + ob.getClass());
      if( ob instanceof RepPattern )
        {
          //System.out.println("selected rule " + incompleteRule);

          RepPattern repPattern = (RepPattern) ob;

          int index = rawRulesJListChord.getSelectedIndex();
          // There should be some criterion here to mask out lines that
          // don't correspond to rules. The old way, checking for
          // parens at the start and end, is no longer relevant.

          RepresentativeChordRules.ChordPattern selectedChordRule = (RepresentativeChordRules.ChordPattern) repPattern;

          selectedChordRules.add(selectedChordRule);
          setChordSelectedRules();

          rawRulesModelChord.removeElement(ob);
          rawRulesJListChord.setSelectedIndex(Math.max(0, index - 1));
        }
  }//GEN-LAST:event_selectPatternBtnChordActionPerformed

private void leftPlayPatternBtnChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leftPlayPatternBtnChordActionPerformed
  {//GEN-HEADEREND:event_leftPlayPatternBtnChordActionPerformed
    playRawRule(CHORD);
  }//GEN-LAST:event_leftPlayPatternBtnChordActionPerformed

private void removePatternBtnChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removePatternBtnChordActionPerformed
  {//GEN-HEADEREND:event_removePatternBtnChordActionPerformed
    int indexOfRuleToBeRemoved = selectedRulesJListBass.getSelectedIndex();
    try
      {
      selectedChordRules.remove(indexOfRuleToBeRemoved);
      selectedRulesJListChord.setListData(selectedChordRules.toArray());
      selectedRulesJListChord.setSelectedIndex(Math.max(0, indexOfRuleToBeRemoved - 1));
      }
    catch( Exception e )
      {
        // Should not happen, but has.
      }
  }//GEN-LAST:event_removePatternBtnChordActionPerformed

private void rightPlayPatternBtnChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rightPlayPatternBtnChordActionPerformed
  {//GEN-HEADEREND:event_rightPlayPatternBtnChordActionPerformed
    playSelectedRule(CHORD);
  }//GEN-LAST:event_rightPlayPatternBtnChordActionPerformed

private void copySelectionsBtnChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copySelectionsBtnChordActionPerformed
  {//GEN-HEADEREND:event_copySelectionsBtnChordActionPerformed
      MIDIBeast.selectedChordRules = selectedChordRules;
      styleEditor.loadChordPatterns(MIDIBeast.getRepChordRules().getChordRules());
  }//GEN-LAST:event_copySelectionsBtnChordActionPerformed

private void nextTabBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextTabBtnActionPerformed
  {//GEN-HEADEREND:event_nextTabBtnActionPerformed
  extractionTabbedPane.setSelectedIndex(2);    
  }//GEN-LAST:event_nextTabBtnActionPerformed

private void widePatternTextFieldChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_widePatternTextFieldChordActionPerformed
  {//GEN-HEADEREND:event_widePatternTextFieldChordActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_widePatternTextFieldChordActionPerformed

private void rawPatternsMouseClickedDrum(java.awt.event.MouseEvent evt)//GEN-FIRST:event_rawPatternsMouseClickedDrum
  {//GEN-HEADEREND:event_rawPatternsMouseClickedDrum
      Object selectedOb = rawRulesJListDrum.getSelectedValue();
      if( selectedOb instanceof RepPattern )
        {
        widePatternTextFieldDrum.setText(selectedOb.toString());
        playRawRule(DRUM);
        }
  }//GEN-LAST:event_rawPatternsMouseClickedDrum

private void selectedPatternsMouseClickedDrum(java.awt.event.MouseEvent evt)//GEN-FIRST:event_selectedPatternsMouseClickedDrum
  {//GEN-HEADEREND:event_selectedPatternsMouseClickedDrum
      Object selectedOb = selectedRulesJListDrum.getSelectedValue();
      if( selectedOb instanceof RepPattern )
        {
        widePatternTextFieldDrum.setText(selectedOb.toString());
        playSelectedRule(DRUM);
        }
  }//GEN-LAST:event_selectedPatternsMouseClickedDrum

private void startBeatTextFieldDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startBeatTextFieldDrumActionPerformed
  {//GEN-HEADEREND:event_startBeatTextFieldDrumActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_startBeatTextFieldDrumActionPerformed

private void endBeatTextFieldDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_endBeatTextFieldDrumActionPerformed
  {//GEN-HEADEREND:event_endBeatTextFieldDrumActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_endBeatTextFieldDrumActionPerformed

private void reExtractBtnDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reExtractBtnDrumActionPerformed
  {//GEN-HEADEREND:event_reExtractBtnDrumActionPerformed
      checkForAndThrowErrors();

      double endBeat = Double.parseDouble(endBeatTextFieldBass.getText());
      double startBeat = Double.parseDouble(startBeatTextFieldBass.getText());

      Integer maxNumberOfClusters = (Integer) numberOfClustersSpinnerBass.getValue();
      int selectedIndex;
      jm.music.data.Part selectedPart;

      potentialInstrumentsJListBass.setSelectedValue("DRUMS", true);
      selectedIndex = potentialInstrumentsJListBass.getSelectedIndex();
      selectedPart = MIDIBeast.allParts.get(selectedIndex); //Implement part selection
      repDrumRules =
              new RepresentativeDrumRules(startBeat,
                                          endBeat,
                                          maxNumberOfClusters,
                                          selectedPart);
      MIDIBeast.setRepDrumRules(repDrumRules);
      setDrumRawRules();
      setDrumSelectedRules();
  }//GEN-LAST:event_reExtractBtnDrumActionPerformed

private void doubleDrumLengthDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doubleDrumLengthDrumActionPerformed
  {//GEN-HEADEREND:event_doubleDrumLengthDrumActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_doubleDrumLengthDrumActionPerformed

private void noteResolutionComboBoxDrumimportMidiNoteResolutionChanged(java.awt.event.ActionEvent evt)//GEN-FIRST:event_noteResolutionComboBoxDrumimportMidiNoteResolutionChanged
  {//GEN-HEADEREND:event_noteResolutionComboBoxDrumimportMidiNoteResolutionChanged
    // TODO add your handling code here:
  }//GEN-LAST:event_noteResolutionComboBoxDrumimportMidiNoteResolutionChanged

private void selectPatternBtnDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectPatternBtnDrumActionPerformed
  {//GEN-HEADEREND:event_selectPatternBtnDrumActionPerformed
      Object ob = rawRulesJListDrum.getSelectedValue();
      //System.out.println("ob = " + ob + " class = " + ob.getClass());
      if( ob instanceof RepPattern )
        {
          //System.out.println("selected rule " + incompleteRule);

          RepPattern repPattern = (RepPattern) ob;

          int index = rawRulesJListDrum.getSelectedIndex();

          RepresentativeDrumRules.DrumPattern drumPattern = (RepresentativeDrumRules.DrumPattern) repPattern;

          selectedDrumRules.add(drumPattern);
          setDrumSelectedRules();

          rawRulesModelDrum.removeElement(ob);
          rawRulesJListDrum.setSelectedIndex(Math.max(0, index-1));
        }
  }//GEN-LAST:event_selectPatternBtnDrumActionPerformed

private void leftPlayPatternBtnDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leftPlayPatternBtnDrumActionPerformed
  {//GEN-HEADEREND:event_leftPlayPatternBtnDrumActionPerformed
    playRawRule(DRUM);
  }//GEN-LAST:event_leftPlayPatternBtnDrumActionPerformed

private void removePatternBtnDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removePatternBtnDrumActionPerformed
  {//GEN-HEADEREND:event_removePatternBtnDrumActionPerformed
      int indexOfRuleToBeRemoved = selectedRulesJListDrum.getSelectedIndex();

      selectedDrumRules.remove(indexOfRuleToBeRemoved);
      selectedRulesJListDrum.setListData(selectedDrumRules.toArray());
      selectedRulesJListDrum.setSelectedIndex(Math.max(0, indexOfRuleToBeRemoved - 1));
  }//GEN-LAST:event_removePatternBtnDrumActionPerformed

private void rightPlayPatternBtnDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rightPlayPatternBtnDrumActionPerformed
  {//GEN-HEADEREND:event_rightPlayPatternBtnDrumActionPerformed
    playSelectedRule(DRUM);
  }//GEN-LAST:event_rightPlayPatternBtnDrumActionPerformed

private void copySelectionsBtnDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copySelectionsBtnDrumActionPerformed
  {//GEN-HEADEREND:event_copySelectionsBtnDrumActionPerformed
      MIDIBeast.selectedDrumRules = selectedDrumRules;
      styleEditor.loadDrumPatterns(MIDIBeast.getRepDrumRules().getRepresentativePatterns());
  }//GEN-LAST:event_copySelectionsBtnDrumActionPerformed

private void closeWindowBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowBtnActionPerformed
  {//GEN-HEADEREND:event_closeWindowBtnActionPerformed
    dispose();
  }//GEN-LAST:event_closeWindowBtnActionPerformed

private void widePatternTextFieldDrumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_widePatternTextFieldDrumActionPerformed
  {//GEN-HEADEREND:event_widePatternTextFieldDrumActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_widePatternTextFieldDrumActionPerformed

private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowMIActionPerformed
  {//GEN-HEADEREND:event_closeWindowMIActionPerformed
    dispose();
  }//GEN-LAST:event_closeWindowMIActionPerformed

private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMIActionPerformed
  {//GEN-HEADEREND:event_cascadeMIActionPerformed
    WindowRegistry.cascadeWindows(this);
  }//GEN-LAST:event_cascadeMIActionPerformed

private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt)//GEN-FIRST:event_windowMenuMenuSelected
  {//GEN-HEADEREND:event_windowMenuMenuSelected
    windowMenu.removeAll();

    windowMenu.add(closeWindowMI);

    windowMenu.add(cascadeMI);

    windowMenu.add(windowMenuSeparator);

    for( WindowMenuItem w : WindowRegistry.getWindows() )
      {
        windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
      }

    windowMenu.repaint();
  }//GEN-LAST:event_windowMenuMenuSelected

    private void chordChannelNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordChannelNameActionPerformed
        ChannelInfo chordInfo = (ChannelInfo) chordChannelName.getSelectedItem();
        MIDIBeast.setChordChannel(chordInfo.getChannelNum()-1);
        chordChannelName.setSelectedIndex(chordChannelName.getSelectedIndex());
    }//GEN-LAST:event_chordChannelNameActionPerformed

    private void bassChannelNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassChannelNameActionPerformed
        ChannelInfo bassInfo = (ChannelInfo) bassChannelName.getSelectedItem();
        MIDIBeast.setBassChannel(bassInfo.getChannelNum()-1);
        bassChannelName.setSelectedIndex(bassChannelName.getSelectedIndex());
    }//GEN-LAST:event_bassChannelNameActionPerformed

    private void extractChordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractChordsActionPerformed
        if (MIDIBeast.useLeadsheet) {
            String chordFile = "";
            int chordChoice = chordFileChooser.showOpenDialog(this);
            if (chordChoice == JFileChooser.CANCEL_OPTION) {
                useLeadsheet.setSelected(false);
                MIDIBeast.useLeadsheet = false;
                return;
            }
            if (chordChoice == JFileChooser.APPROVE_OPTION) {
                chordFile = chordFileChooser.getSelectedFile().getAbsolutePath();
            }
            MIDIBeast.chordFileName = chordFile;
        }
        else
        {
            MIDIBeast.extractChords();
        }
    }//GEN-LAST:event_extractChordsActionPerformed

    private void useLeadsheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLeadsheetActionPerformed
        MIDIBeast.useLeadsheet = useLeadsheet.isSelected();
    }//GEN-LAST:event_useLeadsheetActionPerformed

    private void chordResolutionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordResolutionComboBoxActionPerformed
        ChordResolutionComboBoxModel.setSelectedIndex(chordResolutionComboBox.getSelectedIndex());
        MIDIBeast.setChordResolution(ChordResolutionComboBoxModel.getResolution());
    }//GEN-LAST:event_chordResolutionComboBoxActionPerformed


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox bassChannelName;
    private javax.swing.JPanel bassPanel;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JComboBox chordChannelName;
    private javax.swing.JPanel chordPanel;
    private javax.swing.JComboBox chordResolutionComboBox;
    private javax.swing.JButton closeWindowBtn;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JButton copySelectionsBtnBass;
    private javax.swing.JButton copySelectionsBtnChord;
    private javax.swing.JButton copySelectionsBtnDrum;
    private javax.swing.JCheckBox doubleDrumLengthDrum;
    private javax.swing.JPanel drumPanel;
    private javax.swing.JLabel endBeatLabelBass;
    private javax.swing.JLabel endBeatLabelChord;
    private javax.swing.JLabel endBeatLabelDrum;
    private javax.swing.JTextField endBeatTextFieldBass;
    private javax.swing.JTextField endBeatTextFieldChord;
    private javax.swing.JTextField endBeatTextFieldDrum;
    private javax.swing.JButton extractChords;
    private javax.swing.JMenuBar extractionEditorMenuBar;
    private javax.swing.JTabbedPane extractionTabbedPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton leftPlayPatternBtnBass;
    private javax.swing.JButton leftPlayPatternBtnChord;
    private javax.swing.JButton leftPlayPatternBtnDrum;
    private javax.swing.JLabel maximumClustersLabelBass;
    private javax.swing.JLabel maximumClustersLabelChord;
    private javax.swing.JLabel maximumClustersLabelDrum;
    private javax.swing.JMenu menuServingAsLabel;
    private javax.swing.JButton nextTabBtn;
    private javax.swing.JButton nextTabBtnBass;
    private javax.swing.JComboBox noteResolutionComboBoxBass;
    private javax.swing.JComboBox noteResolutionComboBoxChord;
    private javax.swing.JComboBox noteResolutionComboBoxDrum;
    private javax.swing.JSpinner numberOfClustersSpinnerBass;
    private javax.swing.JSpinner numberOfClustersSpinnerChord;
    private javax.swing.JSpinner numberOfClustersSpinnerDrum;
    private javax.swing.JPanel optionPanelBass;
    private javax.swing.JPanel optionPanelChord;
    private javax.swing.JPanel optionPanelDrum;
    private javax.swing.JList potentialInstrumentsJListBass;
    private javax.swing.JList potentialInstrumentsJListChord;
    private javax.swing.JScrollPane potentialInstrumentsScrollPaneBass;
    private javax.swing.JScrollPane potentialInstrumentsScrollPaneChord;
    private javax.swing.JScrollPane rawPatternsPanelBass;
    private javax.swing.JScrollPane rawPatternsPanelChord;
    private javax.swing.JScrollPane rawPatternsPanelDrum;
    private javax.swing.JList rawRulesJListBass;
    private javax.swing.JList rawRulesJListChord;
    private javax.swing.JList rawRulesJListDrum;
    private javax.swing.JButton reExtractBtnBass;
    private javax.swing.JButton reExtractBtnChord;
    private javax.swing.JButton reExtractBtnDrum;
    private javax.swing.JButton removePatternBtnBass;
    private javax.swing.JButton removePatternBtnChord;
    private javax.swing.JButton removePatternBtnDrum;
    private javax.swing.JButton rightPlayPatternBtnBass;
    private javax.swing.JButton rightPlayPatternBtnChord;
    private javax.swing.JButton rightPlayPatternBtnDrum;
    private javax.swing.JButton selectPatternBtnBass;
    private javax.swing.JButton selectPatternBtnChord;
    private javax.swing.JButton selectPatternBtnDrum;
    private javax.swing.JScrollPane selectedPatternsPanelBass;
    private javax.swing.JScrollPane selectedPatternsPanelChord;
    private javax.swing.JScrollPane selectedPatternsPanelDrum;
    private javax.swing.JList selectedRulesJListBass;
    private javax.swing.JList selectedRulesJListChord;
    private javax.swing.JList selectedRulesJListDrum;
    private javax.swing.JLabel startBeatLabelBass;
    private javax.swing.JLabel startBeatLabelChord;
    private javax.swing.JLabel startBeatLabelDrum;
    private javax.swing.JTextField startBeatTextFieldBass;
    private javax.swing.JTextField startBeatTextFieldChord;
    private javax.swing.JTextField startBeatTextFieldDrum;
    private javax.swing.JCheckBox useLeadsheet;
    private javax.swing.JScrollPane widePatternScrollPaneBass;
    private javax.swing.JScrollPane widePatternScrollPaneChord;
    private javax.swing.JScrollPane widePatternScrollPaneDrum;
    private javax.swing.JTextField widePatternTextFieldBass;
    private javax.swing.JTextField widePatternTextFieldChord;
    private javax.swing.JTextField widePatternTextFieldDrum;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

private javax.swing.JButton errorButton;
private javax.swing.JDialog errorDialog;
private javax.swing.JLabel errorMessage;

  /**
   * Override dispose so as to unregister this window first.
   */
  
  @Override
  public void dispose()
    {
    WindowRegistry.unregisterWindow(this);
    super.dispose();
    }
}
