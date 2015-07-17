/* --------------------
* A jMusic tool which displays a score as a
* Common Practice Notation in a window.
* @author Andrew Brown 
 * @version 1.0,Sun Feb 25 18:43
* ---------------------
*/
package jm.gui.show;

import java.awt.*;
/*

<This Java Class is part of the jMusic API>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/ 
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import jm.JMC;
import jm.music.data.*;
import jm.midi.*;

//--------------
//third class!!
//--------------
public class ShowRuler extends Canvas implements MouseListener, MouseMotionListener{
	//attributes
	private int startX;
	private int height = 15;
	private int timeSig = 2;
	private ShowPanel sp;
        private Font font = new Font("Helvetica", Font.PLAIN, 10);
	
	public ShowRuler(ShowPanel sp) {
		super();
		this.sp = sp;
		this.setSize((int)(sp.score.getEndTime()*sp.beatWidth),height);
		this.setBackground(Color.lightGray);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setCursor(new Cursor(13));
	}
        
        /**
        * Report the height of this ruler panel.
        */
        public int getHeight() {
            return height;
        }
	
	public void paint(Graphics g) {
	    double beatWidth = sp.beatWidth;
            g.setFont(font);
            for(int i=0;i<(sp.score.getEndTime());i++){ 
                int xLoc = (int)Math.round(i*beatWidth);
                if (i%timeSig == 0) {
                        g.drawLine( xLoc,0, xLoc,height);
                        if (beatWidth > 15) g.drawString(""+i, xLoc+2, height-2);
                    } else {
                            g.drawLine( xLoc,height/2, xLoc,height);
                    }
            }
	}
	
	// get the position of inital mouse click
	public void mousePressed(MouseEvent e) {
	    //System.out.println("Pressed");
	    this.setCursor(new Cursor(10));
		startX = e.getX();
	}
	
	//Mouse Listener stubs
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
	    this.setCursor(new Cursor(13));
            sp.update();
	}
	//mouseMotionListener stubs
	public void mouseMoved(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
	    //System.out.println("Dragged");
	    double beatWidth = sp.beatWidth;
		beatWidth += (double)((double)e.getX() - (double)startX)/5.0;
		if ( beatWidth< 1.0) beatWidth= 1.0;
		if ( beatWidth> 256.0) beatWidth= 256.0;
		//System.out.println("beatWidth = "+beatWidth);
		sp.beatWidth = beatWidth;
		startX = e.getX();
		//sp.update();
                this.repaint();
	}
}
