/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
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
package imp.roadmap;

import java.awt.event.KeyEvent;

/**
 * A derivative of the textfield that only accepts integers.
 * @author August Toman-Yih
 */
public class IntegerField extends javax.swing.JTextField {
       
    @Override
    public void processKeyEvent(java.awt.event.KeyEvent evt)
    {
        if(isValid(evt))
            super.processKeyEvent(evt);
        else
            evt.consume();
    }
    
    /**
     * Returns the contained integer
     * @return 
     */
    public int getInt()
    {
        return Integer.parseInt(super.getText());
    }
    
    /**
     * Checks whether a KeyEvent is to be accepted
     * @param evt
     * @return 
     */
    private boolean isValid(java.awt.event.KeyEvent evt)
    {
        return Character.isDigit(evt.getKeyChar()) ||
                evt.isActionKey() ||
                evt.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
                evt.getKeyCode() == KeyEvent.VK_DELETE;
    }
}
