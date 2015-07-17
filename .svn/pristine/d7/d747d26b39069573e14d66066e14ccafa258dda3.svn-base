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

import imp.gui.ErrorDialogNonModal;

/**
 * ErrorLog is intended as a singleton class.  It includes static
 * methods that can be called from anywhere to indicate a user
 * error, with a message and severity.  It is intended that message
 * will be collected together in a window and/or file for presentation
 * to the user.
 *
 * This class is also the keeper of the one error dialog for this app,
 * which is obtained by getDialog();
 */

public class ErrorNonModal
{
/** 
 * tag put in front of error messages
 */
  
private static String tag = "";

/** 
 * dialog for GUI 
 */

private static ErrorDialogNonModal errorDialogNonModal = new ErrorDialogNonModal(true);

/** 
 * text in dialog 
 */

private static javax.swing.JTextPane errorText = errorDialogNonModal.getTextPane();

/** 
 * button in dialog 
 */

private static javax.swing.JButton okButton = errorDialogNonModal.getButton();


/**
 * Changes the title of the errorDialogNonModal to title
 **/
public static void setDialogTitle(String title)
  {
  errorDialogNonModal.setTitle(title);
  }


/**
 * Call to log message of any type.
 */

public static String log(String message)
  {
  // FIX: This should be modified to use the GUI if it has
  // been opened.

  String line = "Please Fix Error : " + message;

    errorText.setText(line);
    errorDialogNonModal.getRootPane().setDefaultButton(okButton);
    errorDialogNonModal.setVisible(true);
    errorDialogNonModal.requestFocus();


  /* FIX: The following behavior is temporary. What should happen is that the error dialog
   * opens, and when dismissed, then System.exit(1) is called.
   */

  return line;
  }

/**
 * Return the error dialog.
 */

public static ErrorDialogNonModal getDialog()
  {
  return errorDialogNonModal;
  }

}


