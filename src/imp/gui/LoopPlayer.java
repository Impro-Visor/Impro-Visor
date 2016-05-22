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

import imp.style.stylePatterns.Playable;

/**
 *
 * @author keller
 */
public class LoopPlayer implements Runnable
{

private Playable playable;

private double swingValue = 0.5;

long msGap = 0;

boolean play = true;

public LoopPlayer(Playable playable, double swingValue, long msGap)
  {
    this.playable = playable;
    this.swingValue = swingValue;
    this.msGap = msGap;
    this.play = true;
  }

public void setPlayable(Playable playable)
  {
    this.playable = playable;
    setPlaying(true);
  }

public void setGap(long msGap)
  {
    this.msGap = msGap;
  }

public void setPlaying(boolean play)
  {
    this.play = play;
    if( !play )
      {
        playable.stopPlaying();
      }
  }

public void run()
  {
    //System.out.println("go");
    while( true )
      {
        if( play )
          {
            playable.playMe(swingValue);
          }
        try
          {
            Thread.sleep(msGap);
          }
        catch( Exception e )
          {
          }

      }

  }

}
