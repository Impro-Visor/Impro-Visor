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
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public class DSServerConnector extends Thread{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** The permanent TCP socket connection to the client. */
	private Socket connection;
	/** Object stream for this connection */
	private ObjectInputStream ois;
	/** Object output stream for this connection */
	private ObjectOutputStream oos;
	/** The JMDistServer */
	private static DSServer server;
	/** Object */
	private Object obj;

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
	public DSServerConnector(Socket connection, DSServer server){
		this.server = server;
		this.connection = connection;
		try{
			OutputStream os = this.connection.getOutputStream();
			oos = new ObjectOutputStream(os);
			InputStream is = this.connection.getInputStream();
			ois = new ObjectInputStream(is);
			System.out.println(connection);
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
				Object obj = ois.readObject();
				server.broadCast(obj,this);
			}catch(ClassNotFoundException cnfe){
				//Stuff
				System.out.println(cnfe);
			}catch(IOException ioe){
				System.out.println(ioe);
				server.deleteConnection(this);
				break;
			}
		}
	}

	/**
	 * Send an updated phrase to this connections client.
	 */
	public void sendObject(Object obj){
		try{
			oos.writeObject(obj);
		}catch(IOException ioe){
			server.deleteConnection(this);
		}
	}
}
