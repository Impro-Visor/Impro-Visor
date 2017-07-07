/**
 * This Java Class is part of the Impro-Visor API.
 *
 * Copyright (C) 2009-2017 Nicolas Froment (aka Lasconic),
 * Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.com;

import imp.Constants;
import imp.data.Part.PartIterator;
import imp.data.*;
import imp.data.musicXML.ChordDescription;
import imp.data.musicXML.Degree;
import imp.util.ErrorLog;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import polya.Polylist;

/**
 * A Command that exports a score to a midi file
 */
public class ExportToMusicXMLCommand implements Command, Constants {
	// The file to export to
	private File file;

	// The score we want to export (contains a midi render that we can parse)
	private Score score;

	public static String FLAT_PITCHES[] = { "C", "D", "D", "E", "F", "F", "G", "G", "A", "A", "B", "C" };
	public static String SHARP_PITCHES[] = { "B", "C", "D", "D", "E", "E", "F", "G", "G", "A", "A", "B" };
	public static String PITCHES[] = { "C", "C", "D", "D", "E", "F", "F", "G", "G", "A", "A", "B" };

	// tuplet handling
	boolean currentlyInTuplet = false;
	Note tupletEndNote;
	Note tupletStartNote;
	int tupletValue;
	int currentUnitTime = 0;

	/**
	 * stores error if exception during save
	 */
	Exception error = null;

	/**
	 * false since this Command cannot be undone
	 */
	private boolean undoable = false;
	private int melodyPartIndex;
	private Transposition transposition;

	/**
	 * Creates a new Command that can save a Score to a File. file the File to
	 * save to score the Score to save
	 */
	public ExportToMusicXMLCommand(File file, Score score, int melodyPartIndex, Transposition transposition) {
		this.file = file;
		this.score = score;
		this.melodyPartIndex = melodyPartIndex;
		this.transposition = transposition;
	}

	public Exception getError() {
		return error;
	}

	// Save the Score to the File
	public void execute() {
		try {
			OutputStream os = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

			write(osw, transposition);

			osw.flush();
			os.flush();
			os.close();
		} catch (IOException e) {
			error = e;
			ErrorLog.log(ErrorLog.WARNING, "Internal Error: " + e);
		}
	}

	// Initialize the render and write the header chunk.
	private void write(OutputStreamWriter osw, Transposition transposition) throws IOException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String title = score.getTitle();
		String composer = score.getComposer();
		int keySig = score.getKeySignature();
		Polylist polylist = score.getLayoutList();
		long barPerLine = 4;
        if( polylist.nonEmpty() ) {
		  Object o = polylist.first();
		  if (o != null & o instanceof Long) {
			barPerLine = (Long) o;
		  }
        }
		int[] metre = score.getMetre();

		osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		osw.write("<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML 2.0 Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">\n");
		osw.write("<score-partwise>\n");
		osw.write("	<movement-title>" + title + "</movement-title>\n");
		osw.write("	<identification>\n");
		osw.write("		<creator type=\"composer\">" + composer + "</creator>\n");
		osw.write("		<rights></rights>\n");
		osw.write("		<encoding>\n");
		osw.write("			<software>Improvisor 4.07</software>\n");
		osw.write("			<encoding-date>" + sdf.format(new Date()) + "</encoding-date>\n");
		osw.write("		</encoding>\n");
		osw.write("		<source></source>\n");
		osw.write("	</identification>\n");
		osw.write("	<part-list>\n");
		osw.write("		<score-part id=\"P1\">\n");
		osw.write("			<part-name></part-name>\n");
		osw.write("			<score-instrument id=\"P1-I3\">\n");
		osw.write("				<instrument-name></instrument-name>\n");
		osw.write("			</score-instrument>\n");
		osw.write("			<midi-instrument id=\"P1-I3\">\n");
		osw.write("				<midi-channel>1</midi-channel>\n");
		osw.write("				<midi-program>0</midi-program>\n");
		osw.write("			</midi-instrument>\n");
		osw.write("		</score-part>\n");
		osw.write("	</part-list>\n");
		osw.write("	<part id=\"P1\">\n");

		MelodyPart mp = score.getPart(melodyPartIndex);
		ChordPart cp = score.getChordProg();

		int top = metre[0];
		int bottom = metre[1];
		int measureLength = 0;
		ArrayList<Unit> list = null;
		if (mp != null && cp != null) {
			mp.makeTies();
			list = (ArrayList<Unit>) mp.getUnitList().clone();
			measureLength = mp.getMeasureLength();
			if (cp != null) {
				// insert chords in melody
				PartIterator i = cp.iterator();
				int current = 0;
				while (i.hasNext()) {
					Unit u = i.next();
					if (u instanceof Chord) {
						// insert at current
						insert(list, current, u);
						current += u.getRhythmValue();
						;
					}
				}
			}
		}

		if (list != null && measureLength != 0) {
			int measureIndex = 1;
			int measureFill = 0;
			String savePitchForTie = null;
			boolean measureWritten = false;
			int unitIndex = 0;
			Note nextNote = null;
			for (Iterator<Unit> iterator = list.iterator(); iterator.hasNext();) {
				if (measureFill == 0) {
					if (!measureWritten) {
						measureWritten = true;
						osw.write("		<measure number=\"" + measureIndex + "\">\n");
						if (measureIndex != 1 && measureIndex % barPerLine == 1) {
							osw.write("		<print new-system=\"yes\"/>\n");
						}
						if (measureIndex == 1) {
							osw.write("			<attributes>\n");
							osw.write("				<divisions>" + 120 + "</divisions>\n");
							osw.write("				<key>\n");
							osw.write("					<fifths>" + keySig + "</fifths>\n");
							osw.write("					<mode>major</mode>\n");
							osw.write("				</key>\n");
							osw.write("				<time>\n");
							osw.write("					<beats>" + top + "</beats>\n");
							osw.write("					<beat-type>" + bottom + "</beat-type>\n");
							osw.write("				</time>\n");
							osw.write("				<clef>\n");
							if (mp.getStaveType() == StaveType.TREBLE) {
								osw.write("					<sign>G</sign>\n");
								osw.write("					<line>2</line>\n");
							} else if (mp.getStaveType() == StaveType.BASS) {
								osw.write("					<sign>F</sign>\n");
								osw.write("					<line>4</line>\n");
							}
							osw.write("				</clef>\n");
							osw.write("			</attributes>\n");
						}
					}
				}

				Unit unit = (Unit) iterator.next();
				// System.out.println(currentUnitTime + " - " + unit);
				if (unit instanceof Note) {
					Note note = (Note) unit;
					int value = note.getRhythmValue();

					if (!currentlyInTuplet)
						tupletHandler(note, mp);
					osw.write("			<note>\n");
					if (note.isRest()) {
						osw.write("			 	<rest/>\n");
					} else {
						if (!note.firstTied() && note.isTied()) {
							osw.write(savePitchForTie);
						} else {

							StringWriter sw = new StringWriter();
							sw.append("			 	<pitch>\n");
							sw.append("			 		<step>" + getPitch(note) + "</step>\n");
							int pitchOctave = note.getPitch();
							if (note.getAccidental() == Accidental.FLAT) {
								sw.append("					<alter>-1</alter>\n");
								pitchOctave++;
							} else if (note.getAccidental() == Accidental.SHARP) {
								sw.append("					<alter>1</alter>\n");
								pitchOctave--;
							}

							sw.append("			 		<octave>" + (pitchOctave / OCTAVE - 1) + "</octave>\n");

							sw.append("			 	</pitch>\n");
							savePitchForTie = sw.toString();
							osw.write(savePitchForTie);
						}
					}

					osw.write("				<duration>" + value + "</duration>\n");
					if (!note.isRest()) {
						if (note.firstTied()) {
							osw.write("				<tie type=\"start\"/>\n");
						} else if (note.isTied()) {
							osw.write("				<tie type=\"stop\"/>\n");
							// if next note also tied --> start
							nextNote = getNextNote(list, unitIndex);
							if (nextNote != null && nextNote.isTied() && !nextNote.firstTied()) {
								osw.write("				<tie type=\"start\"/>\n");
							}
						}
					}

					osw.write("				<voice>1</voice>\n");
					osw.write("				<type>" + getType(value) + "</type>\n");

					int hasDot = getDots(value);
					if (hasDot > 0) {
						for (int i = 0; i < hasDot; i++) {
							osw.write("				<dot/>\n");
						}
					}

					// Need work. Don't output if in key signature
					if (note.getAccidental() == Accidental.FLAT) {
						osw.write("				<accidental>flat</accidental>\n");
					} else if (note.getAccidental() == Accidental.SHARP) {
						osw.write("				<accidental>sharp</accidental>\n");
					} else if (note.getAccidental() == Accidental.NATURAL) {
						osw.write("				<accidental>natural</accidental>\n");
					}

					if (currentlyInTuplet) {
						osw.write("				<time-modification>\n");
						osw.write("					<actual-notes>" + tupletValue + "</actual-notes>\n");
						osw.write("					<normal-notes>2</normal-notes>\n");
						osw.write("				</time-modification>\n");
					}

					// osw.write("				<staff>1</staff>\n");

					boolean notations = false;
					if (note.firstTied() || note.isTied() || note == tupletEndNote || note == tupletStartNote) {
						notations = true;
						osw.write("				<notations>\n");
					}

					if (!note.isRest()) {
						if (note.firstTied()) {
							osw.write("					<tied type=\"start\"/>\n");
						} else if (note.isTied()) {
							osw.write("					<tied type=\"stop\"/>\n");
							if (nextNote != null && nextNote.isTied() && !nextNote.firstTied()) {
								osw.write("					<tied type=\"start\"/>\n");
								nextNote = null;
							}
						}
					}
					if (note == tupletStartNote) {
						osw.write("					<tuplet type=\"start\" bracket=\"yes\"/>\n");
					}
					if (note == tupletEndNote) {
						osw.write("					<tuplet type=\"stop\"/>\n");
					}
					if (notations) {
						osw.write("				</notations>\n");
					}

					osw.write("			</note>\n");
					if (note == tupletEndNote) {
						currentlyInTuplet = false;
					}
					measureFill += value;

					currentUnitTime += value;

				} else if (unit instanceof Chord) {
					Chord chord = (Chord) unit;
					ChordSymbol symbol = chord.getChordSymbol();
					if ("NC".compareTo(symbol.getName()) != 0) {
						PitchClass root = symbol.getRoot();

						osw.write("			<harmony print-frame=\"no\">\n");
						osw.write("				<root>\n");
						osw.write("  				<root-step>" + root.getChordBase().substring(0, 1) + "</root-step>\n");

						if (!root.getNatural()) {
							if (root.getSharp()) {
								osw.write("  				<root-alter>1</root-alter>\n");
							} else {
								osw.write("  				<root-alter>-1</root-alter>\n");
							}
						}
						osw.write("  			</root>\n");
						ChordDescription cd = ChordDescription.descriptions.get(symbol.getType());
						if (cd == null) {
							cd = ChordDescription.DEFAULT;
						}
						osw.write("				<kind text=\"" + symbol.getType() + "\">" + cd.getKind() + "</kind>\n");
						if (cd.degrees != null) {
							for (Degree degree : cd.degrees) {
								osw.write("				<degree>\n");
								osw.write("					<degree-value>" + degree.degreeValue + "</degree-value>\n");
								osw.write("					<degree-alter>" + degree.degreeAlter + "</degree-alter>\n");
								osw.write("					<degree-type>" + degree.degreeType + "</degree-type>\n");
								osw.write("				</degree>\n");
							}
						}

						if (symbol.isSlashChord()) {
							PitchClass bass = symbol.getBass();
							osw.write("  			<bass>\n");
							osw.write("  				<bass-step>" + bass.getChordBase().substring(0, 1) + "</bass-step>\n");
							if (!bass.getNatural()) {
								if (bass.getSharp()) {
									osw.write("  				<bass-alter>1</bass-alter>\n");
								} else {
									osw.write("  				<bass-alter>-1</bass-alter>\n");
								}
							}
							osw.write("  			</bass>\n");
						}
						osw.write("			</harmony>\n");
					} else {
						osw.write("			<direction>\n");
						osw.write("				<direction-type>\n");
						osw.write("					<words default-y=\"100\">NC</words>\n");
						osw.write("				</direction-type>\n");
						osw.write("			</direction>\n");
					}
				}

				if (measureFill == measureLength) {
					measureFill = 0;
					if (currentUnitTime == score.getLength()) {
						osw.write("			<barline location=\"right\">\n");
						osw.write("				<bar-style>light-heavy</bar-style>\n");
						osw.write("			</barline>\n");
					}
					osw.write("		</measure>\n");
					measureWritten = false;
					measureIndex++;
				}
				unitIndex++;
			}
		}

		osw.write("	</part>\n");
		osw.write("</score-partwise>\n");

	}

	private Note getNextNote(ArrayList<Unit> list, int unitIndex) {
		Note n = null;
		for (int i = unitIndex + 1; i < list.size(); i++) {
			Unit u = list.get(i);
			if (u instanceof Note) {
				n = (Note) u;
				break;
			}
		}
		return n;
	}

	public void tupletHandler(Note note, MelodyPart part) {
		int noteValue = note.getRhythmValue();

		if (noteValue == THIRTYSECOND_TRIPLET) {
			currentlyInTuplet = true;
			tupletValue = 3;
			tupletEndNote = part.getNote(currentUnitTime + 2 * THIRTYSECOND_TRIPLET);
		} else if (noteValue == THIRTYSECOND_QUINTUPLET) {
			currentlyInTuplet = true;
			tupletValue = 5;
			tupletEndNote = part.getNote(currentUnitTime + 4 * THIRTYSECOND_QUINTUPLET);
		} else if (noteValue == SIXTEENTH_TRIPLET) {
			currentlyInTuplet = true;
			tupletValue = 3;
			Note n = part.getNote(currentUnitTime + SIXTEENTH);
			tupletEndNote = n != null ? part.getNote(currentUnitTime + SIXTEENTH_TRIPLET) : part.getNote(currentUnitTime + 2 * SIXTEENTH_TRIPLET);
		} else if (noteValue == SIXTEENTH_QUINTUPLET) {
			currentlyInTuplet = true;
			tupletValue = 5;
			tupletEndNote = part.getNote(currentUnitTime + 4 * SIXTEENTH_QUINTUPLET);
		} else if (noteValue == EIGHTH_TRIPLET) {
			currentlyInTuplet = true;
			tupletValue = 3;
			Note n = part.getNote(currentUnitTime + EIGHTH);
			if (n != null) {
				tupletEndNote = part.getNote(currentUnitTime + EIGHTH_TRIPLET);
				if (tupletEndNote.getRhythmValue() == THIRTYSECOND_TRIPLET) {
					tupletEndNote = part.getNote(currentUnitTime + EIGHTH_TRIPLET + THIRTYSECOND_TRIPLET);
				}
			} else {
				tupletEndNote = part.getNote(currentUnitTime + 2 * EIGHTH_TRIPLET);
			}
		} else if (noteValue == EIGHTH_QUINTUPLET) {
			currentlyInTuplet = true;
			tupletValue = 5;
			tupletEndNote = part.getNote(currentUnitTime + 4 * EIGHTH_QUINTUPLET);
		} else if (noteValue == QUARTER_TRIPLET) {
			currentlyInTuplet = true;
			tupletValue = 3;
			Note n = part.getNote(currentUnitTime + QUARTER);
			if (n != null) {
				tupletEndNote = part.getNote(currentUnitTime + QUARTER_TRIPLET);
				if (tupletEndNote.getRhythmValue() == SIXTEENTH_TRIPLET) {
					tupletEndNote = part.getNote(currentUnitTime + QUARTER_TRIPLET + SIXTEENTH_TRIPLET);
				}
			} else {
				tupletEndNote = part.getNote(currentUnitTime + 2 * QUARTER_TRIPLET);
			}
		} else if (noteValue == QUARTER_QUINTUPLET) {
			currentlyInTuplet = true;
			tupletValue = 5;
			tupletEndNote = part.getNote(currentUnitTime + 4 * QUARTER_QUINTUPLET);
		} else if (noteValue == HALF_TRIPLET) {
			currentlyInTuplet = true;
			tupletValue = 3;
			Note n = part.getNote(currentUnitTime + HALF);
			if (n != null) {
				tupletEndNote = part.getNote(currentUnitTime + HALF_TRIPLET);
				if (tupletEndNote.getRhythmValue() == EIGHTH_TRIPLET) {
					tupletEndNote = part.getNote(currentUnitTime + HALF_TRIPLET + EIGHTH_TRIPLET);
				}
			} else {
				tupletEndNote = part.getNote(currentUnitTime + 2 * HALF_TRIPLET);
			}
		}
		if (currentlyInTuplet) {
			tupletStartNote = note;
		}
	}

	private static void insert(ArrayList<Unit> list, int current, Unit c) {

		int count = 0;
		int i = 0;
		for (Iterator<Unit> iterator = list.iterator(); iterator.hasNext();) {
			Unit unit = iterator.next();
			if (unit != null && unit instanceof Note) {
				if (current <= count) {
					list.add(i, c);
					break;
				}
				count += unit.getRhythmValue();
			}
			i++;
		}

	}

	public static String getPitch(Note note) {
		int pitch_within_octave = note.getPitch() % OCTAVE;

		Accidental accidental = note.getAccidental();
		if (accidental == Accidental.SHARP) {
			return SHARP_PITCHES[pitch_within_octave];
		} else if (accidental == Accidental.FLAT) {
			return FLAT_PITCHES[pitch_within_octave];
		} else {
			return PITCHES[pitch_within_octave];
		}
	}

	private static int getDots(int value) {
		int dots = 0;
		if (value == DOTTED_EIGHTH || value == DOTTED_QUARTER || value == DOTTED_HALF || value == DOTTED_SIXTEENTH) {
			return 1;
		}
		return dots;
	}

	static String getType(int value) {
		switch (value) {
		case WHOLE:
			return "whole";
		case HALF:
		case HALF_TRIPLET:
		case DOTTED_HALF:
			return "half";
		case QUARTER:
		case QUARTER_TRIPLET:
		case QUARTER_QUINTUPLET:
		case DOTTED_QUARTER:
			return "quarter";
		case EIGHTH:
		case EIGHTH_TRIPLET:
		case EIGHTH_QUINTUPLET:
		case DOTTED_EIGHTH:
			return "eighth";
		case SIXTEENTH:
		case SIXTEENTH_TRIPLET:
		case SIXTEENTH_QUINTUPLET:
		case DOTTED_SIXTEENTH:
			return "16th";
		case THIRTYSECOND:
		case THIRTYSECOND_TRIPLET:
		case THIRTYSECOND_QUINTUPLET:
			return "32nd";
		default:
			return "quarter";
		}
	}

	/**
	 * Undo unsupported for SaveLeadsheetCommand.
	 */
	public void undo() {
		throw new UnsupportedOperationException("Undo unsupported for Export To MusicXML.");
	}

	/**
	 * Redo unsupported for SaveLeadsheetCommand.
	 */
	public void redo() {
		throw new UnsupportedOperationException("Redo unsupported for Export To MusicXML.");
	}

	public boolean isUndoable() {
		return undoable;
	}
}
