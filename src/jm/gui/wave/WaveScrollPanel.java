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
public class WaveScrollPanel extends Panel implements ActionListener, AdjustmentListener {
        private Label name, bitSize, sampleRate, channels, resLable;
        private Button minus, plus, play, stop;
        private WaveView viewer;
        private WaveRuler ruler = new WaveRuler();
        private Panel resizePanel;
        private Scrollbar scroll = new Scrollbar(Scrollbar.HORIZONTAL);
        private Font font = new Font("Helvetica", Font.PLAIN, 10);
        
	public WaveScrollPanel() {
		setBackground(Color.lightGray);
        this.setLayout(new BorderLayout());
        resizePanel = new Panel();
        resLable = new Label("Display Resolution: 1:"+ 0);
        resLable.setFont(font);
        resizePanel.add(resLable);
        minus = new Button("-");
        minus.addActionListener(this);
        resizePanel.add(minus);
        plus = new Button("+");
        plus.addActionListener(this);
        resizePanel.add(plus);
        this.add(resizePanel, "East");
        // Info
        Panel infoPanel = new Panel();
        play = new Button("Play");
        play.addActionListener(this);
        infoPanel.add(play);
        stop = new Button("Stop");
        stop.setEnabled(false);
        stop.addActionListener(this);
        infoPanel.add(stop);
        name = new Label();
        name.setFont(font);
        infoPanel.add(name);
        bitSize = new Label();
        bitSize.setFont(font);
        infoPanel.add(bitSize);
        sampleRate = new Label();
        sampleRate.setFont(font);
        infoPanel.add(sampleRate);
        channels = new Label();
        channels.setFont(font);
        infoPanel.add(channels);
        this.add(infoPanel, "West");
        // scroll bar
        scroll.addAdjustmentListener(this);
        this.add(scroll, "South");
        //ruler
        ruler.setWaveScrollPanel(this);
        this.add(ruler, "North");
	}
        
        /*
        * Inform this object of its containing object
        * in order to pass actions back to it.
        * @param viewer A WaveView instance
        */
        public void setViewer(WaveView viewer) {
            this.viewer = viewer;
            setResolution(viewer.getResolution());
        }
        
        /*
        * Specify or update the scrollbar parrameters.
        * Used when a new file is read.
        * @param int waveSize The number of samples in a channel
        * @param int frameWidth The width of the current display area.
        */
        public void setScrollbarAttributes(int waveSize, int frameWidth, int resolution) {
            scroll.setUnitIncrement(1000);
            scroll.setBlockIncrement(frameWidth * resolution / 2);
            scroll.setMinimum(0);
            scroll.setMaximum(waveSize * 2);
            scroll.setVisibleAmount(frameWidth * resolution);
        }
        
        /*
        * Resize the scroll bar thumb when display resolution varies.
        */
        public void setScrollbarResolution(int resolution) {
            if (viewer != null) {
                scroll.setVisibleAmount(viewer.getWidth() * resolution);
                scroll.setBlockIncrement(viewer.getWidth() * resolution / 2);
            } else {
                scroll.setVisibleAmount(800 * 256);
                scroll.setBlockIncrement(800 * 256 / 2);
            }
        }
        
        /*
        * Update the scroll bar thumb position.
        * @param newValue The new scrollbar value.
        */
        public void setScrollbarValue(int newValue) {
            scroll.setValue(newValue);
        }
        
        /*
        * Notify panel of a new screen resolution value.
        */
        public void setResolution(int res) {
            String resStr = new String("Display Resolution = 1:"+res);
            if(res < 1000) resStr += "  "; // spacing
            resLable.setText(resStr);
            ruler.setMarkerWidth(viewer.getSampleRate() / res);
            repaint();
        }
        
        /*
        * Specify the current file name to display in the scroll panel
        */
        public void setFileName(String fileName) {
            name.setText("File = " + fileName + ". ");
            repaint();
        }
        
        /*
        * Specify the current sample bit size to display in the scroll panel
        */
        public void setBitSize(int bits) {
            bitSize.setText("Bit Depth = " + bits + ". ");
            repaint();
        }
        
        /*
        * Specify the current sample rate to display in the scroll panel
        */
        public void setSampleRate(int rate) {
            sampleRate.setText("Sample Rate = " + rate + ". ");
            repaint();
        }
        
        
        /*
        * Specify the current number of channels to display in the scroll panel
        */
        public void setChannels(int chan) {
            channels.setText("Channels = " + chan + ". ");
            repaint();
        }
        
        /*
        * Send back the current ruler object.
        */
        public WaveRuler getWaveRuler() {
            return ruler;
        }
        
        /*
        * Send back the current wave viewer instance.
        */
        public WaveView getWaveView() {
            return viewer;
        }

    /**
    * Handle button clicks.
    */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == minus) {
            int res = viewer.getResolution();
            if (res > 0 && res <= 1024) { //4000
                res *= 2;
                this.setResolution(res);
                //ruler.setMarkerWidth(viewer.getSampleRate() / res);
                plus.setEnabled(true);
                if (res > 1024) minus.setEnabled(false); //4000
                viewer.setResolution(res);
            }  
        }
        if(e.getSource() == plus) {
            int res = viewer.getResolution();
            if (res > 1) {
                res /= 2;
                this.setResolution(res);
                //ruler.setMarkerWidth(viewer.getSampleRate() / res);
                minus.setEnabled(true);
                if (res < 2) plus.setEnabled(false);
                viewer.setResolution(res);
            }  
        }
        if(e.getSource() == play) {
            stop.setEnabled(true);
            viewer.playFile();
        }
        if(e.getSource() == stop) {
            viewer.pauseFile();
            stop.setEnabled(false);
        }
	}
         
         
    // adjustable interface methods
    
    public void adjustmentValueChanged(AdjustmentEvent e) {
        viewer.setStartPos(scroll.getValue());
        ruler.repaint();
    }
}