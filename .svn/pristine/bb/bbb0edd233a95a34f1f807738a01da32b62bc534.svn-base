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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jm.music.data.Phrase;
/**
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:42:51  2001
 */

public class DSClientConnector extends Thread{
	//----------------------------------------------
	// Attributes 
	//----------------------------------------------
	/** The permanent TCP socket connection to the client. */
	private Socket connection;
	/** Object stream for this connection */
	private ObjectInputStream ois;
	/** Object output stream for this connection */
	private ObjectOutputStream oos;
	/** The Distributed Score Client */
	private DSClient client;

	private Phrase phr = null;

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
	public DSClientConnector(String host, int port, DSClient client){
		try{
			this.connection = new Socket(host, port);
			OutputStream os = this.connection.getOutputStream();
			oos = new ObjectOutputStream(os);
			InputStream is = this.connection.getInputStream();
			ois = new ObjectInputStream(is);
		}catch(IOException ioe){
			System.out.println("The client is having trouble connecting to the specified server.  Please check the server name and port number.");
			System.exit(1);
		}
		this.client = client;
		this.client.setConnection(this);
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
                    client.newObject(ois.readObject());
                }catch(ClassNotFoundException cnfe){
                        //Stuff
                }catch(IOException ioe){
                        //Stuff
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
			//Do stuff
		}
	}
}
