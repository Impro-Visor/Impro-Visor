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

package jm.gui.wave;

import jm.audio.io.SampleIn;
import jm.audio.Instrument;
import java.awt.*;
import java.awt.event.*;

/*
 * A part of the jMusic audio wave file viewing package
 * @author Andrew Brown
 */
public class WaveRuler extends Panel implements MouseListener, MouseMotionListener {
    private WaveScrollPanel scrollPanel;
    private int markerWidth, startX;
    private double tempRes;
    private Font font = new Font("Helvetica", Font.PLAIN, 9);
    private int startSecond = 0;
    
    public WaveRuler() {
        super();
        setBackground(Color.lightGray);
        this.setSize(new Dimension(600,20));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    /*
    * Set up a link with the object tht contains this ruler.
    */
    public void setWaveScrollPanel(WaveScrollPanel scrollPanel) {
        this.scrollPanel = scrollPanel;
    }
    
    public void setMarkerWidth(int newWidth) {
        if (newWidth > 0) {
            this.markerWidth = newWidth;
            repaint();
        }
    }
    
    public void paint(Graphics g) {
        g.setColor(Color.darkGray);
        g.setFont(font);
        int max = this.getSize().width;
        int startLoc = -1 * scrollPanel.getWaveView().getStartPos();
        int secInc = markerWidth;
        int tenthInc = (int)Math.round(markerWidth/10.0);
        int centInc = (int)Math.round(markerWidth/100.0);
        int milliInc = (int)Math.round(markerWidth/1000.0);
        int res = scrollPanel.getWaveView().getResolution();
        startSecond= scrollPanel.getWaveView().getStartPos() / 
            scrollPanel.getWaveView().getSampleRate() /
            scrollPanel.getWaveView().getChannels();
        // 1000ths of seconds
        int counter = 0;
        g.setColor(Color.white);
        if (markerWidth > 20000)  {
            for(int j=startLoc/res; j < max; j += secInc) {
                for(int k=0; k < 10; k++) {
                    for(int m=0; m < 10; m++) {
                        for(int i=0; i < 10; i++) {  
                            if(counter%10 != 0) {
                                int pos = j+k*tenthInc+m*centInc+i*milliInc;
                                g.drawLine (pos, getSize().height/8 * 7, pos, getSize().height);
                                if (markerWidth>40000) g.drawString(""+ (startSecond + counter/1000.0), 
                                    pos + 2, getSize().height - 1);
                            }
                        counter++;
                        }
                    }
                }
            }
        }
        
        // 100ths of seconds
        counter = 0;
        g.setColor(Color.magenta);
        if (markerWidth > 1200)  {
            for(int j=startLoc/res; j < max; j += secInc) {
                for(int k=0; k < 10; k++) {
                    for(int i=0; i < 10; i++) { 
                        if(counter%10 != 0) {
                        int pos = j+k*tenthInc+i*centInc;
                            g.drawLine (pos, getSize().height/4 * 3, pos, getSize().height);
                            if (markerWidth>4800) g.drawString(""+ (startSecond + counter/100.0), 
                                pos + 2, getSize().height - 1);
                        }
                    counter++;
                    }
                }
            }
        }

        // 10th of seconds
        counter = 0;
        g.setColor(Color.blue);
        if (markerWidth > 150) {
            for(int j=startLoc/res; j < max; j += secInc) {
                for(int i=0; i < 10; i++) { 
                    if(counter%10 != 0) {
                        int pos = j+i*tenthInc;
                        g.drawLine (pos, getSize().height/2, pos, getSize().height);
                        if (markerWidth>300) g.drawString(""+ (startSecond + counter/10.0), pos + 2,  
                            getSize().height - 1);
                    }
                    counter++;
                }
            }
        }

        // seconds
        counter = 0;
        g.setColor(Color.red);
        for(int i=startLoc/res; i < max; i += secInc) { 
            g.drawLine (i, 1, i, getSize().height);
            if (markerWidth>20 && markerWidth <= 300) g.drawString(""+ (startSecond + counter), i+ 2,  
                getSize().height - 1); // single digit
            else if (markerWidth>300) g.drawString(""+(startSecond + counter/ 1.0), i+ 2,  
                getSize().height - 1); // with decimal place
            counter++;
        }
                
    }

    
    // get the position of inital mouse click
	public void mousePressed(MouseEvent e) {
	    //System.out.println("Pressed");
	    this.setCursor(new Cursor(10));
            startX = e.getX();
            tempRes = (double)scrollPanel.getWaveView().getResolution();
	}
	
	//Mouse Listener stubs
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
	    this.setCursor(new Cursor(13));
        scrollPanel.getWaveView().setResolution((int)tempRes);
	}
	//mouseMotionListener stubs
	public void mouseMoved(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
            int dist = 5;
            if (e.getX() > startX + dist) {
                if (tempRes < 8.0) tempRes = Math.round(tempRes / 2.0);
                else tempRes = Math.round(tempRes / 1.1);
                if (tempRes < 1.0) tempRes = 1.0;
                if (tempRes > 2048.0) tempRes = 2048.0; // 8000
                scrollPanel.setResolution((int)Math.round(tempRes));
                startX = e.getX();
                repaint();
            }
            if (e.getX() < startX - dist) {
                if (tempRes < 8.0) tempRes = Math.round(tempRes * 2.0);
                else tempRes = Math.round(tempRes * 1.1);
                if (tempRes < 1.0) tempRes = 1.0;
                if (tempRes > 2048.0) tempRes = 2048.0; // 8000
                scrollPanel.setResolution((int)Math.round(tempRes));
                startX = e.getX();
                repaint();
            }
	}


}