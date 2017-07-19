/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.trading;

import imp.ImproVisor;
import imp.com.CommandManager;
import imp.data.Key;
import imp.data.MelodyPart;
import imp.data.NoteSymbol;
import imp.data.RhythmCluster;
import imp.data.advice.Advice;
import imp.data.advice.AdviceForMelody;
import imp.data.advice.AdviceForRhythm;
import imp.generalCluster.Cluster;
import imp.generalCluster.CreateGrammar;
import imp.generalCluster.DataPoint;
import imp.gui.Notate;
import imp.trading.tradingResponseModes.RhythmHelperTRM;
import imp.trading.tradingResponseModes.TradingResponseMode;
import imp.util.NonExistentParameterException;
import imp.util.Preferences;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import polya.Polylist;

/**
 *
 * @author cssummer17
 */
public class UserRhythmSelecterDialog extends javax.swing.JDialog implements java.beans.Customizer {
    
    private Object bean;
    /*
     The list of rhythm clusters from the last trading session
    */
    private ArrayList<RhythmCluster> rhythmClusters;
    /*
     The list of datapoints for all of the rhythms played by the user in the last session
    */
    private ArrayList<DataPoint> userData;
    public static final java.awt.Point INITIAL_OPEN_POINT = new java.awt.Point(25, 0);
    /*
     An array of checkbox's from the add panel. Syncronized with userData to keep track of which rhythms to add and which rhythms are left to add 
    */
    private ArrayList<JCheckBox> checkBoxArray;
    
    private TradingResponseInfo tradingResponseInfo;
    private Notate notate;
    
    private JButton addButton;
    private JButton deleteButton;
    private ArrayList<DataPoint> selectedRhythms;
    /*
     An array of checkbox's from the editor panel. Syncronized with userRuleStringsToWrite to keep track of which rhythms will be deleted or not
    */
    private ArrayList<JCheckBox> editorCheckBoxArray;
    //private ArrayList<Polylist> userRhythms;
    private static boolean addedRhythm = false;
    ArrayList<Polylist> userRhythms;
    ArrayList<Polylist> userRuleStrings;
    JScrollPane editorScrollPane;
    JScrollPane UserRhythmScrollPane;
    private JPanel rhythmTextPanel;
    private JPanel editorPanel;
    private ArrayList<Polylist> userRuleStringsToWrite;
    String filePath;
    private ArrayList<Polylist> rhythmsToDelete;
    private int windowSize;
    private Double[] maxMetricValues;
    private Double[] minMetricValues;
    private int totalNumDataPointsSaved;
    private ArrayList<DataPoint> dataPointsAdded;
    private RhythmHelperTRM rhythmHelperTRM;
    
    

    /**
     * Creates new customizer UserRhythmSelecterDialog
     */
    public UserRhythmSelecterDialog(TradingResponseMode trm) {
        if( !(trm instanceof RhythmHelperTRM) ){
            System.out.println("As of now, UserRhythmSelecter Dialog only works with TRM's of type RhythmHelper! "
                    + "Not going to show UserRhythmSelectorDialog.");
            dispose();
        }
            
        rhythmHelperTRM = (RhythmHelperTRM) trm;
        
        //this.tradingResponseInfo = tradingResponseInfo;
        
        this.windowSize = rhythmHelperTRM.getWindowSizeOfCluster();
        this.rhythmClusters = rhythmHelperTRM.getRhythmClusters();
        this.notate  = rhythmHelperTRM.getNotate();
        
        this.selectedRhythms = new ArrayList<DataPoint>();
        
        userData = getUsersData(rhythmClusters);
        dataPointsAdded = new ArrayList<DataPoint>();
        
        filePath = retrieveUserRhythmsFileName();
        userRuleStringsToWrite = readInRuleStringsFromFile(filePath);
        
        userRuleStrings = extractRuleStrings(userData);
        userRhythms = extractUserRhythms(userData);
        
        rhythmsToDelete = new ArrayList<Polylist>();
        
        maxMetricValues = rhythmHelperTRM.getMaxMetricVals();
        minMetricValues = rhythmHelperTRM.getMinMetricVals();
        totalNumDataPointsSaved = rhythmHelperTRM.getTotalNumSavedDataPoints();
        
        System.out.println("in constructor, userRuleStringsToWrite: " + userRuleStringsToWrite.toString());
        
        checkBoxArray = new ArrayList<JCheckBox>();
        editorCheckBoxArray = new ArrayList<JCheckBox>();
        initComponents();
     
        createDialog(userRhythms);
        
//        addButton.addActionListener(new ActionListener(){
//          public void actionPerformed(ActionEvent e){
//              addSelectedRuleStrings();
//              try {
//                  rewriteRhythmClustersToFile();
//              } catch (IOException ex) {
//                  Logger.getLogger(UserRhythmSelecterDialog.class.getName()).log(Level.SEVERE, null, ex);
//              }
//              refreshDialog();
//          }  
//        
//        } );
        
//        deleteButton.addActionListener(new ActionListener(){
//          public void actionPerformed(ActionEvent e){
//              ArrayList<Polylist> deletedRhythms = removeRhythms();
//              rhythmsToDelete.addAll(deletedRhythms);
//              System.out.println("\n\nrefreshing editor pane...\n\n");
//              refreshEditorPane();
//                
//              
//          }  
//
//
//        
//        } );
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
               @Override
               public void windowClosing(java.awt.event.WindowEvent windowEvent){
                   saveChanges();
               }
        });
        
    }
    
    /*
     Get the file name from preferences and prepend the file path to it.
    */
    public String retrieveUserRhythmsFileName(){
        String fileName = getUserRhythmFileNameFromPreferences();
        fileName = ImproVisor.getRhythmClusterDirectory() + "/" + fileName;
        return fileName;
    }
    
    /**
     * Get the file name for the user rhythms stored in the preferences file
     * @return 
     */
    public String getUserRhythmFileNameFromPreferences(){
        String rtn = "";
        try{
            rtn = Preferences.getPreferenceQuietly(Preferences.MY_RHYTHMS_FILE);
        }catch(NonExistentParameterException e){
            System.out.println("No user rhythm file name found in preferences!");
        }
        
        
        return rtn;
    }
    
    /**Takes Rule Strings represented as Polylist and writes them to the 
     * file specified in filePath. Used to write the rhythms to the user rhythm file.
     * 
     * @param filePath - path to the My.rhythms file
     */
    private void writeMyRuleStringPLsToFile(String filePath){
         Writer writer;
             
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            for(int i = 0; i < userRuleStringsToWrite.size(); i++){
                Polylist ruleStringPL = userRuleStringsToWrite.get(i);
                writer.write(ruleStringPL.toString() + "\n");
            }
            
            
            
            writer.close();
        } catch (IOException ex) {
            System.out.println("Could not write to file: " + filePath);
            Logger.getLogger(UserRhythmSelecterDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    /**Takes RuleStrings from the file specified and return them in an ArrayList
     * represented as Polylists
     * 
     * @param fileName
     * @return ArrayList of 
     */
     public static ArrayList<Polylist> readInRuleStringsFromFile(String fileName){
        BufferedReader reader;
       
        try {
            reader = new BufferedReader(new FileReader(fileName));
            ArrayList<Polylist> ruleStringPL=fillUserRuleStringsToWrite(reader);
            reader.close();
            return ruleStringPL;
               
        } catch (IOException ex) {
            //Logger.getLogger(UserRhythmSelecterDialog.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("***Could not load user rhythms from file: " + fileName);
            System.out.println(ex);
            return new ArrayList<Polylist>();
        }
    }
     
     /**Does the bulk of the work for readInRuleStringsFromFile, makes an ArrayList
      * of polylists of the ruleStrings in the BufferedReader passed in
      * 
      * @param reader - buffered reader for file with rulestrings in it
      * @return ArrayList of ruleStrings represented as Polylists
      * @throws IOException - if theres a problem with the bufferedReader
      */
     public static ArrayList<Polylist> fillUserRuleStringsToWrite(BufferedReader reader) throws IOException{
         ArrayList<Polylist> ruleStringsFromFile = new ArrayList<Polylist>();
         String line = reader.readLine();
         while( line != null){
             Polylist ruleStringPL = (Polylist) Polylist.PolylistFromString(line).first();
             ruleStringsFromFile.add(ruleStringPL);
             line = reader.readLine();
         }
         return ruleStringsFromFile;
     }
    
    
    
        
    
    /**Removes the rhythms in the array from the rhythm clusters they were placed into,
     * and writes the edited rhythmCluster to the file.
     * 
     * @param rhythmsToExclude - rhythms to be deleted from their respective rhythmClusters
     * @throws IOException - if there's a problem writing to the rhythm Clusters file
     */
    private void deleteSelectedRhythmsFromClusters(ArrayList<Polylist> rhythmsToExclude) throws IOException {
        Cluster[] clusterArray = ClusterArrayListToClusterArray(rhythmClusters);
        CreateGrammar.selectiveClusterToFile(clusterArray, rhythmHelperTRM.getClusterFileName(), rhythmsToExclude, windowSize,
               maxMetricValues, minMetricValues);

    }
    
    /**Plays and displays the rhythm specified in the argument on the leadsheet, using middle
     * c for the pitch value
     * 
     * @param userRhythm - rhythm to display
     */
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
                        tradingResponseInfo.getMetre(), 0);//make a new advice for melody object 
        
        advice.setNewPart(melodyPartToWrite);//new part of advice object is the part that gets pasted to the leadsheet

        advice.insertInPart(notate.getScore().getPart(0), 0, new CommandManager(), notate);//insert melodyPartToWrite into the notate score
        notate.repaint();//refresh the notate page
        notate.playCurrentSelection(false, 0, false, "Printing Rhythm");//play the leadsheet
        
    }
    
    
    /**
     * Updates the entire dialog. Updates both add rhythms panel (userRhythmScrollPane) and edit rhythms panel.
     * Refills these panels with new data after an add or a delete.
     */
    private void refreshDialog(){
        JPanel updatedUserContents = getRhythmPanel();
        UserRhythmScrollPane.setViewportView(updatedUserContents);
        UserRhythmScrollPane.revalidate();
        
        JPanel updatedEditorContents = getEditorPanel();
        editorScrollPane.setViewportView(updatedEditorContents);
        editorScrollPane.revalidate();
        
        this.repaint();
    }

    /**
     * Just updates the edit rhythms panel.
     * Refills this panel with updated data after a rhythm has been removed.
     */
    private void refreshEditorPane(){
        //UserRhythmScrollPane.removeAll();
        
        JPanel updatedEditorContents = getEditorPanel();
        editorScrollPane.setViewportView(updatedEditorContents);
        editorScrollPane.revalidate();
        
        this.repaint();
    }
    
    
    /**
     * Removes rhythms from userRuleStringsToWrite and 
     * updates the list of rhythms to delete from the rhythm clusters (deletedRhythms).
     * 
     * 
     * @return updated list of rhythms to delete from the rhythm clusters deletedRhythms
     */
    public ArrayList<Polylist> removeRhythms(){
        ArrayList<Polylist> remainingRuleStringPLs = new ArrayList<Polylist>();
        ArrayList<Polylist> deletedRhythms = new ArrayList<Polylist>();
        
        for(int i = 0; i < editorCheckBoxArray.size(); i++){
            Polylist ruleStringPL = userRuleStringsToWrite.get(i);
            if(editorCheckBoxArray.get(i).isSelected()){
                deletedRhythms.add(getRhythmPolylistFromRuleStringPL(ruleStringPL));
            }else{
                remainingRuleStringPLs.add(ruleStringPL);
            }
        }
        
        userRuleStringsToWrite = remainingRuleStringPLs;       
        return deletedRhythms;
    }
    
    
    
    /**
     * Adds all rhythms selected by a user in add panel to a rhythmCluster and 
     * the list of user rules strings to be stored in the user rhythms file 
     * (userRuleStringsToWrite).
     * 
     * Finds the best fit cluster for each selected user rhythm and adds to that 
     * cluster.
     */
    private void addSelectedRuleStrings(){      
        ArrayList<DataPoint> updatedUserData = new ArrayList<DataPoint>();
        ArrayList<Polylist> updatedUserRhythms = new ArrayList<Polylist>();
             
        for(int i = 0; i < checkBoxArray.size(); i++){
            if(checkBoxArray.get(i).isSelected()){
                DataPoint selectedDP = userData.get(i);
                
                dataPointsAdded.add(selectedDP);
                
                RhythmCluster rc = rhythmHelperTRM.findNearestCluster(rhythmClusters, selectedDP);
                Polylist ruleStringPL = extractRuleStringPolylistFromDatapoint(selectedDP);
                
                
                rc.addSelectedRuleString(ruleStringPL);//add datapoint to rhythm cluster
                userRuleStringsToWrite.add(ruleStringPL);
                
            }else{//everything that is not selected should be displayed after selected rhythms have been added
                
                updatedUserData.add(userData.get(i));
                updatedUserRhythms.add(userRhythms.get(i));
            }
        }
        
        userData = updatedUserData;
        userRhythms = updatedUserRhythms;
    }
    
    
    
   
    /**
     * Used to convert an arrayList of clusters to an array of clusters.
     * 
     * Necessary because clusterToFile takes an array of clusters.
     * 
     * @param rcArrayList The arrayList of clusters.
     * 
     * @return the array version of the arrayList. 
     */
    private Cluster[] ClusterArrayListToClusterArray(ArrayList<RhythmCluster> rcArrayList){
        Cluster[] clusterArray = new Cluster[rcArrayList.size()];
        for(int i = 0; i < rcArrayList.size(); i++){
            clusterArray[i] = rcArrayList.get(i);
        }
        return clusterArray;
    }
    
    /**
     * Rewrites the rhythm clusters with user rhythms added by user and without
     * user rhythms removed by user.
     * 
     * @throws IOException if fileName argument to clusterToFile incorrect
     */
    private void rewriteRhythmClustersToFile() throws IOException{
        Cluster[] clusterArray = ClusterArrayListToClusterArray(rhythmClusters);
        CreateGrammar.clusterToFile(clusterArray, tradingResponseInfo.getClusterFileName(),windowSize);
    }
    
    
    /**
     * creates the scrollpane from which the user can delete their own rhythms.
     * This scrollpane will be filled with the editorPanel which contains
     * all available user rhythms.
     * 
     * @return the scrollpane object with the editorPanel as the content
     */
    private JScrollPane createEditorScrollPane(){
        //Stick rhythmTextPanel into scroll pane and return completed scroll pane
        return (new JScrollPane(getEditorPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));             
    }
    
    /**
     * Creates the editorPanel which contains all of the user rhythms a user ever added to
     * the current user rhythms file. The user rhythms are extracted from the ruleStrings.
     * 
     * converted from ruleString to this format:
     * [noteType][duration]+[noteType][duration]+....
     * 
     * Becomes content of editor scrollpane.
     * 
     * @return a JPanel object with all of the rhythms a user has ever added to
     * the current user rhythms file
     */
    private JPanel getEditorPanel(){
        editorPanel = new JPanel();
        editorPanel.setLayout(new GridBagLayout());
        editorCheckBoxArray = new ArrayList<JCheckBox>();
        //Set up constraints for rhythmTextPanel layout
        GridBagConstraints scrollConstraints = new GridBagConstraints();
        scrollConstraints.anchor = GridBagConstraints.NORTH;
        scrollConstraints.weighty = 1;
        scrollConstraints.gridx = 0;
        scrollConstraints.fill = GridBagConstraints.HORIZONTAL;
        scrollConstraints.gridy = 0;
        
        //Fill rhythmTextPanel with rhythms
        

        for (int i = 0; i < userRuleStringsToWrite.size();i++){
            Polylist rhythm = userRuleStringsToWrite.get(i);
            
            //System.out.println("rhythm is: " + rhythm);
            
            String visualization = getVisualizationFromRuleStringPolylist(rhythm);
            
            JTextField rhythmTextRepresentation = new JTextField();
            rhythmTextRepresentation.setText(visualization);
            rhythmTextRepresentation.setEditable(false);
            addMouseListenerToRhythmTextField(rhythmTextRepresentation);
            
            //System.out.println("current visualization: "+ visualization);
            
            
            scrollConstraints.gridx = 0;
//            JCheckBox temp = new JCheckBox();
//            editorPanel.add(temp, scrollConstraints);
//            editorCheckBoxArray.add(temp);
            addXButton(editorPanel, scrollConstraints, i);
            
            scrollConstraints.gridx = 1;
            editorPanel.add(rhythmTextRepresentation, scrollConstraints);
            scrollConstraints.gridy++;
        }
        return editorPanel;
    }
    
    public void addXButton(JPanel editorPanel, GridBagConstraints scrollConstraints, int i){
        JButton xButton = new JButton("");
        ImageIcon redX = new ImageIcon(getClass().getResource("/imp/gui/graphics/redX.png"));
        xButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
              Polylist ruleStringPL = userRuleStringsToWrite.get(i);
              //ArrayList<Polylist> deletedRhythms = removeRhythms();
              rhythmsToDelete.add(getRhythmPolylistFromRuleStringPL(ruleStringPL));
              userRuleStringsToWrite.remove(i);
              //System.out.println("\n\nrefreshing editor pane...\n\n");
              refreshEditorPane();
                
              
          }  
        } );
        
        editorPanel.add(xButton, scrollConstraints);
        int horizontalOffset = xButton.getInsets().right+32;
        int verticalOffset = 15;
        xButton.setIcon(resizeIcon(redX, xButton.getPreferredSize().width-horizontalOffset, xButton.getPreferredSize().height-verticalOffset));
    }
    
    /**
     * Writes the rule strings (aka rhythms) that a user wants to keep to the
     * user rhythms file specified in My.prefs. Also rewrites the cluster files
     * so that the rhythms a user wishes to delete are not written.
     */
    private void saveChanges(){
        writeMyRuleStringPLsToFile(filePath);
            try {
                deleteSelectedRhythmsFromClusters(rhythmsToDelete);
            } catch (IOException ex) {
                Logger.getLogger(UserRhythmSelecterDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    /**
     * Creates the entire userRhythmSelecterDialog.
     * 
     * Two scrollpanes (add and edit) are in a splitpane which is itself in a
     * panel. Underneath the splitpane is the save/close button.
     * 
     * @param userRhythms (not really used anymore)
     */
    private void createDialog(ArrayList<Polylist> userRhythms){
        //Create the panel that constains everything in dialog
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.Y_AXIS));
        
        //add label for the add side
        JLabel addLabel = new JLabel("Add Selected Rhythms to Preferences");
        framePanel.add(addLabel);
        
        //Create and add scroll pane with user rhythms to dialog
        UserRhythmScrollPane = createUserRhythmsScrollPane(userRhythms);
        framePanel.add(UserRhythmScrollPane);

        //Create and add add button to dialog
        //addButton = getAddButton();
        //addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        //framePanel.add(addButton);
        
        //Create The User Rhythm Editor panel
        JPanel editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
        
        //create the editor label
        JLabel editorLabel = new JLabel("Remove Selected Rhythms from Preferences");
        editorPanel.add(editorLabel);
        
        //Create the editor scrollView
        editorScrollPane = createEditorScrollPane();
        editorScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        editorPanel.add(editorScrollPane);
        
        //create the delete rhythm button
//        deleteButton = getDeleteButton();
//        deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
//        editorPanel.add(deleteButton);
        
        //make split pane with editor and frame components
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,framePanel, editorPanel);
        splitPane.setDividerLocation(400);
        //add the framePanel to the window
        
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        outerPanel.add(splitPane);
        
        JButton closeButton = new JButton("Save");
        closeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        closeButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
            saveChanges();
            dispose();
          }   
        } );
        outerPanel.add(closeButton);
        
        this.add(outerPanel);
        
    }
    
    /**
     * Creates the add button which is to be put in the add scrollpane.
     * 
     * @return the add button
     */
    private JButton getAddButton(){
        JButton button = new JButton("Add Rhythms");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
    
    /**
     * Creates the delete button which is to be put in the edit scrollpane.
     * 
     * @return the delete button
     */
    private JButton getDeleteButton(){
        JButton button = new JButton("Delete Selected Rhythms");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
    
    
 
   /**
     * creates the scrollpane from which the user can add their own rhythms.
     * This scrollpane will be filled with the rhythm which contains
     * all rhythms played by user in last trading session.
     * 
     * @return the scrollpane object with the rhythmPanel as the content
     */
    private JScrollPane createUserRhythmsScrollPane(ArrayList<Polylist> userRhythms){
        
        //Stick rhythmTextPanel into scroll pane and return completed scroll pane
        return (new JScrollPane(getRhythmPanel(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
       
    }
    
    /**
     * Creates the rhythmPanel which contains all of the rhythms a user played
     * in the last trading session. The user rhythms are extracted from ruleStrings.
     * 
     * added in this format:
     * [noteType][duration]+[noteType][duration]+....
     * 
     * Becomes content of add scrollpane.
     * 
     * @return a JPanel object with all of the rhythms a user played in the last
     * trading session.
     */
    private JPanel getRhythmPanel(){
        JPanel rhythmTextPanel = new JPanel();
        rhythmTextPanel.setLayout(new GridBagLayout());
        //checkBoxArray = new ArrayList<JCheckBox>();
        //Set up constraints for rhythmTextPanel layout
        GridBagConstraints scrollConstraints = new GridBagConstraints();
        scrollConstraints.anchor = GridBagConstraints.NORTH;
        scrollConstraints.weighty = 1;
        scrollConstraints.gridx = 0;
        scrollConstraints.fill = GridBagConstraints.HORIZONTAL;
        scrollConstraints.gridy = 0;
        
        //Fill rhythmTextPanel with rhythms
        for(int i = 0; i < userRhythms.size(); i++){
            JTextField rhythmTextRepresentation = new JTextField();
            rhythmTextRepresentation.setText(makeRealMelodyFromRhythmPolylist(userRhythms.get(i)));
            rhythmTextRepresentation.setEditable(false);
            addMouseListenerToRhythmTextField(rhythmTextRepresentation);
            //System.out.println("current visualization: "+ makeRealMelodyFromRhythmPolylist(userRhythms.get(i)));
            scrollConstraints.gridx = 0;
            rhythmTextPanel.add(rhythmTextRepresentation, scrollConstraints);
            scrollConstraints.gridx = 1;
            addArrowButton(rhythmTextPanel, scrollConstraints, i);
           
            scrollConstraints.gridy++;
        }
        
        return rhythmTextPanel;
    }
    
    
    
    public void addArrowButton(JPanel rhythmTextPanel, GridBagConstraints scrollConstraints, int i){
        JButton arrowButton = new JButton("");
        ImageIcon greenArrow = new ImageIcon(getClass().getResource("/imp/gui/graphics/rightArrow.png"));
        arrowButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
            DataPoint selectedDP = userData.get(i);
                
            dataPointsAdded.add(selectedDP);

            RhythmCluster rc = rhythmHelperTRM.findNearestCluster(rhythmClusters, selectedDP);
            Polylist ruleStringPL = extractRuleStringPolylistFromDatapoint(selectedDP);


            rc.addSelectedRuleString(ruleStringPL);//add datapoint to rhythm cluster
            userRuleStringsToWrite.add(ruleStringPL);

            userData.remove(selectedDP);

            userRhythms.remove(i);
                
            refreshDialog();
          }  
        
        } );
        rhythmTextPanel.add(arrowButton, scrollConstraints);
        //System.out.println("arrowButton right inset: "+arrowButton.getInsets().right);
        int horizontalOffset = arrowButton.getInsets().right+4;
        int verticalOffset = 15;
        arrowButton.setIcon(resizeIcon(greenArrow, arrowButton.getPreferredSize().width-horizontalOffset, arrowButton.getPreferredSize().height-verticalOffset));
    }
    
    private ImageIcon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }
    /**
     * Add mouse listener to a text field in the add rhythms panel. 
     * 
     * This
     * @param rhythmTextRepresentation 
     */
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
    
    private ArrayList<DataPoint> getUsersData(ArrayList<RhythmCluster> rhythmClusters){
        ArrayList<DataPoint> userData = new ArrayList<DataPoint>();
        for(int i = 0; i < rhythmClusters.size(); i++){
            ArrayList<DataPoint> rcData = rhythmClusters.get(i).getUserDataPoints();
            for(int j = 0; j < rcData.size(); j++){
                userData.add(rcData.get(j));
            }
        }
        return userData;
    }
    
    public void setObject(Object bean) {
        this.bean = bean;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
    }// </editor-fold>//GEN-END:initComponents

    private ArrayList<Polylist> extractUserRhythms(ArrayList<DataPoint> userData) {
        ArrayList<Polylist> userRhythms = new ArrayList<Polylist>();
        for(int i = 0; i < userData.size(); i++){
            userRhythms.add(userData.get(i).getRelativePitchPolylist());
        }
        
        return userRhythms;
    }
    
    private Polylist extractRuleStringPolylistFromDatapoint(DataPoint d){
        String ruleStringString = "(userRuleString " + d.getRuleString() + ")";
        Polylist ruleStringPL = Polylist.PolylistFromString(ruleStringString);
        return (Polylist) ruleStringPL.first();
    }

    private ArrayList<Polylist> extractRuleStrings(ArrayList<DataPoint> userData) {
        ArrayList<Polylist> ruleStringsPL = new ArrayList<Polylist>();

        for(int i = 0; i < userData.size(); i++){
            String plString = "(userRuleString " + userData.get(i).getRuleString() + ")";
            Polylist addPL = Polylist.PolylistFromString(plString);
            addPL = (Polylist) addPL.first();
            
            ruleStringsPL.add(addPL);
            
            //test stuff
            Polylist p = ruleStringsPL.get(i);

        }
        
        return ruleStringsPL;
    }
    
    private String getVisualizationFromRuleStringPolylist(Polylist rulePL){
        
        
        Polylist rhythmPL = getRhythmPolylistFromRuleStringPL(rulePL);

        String visualization = makeRealMelodyFromRhythmPolylist(rhythmPL);
        
        return visualization;
        
    }
    
    private Polylist getRhythmPolylistFromRuleStringPL(Polylist rulePL){
//        System.out.println("\nrulePL: " + rulePL);
//        System.out.println("\nrulePL first: " + rulePL.first());
//        System.out.println("rulePL second is: " + rulePL.second());
//        System.out.println("is rulePL second a polylist?? " + (rulePL.second() instanceof Polylist));
//        System.out.println("rulePL third(): " + rulePL.third());
        Polylist Xnotation = (Polylist) rulePL.third();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
