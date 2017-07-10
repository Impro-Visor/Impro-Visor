/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2017 Robert Keller and Harvey Mudd College
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


package imp.gui;

/**
 * LickLog is intended as a singleton class and is modeled after ErrorLog.  
 * LickForm has to know about it, but it also needs to know about its dialog,
 * which is defined as part of class Notate.
 */

public class LickLog {

    /** dialog for GUI */

    private static DuplicateLickWarningDialog errorDialog;

    private static Notate notate;

    /**
     * Set error dialog to an actual dialog
     */
    
    public static void setDialog(Notate _notate, DuplicateLickWarningDialog _errorDialog)
      {
      errorDialog = _errorDialog;
      notate = _notate;
      }


    /**
     * Call to log message of any type.
     */

    public static int log(String message)
      {
     if( errorDialog != null )
       {
       errorDialog.setMessage(message);
       errorDialog.pack();
       errorDialog.setLocation(notate.getWidth() / 3, notate.getHeight() / 3);
       errorDialog.setVisible(true);
       }
    return notate.getIgnoreDuplicateLick();
    }
}


