/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

package jm.music.net;

import java.io.IOException;
import java.util.Vector;
import java.net.ServerSocket;
import java.util.Enumeration;

/**
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public class DSServer extends Thread{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** The listener socket for this server. */
	private ServerSocket ss;
	/** A vector holding all current client connections */
	private Vector clientConnections;

	//----------------------------------------------
	// Constructors 
	//----------------------------------------------
	/**
	 * The default JMDistServer is responsible for establishing a 
	 * ServerSocket and a thread responsible for listening for 
	 * ServerSocket accepts.
	 * A default listener port number of 6767 is chosen for client
	 * connections.
	 */
	public DSServer(){
		this(6767);
	}
	
	/**
	 * The default JMDistServer is responsible for establishing a 
	 * ServerSocket and a thread responsible for listening for 
	 * ServerSocket accepts.
	 * @param port the port number for this server to listen on.
	 */
	public DSServer(int port){
		clientConnections = new Vector();
		try{
			this.ss = new ServerSocket(port);
		}catch(IOException ioe){
		}
		this.start();
	}
		
	//----------------------------------------------
	// Public Methods
	//----------------------------------------------
	/**
	 * This threads run method
	 */
	public void run(){
            for(;;){
                try{
                	DSServerConnector dsc = new DSServerConnector(this.ss.accept(),this);
                    clientConnections.addElement(dsc);
                }catch(IOException ioe){
                    //Stuff
                }
            }
	}

	/**
	 * Broadcast updates to all attached clients
	 */
	public void broadCast(Object obj, DSServerConnector conin){
		Enumeration enum1 = clientConnections.elements();
		while(enum1.hasMoreElements()){
			 DSServerConnector con =
				(DSServerConnector)enum1.nextElement();
			if(con != conin) con.sendObject(obj);
		}
	}

	/**
	 * Delete this connection from the list of options
	 */
	public void deleteConnection(DSServerConnector con){
		this.clientConnections.removeElement(con);
		con = null;
	}
}
