/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.com;

import imp.data.Advisor;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.SplashDialog;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import polya.Polylist;
import polya.Tokenizer;

/**
 * A Command that opens an advice rulebase.
 * @see         Command
 * @see         CommandManager
 * @see         Advisor
 * @see         File
 * @author      Stephen Jones
 */
public class LoadAdviceCommand implements Command, Runnable {

    /** 
     * the File to open
     */
    private File file;

    /**
     * the Advisor to read the File into
     */
    private Advisor adv;

    /**
     * true if the rulebase is appended to
     */
    private boolean append = false;
    
    /**
     * set to true means hide the splash screen when finished
     * if false, can be hidden by a call to hideLoadDialog
     */    
    private boolean hideSplash;
    
    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;
    
    private Notate parent;
    
    private SplashDialog ld;
    
    /**
     * Creates a new Command that can read a File into an Advisor.
     * @param file      the File to read
     * @param adv     the Advisor to read the File into
     */
    public LoadAdviceCommand(File file, Advisor adv, JFrame notate, boolean showSplash, boolean hideSplash) {
        this.file = file;
        this.adv = adv;
	parent = (Notate)notate;
	ld = new SplashDialog(notate, false, showSplash);
        this.hideSplash = hideSplash;
    }

    public LoadAdviceCommand(File file, Advisor adv, JFrame notate, boolean showSplash, boolean hideSplash, boolean append) {
        this(file, adv, notate, showSplash, hideSplash);
        this.append = append;
    }

    /**
     * Reads the File into the Advisor.
     */
    public void run() {
        FileInputStream adviceStream = null;
        ld.setLocationRelativeTo(parent);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(parent != null)
                    parent.showFakeModalDialog(ld);
                else
                    ld.setVisible(true);
            }
        });

        try {
            adviceStream = new FileInputStream(file);
        } catch(Exception e) {
            //e.printStackTrace();
        }

        Tokenizer in = new Tokenizer(adviceStream);
        Object ob;
        Polylist rules = new Polylist();
        Object prevOb = null;
        String typeOfItemLoaded = "";
        
        ld.setText("Loading Vocabulary ...");
        ld.repaint();

        while( (ob = in.nextSexp()) != Tokenizer.eof ) {
            if( ob.equals(prevOb) ) {
                ErrorLog.log(ErrorLog.WARNING, "Two consecutive vocabulary items are identical: " + ob);
            }
            if( (ob instanceof Polylist) && ((Polylist)ob).nonEmpty() ) {
                 String loading = (String)((Polylist)ob).first();
                // avoid extra repainting
                if( !loading.equals(typeOfItemLoaded) )
                  {
                  //ld.setText("Loading " + loading + "s...");
                  // ??not sure why we need to repaint here if using the same text.
                  // However, without it, no text appears.
                  ld.repaint();
                  typeOfItemLoaded = loading;
		  }
                 
                /* Do we really need to do this?
                 
                // Check to see if graded licks are the same with different grades:
                // Strip off the last element (which is the grade) and then compare
                Polylist obNoGrade = new Polylist();
                if (((String)((Polylist)((Polylist)ob).reverse().first()).first()).equals("grade"))
                    obNoGrade = ((Polylist)ob).reverse().rest();
                else
                    obNoGrade = (Polylist)ob;

                Polylist prevObNoGrade = new Polylist();
                if (prevOb != null &&
                        ((String)((Polylist)((Polylist)prevOb).reverse().first()).first()).equals("grade"))
                    prevObNoGrade = ((Polylist)prevOb).reverse().rest();
                else
                    prevObNoGrade = (Polylist)prevOb;

                if (!ob.equals(prevOb) && obNoGrade.equals(prevObNoGrade))
                    ErrorLog.log(ErrorLog.WARNING, "Two consecutive vocabulary items are identical: " + ob);
                */
                rules = rules.cons((Polylist)ob);	// FIX: Need more form checking here.
            }
            prevOb = ob;
        }

        adv.setRules(rules.reverse());
/* REVISIT
        if(parent != null) {
            parent.redoScales();
            parent.resetTriageParameters(true);
        }
*/

        if(hideSplash)
            hideLoadDialog();

        synchronized(this) {
            doneLoading = true;
            this.notifyAll();
        }
    }
    
    public boolean hasLoaded() {
        return doneLoading;
    }
    
    private boolean doneLoading = false;
    public void hideLoadDialog() {
        if(parent != null) {
            parent.hideFakeModalDialog(ld);
        } else {
            ld.setVisible(false);
        }
        ld.dispose();
    }
    
    public void setLoadDialogText(String msg) {
        ld.setText(msg);
    }
    
    public void execute() {
	Thread t = new Thread(this);
	t.start();
    }

    /**
     * Undo unsupported for LoadAdviceCommand.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for LoadAdvice.");
    }

    /**
     * Redo unsupported for LoadAdviceCommand.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for LoadAdvice.");
    }
    
    public boolean isUndoable() {
        return undoable;
    }
}
