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

package imp.util;

import imp.ImproVisor;
import imp.gui.ErrorDialogWithResponse;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * ErrorLogWithResponse is intended as a singleton class.  It includes static
 * methods that can be called from anywhere to indicate a user
 * error, with a message and severity.  It is intended that message
 * will be collected together in a window and/or file for presentation
 * to the user.
 *
 * This class is also the keeper of the one error dialog for this app,
 * which is obtained by getDialog();
 */

public class ErrorLogWithResponse
{
  
/** 
 * the names of error serverities
 */

static String name[] =
  {
  "Comment",        // severity 0
  "Warning",        // severity 1
  "Severe Error",   // severity 2
  "Fatal Error"     // severity 3
  };

/** 
 * tag put in front of error messages
 */
  
private static String tag = "*** ";


/** 
 * severity: FATAL: 
 * The run cannot proceed. 
 */

public static final int FATAL = 3;

/** 
 * severity: SEVERE: 
 * The run can proceed, but will stop at some point. 
 */

public static final int SEVERE = 2;

/** 
 * severity: WARNING: 
 * The run can proceed, but a warning message is given. 
 */

public static final int WARNING = 1;

/** 
 * severity: COMMENT: 
 * This is just a logged comment, not an error 
 * (to be used in debuggging and testing) 
 */

public static final int COMMENT = 0;

/** 
 * an array of counts of the various severities
 */

static int count[] = new int[FATAL + 1];


/** 
 * Stream on which to write error messages 
 */

static PrintStream errorStream;

/** 
 * indicator that file was opened 
 */

static boolean fileOpened = false;

/** 
 * indicator that a fatal error occured 
 */

static boolean fatal = false;

/** 
 * indicator that a severe error occured 
 */

static boolean severe = false;

/** 
 * dialog for GUI 
 */

private static ErrorDialogWithResponse errorDialog; 


/**
 * Change the title of the errorDialog to title
 **/
public static void setDialogTitle(String title)
  {
  errorDialog.setTitle(title);
  }


/**
 * Call to log message of any type.
 * Return value of true means to abort, false to continue.
 */

public static boolean log(int severity, String message)
  {
  return log(severity, message, true);
  }


public static boolean log(int severity, String message, boolean showDialog)
  {
  // severity must be within range
  assert (severity <= FATAL && severity >= 0);
  

  count[severity]++;
  
  switch( severity )
    {
    case FATAL:
      fatal = true;
      break;

    case SEVERE:
      severe = true;
      break;
    }

  // FIX: This should be modified to use the GUI if it has
  // been opened.

  String line = tag + name[severity] + ": " + message;

  if( severity == COMMENT )
    {
      return false;
    }

  // Took this line out in order to return the error to the GUI instead
  // But right now it doesn't show!

  if( !fileOpened )
    {
    try
      {
      errorStream = new PrintStream(new FileOutputStream(ImproVisor.getErrorLogFile()));
      fileOpened = true;
      }
    catch( Exception e )
      {
      System.err.println("*** Could not open error log file.");
      }
    }

  errorStream.println(line);

  if( showDialog )
    {
    if( errorDialog == null )
      {
      errorDialog = new ErrorDialogWithResponse(true);
      }
    javax.swing.JTextPane errorText = errorDialog.getTextPane();
    javax.swing.JButton okButton = errorDialog.getButton();
    errorText.setText(line);
    errorDialog.getRootPane().setDefaultButton(okButton);
    errorDialog.setVisible(true);
    errorDialog.requestFocus();
    }

  if( errorDialog.getAbort() || hadFatal() )
    {
    return true;
    }

  return false;
  }


/**
 * Return true if there was a fatal error.
 */

public static boolean hadFatal()
  {
  return fatal;
  }


/**
 * Return true if there was a severe or fatal error.
 */

public static boolean hadSevere()
  {
  return severe || fatal;
  }


/**
 * Return the error dialog.
 */

public static ErrorDialogWithResponse getDialog()
  {
  return errorDialog;
  }

}


