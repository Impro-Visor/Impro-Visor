/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.architecture;

import java.util.ArrayList;
import java.util.Collections;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Class FragmentedNeuralQueue is an implementation of a fragmented neural queue simplified for operation (cannot be used for training) of a CompressingAutoencoder
 * A fragmented neural queue is similar to a neural queue as proposed by Google DeepMind, but separates enqueueing and dequeueing operations
 * by means of vector combination in the scalar space of vectors' assigned strengths.
 * Wow. Thats the fancy definition. Really what this means is that unlike a neural queue, which pushes and pops portions of vectors over the entire queue,
 * this fragmented queue design, on dequeue, combines vectors from the front of the queue until the total strength is 1.0, and then pops off only the first vector.
 * It makes for a very fun interpolation between recognized features, and allows for decoding to start before an entire dataset is encoded.
 * 
 * @author Nicholas Weintraut
 */
public class FragmentedNeuralQueue {
    private LinkedList<AVector> vectorList; //[index][vectorIndex]
    private LinkedList<Double> strengthList; //[index]
    private double fragmentStrength;
    private Double totalStrength;
    
    /**
     * Initializes FragmentedNeuralQueue instance with the given input vector size and a fragment strength of 1.
     */
    public FragmentedNeuralQueue()
    {
        this(1);
    }
   
    /**
     * Initializes FragmentedNeuralQueue instance with the given input vector size and fragment strength
     * @param fragmentStrength 
     */
    public FragmentedNeuralQueue(double fragmentStrength)
    {
        vectorList = new LinkedList<AVector>(); 
        strengthList = new LinkedList<Double>();
        this.fragmentStrength = fragmentStrength;
        totalStrength = 0.0;
    }
    
    public boolean isEmpty()
    {
        return vectorList.isEmpty();
    }
    
    /**
     * Perform the next timeStep iteration of the neural queue without dequeueing
     * @param inputVector
     * @param enqueueSignal

     */
    public void enqueueStep(AVector inputVector, double enqueueSignal)
    {
        //System.out.println("are we gonna do it");
        //add inputVector to valueMatrix
        //System.out.println(inputVector == null);
        vectorList.add(inputVector);
        //System.out.println("hurr");
        //System.out.println("we added column vector, now lets add an enqueueSignal");
        //we won't update our strengthVector other than adding enqueueSignal
       // System.out.println(strengthList == null);
        
        strengthList.add(enqueueSignal);
        
        totalStrength += enqueueSignal;
    }
    
    public void shuffleVectors()
    {
        Collections.shuffle(vectorList);
    }
    
    public void halfAndHalfQueue()
    {
        for(int i = 0; i < strengthList.size()/2; i++)
        {
            strengthList.offer(strengthList.poll());
            vectorList.offer(vectorList.poll());
        }
    }
    
    public void shuffleQueue()
    {
        Random rand = new Random();
        List<Double> strengths = (List<Double>) strengthList;
        List<AVector> vectors = (List<AVector>) vectorList;
        for(int i = 0; i < strengths.size(); i++)
        {
            int sourceIndex = rand.nextInt(strengths.size());
            int destIndex = rand.nextInt(strengths.size());
            AVector tempVector = vectors.get(destIndex);
            Double tempStrength = strengths.get(destIndex);
            strengths.set(destIndex, strengths.get(sourceIndex));
            vectors.set(destIndex, vectors.get(sourceIndex));
            strengths.set(sourceIndex, tempStrength);
            vectors.set(sourceIndex, tempVector);
        }
    }
    
    
    
    /**
     * Based on vector strengths, returns lists of contiguous vectors that represent musical "features" as recognized by the encoder.
     * @return List of lists of indexes that point to paired vectors and strengths in the queue
     */
    public List<List<Integer>> findFeatureGroups()
    {
        double threshold = 0.6;
        List<List<Integer>> featureGroups = new ArrayList<>();
        featureGroups.add(new ArrayList<>());
        Iterator<Double> strengthIterator = strengthList.iterator();
        boolean filledGroup = false;
        int strengthIndex = 0;
        while(strengthIterator.hasNext()) {
            Double strength = strengthIterator.next();
            if(filledGroup && strength < threshold)
            {
                featureGroups.add(new ArrayList<>());
                filledGroup = false; 
            }
            featureGroups.get(featureGroups.size() - 1).add(strengthIndex);
            if(strength >= threshold) {
                filledGroup = true;
            }
            strengthIndex++;
        }
        System.out.println(featureGroups.size() + " feature groups found");
        System.out.println((strengthList.size() / featureGroups.size()) + " timeSteps per feature group(rounded down)");
        return featureGroups;
    }
    
    public void printFeatureGroups()
    {
        for(List<Integer> group : findFeatureGroups()) {
            double maxStrength = 0;
            int maxIndex = 0;
            for(int i = 0; i < group.size(); i++) {
                if(strengthList.get(group.get(i)) > maxStrength) {
                    maxStrength = strengthList.get(group.get(i));
                    maxIndex = group.get(i);
                }
            }
            System.out.println(vectorList.get(maxIndex) + " with strength " + maxStrength + " at timeStep " + maxIndex);
        }
    }
    
    public void testFill()
    {
        int size = strengthList.size();
        int vectorSize = vectorList.get(0).length();
        strengthList.clear();
        vectorList.clear();
        int bitIndex = 0;
        for(int i = 0; i < size - 1; i++)
        {
            if(i % 48 == 0)
            {
                AVector v = Vector.createLength(vectorSize);
                v.set(bitIndex++ % v.length(), 1.0);
                vectorList.add(v);
                strengthList.add(1.0);
            }
            else
            {
                vectorList.add(Vector.createLength(vectorSize));
                strengthList.add(0.0);
            }
        }
        AVector v = Vector.createLength(vectorSize);
        v.set(bitIndex % v.length(), 1.0);
        vectorList.add(v);
        strengthList.add(1.0);
    }
    
    
    public void shuffleFeatures(boolean shouldPermutate, boolean favorRecognition)
    {
        List<List<Integer>> featureGroups = findFeatureGroups();
        List<List<Integer>> shuffledGroups = new ArrayList<>();
        LinkedList<Double> newStrengths = new LinkedList<>();
        LinkedList<AVector> newVectors = new LinkedList<>();
        Random rand = new Random();
        if(shouldPermutate) {
            shuffledGroups.addAll(featureGroups);
            Collections.shuffle(shuffledGroups);
        }
        /*
        else if(favorRecognition) {
            List<Double> groupMaxStrengths = new ArrayList<>();
            double totalMaxStrength = 0.0;
            for(List<Integer> group :  featureGroups) {
                double maxStrength = 0.0;
                for(Integer i : group) {
                    if(strengthList.get(i) > maxStrength)
                        maxStrength = strengthList.get(i);
                }
                totalMaxStrength += maxStrength;
                groupMaxStrengths.add(maxStrength);
            }
            int size = 0;
            boolean hasEnough = false;
            //unfinished
            double randIndex = rand.nextDouble() * totalMaxStrength;
            Iterator<Double> groupStrengthIterator = groupMaxStrengths.iterator();
            while(!hasEnough)
            {
                boolean foundLargestFit = false;
                while(!foundLargestFit)
                {
                    //
                }
            }
        }*/
        else {
            for(int i = 0; i < featureGroups.size(); i++) {
                shuffledGroups.add(featureGroups.get(rand.nextInt(featureGroups.size())));
            }
        }
        
        for(List<Integer> group : shuffledGroups) {
            for(Integer index : group) {
                newStrengths.add(strengthList.get(index));
                newVectors.add(vectorList.get(index).copy());
            }
        }
        strengthList = newStrengths;
        vectorList = newVectors;
    }
    
    public void shuffleStrengths()
    {
        Collections.shuffle(strengthList);
    }
    
    /**
     * 
     * @return Does the queue have 
     */
    public boolean hasFullBuffer()
    {
        return totalStrength >= fragmentStrength;
    }
    
    /**
     * Read vectors scaled by their strengths until we fill the fragment, then return the fragment.
     * A vector is scaled by a portion of their strength if their strength would overfill the fragment.
     * @return The sampled vector
     */
    public AVector peek()
    {
        if(!vectorList.isEmpty())
        {
        //lets start generating the readVector
        AVector readVector = Vector.createLength(vectorList.peek().length());
        
        double strengthSum = 0.0;
        
        Iterator<AVector> vectorIterator = vectorList.iterator();
        Iterator<Double> strengthIterator = strengthList.iterator();
        //while we have not reached the strength limit for our fragment
        while(strengthSum < fragmentStrength && vectorIterator.hasNext())
        {
            AVector currVector = vectorIterator.next();
            //System.out.println(currVector.length());
            double currStrength = strengthIterator.next();
            //if our strengthSum would exceed our fragment strength, only take the portion needed to fill it
            if(strengthSum + currStrength > fragmentStrength)
            {
                AVector currVectorCopy = currVector.copy();
                currVectorCopy.multiply(fragmentStrength - strengthSum);
                readVector.add(currVectorCopy);
                strengthSum = fragmentStrength;
            }
            //if we have space left, we'll simply multiply the current vector by it's scale and add it.
            else
            {
                AVector currVectorCopy = currVector.copy();
                currVectorCopy.multiply(currStrength);
                readVector.add(currVectorCopy);
                strengthSum += currStrength;
            }
        }
            return readVector;
        }
        throw new RuntimeException("The neural queue is empty!!");
    }
    
    /**
     * Removes an element of the queue in first-in, first-out order.
     */
    public AVector dequeueStep()
    {
        AVector result = this.peek();
        vectorList.remove(0);
        totalStrength -= strengthList.remove(0);
        return result;
    }
}
