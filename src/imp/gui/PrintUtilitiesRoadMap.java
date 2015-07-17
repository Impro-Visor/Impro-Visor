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

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.print.*;
import javax.print.*;

/** 
 *  A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */
public class PrintUtilitiesRoadMap implements Printable{
    private Component compToBePrinted;
    
    public PrintUtilitiesRoadMap(Component comp) {
        compToBePrinted = comp;
    }
    
    public static void printRoadMap(Component comp)
    {
        PrintUtilitiesRoadMap util = new PrintUtilitiesRoadMap(comp);
        PrintService[] pservices = PrinterJob.lookupPrintServices();
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(util);
        if(printJob.printDialog())
        {
            try
            {
                printJob.print();
            }
            catch(PrinterException pe)
            {
                System.out.println("Error Printing: " + pe);
            }
        }
    }
    
    /**
     * 7/20/11 This method was created by Amos Byon specifically for RoadMaps, who edited the
     * method created by Rob MacGrogan, who edited the
     * original method written by Marty Hall.
     * 2/05 Rob MacGrogan, http://www.developerdotstar.com/community/node/124/print
     */
    public int print(Graphics g, PageFormat pf, int pageIndex)
    {
        int response = NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D)g;
        Dimension d = compToBePrinted.getSize();
        double pageHeight = pf.getImageableHeight();
        double pageWidth = pf.getImageableWidth();
        double scale = pageWidth/d.width;
        int numPages = 1;
        if(pageIndex >= numPages) {
            response = NO_SUCH_PAGE;
        }
        else
        {
            g2.scale(scale, scale);
            g2.translate(pf.getImageableX(), pf.getImageableY());
            compToBePrinted.paint(g2);
            response = Printable.PAGE_EXISTS;
        }
        return response;
    }
    
}
