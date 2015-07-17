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
import java.awt.Toolkit;

/**
 * Defines images for use with {@link Stave} components loaded via an AWT
 * Toolkit.
 *
 * @see     Stave
 *
 * @author  Andrew Sorensen, Andrew Brown, Adam Kirby
 * @version 0.0.1, 8th July 2001
 */
public class ToolkitImages implements Images {

    private final Image trebleClef;

    private final Image bassClef;

    private final Image[] semibreve;

    private final Image[] minimUp;

    private final Image[] minimDown;

    private final Image[] filledNoteHead;

    private final Image[] crotchetUp;

    private final Image[] crotchetDown;

    private final Image[] quaverDown;

    private final Image[] quaverUp;

    private final Image[] semiquaverDown;

    private final Image[] semiquaverUp;

    private final Image[] demisemiquaverDown;

    private final Image[] demisemiquaverUp;

    private final Image[] sharp;

    private final Image[] flat;

    private final Image[] natural;

    private final Image semibreveRest;
    
    private final Image minimRest;

    private final Image crotchetRest;

    private final Image quaverRest;

    private final Image semiquaverRest;
    
    private final Image demisemiquaverRest;

    private final Image smallBox;

    private final Image dot;

    private final Image one;

    private final Image two;

    private final Image three;

    private final Image four;

    private final Image five;

    private final Image six;

    private final Image seven;

    private final Image eight;

    private final Image nine;
    
    private final Image zero;

    private final Image tieOver;

    private final Image tieUnder;
    
    private final Image tupletBracket;
    
    private final Image beatBracket;
    
    private final Image splash;

    private static ToolkitImages instance = null;
    
    public static ToolkitImages getInstance() {
        if(instance == null)
            instance = new ToolkitImages();
        
        return instance;
    }
    
    /**
     * Constructs a set of stave images.
     */
    private ToolkitImages() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        trebleClef = toolkit.getImage(
                Stave.class.getResource("graphics/trebleClef.png"));
        bassClef = toolkit.getImage(
                Stave.class.getResource("graphics/bassClef.png"));
        
        /**
         * Load the notes into arrays where
         * Black = 0, Red = 1, Green = 2, Blue =3
         */
        crotchetDown = new Image[4];
        crotchetDown[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/crotchetDown.png"));
        crotchetDown[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/crotchetDown.png"));
        crotchetDown[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/crotchetDown.png"));
        crotchetDown[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/crotchetDown.png"));
        
        crotchetUp = new Image[4];
        crotchetUp[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/crotchetUp.png"));
        crotchetUp[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/crotchetUp.png"));
        crotchetUp[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/crotchetUp.png"));
        crotchetUp[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/crotchetUp.png"));
        
        filledNoteHead = new Image[4];
        filledNoteHead[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/filledNoteHead.png"));
        filledNoteHead[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/filledNoteHead.png"));
        filledNoteHead[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/filledNoteHead.png"));
        filledNoteHead[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/filledNoteHead.png"));
        
        quaverDown = new Image[4];
        quaverDown[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/quaverDown.png"));
        quaverDown[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/quaverDown.png"));
        quaverDown[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/quaverDown.png"));
        quaverDown[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/quaverDown.png"));
        
        quaverUp = new Image[4];
        quaverUp[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/quaverUp.png"));
        quaverUp[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/quaverUp.png"));
        quaverUp[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/quaverUp.png"));
        quaverUp[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/quaverUp.png"));
        
        semiquaverDown = new Image[4];
        semiquaverDown[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/semiquaverDown.png"));
        semiquaverDown[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/semiquaverDown.png"));
        semiquaverDown[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/semiquaverDown.png"));
        semiquaverDown[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/semiquaverDown.png"));
        
        semiquaverUp = new Image[4];
        semiquaverUp[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/semiquaverUp.png"));
        semiquaverUp[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/semiquaverUp.png"));
        semiquaverUp[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/semiquaverUp.png"));
        semiquaverUp[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/semiquaverUp.png"));
        
        demisemiquaverDown = new Image[4];
        demisemiquaverDown[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/demisemiquaverDown.png"));
        demisemiquaverDown[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/demisemiquaverDown.png"));
        demisemiquaverDown[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/demisemiquaverDown.png"));
        demisemiquaverDown[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/demisemiquaverDown.png"));
        
        demisemiquaverUp = new Image[4];
        demisemiquaverUp[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/demisemiquaverUp.png"));
        demisemiquaverUp[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/demisemiquaverUp.png"));
        demisemiquaverUp[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/demisemiquaverUp.png"));
        demisemiquaverUp[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/demisemiquaverUp.png"));
        
        minimDown = new Image[4];
        minimDown[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/minimDown.png"));
        minimDown[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/minimDown.png"));
        minimDown[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/minimDown.png"));
        minimDown[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/minimDown.png"));
        
        minimUp = new Image[4];
        minimUp[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/minimUp.png"));
        minimUp[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/minimUp.png"));
        minimUp[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/minimUp.png"));
        minimUp[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/minimUp.png"));
        
        semibreve = new Image[4];
        semibreve[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/semibreve.png"));
        semibreve[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/semibreve.png"));
        semibreve[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/semibreve.png"));
        semibreve[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/semibreve.png"));
        
        sharp = new Image[4];
        sharp[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/sharp.png"));
        sharp[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/sharp.png"));
        sharp[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/sharp.png"));
        sharp[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/sharp.png"));
        
        flat = new Image[4];
        flat[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/flat.png"));
        flat[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/flat.png"));
        flat[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/flat.png"));
        flat[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/flat.png"));
        
        natural = new Image[4];
        natural[0] = toolkit.getImage(
            Stave.class.getResource("graphics/blacknotes/natural.png"));
        natural[1] = toolkit.getImage(
            Stave.class.getResource("graphics/rednotes/natural.png"));
        natural[2] = toolkit.getImage(
            Stave.class.getResource("graphics/greennotes/natural.png"));
        natural[3] = toolkit.getImage(
            Stave.class.getResource("graphics/bluenotes/natural.png"));

        dot = toolkit.getImage(
                Stave.class.getResource("graphics/dot.png"));
        smallBox = toolkit.getImage(
                Stave.class.getResource("graphics/smallBox.png"));
        demisemiquaverRest = toolkit.getImage(
                Stave.class.getResource("graphics/demisemiquaverRest.png"));
        semiquaverRest = toolkit.getImage(
                Stave.class.getResource("graphics/semiquaverRest.png"));
        quaverRest = toolkit.getImage(
                Stave.class.getResource("graphics/quaverRest.png"));
        crotchetRest = toolkit.getImage(
                Stave.class.getResource("graphics/crotchetRest.png"));
        minimRest = toolkit.getImage(
                Stave.class.getResource("graphics/minimRest.png"));
        semibreveRest = toolkit.getImage(
                Stave.class.getResource("graphics/semibreveRest.png"));
        one = toolkit.getImage(
                Stave.class.getResource("graphics/one.png"));
        two = toolkit.getImage(
                Stave.class.getResource("graphics/two.png"));
        three = toolkit.getImage(
                Stave.class.getResource("graphics/three.png"));
        four = toolkit.getImage(
                Stave.class.getResource("graphics/four.png"));
        five = toolkit.getImage(
                Stave.class.getResource("graphics/five.png"));
        six = toolkit.getImage(
                Stave.class.getResource("graphics/six.png"));
        seven = toolkit.getImage(
                Stave.class.getResource("graphics/seven.png"));
        eight = toolkit.getImage(
                Stave.class.getResource("graphics/eight.png"));
        nine = toolkit.getImage(
                Stave.class.getResource("graphics/nine.png"));
	zero = toolkit.getImage(
		Stave.class.getResource("graphics/zero.png"));
        tieOver = toolkit.getImage(
                Stave.class.getResource("graphics/tieOver.png"));
        tieUnder = toolkit.getImage(
                Stave.class.getResource("graphics/tieUnder.png"));
        tupletBracket = toolkit.getImage(
                Stave.class.getResource("graphics/tupletBracket.png"));
        beatBracket = toolkit.getImage(
                Stave.class.getResource("graphics/beatBracket.png"));
	splash = toolkit.getImage(
		Stave.class.getResource("graphics/splash.png"));
    }

    public Image getTrebleClef() {
        return trebleClef;
    }

    public Image getBassClef() {
        return bassClef;
    }

    public Image[] getSemibreve() {
        return semibreve;
    }

    public Image[] getMinimUp() {
        return minimUp;
    }

    public Image[] getMinimDown() {
        return minimDown;
    }

    public Image[] getFilledNoteHead() {
        return filledNoteHead;
    }

    public Image[] getCrotchetUp() {
        return crotchetUp;
    }

    public Image[] getCrotchetDown() {
        return crotchetDown;
    }

    public Image[] getQuaverUp() {
        return quaverUp;
    }

    public Image[] getQuaverDown() {
        return quaverDown;
    }

    public Image[] getSemiquaverUp() {
        return semiquaverUp;
    }

    public Image[] getSemiquaverDown() {
        return semiquaverDown;
    }
    
    public Image[] getDemisemiquaverUp() {
        return demisemiquaverUp;
    }

    public Image[] getDemisemiquaverDown() {
        return demisemiquaverDown;
    }

    public Image[] getSharp() {
        return sharp;
    }

    public Image[] getFlat() {
        return flat;
    }

    public Image[] getNatural() {
        return natural;
    }

    public Image getSemibreveRest() {
        return semibreveRest;
    }

    public Image getMinimRest() {
        return minimRest;
    }

    public Image getCrotchetRest() {
        return crotchetRest;
    }

    public Image getQuaverRest() {
        return quaverRest;
    }

    public Image getSemiquaverRest() {
        return semiquaverRest;
    }

    public Image getDemisemiquaverRest() {
        return demisemiquaverRest;
    }

    public Image getSmallBox() {
        return smallBox;
    }

    public Image getDot() {
        return dot;
    }

    public Image getOne() {
        return one;
    }

    public Image getTwo() {
        return two;
    }

    public Image getThree() {
        return three;
    }

    public Image getFour() {
        return four;
    }

    public Image getFive() {
        return five;
    }

    public Image getSix() {
        return six;
    }

    public Image getSeven() {
        return seven;
    }

    public Image getEight() {
        return eight;
    }

    public Image getNine() {
        return nine;
    }
    
    public Image getZero() {
	return zero;
    }

    public Image getTieOver() {
        return tieOver;
    }

    public Image getTieUnder() {
        return tieUnder;
    }
    
    public Image getTupletBracket() {
        return tupletBracket;
    }
    
    public Image getBeatBracket() {
        return beatBracket;
    }
    
    public Image getSplash() {
	return splash;
    }
}
