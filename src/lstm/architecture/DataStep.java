/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture;

import mikera.vectorz.AVector;

/**
 *
 * @author cssummer16
 */
public class DataStep {
    
    private String[] names;
    private AVector[] components;
    
    public DataStep() {
    }
    
    public void addNames(String...names)
    {
        this.names = names;
    }
    
    public void addComponents(AVector... components)
    {
        this.components = components;
    }
    
    public void authenticate()
    {
        if(names == null)
            throw new DataStepException("Names of components have not been initialized.");
        else if(components == null)
            throw new DataStepException("Components have not been initialized.");
        else if(names.length > components.length)
            throw new DataStepException("There are more names than components.");
        else if(names.length < components.length)
            throw new DataStepException("There are more components than names");
    }
    
    public AVector get(String componentName)
    {
        for(int i = 0; i < names.length; i++)
        {
            if(names[i].equals(componentName))
                return components[i];
        }
        throw new DataStepException("There is no component with name " + componentName);
    }
}
