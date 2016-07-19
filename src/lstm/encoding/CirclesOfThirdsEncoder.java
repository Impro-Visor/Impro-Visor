/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.encoding;


import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Class CirclesOfThirdsEncoder describes encoding notes using a method described in Judy Franklin's paper on lstm-improvised music
 * @author Nicholas Weintraut
 */
public class CirclesOfThirdsEncoder implements NoteEncoder {

    private Group[] groups;
    private Group majorGroup;
    private Group minorGroup;
    private Group octaveGroup;
    private CirclePair[] pitchData;
    private int articulateIndex;
    private int sustainIndex;
    private int restIndex;
    private int sustainKey;
    
    private class CirclePair
    {
        public final int major;
        public final int minor;
        public CirclePair(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }
    }
    
    public CirclesOfThirdsEncoder() {
        majorGroup = new Group(3, 7, true);
        minorGroup = new Group(7, 10, true);
        octaveGroup = new Group(10, 13, true);
        groups = new Group[]{new Group(0,3,true), majorGroup, minorGroup, octaveGroup};
        pitchData = new CirclePair[]    {
            new CirclePair(0,0),    //C
            new CirclePair(1,1),
            new CirclePair(2,2),
            new CirclePair(3,0),
            new CirclePair(0,1),
            new CirclePair(1,2),
            new CirclePair(2,0),
            new CirclePair(3,1),
            new CirclePair(0,2),
            new CirclePair(1,0),
            new CirclePair(2,1),
            new CirclePair(3,2)     //B
        };
        sustainKey = -2;
        articulateIndex = 2;
        restIndex = 0;
        sustainIndex = 1;
    }
    
    public int getSustainKey()
    {
        return sustainKey;
    }
    
    @Override
    public AVector encode(int midiValue) {
        AVector output = Vector.createLength(getNoteLength());
        if(midiValue == sustainKey)
        {
            output.set(sustainIndex, 1.0);
        }
        else if(midiValue == -1)
            output.set(restIndex, 1.0);
        else
        {
            output.set(articulateIndex, 1.0);
            int noteIndex = midiValue % 12;
            int octaveIndex = midiValue / 12 - 4;
            CirclePair circleIndexes = pitchData[noteIndex];
            output.set(majorGroup.startIndex + circleIndexes.major, 1.0);
            output.set(minorGroup.startIndex + circleIndexes.minor, 1.0);
            output.set(octaveGroup.startIndex + ((octaveIndex > 2) ? 2 : octaveIndex), 1.0);
        }
        return output;
    }
    
    @Override
    public boolean hasSustain(AVector input)
    {
        return input.get(sustainIndex) == 1.0;
    }

    @Override
    public int decode(AVector input) {
        if(input.get(restIndex) == 1.0)
            return -1;
        else
        {
            int pitchIndex = 0;
            for(; pitchIndex < pitchData.length; pitchIndex++)
            {
                CirclePair pair = pitchData[pitchIndex];
                if(input.get(majorGroup.startIndex + pair.major) == 1.0 && input.get(minorGroup.startIndex + pair.minor) == 1.0)
                    break;
            }
            int octaveIndex = 0;
            for(; octaveIndex < octaveGroup.length(); octaveIndex++)
            {
                if(input.get(octaveGroup.startIndex + octaveIndex) == 1.0)
                    break;
            }
            return ((octaveIndex + 4) * 12) + pitchIndex;
        }
                
    }

    @Override
    public Group[] getGroups() {
        return groups;
    }

    @Override
    public AVector clean(AVector input) {
        if(!(input.get(articulateIndex) == 1.0))
        {
            for(Group group : new Group[]{majorGroup, minorGroup, octaveGroup})
                for(int i = group.startIndex; i < group.endIndex; i++)
                    input.set(i, 0.0);
        }
        return input;
    }
    
}
