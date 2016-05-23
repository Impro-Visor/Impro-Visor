/*
 * 
 */

package imp.complexity;

import imp.gui.BarDimensions;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import polya.Polylist;

/**
 * Handling code for any sort of user-adjustable complexity attribute panel.
 * @author Julia Botev
 */
public class ComplexityPanel extends JPanel  {

    /** Width of the entire graph, varies depending on the number of beats selected */
    private int width;
    /** Bounds of the chord and bar number boxes */
    private int upperY, lowerY;
    /** User-defined min and max bounds on the curve, realized as a gray box on screen */
    private int maxUpper, minLower;
    /** Max height of any given bar in the graph, lowerY - upperY */
    private int barHeight;
    /** Unit of musical duration that corresponds to each bar, i.e. half bar, full bar, single beat */
    private int granularity;
    /** Total number of beats in the section of music to generate over */
    private int totalNumBeats;
    /** Number of beats per bar in the music, essentially the time signature */
    private int beatsPerBar;
    /** Indicates whether or not this component is enabled or disabled */
    private boolean enabled;
    /** Name for this panel--used for saving */
    private String name;

    /** Buffer for off-screen buffering of the image */
    private Image buffer;
    /** Graphics component for off-screen buffering of the image */
    private Graphics2D graphics;
    /** A list of the starting coordinate and lower and upper bounds of each bar, size is the number of bars */
    private ArrayList<BarDimensions> bars;
    /** The color of this particular graph */
    private Color color;
    public JTextField upperLimitField, lowerLimitField;
    public JCheckBox noComputeBox;
    /** Alpha composite for painting a transparent grayed out image for disabled graphs */
    private AlphaComposite composite;

    //Immutables
    /** Total height of the entire graph window, including the bar number and chord sections */
    public static final int TOTAL_HEIGHT = 200;
    /** Width of each bar in the graph */
    private static final int BAR_WIDTH = 30; //set for every graph
    /** Gap that must be maintained between the lower and upper bounds of a complexity curve */
    private static final int GAP = 15;


    public ComplexityPanel(int time, int gran, int tot) {
        upperY = 25;
        lowerY = TOTAL_HEIGHT-25;
        barHeight = lowerY-upperY;
        beatsPerBar = time;
        totalNumBeats = tot;
        granularity = gran;
        int numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;
        instantiateBars(numBars);

        setSize(width, TOTAL_HEIGHT);
        color = Color.RED;

        float alpha = .75f;
        int type = AlphaComposite.SRC_OVER;
        composite = AlphaComposite.getInstance(type, alpha);
    }

////////////////////////////////////////// Initializing Methods ////////////////////////////////////////////////////////

    /**
     * Sets the upper and lower text fields that denote the upper and lower limits on this complexity graph,
     * and defines their listeners.
     * @param lower the lower limit text field
     * @param upper the upper limit text field
     */
    public void setTextFields(JTextField lower, JTextField upper) {
        lowerLimitField = lower;
        upperLimitField = upper;
        minLower = lowerY;
        maxUpper = upperY;

        lowerLimitField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int min = Integer.parseInt(lowerLimitField.getText());
                if (min < 0) {
                    min = 0;
                    lowerLimitField.setText(Integer.toString(min));
                }
                min = -1*(min-150)+upperY;
                if(min < maxUpper + GAP) {
                    min = maxUpper+GAP;
                    lowerLimitField.setText(Integer.toString(-1*((min-upperY)-150)));
                }
                minLower = min;
                resizeBars();
                drawAll();
            }
        });
        upperLimitField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int max = Integer.parseInt(upperLimitField.getText());
                if (max > 150) {
                    max = 150;
                    upperLimitField.setText(Integer.toString(max));
                }
                max = -1*(max-150)+upperY;
                if (max > minLower-GAP) {
                    max = minLower-GAP;
                    upperLimitField.setText(Integer.toString(-1*((max-upperY)-150)));
                }
                maxUpper = max;
                resizeBars();
                drawAll();
            }
        });
    }
    /**
     * Sets the checkbox that denotes whether or not to compute this attribute.
     * @param box a JCheckBox
     */
    public void setCheckBox(JCheckBox box) {
        noComputeBox = box;
        noComputeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!toCompute()) {
                    upperLimitField.setEnabled(false);
                    lowerLimitField.setEnabled(false);
                }
                else {
                    upperLimitField.setEnabled(true);
                    lowerLimitField.setEnabled(true);
                }
            }
        });
    }
    /**
     * Initializes the off-screen buffer that draws the graph image onto the graph panel.
     * @param c the color of the graph.
     */
    public void initBuffer(Color c) {
        this.buffer = this.createImage(width, TOTAL_HEIGHT);
        this.setGraphics();
        color = c;
    }
    /**
     * Fills the bar arraylist initially with a starting position, and an upper and lower bound, the initial curve is flat.
     * @param numBars the number of bars to initialize the arraylist with
     */
    public void instantiateBars(int numBars) {
        BarDimensions d;
        bars = new ArrayList<BarDimensions>();
        for(int i = 0; i < width; i+=BAR_WIDTH) {
            d = new BarDimensions(i, barHeight/2+upperY, lowerY); //TODO: give each bar a different starting height
            bars.add(d);
        }
    }


////////////////////////////////////////// Getters and Setters ////////////////////////////////////////////////////////

    @Override
    public int getWidth() {
        return width;
    }
    @Override
    public void setName(String n) {
        name = n;
    }
    @Override
    public String getName() {
        return name;
    }
    public int getGran() {
        return granularity;
    }
    public void setGran(int gran) {
        granularity = gran;
    }

    public int getMinLower() {
        return minLower;
    }
    public int getMaxUpper() {
        return maxUpper;
    }
    public void setMinLower(int min) {
        minLower = min;
        lowerLimitField.setText(Integer.toString(-1*((min-upperY)-150)));
    }
    public void setMaxUpper(int max) {
        maxUpper = max;
        upperLimitField.setText(Integer.toString(-1*((max-upperY)-150)));
    }
    public void setGraphics() {
        graphics = (Graphics2D) buffer.getGraphics();
    }
    public void setTime(int time) {
        beatsPerBar = time;
    }
    @Override
    public void setEnabled(boolean e) {
        enabled = e;
    }

    public boolean toCompute() {
        if (noComputeBox.isSelected()) {
            return false;
        }
        return true;
    }


////////////////////////////////////////// Saving and Loading ////////////////////////////////////////////////////////

    public Integer[] lowerBounds() {
        Integer[] toReturn = new Integer[bars.size()];
        for (int i = 0; i<bars.size(); i++) {
            toReturn[i] = bars.get(i).getLowerBound();
        }
        return toReturn;
    }
    public Integer[] upperBounds() {
        Integer[] toReturn = new Integer[bars.size()];
        for (int i = 0; i<bars.size(); i++) {
            toReturn[i] = bars.get(i).getUpperBound();
        }
        return toReturn;
    }
    /**
     * Re-instates the bar array with new dimensions--used to load a profile curve.
     * lowers.length == uppers.length always
     */
    public void reInitBars(int[] lowers, int[] uppers){
        int xCoord = 0; // the barStart variable for each barDimension in the array
        bars = new ArrayList<BarDimensions>(lowers.length);
        for(int i = 0; i<lowers.length; i++) {
            //System.out.println("in reinit bars: xCoord: "+xCoord+" upper: "+uppers[i]+" lower: "+lowers[i]);
            bars.add(new BarDimensions(xCoord, uppers[i], lowers[i]));
            xCoord += BAR_WIDTH;
        }
        //System.out.println("about to draw all in reInitBars");
    }
    /**
     * Re-initializes the panel with new specifications. Used for loading from a file.
     */
    public void reInitPanel(int beatsPer, int numBeats, int gran, int totalWidth) {
        clear();
        beatsPerBar = beatsPer;
        totalNumBeats = numBeats;
        granularity = gran;
        width = totalWidth;
    }

////////////////////////////////////////// Image Drawing ////////////////////////////////////////////////////////

    /**
     * Updates the positions of all the bars on the screen.
     */
    private void drawAll() {
        Composite oldComp = graphics.getComposite(); //save the old composite in case the screen needs to be greyed out
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, TOTAL_HEIGHT);

        graphics.setColor(new Color(194, 194, 194)); // gray out part of the graph for a user-specified hard limit
        graphics.fillRect(0, upperY, width, maxUpper - upperY); //Fill the top range limit
        graphics.fillRect(0, minLower, width, lowerY - minLower); //Fill the bottom range limit

        //Fill in the bars of the graph
        for (int i = 0; i < bars.size(); i++) {
            graphics.setColor(color);
            graphics.fillRect((int) bars.get(i).getBarStart(), (int) bars.get(i).getUpperBound(),
                    BAR_WIDTH, (int) bars.get(i).getLowerBound() - (int) bars.get(i).getUpperBound());
            graphics.setColor(Color.black);
            graphics.drawRect((int) bars.get(i).getBarStart(), upperY, BAR_WIDTH, barHeight);
        }
        //draw bar numbers
        drawBarNumbers();
        graphics.drawRect(0, 0, width, upperY); //Bar Number box
        //TODO: draw chords
        graphics.drawRect(0, lowerY, width, TOTAL_HEIGHT); //Chord box
        graphics.drawRect(0, 0, width - 1, TOTAL_HEIGHT - 1); //box to surround the whole graph

        //dark gray for non-computed attributes
        if (!toCompute()) {
            graphics.setComposite(composite);
            graphics.setColor(new Color(190, 190, 190)); 
            graphics.fillRect(0, 0, width, TOTAL_HEIGHT);
            graphics.setComposite(oldComp);
        }
        //lighter gray for disabled attributes
        else if (!enabled) {
            graphics.setComposite(composite);
            graphics.setColor(new Color(230, 230, 230)); 
            graphics.fillRect(0, 0, width, TOTAL_HEIGHT);
            graphics.setComposite(oldComp);
        }
        repaint();
    }
    /**
     * Redraws the number of bars when the user specifies a new number of beats
     * preserves the exact dimensions of the current curve by truncating it, or
     * adding more curve at the same height as the last bar.
     */
    public void redrawBeats(int newBeats) {
        clear();
        int i;
        int oldBeats = totalNumBeats;
        //don't forget to account for granularity
        ArrayList<BarDimensions> newList = new ArrayList<BarDimensions>(newBeats/granularity);

        //fewer beats than before--truncate the curve
        if (newBeats < oldBeats) {
            //System.out.println("in redraw beats, old beats: "+oldBeats+" new beats: "+newBeats+"gran: "+granularity);
            for(i = 0; i<newBeats/granularity; i++) {
                newList.add(i, bars.get(i));
            }
            bars = newList;
        }
        //more beats than before--extend the existing curve
        else if (newBeats > oldBeats) {
            for(i = 0; i<bars.size(); i++) {
                newList.add(i, bars.get(i));
            }
            int xCoord = bars.get(bars.size()-1).getBarStart()+BAR_WIDTH;

            BarDimensions dim;
            for (i = bars.size(); i<newBeats/granularity; i++) {
                dim = new BarDimensions(xCoord, bars.get(bars.size()-1).getUpperBound(),
                        bars.get(bars.size()-1).getLowerBound());
                newList.add(i, dim);
                xCoord += BAR_WIDTH;
            }
            bars = newList;
        }

        totalNumBeats = newBeats;
        int numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;
        this.setSize(width, TOTAL_HEIGHT);
        update(graphics);
        repaint();
    }

    /**
     * Redraws the number of bars when the user specifies a new granularity.
     * Preserves the old complexity curve, but with different spacing
     */
    public void redrawGran(int newGran) {
        clear();
        int i, index;
        int newUpper, newLower;
        int numBars;
        ArrayList<BarDimensions> newList = new ArrayList<BarDimensions>();
        int oldGran = granularity;
        
        //Less fine resolution--average values
        if (newGran > oldGran) {
            int div = newGran/oldGran;
            index = 0;
            for (i = 0; i<bars.size(); i+=div) {
                newUpper = 0;
                newLower = 0;
                for (int j = i; j<i+div; j++) {
                    if (j < bars.size()) {
                        newUpper += bars.get(j).getUpperBound();
                        newLower += bars.get(j).getLowerBound();
                    }
                    //If it is added later that odd-numbered beats can have granularities that don't divide evenly into
                    //them, then uncomment this:
//                    else { //for odd-numbered beats, to avoid an index out of bounds exception
//                        newUpper += bars.get(bars.size()-1).getUpperBound();
//                        newLower += bars.get(bars.size()-1).getLowerBound();
//                    }
                }
                newList.add(new BarDimensions(index, newUpper/div, newLower/div));
                index+=BAR_WIDTH;
            }
        }
        //Finer resolution, simply doubly the values
        else if (newGran < oldGran) {
            int div = oldGran/newGran;

            numBars = totalNumBeats/granularity;
            if (totalNumBeats % newGran > 0) {
                numBars += totalNumBeats % newGran;
            }

            index = 0;
            int k = 0;
            for (i = 0; i<bars.size(); i++) {
                for (int j = k; j<k+div; j++) {
                    //also this:
                    //if (j < numBars) { //don't exceed the number of beats, matters if the beat count is odd
                        newList.add(new BarDimensions(index, bars.get(i).getUpperBound(), bars.get(i).getLowerBound()));
                        index+=BAR_WIDTH;
                    //}
                }
                k += div;
            }
        }
        bars = newList;
        granularity = newGran;
        numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;

        this.setSize(width, TOTAL_HEIGHT);
        update(graphics);
        repaint();
    }
    /**
     * Draws bar numbers above the graph, whose positions vary depending on the granularity selected.
     */
    private void drawBarNumbers() {
        int div = beatsPerBar/granularity;
        int num = 1;
        for (int i = 0; i < bars.size(); i++) {
            if((i)%div == 0) {
                graphics.drawString(((Integer)num).toString(), bars.get(i).getBarStart()+5, upperY-5);
                num++;
            }
        }
    }
    /**
     * Override the paint method to draw the buffer image on this panel's graphics.
     * This method is called implicitly whenever repaint() is called.
     */
    @Override
    public void paint(Graphics g) {
        drawAll();
        g.drawImage(buffer, 0, 0 , null);
    }
    private void clear() {
        graphics.clearRect(0, upperY, width, lowerY);
    }
    @Override
    public void update(Graphics g) {
        paint(g);
    }


//////////////s//////////////////////////// Graph Manipulation and Listening ////////////////////////////////////////////////

    /**
     * Resizes the upper limit of all the bars when the max range is changed so
     * none of the bars exceed the max value.
     */
    public void resizeBars() {
        for (int i = 0; i < bars.size(); i++) {
            if (bars.get(i).getUpperBound() > minLower)
                bars.get(i).setUpperBound(minLower-GAP);
            if (bars.get(i).getUpperBound() < maxUpper)
                bars.get(i).setUpperBound(maxUpper);
            if (bars.get(i).getLowerBound() < maxUpper)
                bars.get(i).setLowerBound(maxUpper+GAP);
            if (bars.get(i).getLowerBound() > minLower)
                bars.get(i).setLowerBound(minLower);
        }
    }
    /**
     * Adjusts the Upper bound position of a single bar in the graph
     */
    public void moveUpperBound(int bar, int y) {
        clear();
        if (y < maxUpper) {
            y = maxUpper;
        } else if (y > minLower) {
            y = minLower - GAP;
        } else if (y > bars.get(bar).getLowerBound()-GAP) {
            y = bars.get(bar).getLowerBound() - GAP;
        }
        bars.get(bar).setUpperBound(y);
        repaint();
    }
    /**
     * Adjusts the lower bound position of a single bar in the graph.
     */
    public void moveLowerBound(int bar, int y) {
        clear();
        if (y > minLower) {
            y = minLower;
        } else if (y < maxUpper) {
            y = maxUpper + GAP;
        } else if (y < bars.get(bar).getUpperBound()+GAP) {
            y = bars.get(bar).getUpperBound() + GAP;
        }
        bars.get(bar).setLowerBound(y);
        repaint();
    }
    /** 
     * Gets the upper edge of a specific bar
     */
    public int getBarUpper(int x) {
        int bar = determineBar(x);
        return bars.get(bar).getUpperBound();
    }
    /** 
     * Gets the lower edge of a specific bar
     */
    public int getBarLower(int x) {
        int bar = determineBar(x);
        return bars.get(bar).getLowerBound();
    }
    /**
     * Given an x coordinate, determines which bar is closest to where the mouse is
     */
    public int determineBar(int x) {
        return (x - 1) / BAR_WIDTH;
    }
    /**
     * Adjusts the bars in the graph according to the mouse position.
     */
    public void mouseHandler(MouseEvent e) {

        if (!(e.getX() > width || e.getY() > TOTAL_HEIGHT)
                && !(e.getX() < 0 || e.getY() < 0)) {
            int bar = determineBar(e.getX());
            int yCoord = e.getY();
            //Holding down the shift key moves the lower bound
            if (e.isShiftDown()) { // lower bound
                moveLowerBound(bar, yCoord);
            }
            else { 
                moveUpperBound(bar, yCoord);
            }
        }
    }


////////////////////////////////////////// Computing Attributes ////////////////////////////////////////////////////////

    /**
     * An exponent for the purpose of rule expanding is 1 + K/(max-min)
     * @return an ArrayList of exponents
     */
    public ArrayList<Double> calcExponents(int k) {
        ArrayList list = new ArrayList(bars.size());
        double lower, upper, exp;

        for (int i = 0; i<bars.size(); i++) {
            lower = -1*((bars.get(i).getLowerBound()-upperY)-150);
            upper = -1*((bars.get(i).getUpperBound()-upperY)-150);
            exp = (1+(k/(upper-lower)));
            list.add(new Double(exp));
        }
        return list;
    }

    /**
     * Averages for each section of the graph for the purpose of rule expanding is ((max-min)/2)/150, where 0<=avg<=1
     * @return an ArrayList of averages
     */
    public ArrayList<Double> calcAverages() {
        ArrayList list = new ArrayList(bars.size());
        double lower, upper, avg;

        for (int i = 0; i<bars.size(); i++) {
            lower = -1*((bars.get(i).getLowerBound()-upperY)-150);
            upper = -1*((bars.get(i).getUpperBound()-upperY)-150);
            avg = ((upper-lower)/2)/150;
            list.add(new Double(avg));
        }
        return list;
    }

//    /**
//     * Returns an ArrayList of ArrayLists of two elements representing the upper and lower bounds of
//     * each bar in the graph. Used for computing the complexity specified by the user
//     */
//    public ArrayList<double> valueRange() {
//        ArrayList allVals = new ArrayList(bars.size());
//        Integer lower, upper;
//        for (int i = 0; i<bars.size(); i++) {
//            ArrayList pair = new ArrayList(2);
//            lower = -1*((bars.get(i).getLowerBound()-upperY)-150);
//            upper = -1*((bars.get(i).getUpperBound()-upperY)-150);
//            pair.add(lower);
//            pair.add(upper);
//        }
//        return allVals;
//    }

}