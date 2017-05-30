/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2016-2017 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.lstm.filters;

import imp.lstm.encoding.Group;
import java.util.Random;
import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
 */
public class GroupedSoftMaxSampler implements DataFilter    {
    
    private Group[] groups;
    
    public GroupedSoftMaxSampler(Group[] groups)
    {
        this.groups = groups;
    }
    
    public AVector filter(AVector output)
    {
        int groupKernel = 0;
                for(Group group : groups) {
                    if(group.isOneHot()) {
                        AVector groupData = Operations.Softmax.operate(output.subVector(group.startIndex, group.endIndex - group.startIndex));
                        
                        int index = 0;
                        double randPoint = (new Random()).nextDouble();
                        while(randPoint > 0.0 && index < groupData.length() - 1) {
                            if(groupData.get(index) < randPoint)
                                randPoint -= groupData.get(index++);
                            else
                                randPoint -= groupData.get(index);
                        }
                        for(int j = 0; j < group.length(); j++) {
                            if(j != index) {
                                output.set(groupKernel + j, 0.0);
                            }
                            else {
                                output.set(groupKernel + j, 1.0);
                            }
                        }
                        
                    }
                    else {
                        AVector groupData = Operations.Sigmoid.operate(output.subVector(group.startIndex, group.endIndex - group.startIndex));
                        Random rand = new Random();
                        for(int j = 0; j < group.length(); j++) {
                            double nextDouble = rand.nextDouble();
                            //System.out.println("(" + gOutput[j] + ", " + nextDouble + ")");
                            if(groupData.get(j) >= nextDouble)
                            {
                                output.set(groupKernel + j, 1.0);
                            }
                            else {
                                output.set(groupKernel + j, 0.0);
                            }
                        }
                    }
                    
                    groupKernel += group.length();
                }
                return output;
    }
}
