/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.gui.Notate;
import static imp.gui.Notate.DEFAULT_BARS_PER_PART;
import imp.trading.UserRhythmSelecterDialog;
import imp.util.Preferences;
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
