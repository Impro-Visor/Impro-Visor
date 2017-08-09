/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2017 Robert Keller and Harvey Mudd College
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

package imp.data;

import imp.gui.Notate;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 *
 * @author Lukas Gnirke
 */
public class RhythmListCellRenderer extends JLabel implements ListCellRenderer<RhythmSelecterEntry> {

    private Border border;

    
    public RhythmListCellRenderer(Notate notate){
        setOpaque(true);
        border = BorderFactory.createLineBorder(Color.BLUE, 1);
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    @Override
    public Component getListCellRendererComponent(JList<? extends RhythmSelecterEntry> list, 
            RhythmSelecterEntry value, int index, boolean isSelected, boolean cellHasFocus) 
    {
        if(value.getRhythmRepresentation() == null){
            //System.out.println("    found a null image....setting the text");
            setText(value.getRealMelody());
            setIcon(null);
        }else{
            setIcon(value.getRhythmRepresentation());
            //repaint();               
            //System.out.println("    image is no longer null.....setting an ICON :{D");
            setText("");
        }
//        
   
        if(isSelected){
            setBackground(Color.WHITE);
            //setBackground(list.getSelectionBackground());
            //setForeground(list.getSelectionForeground());
            //System.out.println("\nselected item!!!!!!!");
        }else{
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
             
        setEnabled(list.isEnabled());
        if(isSelected){
            setBorder(border);
        }else{
            setBorder(null);
        }
        
        return this;
    }
    
}
