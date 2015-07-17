/*
 * ToolkitImages.java 0.0.1 8th July 2001
 *
 * Copyright (C) 2000, 2001 Andrew Sorensen, Andrew Brown, Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.gui.cpn;

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

    private final Image semibreve;

    private final Image minimUp;

    private final Image minimDown;

    private final Image crotchetUp;

    private final Image crotchetDown;

    private final Image quaverDown;

    private final Image quaverUp;

    private final Image semiquaverDown;

    private final Image semiquaverUp;

    private final Image semibreveRest;

    private final Image minimRest;

    private final Image crotchetRest;

    private final Image quaverRest;

    private final Image semiquaverRest;

    private final Image dot;

    private final Image sharp;

    private final Image flat;

    private final Image natural;

    private final Image one;

    private final Image two;

    private final Image three;

    private final Image four;

    private final Image five;

    private final Image six;

    private final Image seven;

    private final Image eight;

    private final Image nine;

    private final Image delete;

    private final Image tieOver;

    private final Image tieUnder;

    /**
     * Constructs a set of stave images.
     */
    public ToolkitImages() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        trebleClef = toolkit.getImage(
                Stave.class.getResource("graphics/trebleClef.gif"));
        bassClef = toolkit.getImage(
                Stave.class.getResource("graphics/bassClef.gif"));
        crotchetDown = toolkit.getImage(
                Stave.class.getResource("graphics/crotchetDown.gif"));
        crotchetUp = toolkit.getImage(
                Stave.class.getResource("graphics/crotchetUp.gif"));
        quaverDown = toolkit.getImage(
                Stave.class.getResource("graphics/quaverDown.gif"));
        quaverUp = toolkit.getImage(
                Stave.class.getResource("graphics/quaverUp.gif"));
        semiquaverDown = toolkit.getImage(
                Stave.class.getResource("graphics/semiquaverDown.gif"));
        semiquaverUp = toolkit.getImage(
                Stave.class.getResource("graphics/semiquaverUp.gif"));
        minimDown = toolkit.getImage(
                Stave.class.getResource("graphics/minimDown.gif"));
        minimUp = toolkit.getImage(
                Stave.class.getResource("graphics/minimUp.gif"));
        semibreve = toolkit.getImage(
                Stave.class.getResource("graphics/semibreve.gif"));
        dot = toolkit.getImage(
                Stave.class.getResource("graphics/dot.gif"));
        semiquaverRest = toolkit.getImage(
                Stave.class.getResource("graphics/semiquaverRest.gif"));
        quaverRest = toolkit.getImage(
                Stave.class.getResource("graphics/quaverRest.gif"));
        crotchetRest = toolkit.getImage(
                Stave.class.getResource("graphics/crotchetRest.gif"));
        minimRest = toolkit.getImage(
                Stave.class.getResource("graphics/minimRest.gif"));
        semibreveRest = toolkit.getImage(
                Stave.class.getResource("graphics/semibreveRest.gif"));
        sharp = toolkit.getImage(
                Stave.class.getResource("graphics/sharp.gif"));
        flat = toolkit.getImage(
                Stave.class.getResource("graphics/flat.gif"));
        natural = toolkit.getImage(
                Stave.class.getResource("graphics/natural.gif"));
        one = toolkit.getImage(
                Stave.class.getResource("graphics/one.gif"));
        two = toolkit.getImage(
                Stave.class.getResource("graphics/two.gif"));
        three = toolkit.getImage(
                Stave.class.getResource("graphics/three.gif"));
        four = toolkit.getImage(
                Stave.class.getResource("graphics/four.gif"));
        five = toolkit.getImage(
                Stave.class.getResource("graphics/five.gif"));
        six = toolkit.getImage(
                Stave.class.getResource("graphics/six.gif"));
        seven = toolkit.getImage(
                Stave.class.getResource("graphics/seven.gif"));
        eight = toolkit.getImage(
                Stave.class.getResource("graphics/eight.gif"));
        nine = toolkit.getImage(
                Stave.class.getResource("graphics/nine.gif"));
        delete = toolkit.getImage(
                Stave.class.getResource("graphics/delete.gif"));
        tieOver = toolkit.getImage(
                Stave.class.getResource("graphics/tieOver.gif"));
        tieUnder = toolkit.getImage(
                Stave.class.getResource("graphics/tieUnder.gif"));
    }

    public Image getTrebleClef() {
        return trebleClef;
    }

    public Image getBassClef() {
        return bassClef;
    }

    public Image getSemibreve() {
        return semibreve;
    }

    public Image getMinimUp() {
        return minimUp;
    }

    public Image getMinimDown() {
        return minimDown;
    }

    public Image getCrotchetUp() {
        return crotchetUp;
    }

    public Image getCrotchetDown() {
        return crotchetDown;
    }

    public Image getQuaverUp() {
        return quaverUp;
    }

    public Image getQuaverDown() {
        return quaverDown;
    }

    public Image getSemiquaverUp() {
        return semiquaverUp;
    }

    public Image getSemiquaverDown() {
        return semiquaverDown;
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

    public Image getDot() {
        return dot;
    }

    public Image getSharp() {
        return sharp;
    }

    public Image getFlat() {
        return flat;
    }

    public Image getNatural() {
        return natural;
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

    public Image getDelete() {
        return delete;
    }

    public Image getTieOver() {
        return tieOver;
    }

    public Image getTieUnder() {
        return tieUnder;
    }
}
