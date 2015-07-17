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

package jm.music.data;

import java.io.Serializable;

/**
  * @author Adam Kirby
  * @version $Revision: 1.1.1.1 $, $Date: 2009/05/30 07:56:42 $
  */
 
public abstract class Alignment implements Serializable {
    public static final Alignment START_TOGETHER = 
                new Alignment() {
                        public double determineStartTime(
                                        final double length,
                                        final double anchorST,
                                        final double anchorET) {
                                return anchorST;   
                        }
                };

    public static final Alignment END_TOGETHER = 
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return anchorET - length;   
                            }
                    };

    public static final Alignment AFTER =
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return anchorET;   
                            }
                    };

    public static final Alignment BEFORE =
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return anchorST - length;   
                            }
                    };
                    
    public static final Alignment CENTRE_ALIGN = 
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return (anchorET + anchorST 
                                                     - length) / 2;   
                            }
                    };

    public static final Alignment CENTER_ALIGN = CENTRE_ALIGN;

    public static final Alignment START_ON_CENTRE = 
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return (anchorST + anchorET) 
                                                    / 2;   
                            }
                    };

    public static final Alignment START_ON_CENTER = START_ON_CENTRE;

    public static final Alignment END_ON_CENTRE = 
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return (anchorST + anchorET) / 2 
                                                    - length;   
                            }
                    };
                    
    public static final Alignment END_ON_CENTER = END_ON_CENTRE;                    
                    
    public static final Alignment CENTRE_ON_START = 
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return anchorST - (length / 2); 
                            }
                    };
                    
    public static final Alignment CENTER_ON_START = CENTRE_ON_START;

    public static final Alignment CENTRE_ON_END = 
                    new Alignment() {
                            public double determineStartTime(
                                            final double length,
                                            final double anchorST,
                                            final double anchorET) {
                                    return anchorET - (length / 2); 
                            }
                    };
                    
    public static final Alignment CENTER_ON_END = CENTRE_ON_END;                    

    protected Alignment() {
    }

    abstract double determineStartTime(final double length, 
                                       final double anchorStartTime, 
                                       final double anchorEndTime);
}
	
