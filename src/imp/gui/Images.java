/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

import java.awt.Image;

/**
 * Interface with methods for returning Images representing notes, rests and
 * other stave elements for use with a CPN {@link Stave}.
 *
 * This abstraction is mainly used to allow different implementations from
 * loading the Images in different ways.  For instance, the {@link
 * ToolkitImages} implementation is suitable for applications, while the 
 * AppletImages implementation is suitable for use in applets.
 *
 * @see Stave
 * @see ToolkitImages
 * 
 * @author  Adam Kirby
 * @version 0.0.1, 8th July 2001  
 */
public interface Images {

    /**
     * Returns an Image representing a treble clef.
     */
    public Image getTrebleClef();

    /**
     * Returns an Image representing a bass clef.
     */
    public Image getBassClef();

    /**
     * Returns an Image representing a semibreve.
     */
    public Image[] getSemibreve();

    /**
     * Returns an Image representing a minim with stem raised.
     */
    public Image[] getMinimUp();

    /**
     * Returns an Image representing a minim with stem lowered.
     */
    public Image[] getMinimDown();

    /**
     * Returns an Image representing a filled note head.
     */
    public Image[] getFilledNoteHead();

    /**
     * Returns an Image representing a crotchet with stem raised.
     */
    public Image[] getCrotchetUp();

    /**
     * Returns an Image representing a crotchet with stem lowered.
     */
    public Image[] getCrotchetDown();

    /**
     * Returns an Image representing a quaver with stem raised.
     */
    public Image[] getQuaverUp();

    /**
     * Returns an Image representing a quaver with stem lowered.
     */
    public Image[] getQuaverDown();

    /**
     * Returns an Image representing a semiquaver with stem raised.
     */
    public Image[] getSemiquaverUp();

    /**
     * Returns an Image representing a semiquaver with stem lowered.
     */
    public Image[] getSemiquaverDown();

    /**
     * Returns an Image representing a demisemiquaver with stem raised.
     */
    public Image[] getDemisemiquaverUp();

    /**
     * Returns an Image representing a demisemiquaver with stem lowered.
     */
    public Image[] getDemisemiquaverDown();

    /**
     * Returns an Image representing a semibreve rest.
     */
    public Image getSemibreveRest();

    /**
     * Returns an Image representing a minim rest.
     */
    public Image getMinimRest();

    /**
     * Returns an Image representing a crotchet rest.
     */
    public Image getCrotchetRest();

    /**
     * Returns an Image representing a quaver rest.
     */                                                                       
    public Image getQuaverRest();

    /**
     * Returns an Image representing a semiquaver rest.
     */                                                                       
    public Image getSemiquaverRest();

    /**
     * Returns an Image representing a demisemiquaver rest.
     */                                                                       
    public Image getDemisemiquaverRest();
    
    /**
     * Returns an Image representing the dot symbol used to indicate the
     * duration of the preceeding symbol should be about half as long again.
     */                                                                       
    public Image getDot();

    /**
     * Returns an Image representing a sharp symbol.
     */                                                                       
    public Image[] getSharp();

    /**
     * Returns an Image representing a flat symbol.
     */                                                                       
    public Image[] getFlat();

    /**
     * Returns an Image representing a natural symbol.
     */                                                                       
    public Image[] getNatural();

    /**
     * Returns an Image representing a small box symbol (used for very small rests)
     */                                                                       
    public Image getSmallBox();

    /**
     * Returns an Image representing the digit '1' used in time signatures.
     */                                                                       
    public Image getOne();

    /**
     * Returns an Image representing the digit '2' used in time signatures.
     */                                                                       
    public Image getTwo();

    /**
     * Returns an Image representing the digit '3' used in time signatures.
     */                                                                       
    public Image getThree();

    /**
     * Returns an Image representing the digit '4' used in time signatures.
     */                                                                       
    public Image getFour();

    /**
     * Returns an Image representing the digit '5' used in time signatures.
     */                                                                       
    public Image getFive();

    /**
     * Returns an Image representing the digit '6' used in time signatures.
     */                                                                       
    public Image getSix();

    /**
     * Returns an Image representing the digit '7' used in time signatures.
     */                                                                       
    public Image getSeven();

    /**
     * Returns an Image representing the digit '8' used in time signatures.
     */                                                                       
    public Image getEight();

    /**
     * Returns an Image representing the digit '9' used in time signatures.
     */                                                                       
    public Image getNine();
    
    /**
     * Returns an Image representing the digit '9' used in time signatures.
     */                                                                       
    public Image getZero();

    /**
     * Returns an Image representing a tie used with notes that have stems
     * lowered.
     */                                                                       
    public Image getTieOver();

    /**
     * Returns an Image representing a tie used with notes that have stems
     * raised.
     */                                                                       
    public Image getTieUnder();
    
    /**
     * Returns an image used for indicating what notes are part of an n-tuplet
     */
    public Image getTupletBracket();
    
    /**
     * Returns an image used for indicating what beat a user is on
     */
    public Image getBeatBracket();
    
    public Image getSplash();

}
