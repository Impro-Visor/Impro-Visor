/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.filters;

import lstm.encoding.Group;
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
