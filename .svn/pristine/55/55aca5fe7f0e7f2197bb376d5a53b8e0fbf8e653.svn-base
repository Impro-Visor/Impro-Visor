/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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

package imp.roadmap;

import java.awt.Color;

/**
 * Analyzer is a Thread that runs the analysis while the screen displays
 * the raw roadmap.
 * @author keller
 */
public class Analyzer extends Thread
{
    private int Xoffset = 200;
    private int Yoffset = 50;
    
    public static Color cautionColor = new Color(255, 243, 116);
    
    RoadMapFrame frame;
    boolean showJoinsOnCompletion;
    
    public Analyzer(RoadMapFrame frame, boolean showJoinsOnCompletion)
      {
        this.frame = frame;
        
        this.showJoinsOnCompletion = showJoinsOnCompletion;
      }
    
    @Override
    public void run()
      {
        frame.setStatus(" Analyzing: Please wait for OK!");
        frame.setStatusColor(cautionColor);
        frame.analyze(showJoinsOnCompletion);
        frame.setStatus(" OK to Edit");
        frame.setStatusColor(Color.WHITE);
       }
    
    public void cancel()
      {
      }
    
}
