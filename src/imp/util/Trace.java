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

package imp.util;

/**
 * Trace is intended as a singleton class.  It includes static
 * methods that can be called from anywhere to indicate the developer
 * significant events during execution.  A trace level from 1-5
 * is set in this class.  If the argument to the trace function
 * is less than or equal to the level, then a message will appear on
 * the standard output.  Thus the higher levels are for more detailed
 * tracing.
 */

public class Trace {

    /** 
      * The current level of trace (can be changed by the developer).
      */

    public static int currentLevel = 0;


    /** tag put in front of trace messages
     */

    public static String tag = ">>> ";


    /**
     * Call to trace a message
     */

    public static void log(int level, String message)
      {
      if( atLevel(level) )
        {
        System.out.println(tag + message);
        }
      }


    /**
     * Call to set trace level
     */

    public static void setLevel(int level)
      {
      currentLevel = level;
      }


    /**
     * Tell whether trace is at or above a specified level
     */

    public static boolean atLevel(int level)
      {
      return currentLevel >= level;
      }
}



