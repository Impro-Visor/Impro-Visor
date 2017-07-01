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

package imp.roadmap.brickdictionary;

import imp.com.*;
import imp.data.Score;
import imp.gui.Notate;
import imp.roadmap.RoadMapFrame;
import imp.util.BasicEditor;

/**
 *
 * @author  David Morrison, August Toman-Yih, Robert Keller
 * This was derived from SourceEditorDialog, but it no longer
 * has the Design component.
 */
public class BrickDictionaryEditorDialog extends javax.swing.JDialog implements BasicEditor
{
     /**
     * Used as a prefix on editor window titles
     */
    
    Notate parent;
    RoadMapFrame frameParent;
    CommandManager cm;
    int type;
    
    public static final String titlePrefix = "Editor For: ";
    
    private boolean firstTime = true;
   
    /** Creates new form sourceEditorDialog */
    public BrickDictionaryEditorDialog(RoadMapFrame parent, boolean modal, Notate p, CommandManager cm)
    {
        super(parent, modal);
        frameParent = parent;
        this.parent = p;
        this.cm = cm;
        this.type = type;
        //initComponents();
        setSize(200,200);
        setTitle("");
        editorToSourceButton.setText("Editor to Dictionary");
        sourceToEditorButton.setText("Dictionary to Editor");
        
        sourceEditor.setFont(new java.awt.Font("Lucida Console", java.awt.Font.PLAIN, 13));
    }

    private String title = "";
    @Override
    public void setTitle(String title) {
        this.title = title;
        super.setTitle(titlePrefix + title);
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    public String getText() {
        return sourceEditor.getText();
    }
    
    public void setText(String text) {
      
        sourceEditor.setSize(600, 2000);
        sourceEditor.setText(text);
    }

    public void setRows(int numRows)
    {
        sourceEditor.setRows(numRows);
    }
 

    private void sourceToEditorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sourceToEditorButtonActionPerformed
    {
    fillEditor();
    }

    public void fillEditor()
    {
    new DictionaryToEditorCommand(((RoadMapFrame)frameParent).getDictionaryFilename(), this).execute();

    if( firstTime )
      {
      sourceEditor.moveCaretPosition(0);
      firstTime = false;
      }
    }
    
    private void editorToSourceButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editorToSourceButtonActionPerformed
    {

     new EditorToDictionaryCommand(frameParent, this).execute();
    }

private void windowClosingHandler(java.awt.event.WindowEvent evt)
  {
 
  }
    
    //used when calling the grammar to editor button automatically from Notate
    public void performEditorToSourceButton(java.awt.event.ActionEvent evt) {
        editorToSourceButtonActionPerformed(evt);

    }
    
    private javax.swing.JButton editorToSourceButton;
    private javax.swing.JTextArea sourceEditor;
    private javax.swing.JScrollPane sourceEditorScrollPane;
    private javax.swing.JButton sourceToEditorButton;    
}
