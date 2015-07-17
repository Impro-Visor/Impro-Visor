/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2009 Nicolas Froment (aka Lasconic), Robert Keller,
 * and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

 package imp.data.musicXML;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author MusicXML routines were contributed by Lasconic (Nicolas Froment) Aug. 15, 2009
 */

public class ChordDescriptionHandler extends DefaultHandler {

	private HashMap<String, ChordDescription> descriptions;
	

	private ChordDescription cd;
	private Degree degree;
	private StringBuilder buffer;

	boolean inCds = false;
	boolean inCd = false;
	boolean inDegree = false;

	public static void main(String[] args) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		File file = new File("./chord_musicxml.xml");
		DefaultHandler handler = new ChordDescriptionHandler();
		parser.parse(file, handler);

	}
	
	public HashMap<String, ChordDescription> getDescriptions() {
		return descriptions;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String read = new String(ch, start, length);
		if (buffer != null)
			buffer.append(read);
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (name.equals("cds")) {
			descriptions = new HashMap<String, ChordDescription>();
			inCds = true;
		} else if (name.equals("cd")) {
			cd = new ChordDescription();
			inCd = true;

		} else if (name.equals("degree")) {
			degree = new Degree();
			inDegree = true;
		} else {
			buffer = new StringBuilder();
			if (name.equals("name")) {
				// inNom = true;
			} else if (name.equals("kind")) {
				// inPrenom = true;
			} else if (name.equals("value")) {
				// inAdresse = true;
			} else if (name.equals("alter")) {
			} else if (name.equals("type")) {
			} else {

				throw new SAXException(name + " unknown.");
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (name.equals("cds")) {
			inCds = false;
		} else if (name.equals("cd")) {
			cd = null;
			inCd = false;
		} 
		else if (name.equals("degree")) {
			cd.addDegree(degree);
			degree = null;
			inDegree = false;
		} 
		else if (name.equals("name")) {
			descriptions.put(buffer.toString(), cd);
			buffer = null;
		} else if (name.equals("kind")) {
			cd.setKind(buffer.toString());
			buffer = null;
		} else if (name.equals("value")) {
			degree.degreeValue = buffer.toString();
			buffer = null;
		} else if (name.equals("alter")) {
			try {
				int d = Integer.parseInt(buffer.toString());
				degree.degreeAlter = d;
			} catch (NumberFormatException nfe) {

			}
			buffer = null;
		} else if (name.equals("type")) {
			degree.degreeType = buffer.toString();
			buffer = null;
		} else {
			throw new SAXException(name + " unknown.");
		}
	}
}
