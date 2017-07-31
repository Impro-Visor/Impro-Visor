package imp.trading;
 
import static imp.Constants.DEFAULT_BEATS_PER_BAR;
import imp.com.CommandManager;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.Key;
import imp.data.LeadsheetImageListModel;
import imp.data.MelodyPart;
import imp.data.NoteSymbol;
import imp.data.RhythmCluster;
import imp.data.RhythmListCellRenderer;
import imp.data.RhythmSelecterEntry;
import imp.data.Score;
import imp.data.Transposition;
import imp.data.advice.AdviceForMelody;
import static imp.generalCluster.CreateGrammar.SEG_LENGTH;
import imp.generalCluster.DataPoint;
import imp.gui.Notate;
import static imp.gui.Notate.DEFAULT_BARS_PER_PART;
import static imp.trading.UserRhythmSelecterDialog.readInRuleStringsFromFile;
import imp.trading.tradingResponseModes.CorrectRhythmTRM;
import imp.trading.tradingResponseModes.RhythmHelperTRM;
import imp.trading.tradingResponseModes.TradingResponseMode;
import imp.util.Preferences;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import polya.Polylist;
 
/**
 *
 * @author cssummer17
 */
public class TradingGoalsDialog extends javax.swing.JDialog implements java.beans.Customizer {
   
    private Object bean;
    /*
     The list of rhythm clusters from the last trading session
    */
    private ArrayList<RhythmCluster> rhythmClusters;
   
    public static final java.awt.Point INITIAL_OPEN_POINT = new java.awt.Point(25, 0);
    /*
     An array of checkbox's from the add panel. Syncronized with userData to keep track of which rhythms to add and which rhythms are left to add
    */
    private ArrayList<JCheckBox> checkBoxArray;
   
    private Notate notate;
   
    private JButton addButton;
 
    JScrollPane UserGoalsScrollPane;

    private CorrectRhythmTRM rhythmHelperTRM;
    
    private ArrayList<Polylist> clusterRuleStrings;
    
    JButton tradeButton;
    
    JPanel framePanel;
    
    private Notate rhythmNotate;
   
    private JList goalsList;
   
   
    /**
     * Creates new customizer TradingGoalsDialog
     */
    public TradingGoalsDialog(TradingResponseMode trm, ActiveTrading activeTrading) {
        if( !(trm instanceof CorrectRhythmTRM) ){
            System.out.println("As of now, TradingGoalsDialog Dialog only works with TRM's of type CorrectRhythmTRM! "
                    + "Not going to show TradingGoalsDialog.");
            dispose();
        }
           
        rhythmHelperTRM = (CorrectRhythmTRM) trm;
        this.notate  = rhythmHelperTRM.getNotate();
        rhythmNotate = getRhythmNotate();
       
       
        //this.windowSize = rhythmHelperTRM.getWindowSizeOfCluster();
        this.rhythmClusters = rhythmHelperTRM.getRhythmClusters();
        
        
        clusterRuleStrings = getClusterRuleStrings(rhythmClusters);
        UserGoalsScrollPane = new JScrollPane();
        System.out.println("clusterRuleStrings: " + clusterRuleStrings);
        //refreshDialog();
        createDialog();
       
       
        tradeButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
              ArrayList<Polylist> selectedRuleStrings = getSelectedRuleStrings();
              if(selectedRuleStrings.size() == 0){
                  JOptionPane.showMessageDialog(framePanel, "Please select at least one rhythm to emulate!", "Warning", JOptionPane.WARNING_MESSAGE); 
              }else{
                System.out.println("selectedRuleStrings: "+selectedRuleStrings);
                rhythmHelperTRM.setRuleStringsToEmulate(selectedRuleStrings);
                activeTrading.startTradingFromTradingGoalsDialog();
                rhythmNotate.dispose();
                dispose();
              }
          }  
        
        } );
       
       
       
    }
    
    private ArrayList<Polylist> getSelectedRuleStrings(){
        ArrayList<Polylist> selectedRuleStrings = new ArrayList<Polylist>();
        List<RhythmSelecterEntry> selectedValues = goalsList.getSelectedValuesList();
        for(int i = 0; i < selectedValues.size(); i++){
            selectedRuleStrings.add(selectedValues.get(i).getRuleStringPL());
        }
        return selectedRuleStrings;
    }
    
    
    
    private void createDialog(){
        //Create the panel that constains everything in dialog
        framePanel = new JPanel();
        framePanel.setBackground(Color.PINK);
        framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.Y_AXIS));
        
        //add label for the add side
        JLabel addLabel = new JLabel("Select the rhythms you want to emulate!");
        addLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        framePanel.add(addLabel);
        
        //Create and add scroll pane with user rhythms to dialog
        goalsList = getGoalsList();
        UserGoalsScrollPane.setViewportView(goalsList);
        framePanel.add(UserGoalsScrollPane);
        this.add(framePanel);

        //Create and add add button to dialog
        tradeButton = getTradeButton();
        JPanel ButtonPanel = new JPanel();
         
        ButtonPanel.add(tradeButton);
        framePanel.add(ButtonPanel);
    }
    
    private JButton getTradeButton(){
        JButton button = new JButton("Start Trading");
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return button;
    }
   
    public void setObject(Object bean) {
        this.bean = bean;
    }
   
//    private void refreshDialog(){
//        JPanel updatedUserContents = getGoalsPanel();
//        UserGoalsScrollPane.setViewportView(updatedUserContents);
//        UserGoalsScrollPane.revalidate();
//       
//        this.repaint();
//    }
       
    private ArrayList<RhythmSelecterEntry> getGoalsEntries(){
        ArrayList<RhythmSelecterEntry> goalsEntries = new ArrayList<RhythmSelecterEntry>();
        for(int i = 0; i < clusterRuleStrings.size(); i++){
            //System.out.println("ruleString: " + clusterRuleStrings.get(i));
            Polylist ruleString = clusterRuleStrings.get(i);
            Polylist rule = (Polylist) ruleString.first();
            Polylist XNotation = (Polylist) ruleString.second();
            //skip XNotationTag
            XNotation = XNotation.rest();
            //System.out.println("XNotation: " + XNotation);
   
            //System.out.println("rule: " + rule);
            Polylist segLenPL = (Polylist) ((Polylist) rule.second());
   
            int numBars = Integer.parseInt(segLenPL.toStringSansParens().substring(SEG_LENGTH));
            /**@TODO don't use DEFAULT_BEATS_PER_BAR, maybe use something from metre instead
             * 
             */
            //System.out.println("adjusting rhythmNotate line length to: " + (numBars / DEFAULT_BEATS_PER_BAR));
            rhythmNotate.adjustLayout(Polylist.list((long) (numBars / DEFAULT_BEATS_PER_BAR)));
       
            Polylist relativePitchPolylist = getRelativePitchPolylist(XNotation.toStringSansParens());
            
            String realMelody = makeRealMelodyFromRhythmPolylist(relativePitchPolylist);
            BufferedImage rhythmPic = createRhythmImage(realMelody, i);
            Image resizedRhythmPic = rhythmPic.getScaledInstance((int) (rhythmPic.getWidth() / 2.25), (int) (rhythmPic.getHeight() / 2.25), Image.SCALE_SMOOTH);
            ImageIcon rhythmIcon = new ImageIcon(resizedRhythmPic);

            RhythmSelecterEntry rse = new RhythmSelecterEntry(rhythmIcon, false, realMelody, ruleString);
            goalsEntries.add(rse);
        }
       
        return goalsEntries;
    }
   
   
    private JList getGoalsList(){
        //JPanel goalPanel = new JPanel();
        JList goalList = new JList();
        LeadsheetImageListModel goalsModel = new LeadsheetImageListModel(getGoalsEntries());
        goalList.setModel(goalsModel);
        goalList.setCellRenderer(new RhythmListCellRenderer());
        return goalList;
//        goalPanel.setBackground(Color.WHITE);
//        goalPanel.setLayout(new GridBagLayout());
//        checkBoxArray = new ArrayList<JCheckBox>();
//        //Set up constraints for rhythmTextPanel layout
//        GridBagConstraints scrollConstraints = new GridBagConstraints();
//        scrollConstraints.anchor = GridBagConstraints.NORTH;
//        scrollConstraints.weighty = 1;
//        scrollConstraints.gridx = 0;
//        scrollConstraints.fill = GridBagConstraints.HORIZONTAL;
//        scrollConstraints.gridy = 0;
//       
//        //Fill goals panel
//        for(int i = 0; i < clusterRuleStrings.size(); i++){
//            System.out.println("ruleString: " + clusterRuleStrings.get(i));
//            Polylist ruleString = clusterRuleStrings.get(i);
//            Polylist XNotation = (Polylist) ruleString.third();
//            //skip XNotationTag
//            XNotation = XNotation.rest();
//            
//            Polylist segLenPL = (Polylist) ((Polylist) ruleString.second()).second();
//            int numBars = Integer.parseInt(segLenPL.toStringSansParens().substring(SEG_LENGTH));
//            /**@TODO don't use DEFAULT_BEATS_PER_BAR, maybe use something from metre instead
//             * 
//             */
//            System.out.println("adjusting rhythmNotate line length to: " + (numBars / DEFAULT_BEATS_PER_BAR));
//            rhythmNotate.adjustLayout(Polylist.list((long) (numBars / DEFAULT_BEATS_PER_BAR)));
//            
//            Polylist relativePitchPolylist = getRelativePitchPolylist(XNotation.toStringSansParens());
//            
//            String realMelody = makeRealMelodyFromRhythmPolylist(relativePitchPolylist);
//            BufferedImage rhythmPic = createRhythmImage(realMelody, i);
//            Image resizedRhythmPic = rhythmPic.getScaledInstance((int) (rhythmPic.getWidth() / 2.5), (int) (rhythmPic.getHeight() / 2.5), Image.SCALE_SMOOTH);
//            ImageIcon rhythmIcon = new ImageIcon(resizedRhythmPic);
//
//            RhythmSelecterEntry rse = new RhythmSelecterEntry(rhythmIcon, false, realMelody, ruleString);
//            //myRhythms.add(rse);
////            JTextField rhythmTextRepresentation = new JTextField();
////            rhythmTextRepresentation.setText(getVisualizationFromRuleStringPolylist(clusterRuleStrings.get(i)));
////            rhythmTextRepresentation.setEditable(false);
////            addMouseListenerToRhythmTextField(rhythmTextRepresentation);
////            //System.out.println("current visualization: "+ makeRealMelodyFromRhythmPolylist(userRhythms.get(i)));
//            scrollConstraints.gridx = 0;
////            JCheckBox temp = new JCheckBox();
//            goalPanel.add(temp, scrollConstraints);
////            checkBoxArray.add(temp);
////            scrollConstraints.gridx = 1;
////            goalPanel.add(rhythmTextRepresentation, scrollConstraints);
////            scrollConstraints.gridy++;
//        }
//       
//        return goalPanel;
    }
   
    private String getVisualizationFromRuleStringPolylist(Polylist rulePL){   
        Polylist rhythmPL = getRhythmPolylistFromRuleStringPL(rulePL);
        
        
        String visualization = makeRealMelodyFromRhythmPolylist(rhythmPL);
       
       
        return visualization;    
    }
    
    private String makeRealMelodyFromRhythmPolylist(Polylist rhythmPolylist){
        String rtn = "";
        //get rid of tag
        if(!rhythmPolylist.isEmpty()){
            rhythmPolylist = rhythmPolylist.rest();
        }
        
        while (!rhythmPolylist.isEmpty()){
            Polylist currentNote = (Polylist) rhythmPolylist.first();
            if (currentNote.first().equals("X")){
                rtn += "c";
            }else{
                rtn += "r";
            }
            rtn += currentNote.second() + " ";
            rhythmPolylist = rhythmPolylist.rest();
        }
        return rtn;
    }
    
    private Polylist getRhythmPolylistFromRuleStringPL(Polylist rulePL){
        Polylist Xnotation = (Polylist) rulePL.second();
        //remove Xnotation tag
        Xnotation = Xnotation.rest();
        String relativePitchString = Xnotation.toString();
        //System.out.println("relativePitchSrt is: " + relativePitchString);
  
        Polylist rhythmPL = getRelativePitchPolylist(relativePitchString);
        return rhythmPL;
    }
    
    public Polylist getRelativePitchPolylist(String relativePitchMelody){
        String[] part = relativePitchMelody.split("\\(|\\)");
            
        Polylist relativePL = Polylist.list("rhythm");
        for (String s: part){
            s = s.trim();//remove trailing and leading whitespace
            if(s.length() == 0){continue;}//skip empty strings
            if(s.charAt(0) == 'R'){   
                Polylist note = DataPoint.getNotePolylistWLeadingR(s);
                        
                relativePL = relativePL.addToEnd(note);
            }else if(s.charAt(0) == 'X'){
                Polylist note = DataPoint.getNotePolylistWLeadingX(s);
                relativePL = relativePL.addToEnd(note);
            }
        }
        
        return relativePL;
    }
    
    private ArrayList<Polylist> getClusterRuleStrings(ArrayList<RhythmCluster> rhythmClusters) {
        ArrayList<Polylist> ruleStrings = new ArrayList<Polylist>();
        for(int i = 0; i < rhythmClusters.size(); i++){
            RhythmCluster rc = rhythmClusters.get(i);
            ruleStrings.addAll(rc.getRhythmList());
        }
        return ruleStrings;
    }
    
    
    private void addMouseListenerToRhythmTextField(JTextField rhythmTextRepresentation){
        rhythmTextRepresentation.addMouseListener(new MouseListener(){
              public void mouseClicked(MouseEvent e){
                  displayRhythmOnLeadsheet(rhythmTextRepresentation.getText());
              }  

                @Override
                public void mousePressed(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            }
            );
    }
    
    private void displayRhythmOnLeadsheet(String userRhythm){
        String[] userRhythmNoteStrings = userRhythm.split(" ");
        Polylist noteSymbolPolylist = new Polylist();
        Polylist pitchNoteSymbolPolylist = new Polylist();
        for (int i = 0; i < userRhythmNoteStrings.length; i++){
            NoteSymbol noteSymbol = NoteSymbol.makeNoteSymbol(userRhythmNoteStrings[i]);
            noteSymbolPolylist = noteSymbolPolylist.addToEnd(noteSymbol);
            pitchNoteSymbolPolylist = pitchNoteSymbolPolylist.addToEnd(NoteSymbol.makeNoteSymbol("c4"));
        }
        
        Polylist notePolylistToWrite = NoteSymbol.newPitchesForNotes(noteSymbolPolylist, pitchNoteSymbolPolylist);
        System.out.println(notePolylistToWrite);
        
        MelodyPart melodyPartToWrite = new MelodyPart(notePolylistToWrite.toStringSansParens());
        //System.out.println("melody part from user is: "+ melodyPartToWrite.toString());
        
        
        AdviceForMelody advice = new AdviceForMelody("RhythmPreview", notePolylistToWrite, "c", Key.getKey("c"),
                        rhythmHelperTRM.getMetre(), 0);//make a new advice for melody object 
        
        advice.setNewPart(melodyPartToWrite);//new part of advice object is the part that gets pasted to the leadsheet

        advice.insertInPart(notate.getScore().getPart(0), 0, new CommandManager(), notate);//insert melodyPartToWrite into the notate score
        notate.repaint();//refresh the notate page
        notate.playCurrentSelection(false, 0, false, "Printing Rhythm");//play the leadsheet
        
    }
    
 
    public BufferedImage createRhythmImage(String userRhythm, int idNum){
//        System.out.println("\n\nuserRhythm is: " + userRhythm);
        String[] userRhythmNoteStrings = userRhythm.split(" ");
        Polylist noteSymbolPolylist = new Polylist();
        Polylist pitchNoteSymbolPolylist = new Polylist();
        for (int i = 0; i < userRhythmNoteStrings.length; i++){
            NoteSymbol noteSymbol = NoteSymbol.makeNoteSymbol(userRhythmNoteStrings[i]);
            noteSymbolPolylist = noteSymbolPolylist.addToEnd(noteSymbol);
            pitchNoteSymbolPolylist = pitchNoteSymbolPolylist.addToEnd(NoteSymbol.makeNoteSymbol("c4"));
        }
        
        Polylist notePolylistToWrite = NoteSymbol.newPitchesForNotes(noteSymbolPolylist, pitchNoteSymbolPolylist);
        System.out.println(notePolylistToWrite);
        
        MelodyPart melodyPartToWrite = new MelodyPart(notePolylistToWrite.toStringSansParens());
        //System.out.println("melody part from user is: "+ melodyPartToWrite.toString());
        
        
        AdviceForMelody advice = new AdviceForMelody("RhythmPreview", notePolylistToWrite, "c", Key.getKey("c"),
                        rhythmHelperTRM.getMetre(), 0);//make a new advice for melody object 
        
        advice.setNewPart(melodyPartToWrite);//new part of advice object is the part that gets pasted to the leadsheet
        
        advice.insertInPart(rhythmNotate.getScore().getPart(0), 0, new CommandManager(), rhythmNotate);//insert melodyPartToWrite into the notate score
        rhythmNotate.getCurrentStave().setSelection(rhythmNotate.getCurrentStave().getMelodyPart().size() - 1);
        rhythmNotate.repaint();//refresh the notate page
      

        BufferedImage notateScreenshot = new BufferedImage(rhythmNotate.getSize().width, rhythmNotate.getSize().height-100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = notateScreenshot.createGraphics();

        rhythmNotate.paint(g);
        rhythmNotate.paint(g);
        g.dispose();

        
        g.dispose();
//        System.out.println("width: " + notateScreenshot.getWidth());
//        System.out.println("height: " + notateScreenshot.getHeight());
        int notateScreenshotWidth = notateScreenshot.getWidth();
        int notateScreenshotHeight = notateScreenshot.getHeight();
        int croppedXStart = (int) (0.069 * notateScreenshotWidth);//(75 / notateScreenshotWidth);
        int croppedYStart = (int) (0.32 * notateScreenshotHeight);// * (290 / notateScreenshotWidth);
        int croppedWidth = (int) (0.855 * notateScreenshotWidth);
        int croppedHeight = (int) (0.08 * notateScreenshotHeight);
        //BufferedImage dest = bi;
        //BufferedImage dest = notateScreenshot.getSubimage(78, 295, 963, 70);
        BufferedImage dest = notateScreenshot.getSubimage(croppedXStart, croppedYStart, croppedWidth, croppedHeight);
        
        return dest;
    }
    
    
    public Notate getRhythmNotate(){
        Score newScore = new Score("");
        //newScore.setLayoutList(Polylist.list("2"));
        
        int chordFontSize = Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE));

        newScore.setChordFontSize(chordFontSize);

        newScore.setTempo(1);

        ChordPart chords = new ChordPart();

        chords.addChord(new Chord("NC")); // Some chord is necessary to preven screw-ups

        newScore.setChordProg(chords);


        /**@TODO don't hardcode measure length to 480*/
        newScore.addPart(new MelodyPart(DEFAULT_BARS_PER_PART * 480));

        newScore.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));

        Transposition transposition =
                new Transposition(notate.getIntFromSpinner(notate.getLeadsheetChordTranspositionSpinner()),
                                  notate.getIntFromSpinner(notate.getLeadsheetBassTranspositionSpinner()),
                                  notate.getIntFromSpinner(notate.getChorusMelodyTranspositionSpinner()));
        newScore.setTransposition(transposition);

        // open a new window

        rhythmNotate = //new Notate(newScore, -1, -1);
               new Notate(newScore, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        System.out.println("layout: " + rhythmNotate.getScore().getLayoutList());
        //rhythmNotate.setLayoutPreference(Polylist.list("2"));
//
//
//            Notate invisibleNotate =
//                new Notate(newScore);

        /**@TODO decide if this makes it invisible*/
        //newNotate.makeVisible(this);

        rhythmNotate.setTransposition(transposition);


        rhythmNotate.makeVisible();
        
        return rhythmNotate;
    }
    
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents




    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
