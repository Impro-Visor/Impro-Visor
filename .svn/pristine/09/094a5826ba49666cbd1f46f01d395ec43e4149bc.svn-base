
package jm.audio.synth;

import java.awt.*;
import java.awt.event.*;

public class SpringPipe {
    
	/** totalLength of network */
	int totalLength = 500;
	/** mass width */
	double width = 1.0;
	/** pluck strength */
	double pluckAmt = 1.0; // from 0.1 - 1.0
    
    private SpringObject[] springObjectArray;
    private MassObject[] massObjectArray;    
    
	//constructor
    public SpringPipe(int nodeNumb, double springConstant, double friction, double jitter) {
    	// set up mass and spring objects
    	SpringObject[] tempS = new SpringObject[nodeNumb+1];
    	MassObject[]  tempM = new MassObject[nodeNumb];
    	int springLength = (int)(totalLength - (nodeNumb * width)) / (nodeNumb + 1);
    	for(int i=0; i< nodeNumb; i++){
    		tempS[i] = new SpringObject(springConstant); // * (1.0 + (Math.random() * jitter - jitter/2)));
    		tempS[i].setRestingLength(springLength);
    		tempM[i] = new MassObject(friction, 1.0 + (Math.random() * jitter - jitter/2)); //size
    		tempM[i].setYPosition((double)springLength * (i+1.0)  + width * i);
    	}
    	tempS[nodeNumb] = new SpringObject();
    	tempS[nodeNumb].setRestingLength(springLength);
    	// set first mass initial displacement
    	tempM[0].setYPosition(tempM[0].getYPosition()  -  tempM[0].getYPosition() * pluckAmt); //0.0); // start with excitation - one node displaced
    	// transfer to class variables
    	springObjectArray = tempS;
    	massObjectArray = tempM;
    }
	
	// runs each iteration of time
    private void updateSpringMassNetwork(){
    	// get current spring tension
    	double[] forceArray = new double[springObjectArray.length];
    	forceArray[0] = springObjectArray[0].getCurrentForce(0, massObjectArray[0].getYPosition());
    	for (int i=1; i< massObjectArray.length; i++) {
       		forceArray[i] = springObjectArray[i].getCurrentForce( massObjectArray[i-1].getYPosition() + width, massObjectArray[i].getYPosition());
       	}
       	// final spring
       	forceArray[forceArray.length - 1] = springObjectArray[forceArray.length - 1].
				getCurrentForce(massObjectArray[massObjectArray.length-1].getYPosition() + 
				width, totalLength);
        // get next position for the mass
        for(int i=0; i<massObjectArray.length; i++) {
            massObjectArray[i].setYPosition( massObjectArray[i].getYPosition() + 
            	massObjectArray[i].getDisplacement(forceArray[i] - forceArray[i+1]));
        }
    }
    
    /** Get the next mass position location */
    public double getNextNodePosition(int nodeIndex) {
    	updateSpringMassNetwork();
    	return massObjectArray[nodeIndex].getYPosition();
   	}    
}
