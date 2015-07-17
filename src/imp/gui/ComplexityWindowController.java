/*
 *
 */
package imp.gui;

import java.awt.event.ItemEvent;
import java.io.*;
import java.util.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import polya.*;

/**
 * Manages communication between the user interface and individual complexity windows. Maintains references
 * to all the complexity graphs.
 *
 * @author Julia Botev
 */
public class ComplexityWindowController {

    /** Graph panels controlled by this controller. */
    ComplexityPanel overallComplexityPanel, densityPanel, varietyPanel,
            syncopationPanel, consonancePanel, leapSizePanel, directionChangePanel;
    /** An ArrayList of those graph panels. */
    private ArrayList<ComplexityPanel> complexityPanels;
    /** Denotes the number of attributes that need to be factored into the computation of complexity */
    private int numValidAttrs;
    /** Number of beats per measure, basically the time signature of the piece. */
    private int beatsPerBar;
    /** Total number of beats currently selected over which the complexity curve should apply. */
    private int totalNumBeats;
    /** Granularity of the attributes. */
    private int attrGranularity;
    /** Width of the graphs at any given time. */
    private int totalWidth;
    /** Check box that toggles which graphs are enabled: overall complexity or the specific attributes. */
    public JCheckBox manageSpecific;
    /** Controls what granularity the graphs are displayed with. */
    public JComboBox granBox;
    private boolean updatingGran; //hack to not have the gran box adjustment trigger the action listener


    /** 
     * Complexity Window Controller constructor, the array panels will always be of length seven.
     */
    public ComplexityWindowController(int beats, int gran, ComplexityPanel... panels) {
        if (panels.length != 7) {
            System.out.println("Incorrect number of panels passed to complexity window constructor!");
        }
        else {
            overallComplexityPanel = panels[0];
            densityPanel = panels[1];
            varietyPanel = panels[2];
            syncopationPanel = panels[3];
            consonancePanel = panels[4];
            leapSizePanel = panels[5];
            directionChangePanel = panels[6];

            complexityPanels = new ArrayList<ComplexityPanel>(7);
            for (int i = 0; i < panels.length; i++) {
                complexityPanels.add(i, panels[i]);
            }
            numValidAttrs = 6;
            totalNumBeats = beats;
            attrGranularity = gran;
            totalWidth = overallComplexityPanel.getWidth();

//            updatingGran = false; //not currently updating
        }
    }

////////////////////////////////////////// Initializers ////////////////////////////////////////////////////////

    /**
     * Initializes which panels are enabled in the beginning and adds listeners for manage specific toggling and
     * the granularity drop-down menu.
     * @param time the number of beats per measure
     * @param specific the checkbox to toggle which attributes are being managed: overall or specific
     * @param gran the current granularity
     */
    public void initController(int time, JCheckBox specific, JComboBox gran) {
        beatsPerBar = time;
        overallComplexityPanel.setEnabled(true);
        for (int i = 1; i < complexityPanels.size(); i++) {
            complexityPanels.get(i).upperLimitField.setEnabled(false);
            complexityPanels.get(i).lowerLimitField.setEnabled(false);
            complexityPanels.get(i).noComputeBox.setEnabled(false);
            complexityPanels.get(i).setEnabled(false);
            complexityPanels.get(i).setTime(beatsPerBar);
        }

        // Set the names of each panel for saving
        overallComplexityPanel.setName("overall");
        densityPanel.setName("density");
        varietyPanel.setName("variety");
        syncopationPanel.setName("syncopation");
        consonancePanel.setName("consonance");
        leapSizePanel.setName("leapSize");
        directionChangePanel.setName("directionChange");

        manageSpecific = specific;
        manageSpecific.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (manageSpecific.isSelected()) { //ungray specific attrs!
                    overallComplexityPanel.setEnabled(false);
                    overallComplexityPanel.upperLimitField.setEnabled(false);
                    ((ComplexityPanel) overallComplexityPanel).lowerLimitField.setEnabled(false);
                    for (int i = 1; i < complexityPanels.size(); i++) {
                        complexityPanels.get(i).upperLimitField.setEnabled(true);
                        complexityPanels.get(i).lowerLimitField.setEnabled(true);
                        complexityPanels.get(i).noComputeBox.setEnabled(true);
                        complexityPanels.get(i).setEnabled(true);
                    }
                }
                if (!manageSpecific.isSelected()) { //re-gray specific attrs!
                    overallComplexityPanel.setEnabled(true);
                    overallComplexityPanel.upperLimitField.setEnabled(true);
                    overallComplexityPanel.lowerLimitField.setEnabled(true);
                    for (int i = 1; i < complexityPanels.size(); i++) {
                        complexityPanels.get(i).upperLimitField.setEnabled(false);
                        complexityPanels.get(i).lowerLimitField.setEnabled(false);
                        complexityPanels.get(i).noComputeBox.setEnabled(false);
                        complexityPanels.get(i).setEnabled(false);
                    }
                }
            }
        });
        granBox = gran;
        granBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                //System.out.println("item state changed, updating gran is: " + updatingGran);
                if (!updatingGran) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        int gran = (Integer) granBox.getSelectedItem();
                        updateGran(gran);
                    }
                }
            }

        });
        initBuffers();
    }

    /** 
     * Initializes the off-screen buffers and colors of the graphs.
     */
    public void initBuffers() {
        overallComplexityPanel.initBuffer(new Color(255, 140, 0));
        densityPanel.initBuffer(new Color(154, 205, 0));
        varietyPanel.initBuffer(new Color(102, 205, 170));
        syncopationPanel.initBuffer(new Color(3, 168, 158));
        consonancePanel.initBuffer(new Color(191, 239, 255));
        leapSizePanel.initBuffer(new Color(28, 134, 238));
        directionChangePanel.initBuffer(new Color(131, 111, 255));
    }

////////////////////////////////////////// Getters and Setters ////////////////////////////////////////////////////////

    /**
     * @return the ArrayList of complexity panels belonging to this specific controller
     */
    public ArrayList<ComplexityPanel> getPanels() {
        return complexityPanels;
    }
    /**
     * @return the number of attributes that are set to be computed
     */
    public int getNumValidAttrs() {
        return numValidAttrs;
    }
    public void setNumValidAttrs(int attrs) {
        numValidAttrs = attrs;
    }

    public void setVisible(boolean vis) {
        for (int i = 0; i<7; i++) {
            complexityPanels.get(i).setVisible(vis);
        }
    }

    public int getTotalNumBeats() {
        return totalNumBeats;
    }

////////////////////////////////////////// Granularity and Selection Changes ///////////////////////////////////////////////////

    /** 
     * Takes a new number of beats and redraws the graphs.
     */
    public void updateBeats(int beats) {
        //System.out.println("beats: "+beats);
        if (totalNumBeats == beats) { return; }
        else if(beats > 0) {
            totalNumBeats = beats;
            //System.out.println("gran before update gran: "+attrGranularity);
            updateGranBox();
            //System.out.println("gran after update gran: "+attrGranularity);
            for (int i = 0; i < complexityPanels.size(); i++) {
                complexityPanels.get(i).redrawBeats(totalNumBeats);
            }
            totalWidth = overallComplexityPanel.getWidth();
        }
        else {
            totalNumBeats = 0;
            updateGranBox();
        }
    }
    /** 
     * Takes a new granularity and redraws the graphs.
     */
    public int updateGran(int gran) {
        //System.out.println("in update gran, gran is: "+gran);
        //if (attrGranularity == gran) { return totalWidth; }
        attrGranularity = gran;
        for(int i = 0; i<complexityPanels.size(); i++) {
            complexityPanels.get(i).redrawGran(attrGranularity);
        }
        totalWidth = overallComplexityPanel.getWidth();
        return totalWidth;
    }
    /**
     * Updates the granularity combo box depending on certain characteristics of the leadsheet
     */
    public void updateGranBox() {
        updatingGran = true;

        //System.out.println("before remove, updating gran box is: "+updatingGran);
        //System.out.println("before remove, item count is: "+granBox.getItemCount());
        //System.out.println("item at 0: "+granBox.getItemAt(0));

        //granBox.removeAllItems();
        if (granBox.getItemCount() > 1) {
            int count = granBox.getItemCount();
            for (int i=count-1; i>0; i--) {
                //System.out.println("item at index: "+i+" is: "+granBox.getItemAt(i));
                updatingGran = true;
                granBox.removeItemAt(i);
            }
            granBox.validate();
        }

        //granBox.addItem(new Integer(1));    //item at index 0 will always be 1

        if (totalNumBeats == 0) {
            //System.out.println("totalnumbeats is 0");
            updatingGran = false;
            return;
        }
        
        if (totalNumBeats == 1) {} // only one item in the combo box
        else if(beatsPerBar % 2 == 0 && totalNumBeats % 2 == 0) { //meters of 2 or 4
            granBox.addItem(new Integer(2));
            if (beatsPerBar % 4 == 0 && totalNumBeats % 4 == 0 && totalNumBeats >=4) {
                granBox.addItem(new Integer(4));
            }
        }//meters of 3
        else if (beatsPerBar % 3 == 0 && totalNumBeats % 3 == 0 && totalNumBeats >=3) {
            granBox.addItem(new Integer(3));
            if (beatsPerBar % 6 == 0 && totalNumBeats % 6 == 0 && totalNumBeats >=6) {
                granBox.addItem(new Integer(6));
            }
        }//meters of 5
        else if (beatsPerBar % 5 == 0 && totalNumBeats % 5 == 0 && totalNumBeats >=5) {
            granBox.addItem(new Integer(5));
        }

        if (!granBox.selectWithKeyChar(Integer.toString(attrGranularity).charAt(0))) {
            ////System.out.println("gran was: "+attrGranularity);
            granBox.selectWithKeyChar('1'); //default--set to highest granularity if the previous gran isn't valid anymore
            attrGranularity = 1;
            //update all the panels with this granularity
            updateGran(attrGranularity);
        }
        updatingGran = false;
    }


////////////////////////////////////////// Mouse Handler ////////////////////////////////////////////////////////

    /**
     * Moves all the curves in relation to each other. If the overall curve is changed, the specific curves are
     * adjusted accordingly, and vice versa. The specific curves do not affect each other, but do affect the overall curve.
     * Holding the shift key down moves the lower bounds of a curve.
     */
    public void mouseHandler(MouseEvent evt) {
        int oldY, newY;
        Double toAdd;
        int dif = totalWidth - evt.getX();

        if (totalNumBeats <= 0) { return; } //don't register any mouse clicks

        if (dif <= 0) { //if x is outside the bounds of the graph, translate the point
            evt.translatePoint((dif-1), 0);
        }
        else if (dif > totalWidth) { //x is negative or 0
            evt.translatePoint((0-evt.getX()), 0);
        }

        //If the action originated in the overall complexity panel
        if (((ComplexityPanel) evt.getSource()).equals(overallComplexityPanel)) {
            if (!manageSpecific.isSelected()) {
                ((ComplexityPanel) evt.getSource()).mouseHandler(evt); //move the overall curve

                newY = evt.getY();
                Double attrs = ((Integer) numValidAttrs).doubleValue(); //number of attributes

                //the other curves are adjusted depending on their current position
                for (int i = 0; i < complexityPanels.size(); i++) {
                    if (!complexityPanels.get(i).noComputeBox.isSelected()) {
                        if (((MouseEvent) evt).isShiftDown()) {
                            oldY = complexityPanels.get(i).getBarLower(evt.getX());
                        } else {
                            oldY = complexityPanels.get(i).getBarUpper(evt.getX());
                        }
                        toAdd = ((oldY - newY) * ((attrs - 1) / attrs)); //number to add to new y

                        evt.translatePoint(0, toAdd.intValue());
                        complexityPanels.get(i).mouseHandler(evt);
                        evt.translatePoint(0, -toAdd.intValue()); //reset the mouse event to move the other curves
                    }
                }
            }
        } else { // overall complexity curve is not the source
            if (manageSpecific.isSelected()) {
                if (!((ComplexityPanel) evt.getSource()).noComputeBox.isSelected()) {

                    if (((MouseEvent) evt).isShiftDown()) {
                        oldY = ((ComplexityPanel) overallComplexityPanel).getBarLower(evt.getX());
                    } else {
                        oldY = ((ComplexityPanel) overallComplexityPanel).getBarUpper(evt.getX());
                    }
                    newY = evt.getY();
                    Double attrs = ((Integer) numValidAttrs).doubleValue();
                    toAdd = ((oldY - newY) * ((attrs - 1) / attrs)); //number to add to new y

                    ((ComplexityPanel) evt.getSource()).mouseHandler(evt);
                    evt.translatePoint(0, toAdd.intValue());
                    ((ComplexityPanel) overallComplexityPanel).mouseHandler(evt);
                }
            }
        }
    }


////////////////////////////////////////// Saving, Loading, and Reseting ///////////////////////////////////////

    /**
     * Resets the curves and all text fields to their default settings.
     */
    public void reset() {
        for (int i = 0; i<complexityPanels.size(); i++) {
            complexityPanels.get(i).instantiateBars(totalNumBeats); //set all bars to flat
            complexityPanels.get(i).setMinLower(175);
            complexityPanels.get(i).setMaxUpper(25);
            complexityPanels.get(i).noComputeBox.setSelected(false);
            complexityPanels.get(i).repaint();
        }
    }

    /** 
     * Creates a file with the specified name and returns it, .soloProfile is the extension
     */
    public File saveComplexityWindow(String pathname) throws FileNotFoundException, IOException {
        File toReturn = new File(pathname);
        FileOutputStream stream = new FileOutputStream(toReturn);
        String windowInfo = convertComplexityWindowToString(); //turn essential info into a string
        stream.write(windowInfo.getBytes()); //write that string to the file
        toReturn.setReadOnly(); //non-modifiable file
        return toReturn;
    }

    /**
     *  Loads the file denoted by pathname to instantiate a saved complexity window.
     */
    public void loadComplexityWindow(String pathname) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathname));
        String info = "";
        info = reader.readLine(); //should be one line
        convertStringToComplexityWindow(info);
    }

    /**
     * Converts essential info about the window into a String for saving.
     *  What needs to be saved: total width, numBars, granularity, individual curve values,
     *  values of each min and max range text field, whether or not a curve is to be computed
     */
    private String convertComplexityWindowToString() {
        Polylist info = new Polylist();
        // structure:
        // List 1: (numValidAttrs beatsPerBar numBeats gran width)
        // List 2: ((minLower maxUpper compute ([list of lower bounds])([list of upper bounds]))), ... <seven times>

        // List 1: global info
        Polylist front = Polylist.list("globalInfo",
                Polylist.list("numValidAttrs", numValidAttrs),
                Polylist.list("beatsPerBar", beatsPerBar),
                Polylist.list("totalNumBeats", totalNumBeats),
                Polylist.list("granularity", attrGranularity),
                Polylist.list("width", totalWidth));

        // List 2: combined attribute graph info
        Polylist back = new Polylist();
        Polylist inner = new Polylist(); // individual attribute graph info

        for (int i = complexityPanels.size()-1; i>=0; i--) {
           inner = Polylist.list(complexityPanels.get(i).getName(),
                   Polylist.list("minLower", complexityPanels.get(i).getMinLower()),
                   Polylist.list("maxUpper", complexityPanels.get(i).getMaxUpper()),
                   Polylist.list("compute", complexityPanels.get(i).toCompute()),
                   Polylist.list("lowerBounds", Polylist.PolylistFromArray(complexityPanels.get(i).lowerBounds())),
                   Polylist.list("upperBounds", Polylist.PolylistFromArray(complexityPanels.get(i).upperBounds())));
           back = Polylist.cons(inner, back);
        }
        back = Polylist.cons("specificAttrs", back);
        info = Polylist.cons(front, back);
        String toReturn = info.toString();
        return toReturn;
    }


    /**
     * Takes a complexity Panel S-Expression and turns it into a re-drawn set of complexity panels
     */
    private void convertStringToComplexityWindow(String s) {
        //System.out.println("s: "+ s);
        Polylist info = Polylist.PolylistFromString(s);
        PolylistEnum itr, itr2, itr3, itrGlobal;
        int oldTotalBeats = 0;
        int oldWidth = 0;

        String label = "";
        Object next, next2, nextGlobal;

        //iterate over the first polylist--the global info
        //System.out.println("info.first(): "+ info.first().toString());
        itr = new PolylistEnum((Polylist)info.first()); //grabs the entire list
        next = itr.nextElement(); //first half of the list--globals
        itrGlobal = new PolylistEnum((Polylist)next);
        nextGlobal = itrGlobal.nextElement();
        //System.out.println("first next: "+nextGlobal.toString());
        if (nextGlobal instanceof String && ((String)nextGlobal).equals("globalInfo")) {
            while (itrGlobal.hasMoreElements()) {
                //System.out.println("\n\n******parsing globals******\n\n");
                nextGlobal = itrGlobal.nextElement();
                if (nextGlobal instanceof Polylist) {
                    if (((Polylist)nextGlobal).first() instanceof String) {
                        if (((String)((Polylist)nextGlobal).first()).equals("numValidAttrs")) {
                            numValidAttrs = ((Long)((Polylist)nextGlobal).last()).intValue();
                            //System.out.println("num valid attrs: "+numValidAttrs);
                        }
                        else if(((String) ((Polylist) nextGlobal).first()).equals("beatsPerBar")) {
                            beatsPerBar = ((Long)((Polylist)nextGlobal).last()).intValue();
                            //System.out.println("beats per bar: "+beatsPerBar);
                        }
                        else if(((String) ((Polylist) nextGlobal).first()).equals("totalNumBeats")) {
                            oldTotalBeats = ((Long)((Polylist)nextGlobal).last()).intValue();
                            //System.out.println("oldTotalBeats: "+oldTotalBeats);
                        }
                        else if(((String) ((Polylist) nextGlobal).first()).equals("granularity")) {
                            attrGranularity = ((Long)((Polylist)nextGlobal).last()).intValue();
                            //System.out.println("gran: "+attrGranularity);
                        }
                        else if(((String) ((Polylist) nextGlobal).first()).equals("width")) {
                            oldWidth = ((Long)((Polylist)nextGlobal).last()).intValue();
                            //System.out.println("width: "+oldWidth);
                        }
                        else {
                            //System.out.println("Error: poorly formed soloProfile file");
                        }
                    }
                }
            }
        }
        //itr = new PolylistEnum((Polylist)info.rest()); //second half of the list, contains specific attr info
        next = itr.nextElement();
        if (next instanceof String && ((String)next).equals("specificAttrs")) {
            for (int i = 0; i<complexityPanels.size(); i++) { //traverse each panel
                int[] lowers = new int[1]; //lower and upper bounds for each panel
                int[] uppers = new int[1]; //init to size 1 to appease the compiler
                next = itr.nextElement(); //list of specific panel attributes
                itr2 = new PolylistEnum((Polylist)next); //iterator for internal lists
                next2 = itr2.nextElement(); //name of the panel
                while (itr2.hasMoreElements()) {
                    next2 = itr2.nextElement(); //first variable pair
                    if (next2 instanceof Polylist) {
                        if (((Polylist)next2).first() instanceof String){
                            if (((String)((Polylist)next2).first()).equals("minLower")) {
                                //System.out.println("minlower"+((Long)((Polylist)next2).last()).intValue());
                                complexityPanels.get(i).setMinLower(((Long)((Polylist)next2).last()).intValue());
                            }
                            else if(((String) ((Polylist) next2).first()).equals("maxUpper")) {
                                complexityPanels.get(i).setMaxUpper(((Long)((Polylist)next2).last()).intValue());
                                //System.out.println("maxUpper"+((Long)((Polylist)next2).last()).intValue());
                            }
                            else if(((String) ((Polylist) next2).first()).equals("compute")) {
                                //System.out.println("compute: "+(String)((Polylist)next2).last());
                                // Do not compute box was checked, check it again
                                if(!Boolean.valueOf((String)((Polylist)next2).last())) {
                                    complexityPanels.get(i).noComputeBox.setSelected(true);
                                }
                            }
                            else if(((String) ((Polylist) next2).first()).equals("lowerBounds")) {
                                //TODO: turn list into array, then at end of this method, redraw the graphs
                                itr3 = new PolylistEnum((Polylist)((Polylist)next2).last());
                                lowers = new int[((Polylist)((Polylist)next2).last()).length()];
                                int j = 0;
                                while(itr3.hasMoreElements()) {
                                    lowers[j] = ((Long)itr3.nextElement()).intValue();
                                    j++;
                                }
                            }
                            else if(((String) ((Polylist) next2).first()).equals("upperBounds")) {
                                //TODO: turn list into array, then at end of this method, redraw the graphs
                                itr3 = new PolylistEnum((Polylist)((Polylist)next2).last());
                                uppers = new int[((Polylist)((Polylist)next2).last()).length()];
                                int j = 0;
                                while(itr3.hasMoreElements()) {
                                    uppers[j] = ((Long)itr3.nextElement()).intValue();
                                    j++;
                                }
                            }
                            else {

                            }
                        }
                    }
                }

                complexityPanels.get(i).reInitPanel(beatsPerBar, oldTotalBeats, attrGranularity, oldWidth);
                complexityPanels.get(i).reInitBars(lowers, uppers);
                complexityPanels.get(i).redrawBeats(totalNumBeats);
            }
        }
    }

////////////////////////////////////////// Attribute Computation ////////////////////////////////////////////////////////

    /**
     * @return a list of the names of the panels that are to be computed, excluding overall complexity.
     */
    public ArrayList<String> validNames() {
        ArrayList<String> names = new ArrayList<String>(numValidAttrs);
        for (int i = 1; i < numValidAttrs; i++) {
            if (!complexityPanels.get(i).noComputeBox.isSelected()) {
                names.add(complexityPanels.get(i).getName());
            }
        }
        return names;
    }

    /**
     * Computes exponents for each valid attribute, excluding overall complexity.
     * @return a list of exponents for each attribute
     */
    public ArrayList<ArrayList<Double>> exponents() {
        int k = 30; // a constant for calculating the exponents, 2*GAP

        ArrayList<ArrayList<Double>> exps = new ArrayList<ArrayList<Double>>(numValidAttrs);
        for (int i = 1; i < numValidAttrs; i++) {
            if (!complexityPanels.get(i).noComputeBox.isSelected()) {
                exps.add(complexityPanels.get(i).calcExponents(k));
            }
        }
        return exps;
    }

    /**
     * Computes averages for each valid attribute, excluding overall complexity.
     * Averages are between 0 and 1.
     * @return a list of averages for each attribute
     */
    public ArrayList<ArrayList<Double>> averages() {
        ArrayList<ArrayList<Double>> avgs = new ArrayList<ArrayList<Double>>(numValidAttrs);
        for (int i = 1; i < numValidAttrs; i++) {
            if (!complexityPanels.get(i).noComputeBox.isSelected()) {
                avgs.add(complexityPanels.get(i).calcAverages());
            }
        }
        return avgs;
    }

//    /**
//     * Assembles a list of attribute ranges of each attribute to be calculated.
//     * If an attribute is to be left out, its value is null. Excludes the overall complexity curve.
//     */
//    public ArrayList<ArrayList> getAttributeRanges() {
//        ArrayList<ArrayList> attrs = new ArrayList<ArrayList>(6);
//        for (int i = 1; i<complexityPanels.size(); i++) {
//            if(complexityPanels.get(i).toCompute()) {
//                attrs.add(complexityPanels.get(i).valueRange());
//            }
//            else {
//                attrs.add(null);
//            }
//        }
//        return attrs;
//    }
}
