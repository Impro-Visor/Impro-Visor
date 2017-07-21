package imp.trading;
 
import imp.com.CommandManager;
import imp.data.Key;
import imp.data.MelodyPart;
import imp.data.NoteSymbol;
import imp.data.RhythmCluster;
import imp.data.advice.AdviceForMelody;
import imp.generalCluster.DataPoint;
import imp.gui.Notate;
import static imp.trading.UserRhythmSelecterDialog.readInRuleStringsFromFile;
import imp.trading.tradingResponseModes.CorrectRhythmTRM;
import imp.trading.tradingResponseModes.RhythmHelperTRM;
import imp.trading.tradingResponseModes.TradingResponseMode;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
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
       
       
        //this.windowSize = rhythmHelperTRM.getWindowSizeOfCluster();
        this.rhythmClusters = rhythmHelperTRM.getRhythmClusters();
        this.notate  = rhythmHelperTRM.getNotate();
        
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
                rhythmHelperTRM.setRuleStringsToEmulate(selectedRuleStrings);
                activeTrading.startTradingFromTradingGoalsDialog();
                dispose();
              }
          }  
        
        } );
       
       
       
    }
    
    private ArrayList<Polylist> getSelectedRuleStrings(){
        ArrayList<Polylist> selectedRuleStrings = new ArrayList<Polylist>();
        for(int i = 0; i < checkBoxArray.size(); i++){
            if(checkBoxArray.get(i).isSelected()){
                selectedRuleStrings.add(clusterRuleStrings.get(i));
                System.out.println("clusterRuleStrings["+i+"]"+ clusterRuleStrings.get(i));
            }
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
        JPanel goalsPanel = getGoalsPanel();
        UserGoalsScrollPane.setViewportView(goalsPanel);
        framePanel.add(UserGoalsScrollPane);
        this.add(framePanel);

        //Create and add add button to dialog
        tradeButton = getTradeButton();
        JPanel ButtonPanel = new JPanel();
         
        ButtonPanel.add(tradeButton);
        framePanel.add(ButtonPanel);
//        
//        //Create The User Rhythm Editor panel
//        JPanel editorPanel = new JPanel();
//        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
//        
//        //create the editor label
//        JLabel editorLabel = new JLabel("Remove Selected Rhythms from Preferences");
//        editorPanel.add(editorLabel);
//        
//        //Create the editor scrollView
//        editorScrollPane = createEditorScrollPane();
//        editorScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
//        editorPanel.add(editorScrollPane);
//        
//        //create the delete rhythm button
////        deleteButton = getDeleteButton();
////        deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
////        editorPanel.add(deleteButton);
//        
//        //make split pane with editor and frame components
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,framePanel, editorPanel);
//        splitPane.setDividerLocation(400);
//        //add the framePanel to the window
//        
//        JPanel outerPanel = new JPanel();
//        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
//        splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
//        outerPanel.add(splitPane);
//        
//        JButton closeButton = new JButton("Save");
//        closeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        closeButton.addActionListener(new ActionListener(){
//          public void actionPerformed(ActionEvent e){
//            saveChanges();
//            dispose();
//          }   
//        } );
//        outerPanel.add(closeButton);
//        
//        this.add(outerPanel);
        
    }
    
    private JButton getTradeButton(){
        JButton button = new JButton("Start Trading");
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return button;
    }
   
    public void setObject(Object bean) {
        this.bean = bean;
    }
   
    private void refreshDialog(){
        JPanel updatedUserContents = getGoalsPanel();
        UserGoalsScrollPane.setViewportView(updatedUserContents);
        UserGoalsScrollPane.revalidate();
       
        this.repaint();
    }
   
   
    private JPanel getGoalsPanel(){
        JPanel goalPanel = new JPanel();
        goalPanel.setBackground(Color.WHITE);
        goalPanel.setLayout(new GridBagLayout());
        checkBoxArray = new ArrayList<JCheckBox>();
        //Set up constraints for rhythmTextPanel layout
        GridBagConstraints scrollConstraints = new GridBagConstraints();
        scrollConstraints.anchor = GridBagConstraints.NORTH;
        scrollConstraints.weighty = 1;
        scrollConstraints.gridx = 0;
        scrollConstraints.fill = GridBagConstraints.HORIZONTAL;
        scrollConstraints.gridy = 0;
       
        //Fill goals panel
        for(int i = 0; i < clusterRuleStrings.size(); i++){
            JTextField rhythmTextRepresentation = new JTextField();
            rhythmTextRepresentation.setText(getVisualizationFromRuleStringPolylist(clusterRuleStrings.get(i)));
            rhythmTextRepresentation.setEditable(false);
            addMouseListenerToRhythmTextField(rhythmTextRepresentation);
            //System.out.println("current visualization: "+ makeRealMelodyFromRhythmPolylist(userRhythms.get(i)));
            scrollConstraints.gridx = 0;
            JCheckBox temp = new JCheckBox();
            goalPanel.add(temp, scrollConstraints);
            checkBoxArray.add(temp);
            scrollConstraints.gridx = 1;
            goalPanel.add(rhythmTextRepresentation, scrollConstraints);
            scrollConstraints.gridy++;
        }
       
        return goalPanel;
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
