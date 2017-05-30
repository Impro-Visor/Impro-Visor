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

package imp.lstm.architecture;

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
