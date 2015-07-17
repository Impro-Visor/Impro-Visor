/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2009-2011 Nicolas Froment (aka Lasconic), Robert Keller
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

import imp.util.ErrorLog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author MusicXML routines were contributed by Lasconic (Nicolas Froment) Aug. 15, 2009
 */

public class ChordDescription {
	public String kind;
	public ArrayList<Degree> degrees;

	public static final ChordDescription DEFAULT = new ChordDescription("major", false);

	public static HashMap<String, ChordDescription> descriptions = new HashMap<String, ChordDescription>();

	public static void load(String path) {

		File file = new File(path);
		if (file.exists()) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			try {
				parser = factory.newSAXParser();
				ChordDescriptionHandler handler = new ChordDescriptionHandler();
				parser.parse(file, handler);
				descriptions = handler.getDescriptions();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ErrorLog.log(ErrorLog.SEVERE, "No MusicXML chord description file at " + path);
		}
	}

	public ChordDescription() {

	}

	public ChordDescription(String kind, boolean hasDegrees) {
		this.kind = kind;
		if (hasDegrees) {
			degrees = new ArrayList<Degree>();
		}
	}

	public void addDegree(Degree d) {
		if (degrees == null) {
			degrees = new ArrayList<Degree>();
		}
		degrees.add(d);
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		String newLine = System.getProperty("line.separator");

		StringBuilder sb = new StringBuilder();
		sb.append("kind:").append(kind).append(newLine);
		if (degrees != null) {
			for (Degree d : degrees) {
				sb.append("		").append(d).append(newLine);
			}
		}
		return sb.toString();
	}
}
