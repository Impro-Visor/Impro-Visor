/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

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
package jm.util;

import jm.JMC;
import jm.music.rt.RTLine;
import jm.audio.Instrument;
import jm.music.data.Note;

public class AudioRTLine extends RTLine {
    private boolean firstTime = true;
    
    public AudioRTLine (String fileName) {
            super(new Instrument[] {new AudioSampleInst(fileName)});
    }
    
    public synchronized Note getNextNote() {
        // duration and pitch are disregarded by the instrument
        Note n;
        if(firstTime) {
            n = new Note(67, 1.0);
            firstTime = false;
        } else n = new Note(jm.JMC.REST, 1.0);
        return n;
    }
}