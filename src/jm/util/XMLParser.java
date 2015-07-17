/*
 * XMLParser.java 0.2 30th June 2002
 *
 * Copyright (C) 2002 Adam Kirby
 *
 * <This Java Class is part of the jMusic API version 1.5, March 2004.>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.util;

import java.util.Enumeration;
import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

/**
  *
  * @author Adam Kirby
  * @version 0.2, 30th June 2002
  */
class XMLParser {
    
    private XMLParser() {
    }

    private static final XMLStyle DEFAULT_XML_STYLE =
            new StandardXMLStyle();

    public static String scoreToXMLString(final Score score) {
        return DEFAULT_XML_STYLE.initialXMLDeclaration()
               + XMLParser.scoreToXMLString(score, DEFAULT_XML_STYLE);
    }

    private static String scoreToXMLString(
                final Score score,
                final XMLStyle xmlStyle) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(
                xmlStyle.getLeftAngleBracket() + xmlStyle.getScoreTagName());
        if (! score.getTitle().equals(Score.DEFAULT_TITLE)) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getTitleAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote());
            String title = score.getTitle();
            for (int i = 0; i < title.length(); i++) {
                char character = title.charAt(i);
                if (character == ' ') {
                    buffer.append(xmlStyle.getSpace());
                } else if (character == '/') {
                    buffer.append(xmlStyle.getSlash());
                } else if (character == '&') {
                    buffer.append(xmlStyle.getAmpersandInString());
                } else if (character == '<') {
                    buffer.append(xmlStyle.getLeftAngleBracketInString());
                } else if (character == '>') {
                    buffer.append(xmlStyle.getRightAngleBracketInString());
                } else if (character == '"') {
                    buffer.append(xmlStyle.getDoubleQuoteInString());
                } else if (character == '#') {
                    buffer.append(xmlStyle.getHash());
                } else if (character == '/') {
                    buffer.append(xmlStyle.getSlash());
                } else if (character == '?') {
                    buffer.append(xmlStyle.getQuestionMark());
                } else if (character == ';') {
                    buffer.append(xmlStyle.getSemicolon());
                } else {
                    buffer.append(character);
                }
            }
            buffer.append(xmlStyle.getDoubleQuote());
        }
        if (score.getTempo() != Score.DEFAULT_TEMPO) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getTempoAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(score.getTempo(), 2)
                        : Double.toString(score.getTempo()))
                    + xmlStyle.getDoubleQuote());
        }
        if (score.getKeySignature() != Score.DEFAULT_KEY_SIGNATURE) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getKeySignatureAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(score.getKeySignature())
                    + xmlStyle.getDoubleQuote());
        }
        if (score.getKeyQuality() != Score.DEFAULT_KEY_QUALITY) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getKeyQualityAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(score.getKeyQuality())
                    + xmlStyle.getDoubleQuote());
        }
        if (score.getNumerator() != Score.DEFAULT_NUMERATOR) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getNumeratorAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(score.getNumerator())
                    + xmlStyle.getDoubleQuote());
        }
        if (score.getDenominator() != Score.DEFAULT_DENOMINATOR) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getDenominatorAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(score.getDenominator())
                    + xmlStyle.getDoubleQuote());
        }
        int size = score.size();
        if (size == 0) {
            buffer.append(
                    xmlStyle.getSlash() + xmlStyle.getRightAngleBracket());
        } else {
            buffer.append(xmlStyle.getRightAngleBracket());
            for (int i = 0; i < score.size(); i++) {
                buffer.append(partToXMLString(score.getPart(i), xmlStyle));
            }
            buffer.append(
                    xmlStyle.getLeftAngleBracket()
                    + xmlStyle.getSlash()
                    + xmlStyle.getScoreTagName()
                    + xmlStyle.getRightAngleBracket());
        }
        return buffer.toString();            
    }

    public static String partToXMLString(final Part part) {
        return DEFAULT_XML_STYLE.initialXMLDeclaration()
               + XMLParser.partToXMLString(part, DEFAULT_XML_STYLE);
    }

    private static String partToXMLString(
                final Part part,
                final XMLStyle xmlStyle) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(
                xmlStyle.getLeftAngleBracket() + xmlStyle.getPartTagName());
        if (! part.getTitle().equals(Part.DEFAULT_TITLE)) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getTitleAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote());
            String title = part.getTitle();
            for (int i = 0; i < title.length(); i++) {
                char character = title.charAt(i);
                if (character == ' ') {
                    buffer.append(xmlStyle.getSpace());
                } else if (character == '/') {
                    buffer.append(xmlStyle.getSlash());
                } else if (character == '&') {
                    buffer.append(xmlStyle.getAmpersandInString());
                } else if (character == '<') {
                    buffer.append(xmlStyle.getLeftAngleBracketInString());
                } else if (character == '>') {
                    buffer.append(xmlStyle.getRightAngleBracketInString());
                } else if (character == '"') {
                    buffer.append(xmlStyle.getDoubleQuoteInString());
                } else if (character == '#') {
                    buffer.append(xmlStyle.getHash());
                } else if (character == '?') {
                    buffer.append(xmlStyle.getQuestionMark());
                } else if (character == ';') {
                    buffer.append(xmlStyle.getSemicolon());
                } else {
                    buffer.append(character);
                }
            }
            buffer.append(xmlStyle.getDoubleQuote());
        }
        if (part.getChannel() != Part.DEFAULT_CHANNEL) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getChannelAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(part.getChannel())
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getInstrument() != Part.DEFAULT_INSTRUMENT) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getInstrumentAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(part.getInstrument())
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getTempo() != Part.DEFAULT_TEMPO) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getTempoAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(part.getTempo(), 2)
                        : Double.toString(part.getTempo()))
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getKeySignature() != Part.DEFAULT_KEY_SIGNATURE) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getKeySignatureAttributeName()
                    + "=" 
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(part.getKeySignature())
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getKeyQuality() != Part.DEFAULT_KEY_QUALITY) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getKeyQualityAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(part.getKeyQuality())
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getNumerator() != Part.DEFAULT_NUMERATOR) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getNumeratorAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(part.getNumerator())
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getDenominator() != Part.DEFAULT_DENOMINATOR) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getDenominatorAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(part.getDenominator()) 
                    + xmlStyle.getDoubleQuote());
        }
        if (part.getPan() != Part.DEFAULT_PAN) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getPanAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(part.getPan(), 2)
                        : Double.toString(part.getPan()))
                    + xmlStyle.getDoubleQuote());
        }
        int size = part.size();
        if (size == 0) {
            buffer.append(
                    xmlStyle.getSlash() + xmlStyle.getRightAngleBracket());
        } else {
            buffer.append(xmlStyle.getRightAngleBracket());
            for (int i = 0;  i < part.size(); i++) {
                buffer.append(phraseToXMLString(part.getPhrase(i), xmlStyle));
            }
            buffer.append(
                    xmlStyle.getLeftAngleBracket() + xmlStyle.getSlash()
                    + xmlStyle.getPartTagName()
                    + xmlStyle.getRightAngleBracket());
        }
        return buffer.toString();
    }

    public static String phraseToXMLString(final Phrase phrase) {
        return DEFAULT_XML_STYLE.initialXMLDeclaration() 
               + XMLParser.phraseToXMLString(phrase, DEFAULT_XML_STYLE);
    }

    private static String phraseToXMLString(
                final Phrase phrase,
                final XMLStyle xmlStyle) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(
                xmlStyle.getLeftAngleBracket() + xmlStyle.getPhraseTagName());
        if (! phrase.getTitle().equals(Phrase.DEFAULT_TITLE)) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getTitleAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote());
            String title = phrase.getTitle();
            for (int i = 0; i < title.length(); i++) {
                char character = title.charAt(i);
                if (character == ' ') {
                    buffer.append(xmlStyle.getSpace());
                } else if (character == '/') {
                    buffer.append(xmlStyle.getSlash());
                } else if (character == '&') {
                    buffer.append(xmlStyle.getAmpersandInString());
                } else if (character == '<') {
                    buffer.append(xmlStyle.getLeftAngleBracketInString());
                } else if (character == '>') {
                    buffer.append(xmlStyle.getRightAngleBracketInString());
                } else if (character == '"') {
                    buffer.append(xmlStyle.getDoubleQuoteInString());
                } else if (character == '#') {
                    buffer.append(xmlStyle.getHash());
                } else if (character == '?') {
                    buffer.append(xmlStyle.getQuestionMark());
                } else if (character == ';') {
                    buffer.append(xmlStyle.getSemicolon());
                } else {
                    buffer.append(character);
                }
            }
            buffer.append(xmlStyle.getDoubleQuote());
        }
        if (phrase.getStartTime() != Phrase.DEFAULT_START_TIME) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getStartTimeAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(phrase.getStartTime(), 2)
                        : Double.toString(phrase.getStartTime()))
                    + xmlStyle.getDoubleQuote());
        }
        if (phrase.getInstrument() != Phrase.DEFAULT_INSTRUMENT) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getInstrumentAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(phrase.getInstrument())
                    + xmlStyle.getDoubleQuote());
        }
        if (phrase.getPan() != Phrase.DEFAULT_TEMPO) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getTempoAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(phrase.getTempo(), 2)
                        : Double.toString(phrase.getTempo()))
                    + xmlStyle.getDoubleQuote());
        }
        if (phrase.getAppend() != Phrase.DEFAULT_APPEND) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getAppendAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (phrase.getAppend()
                        ? Boolean.TRUE.toString()
                        : Boolean.FALSE.toString())
                    + xmlStyle.getDoubleQuote());
        }
        if (phrase.getPan() != Phrase.DEFAULT_PAN) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getPanAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(phrase.getPan(), 2)
                        : Double.toString(phrase.getPan()))
                    + xmlStyle.getDoubleQuote());
        }
        int size = phrase.size();
        if (size == 0) {
            buffer.append(
                    xmlStyle.getSlash() + xmlStyle.getRightAngleBracket());
        } else {
            buffer.append(xmlStyle.getRightAngleBracket());
            for (int i = 0; i < size; i++) {
                buffer.append(noteToXMLString(phrase.getNote(i), xmlStyle));
            }
            buffer.append(
                    xmlStyle.getLeftAngleBracket()
                    + xmlStyle.getSlash()
                    + xmlStyle.getPhraseTagName()
                    + xmlStyle.getRightAngleBracket());
        }
        return buffer.toString();
    }

    public static String noteToXMLString(final Note note) {
        return DEFAULT_XML_STYLE.initialXMLDeclaration() 
               + XMLParser.noteToXMLString(note, DEFAULT_XML_STYLE);
    }

    private static String noteToXMLString(
                final Note note,
                final XMLStyle xmlStyle) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(
                xmlStyle.getLeftAngleBracket() + xmlStyle.getNoteTagName());
		if (note.getPitchType() == Note.MIDI_PITCH) {
			if (note.getPitch() != Note.DEFAULT_PITCH) {
				buffer.append(
						xmlStyle.getSpace()
						+ xmlStyle.getPitchAttributeName()
						+ "="
						+ xmlStyle.getDoubleQuote()
						+ Integer.toString(note.getPitch())
						+ xmlStyle.getDoubleQuote());
			}
		} else {
			buffer.append(
				xmlStyle.getSpace()
				+ xmlStyle.getFrequencyAttributeName()
				+ "="
				+ xmlStyle.getDoubleQuote()
				+ Double.toString(note.getFrequency())
				+ xmlStyle.getDoubleQuote());
		}
        if (note.getDynamic() != Note.DEFAULT_DYNAMIC) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getDynamicAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + Integer.toString(note.getDynamic())
                    + xmlStyle.getDoubleQuote());
        }
        if (note.getRhythmValue() != Note.DEFAULT_RHYTHM_VALUE) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getRhythmValueAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(note.getRhythmValue(), 2)
                        : Double.toString(note.getRhythmValue()))
                    + xmlStyle.getDoubleQuote());
        }
        if (note.getPan() != Note.DEFAULT_PAN) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getPanAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(note.getPan(), 2)
                        : Double.toString(note.getPan()))
                    + xmlStyle.getDoubleQuote());
        }
        if (note.getDuration() != Note.DEFAULT_DURATION) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getDurationAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(note.getDuration(), 2)
                        : Double.toString(note.getDuration()))
                    + xmlStyle.getDoubleQuote());
        }
        if (note.getOffset() != Note.DEFAULT_OFFSET) {
            buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getOffsetAttributeName()
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(note.getOffset(), 2)
                        : Double.toString(note.getOffset()))
                    + xmlStyle.getDoubleQuote());
        }
        if (note.getSampleStartTime() != Note.DEFAULT_SAMPLE_START_TIME) {
                buffer.append(
                    xmlStyle.getSpace()
                    + xmlStyle.getSampleStartTimeAttributeName() 
                    + "="
                    + xmlStyle.getDoubleQuote()
                    + (xmlStyle.limitDecimalPlaces()
                        ? XMLParser.limitDecimalPlaces(
                                note.getSampleStartTime(), 2)
                        : Double.toString(note.getSampleStartTime()))
                    + xmlStyle.getDoubleQuote());
        }
        buffer.append(xmlStyle.getSlash() + xmlStyle.getRightAngleBracket());
        return buffer.toString();
    }

    private static String limitDecimalPlaces(final double d,
                                     final int places) {
        String dString = Double.toString(d);
        int lastIndex = dString.lastIndexOf(".") + places + 1;
        if (lastIndex > dString.length()) {
            lastIndex = dString.length();
        }
        return dString.substring(0, lastIndex);
    }
    
    public static Score xmlStringToScore(final String string)
                                  throws ConversionException {
        String xmlAsString = preprocessString(string);
        Element[] elements = XMLParser.xmlStringToElements(xmlAsString);
        if (elements.length != 1) {
            throw new ConversionException(
                    "There can be only one root element.  This string invalidly"
                    + " has " + elements.length + " root elements.");
        }
        Element element = elements[0];
        if (XMLStyles.isValidScoreTag(elements[0].getName())) {           
                return elementToScore(elements[0]);
        } else if (XMLStyles.isValidPartTag(elements[0].getName())) {
                return new Score(elementToPart(elements[0]));
        } else if (XMLStyles.isValidPhraseTag(elements[0].getName())) {
                return new Score(new Part(elementToPhrase(elements[0])));
        } else if (XMLStyles.isValidNoteTag(elements[0].getName())) {
                return new Score(new Part(
                                new Phrase(elementToNote(elements[0]))));
        }
        throw new ConversionException("Unrecognised root element: " 
                                      + elements[0].getName());
    }

    public static Part xmlStringToPart(final String string)
                                throws ConversionException {
        String xmlAsString = preprocessString(string);
        Element[] elements = XMLParser.xmlStringToElements(xmlAsString);
        if (elements.length != 1) {
            throw new ConversionException(
                    "There can be only one root element.  This string invalidly"
                    + " has " + elements.length + " root elements.");
        }
        Element element = elements[0];
        if (XMLStyles.isValidScoreTag(elements[0].getName())) {           
                throw new ConversionException(
                                "This XML string represents a Score, use the "
                                + "xmlStringToScore(String) method instead.");
        } else if (XMLStyles.isValidPartTag(elements[0].getName())) {
                return elementToPart(elements[0]);
        } else if (XMLStyles.isValidPhraseTag(elements[0].getName())) {
                return new Part(elementToPhrase(elements[0]));
        } else if (XMLStyles.isValidNoteTag(elements[0].getName())) {
                return new Part(new Phrase(elementToNote(elements[0])));
        }
        throw new ConversionException("Unrecognised root element: " 
                                      + elements[0].getName());
    }
    
    public static Phrase xmlStringToPhrase(final String string)
                                    throws ConversionException {
        String xmlAsString = preprocessString(string);
        Element[] elements = XMLParser.xmlStringToElements(xmlAsString);
        if (elements.length != 1) {
            throw new ConversionException(
                    "There can be only one root element.  This string invalidly"
                    + " has " + elements.length + " root elements.");
        }
        Element element = elements[0];
        if (XMLStyles.isValidScoreTag(elements[0].getName())) {           
                throw new ConversionException(
                                "This XML string represents a Score, use the "
                                + "xmlStringToScore(String) method instead.");
        } else if (XMLStyles.isValidPartTag(elements[0].getName())) {
                throw new ConversionException(
                                "This XML string represents a Part, use the "
                                + "xmlStringToPart(String) method instead.");
        } else if (XMLStyles.isValidPhraseTag(elements[0].getName())) {
                return elementToPhrase(elements[0]);
        } else if (XMLStyles.isValidNoteTag(elements[0].getName())) {
                return new Phrase(elementToNote(elements[0]));
        }
        throw new ConversionException("Unrecognised root element: " 
                                      + elements[0].getName());
    }
    
    public static Note xmlStringToNote(final String string)
                                throws ConversionException {
        String xmlAsString = preprocessString(string);
        Element[] elements = XMLParser.xmlStringToElements(xmlAsString);
        if (elements.length != 1) {
            throw new ConversionException(
                    "There can be only one root element.  This string invalidly"
                    + " has " + elements.length + " root elements.");
        }
        Element element = elements[0];
        if (XMLStyles.isValidScoreTag(elements[0].getName())) {           
                throw new ConversionException(
                                "This XML string represents a Score, use the "
                                + "xmlStringToScore(String) method instead.");
        } else if (XMLStyles.isValidPartTag(elements[0].getName())) {
                throw new ConversionException(
                                "This XML string represents a Part, use the "
                                + "xmlStringToPart(String) method instead.");
        } else if (XMLStyles.isValidPhraseTag(elements[0].getName())) {
                throw new ConversionException(
                                "This XML string represents a Phrase, use the "
                                + "xmlStringToPhrase(String) method instead.");
        } else if (XMLStyles.isValidNoteTag(elements[0].getName())) {
                return elementToNote(elements[0]);
        }
        throw new ConversionException("Unrecognised root element: " 
                                      + elements[0].getName());
    }
    private static String preprocessString(final String string) 
                                    throws ConversionException
    {
        String xmlAsString = string;
        for (int i = 0; i < XMLStyles.styles.length; i++) {
                String decl = XMLStyles.styles[i].initialXMLDeclaration(); 
                if (string.startsWith(decl)) {
                        xmlAsString = xmlAsString.substring(decl.length());
                        break;
                }
        }
        char[] xmlAsCharArray = xmlAsString.toCharArray();
        StringBuffer buffer = null;

        XMLStyle xmlStyle = new StandardXMLStyle();
        char[][] CHARS_ARRAY = xmlStyle.getEncodingsOfReferenceChars();
        char[] REFERENCE_CHARS = xmlStyle.getReferenceChars();

        for (int i = 0; i < CHARS_ARRAY.length; i++) {
            buffer = new StringBuffer();
            String pattern = new String(CHARS_ARRAY[i]);
            int lastIndex = 0;

            int currentIndex = xmlAsString.indexOf(pattern);
            while (currentIndex != -1) {
                while (lastIndex < currentIndex) {
                    buffer.append(xmlAsCharArray[lastIndex]);
                    lastIndex++;
                }
                buffer.append(REFERENCE_CHARS[i]);
                lastIndex += 3;
    
                currentIndex = xmlAsString.indexOf(pattern, lastIndex);
            }

            currentIndex = xmlAsString.length();
            while (lastIndex < currentIndex) {
                buffer.append(xmlAsCharArray[lastIndex]);
                lastIndex++;
            }

            xmlAsString = buffer.toString();
            xmlAsCharArray = xmlAsString.toCharArray();
        }
        return xmlAsString;
    }
    
    private static Score elementToScore(Element element)
            throws
                ConversionException {
        XMLStyle xmlStyle = new StandardXMLStyle();
        if (! XMLStyles.isValidScoreTag(element.getName())) {
           throw new ConversionException(
                    "The root element must have the name '"
                    + xmlStyle.getScoreTagName() + "'.  The invalid name used "
                    + "was '" + element.getName() + "'.");
        }
        Score returnScore = new Score();
        String attributeValue;
                                         
        attributeValue = XMLStyles.getTitleAttributeValue(element);
        if (! attributeValue.equals("")) {
            returnScore.setTitle(attributeValue);
        }
        attributeValue = XMLStyles.getTempoAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnScore.setTempo(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getTempoAttributeName() + "' of element '"
                        + xmlStyle.getScoreTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getKeySignatureAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnScore.setKeySignature(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getKeySignatureAttributeName() + "' of element '"
                        + xmlStyle.getScoreTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getKeyQualityAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnScore.setKeyQuality(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getKeyQualityAttributeName() + "' of element '"
                        + xmlStyle.getScoreTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getNumeratorAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnScore.setNumerator(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getNumeratorAttributeName() + "' of element '"
                        + xmlStyle.getScoreTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getDenominatorAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnScore.setDenominator(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getDenominatorAttributeName() + "' of element '"
                        + xmlStyle.getScoreTagName() + "' must represent a Java integer.");
            }
        }
        Element[] children = element.getChildren();
        for (int i = 0; i < children.length; i++) {
            if (XMLStyles.isValidPartTag(children[i].getName())) {
                returnScore.addPart(elementToPart(children[i]));
            }
        }
        return returnScore;
    }

    private static Part elementToPart(Element element)
            throws
                ConversionException {
        XMLStyle xmlStyle = new StandardXMLStyle();
        if (! XMLStyles.isValidPartTag(element.getName())) {
            throw new ConversionException(
                    "Invalid element: " + element.getName() + ".  The only "
                    + "accepted tag name is '" + xmlStyle.getPartTagName() + "'.");
        }
        Part returnPart = new Part();
        String attributeValue;

        attributeValue = XMLStyles.getTitleAttributeValue(element);
        if (! attributeValue.equals("")) {
            returnPart.setTitle(attributeValue);
        }
        attributeValue = XMLStyles.getChannelAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setChannel(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getChannelAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getInstrumentAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setInstrument(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getInstrumentAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getTempoAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setTempo(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getTempoAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getKeySignatureAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setKeySignature(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getKeySignatureAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getKeyQualityAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setKeyQuality(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getKeyQualityAttributeName() + "' of element '"
                        + xmlStyle.getScoreTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getNumeratorAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setNumerator(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getNumeratorAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getDenominatorAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setDenominator(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getDenominatorAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getPanAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPart.setPan(Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getPanAttributeName() + "' of element '"
                        + xmlStyle.getPartTagName() + "' must represent a Java double.");
            }
        }
        Element[] elements = element.getChildren();
        for (int i = 0; i < elements.length; i++) {
            if (XMLStyles.isValidPhraseTag(elements[i].getName())) {
                returnPart.addPhrase(elementToPhrase(elements[i]));
            }
        }
        return returnPart;
    }

    private static Phrase elementToPhrase(Element element)
            throws
                ConversionException {
        XMLStyle xmlStyle = new StandardXMLStyle();
        if (! XMLStyles.isValidPhraseTag(element.getName())) {
            throw new ConversionException(
                    "Invalid element: " + element.getName() + ".  The only "
                    + "accepted tag name is '" + xmlStyle.getPhraseTagName() + "'.");
        }
        Phrase returnPhrase = new Phrase();
        String attributeValue;

        attributeValue = XMLStyles.getTitleAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPhrase.setTitle(attributeValue);
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getTitleAttributeName() + "' of element '"
                        + xmlStyle.getPhraseTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getStartTimeAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPhrase.setStartTime(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getStartTimeAttributeName() + "' of element '"
                        + xmlStyle.getPhraseTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getInstrumentAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPhrase.setInstrument(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getInstrumentAttributeName() + "' of element '"
                        + xmlStyle.getPhraseTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getTempoAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPhrase.setTempo(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getTempoAttributeName() + "' of element '"
                        + xmlStyle.getPhraseTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getAppendAttributeValue(element);
        if (! attributeValue.equals("")) {
            returnPhrase.setAppend(new Boolean(attributeValue).booleanValue());
        }
        attributeValue = XMLStyles.getPanAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnPhrase.setPan(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getPanAttributeName() + "' of element '"
                        + xmlStyle.getPhraseTagName() + "' must represent a Java double.");
            }
        }
        Element[] elements = element.getChildren();
        for (int i = 0; i < elements.length; i++) {
            if (XMLStyles.isValidNoteTag(elements[i].getName())) {
                returnPhrase.addNote(elementToNote(elements[i]));
            }
        }
        return returnPhrase;
    }

    private static Note elementToNote(Element element)
            throws
                ConversionException {
        XMLStyle xmlStyle = new StandardXMLStyle();
        if (! XMLStyles.isValidNoteTag(element.getName())) {
            throw new ConversionException(
                    "Invalid element: " + element.getName() + ".  The only "
                    + "accepted tag name is '" + xmlStyle.getNoteTagName() + "'.");
        }
        Note returnNote = new Note();
        String attributeValue;

        attributeValue = XMLStyles.getPitchAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setPitch(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getPitchAttributeName() + "' of element '"
                        + xmlStyle.getNoteTagName() + "' must represent a Java integer.");
            }
        }
		attributeValue = XMLStyles.getFrequencyAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                double tempVal = Double.valueOf(attributeValue).doubleValue();
                returnNote.setFrequency(tempVal);
            } catch (NumberFormatException e) {
                throw new ConversionException(
					"Invalid attribute value: " + attributeValue + ".  The "
					+ "attribute '" + xmlStyle.getFrequencyAttributeName() + "' of element '"
					+ xmlStyle.getNoteTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getDynamicAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setDynamic(Integer.parseInt(attributeValue));
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getDynamicAttributeName() + "' of element '"
                        + xmlStyle.getNoteTagName() + "' must represent a Java integer.");
            }
        }
        attributeValue = XMLStyles.getRhythmValueAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setRhythmValue(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getRhythmValueAttributeName() + "' of element '"
                        + xmlStyle.getNoteTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getPanAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setPan(Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getPanAttributeName() + "' of element '"
                        + xmlStyle.getNoteTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getDurationAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setDuration(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getDurationAttributeName() + "' of element '"
                        + xmlStyle.getNoteTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getOffsetAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setOffset(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getOffsetAttributeName() + "' of element '"
                        + xmlStyle.getNoteTagName() + "' must represent a Java double.");
            }
        }
        attributeValue = XMLStyles.getSampleStartTimeAttributeValue(element);
        if (! attributeValue.equals("")) {
            try {
                returnNote.setSampleStartTime(
                        Double.valueOf(attributeValue).doubleValue());
            } catch (NumberFormatException e) {
                throw new ConversionException(
                        "Invalid attribute value: " + attributeValue + ".  The "
                        + "attribute '" + xmlStyle.getSampleStartTimeAttributeName() + "' of "
                        + "element '" + xmlStyle.getNoteTagName() + "' must represent a "
                        + "Java double.");
            }
        }
        return returnNote;
    }


    private static Element[] xmlStringToElements(final String string) 
            throws
                ConversionException {
        Vector elementsToBeReturned = new Vector();
        XMLStyle xmlStyle = new StandardXMLStyle();
        char[][] ENCODINGS_ARRAY = xmlStyle.getEncodingsOfValueReferenceChars();
        char[] REFERENCE_CHARS = xmlStyle.getValueReferenceChars();
        try {
            int count = 0;
            if (string.charAt(count++) != '<') {
                throw new ConversionException(
                        "XML String does not begin with '<'");
            }
            StringBuffer elementName = new StringBuffer();
            char character = string.charAt(count++);
            while (character != ' ' && character != '/' && character != '>') {
                elementName.append(character);
                character = string.charAt(count++);
            }
            Element element = new Element(elementName.toString());
            while (character == ' ') {
                StringBuffer attributeName = new StringBuffer();
                character = string.charAt(count++);
                while (character != '=') {
                    if (character == '/') {
                        throw new ConversionException(
                                "Illegal character '/' in attribute name of "
                                + "the '" + element.getName() + "' element.");
                    }
                    if (character == '>') {
                        throw new ConversionException(
                                "Illegal character '>' in attribute name of "
                                + "the '" + element.getName() + "' element.");
                    }
                    attributeName.append(character);
                    character = string.charAt(count++);
                }
                Attribute attribute = new Attribute(attributeName.toString());
                character = string.charAt(count++);
                if (character != '"') {
                    throw new ConversionException(
                            "The value of the '" + attribute.getName()
                            + "' attribute in the '" + element.getName()
                            + "' element does not begin with a double-quote "
                            + "(\").");
                }
                StringBuffer valueBuffer = new StringBuffer();
                character = string.charAt(count++);
                while (character != '"') {
//                    if (character == '/') {
//                        throw new ConversionException(
//                                "Illegal character '/' in value of the '"
//                                + attribute.getName() + "' attribute name in "
//                                + "the '" + element.getName() + "' element.");
//                    }
//                    if (character == '>') {
//                        throw new ConversionException(
//                                "Illegal character '>' in value of the '"
//                                + attribute.getName() + "' attribute name in "
//                                + "the '" + element.getName() + "' element.");
//                    }
//                    if (character == '<') {
//                        throw new ConversionException(
//                                "Illegal character '<' in value of the '"
//                                + attribute.getName() + "' attribute name in "
//                                + "the '" + element.getName() + "' element.");
//                    }
                    loop: for (int i = 0; i < ENCODINGS_ARRAY.length; i++) {
                        for (int j = 0; j < ENCODINGS_ARRAY[i].length; j++) {
                            try {
                                 if (ENCODINGS_ARRAY[i][j] != 
                                                string.charAt(count + j - 1)) {
                                        // if this is last array
                                        if (i == ENCODINGS_ARRAY.length - 1) {
                                                valueBuffer.append(character);
                                        }
                                        continue loop;
                                 }
                                 // if this is last character
                                 if (j == ENCODINGS_ARRAY[i].length - 1) {
                                        count += ENCODINGS_ARRAY[i].length - 1;
                                        valueBuffer.append(REFERENCE_CHARS[i]);
                                        break loop;
                                 }
                            } catch (final IndexOutOfBoundsException e) {
                                    // if this is last array
                                    if (i == ENCODINGS_ARRAY.length - 1) {
                                            valueBuffer.append(character);
                                    }
                                    continue loop;
                            }
                        }
                    }
                    character = string.charAt(count++);
                }
                attribute.setValue(valueBuffer.toString());
                element.addAttribute(attribute);
                character = string.charAt(count++);
            }
            if (character == '>') {
                int endIndex = string.indexOf("</" + element.getName() + ">");
                if (endIndex == -1) {
                    throw new ConversionException(
                            "No closing tag found: </" + element.getName() + ">");
                }
                element.appendChildren(
                        xmlStringToElements(string.substring(count, endIndex)));
                count = endIndex + element.getName().length() + 3;
            } else if (character == '/') {
                character = string.charAt(count++);
                if (character != '>') {
                    throw new ConversionException(
                            "Character '>' is expected to terminate the '"
                            + element.getName() + "' element but was not "
                            + "found.");
                }
            } else {
                throw new ConversionException(
                        "Either '>' or '/>' is expected to terminate the '"
                        + element.getName() + "' element but neither was "
                        + "found.");
            }
            elementsToBeReturned.addElement(element);
            if (count < string.length()) {
                Element[] furtherElements =
                        xmlStringToElements(string.substring(count));
                for (int i = 0; i < furtherElements.length; i++) {
                    elementsToBeReturned.addElement(furtherElements[i]);
                }
            }
            Element[] returnElements =
                    new Element[elementsToBeReturned.size()];
            elementsToBeReturned.copyInto(returnElements);
            return returnElements;
        } catch (IndexOutOfBoundsException e) {
            throw new ConversionException(
                    "Xml string ended prematurely.  Further characters were "
                    + "excepted.");
        }
    }
}

class XMLStyles {
    public static final XMLStyle[] styles = { new StandardXMLStyle() };
//            { new ExcessiveURLEncodingXMLStyle(),
//              new ConciseQueryStringXMLStyle() };

    public static boolean isValidScoreTag(final String string) {
        for (int i = 0; i < styles.length; i++) {
            if (string.equals(styles[i].getScoreTagName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidPartTag(final String string) {
        for (int i = 0; i < styles.length; i++) {
            if (string.equals(styles[i].getPartTagName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidPhraseTag(final String string) {
        for (int i = 0; i < styles.length; i++) {
            if (string.equals(styles[i].getPhraseTagName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidNoteTag(final String string) {
        for (int i = 0; i < styles.length; i++) {
            if (string.equals(styles[i].getNoteTagName())) {
                return true;
            }
        }
        return false;
    }

    public static String getTitleAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(styles[i].getTitleAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getTempoAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(styles[i].getTempoAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getKeySignatureAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getKeySignatureAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getKeyQualityAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getKeyQualityAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getNumeratorAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(styles[i].getNumeratorAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getDenominatorAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(styles[i].getDenominatorAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getChannelAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(styles[i].getChannelAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getInstrumentAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getInstrumentAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getPanAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getPanAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getStartTimeAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getStartTimeAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getAppendAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getAppendAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getPitchAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getPitchAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

	public static String getFrequencyAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
			element.getAttribute(
						styles[i].getFrequencyAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }
	

    public static String getDynamicAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getDynamicAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getRhythmValueAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getRhythmValueAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getDurationAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getDurationAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getOffsetAttributeValue(final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getOffsetAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

    public static String getSampleStartTimeAttributeValue(
                final Element element) {
        for (int i = 0; i < styles.length; i++) {
            String string =
                    element.getAttribute(
                            styles[i].getSampleStartTimeAttributeName());
            if (! string.equals("")) {
                return string;
            }
        }
        return "";
    }

}                          

class Element {

    private String name;

    private Vector attributeVector = new Vector();

    private Vector childrenVector = new Vector();

    public Element(String name) {
        this.name = name;
    }

    public void addAttribute(Attribute attribute) {
        attributeVector.addElement(attribute);
    }

    public void appendChildren(Element[] children) {
        for (int i = 0; i < children.length; i++) {
            childrenVector.addElement(children[i]);    
        }
    }

    public String getAttribute(final String string) {
        Enumeration enum1 = attributeVector.elements();
        while (enum1.hasMoreElements()) {
            Attribute attribute = (Attribute) enum1.nextElement();
            if (attribute.getName().equals(string)) {
                return attribute.getValue();
            }
        }
        return "";
    }

    public Element[] getChildren() {
        Element[] children = new Element[childrenVector.size()];
        childrenVector.copyInto(children);
        return children;
    }

    public String getName() {
        return name;
    }
}

class Attribute {

    private String name;

    private String value;

    public Attribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

abstract class XMLStyle {
    
    public String initialXMLDeclaration() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    }

    public String getSpace() {
        return " "; //"%20";
    }

    public String getDoubleQuote() {
        return "\""; //"%22";
    }

    public String getDoubleQuoteInString() {
        return "&quot;"; //"%26quot%3B";
    }

    public String getHash() {
        return "#"; //"%23";
    }

    public String getAmpersand() {
        return "&"; //"%26amp%3B";
    }
    
    public String getAmpersandInString() {
        return "&amp;";
    }

    public String getSlash() {
        return "/"; //"%2F";
    }

    public String getSemicolon() {
        return ";"; //"%3B";
    }

    public String getQuestionMark() {
        return "?"; //"%3F";
    }

    final char[] WHITESPACE_CHARS =
            new char[] { '%', '2', '0' };

    final char[] DOUBLE_QUOTE_CHARS =
            new char[] { '%', '2', '2' };

    final char[] HASH_CHARS =
            new char[] { '%', '2', '3' };

    final char[] AMPERSAND_CHARS =
            new char[] { '%', '2', '6' };

    final char[] SLASH_CHARS =
            new char[] { '%', '2', 'F' };

    final char[] SEMICOLON_CHARS =
            new char[] { '%', '3', 'B' };

    final char[] QUESTION_MARK_CHARS =
            new char[] { '%', '3', 'F' };

    final char[] LEFT_ANGLE_CHARS =
            new char[] { '%', '3', 'C' };

    final char[] RIGHT_ANGLE_CHARS =
            new char[] { '%', '3', 'E' };

    public abstract char[] getReferenceChars();

    public abstract char[][] getEncodingsOfReferenceChars();

    public abstract char[] getValueReferenceChars();

    public abstract char[][] getEncodingsOfValueReferenceChars();

    public String getLeftAngleBracket() {
        return "<"; //"%3C";
    }

    public String getLeftAngleBracketInString() {
        return "&lt;"; //"%26lt%3B";
    }

    public String getRightAngleBracket() {
        return ">"; //"%3E";
    }

    public String getRightAngleBracketInString() {
        return "&gt;"; //"%26gt%3B"; 
    }

    public String getScoreTagName() {
        return "Score";
    }

    public String getPartTagName() {
        return "Part";
    }

    public String getPhraseTagName() {
        return "Phrase";
    }

    public String getNoteTagName() {
        return "Note";
    }

    public String getTitleAttributeName() {
        return "title";
    }

    public String getTempoAttributeName() {
        return "tempo";
    }

    public String getKeySignatureAttributeName() {
        return "keySignature";
    }

    public String getKeyQualityAttributeName() {
        return "keyQuality";
    }

    public String getNumeratorAttributeName() {
        return "numerator";
    }

    public String getDenominatorAttributeName() {
        return "denominator";
    }

    public String getChannelAttributeName() {
        return "channel";
    }

    public String getInstrumentAttributeName() {
        return "instrument";
    }

    public String getPanAttributeName() {
        return "pan";
    }

    public String getStartTimeAttributeName() {
        return "startTime";
    }

    public String getAppendAttributeName() {
        return "append";
    }

    public String getPitchAttributeName() {
        return "pitch";
    }

	public String getFrequencyAttributeName() {
        return "frequency";
    }

    public String getDynamicAttributeName() {
        return "dynamic";                       
    }

    public String getRhythmValueAttributeName() {
        return "rhythmValue";
    }

    public String getDurationAttributeName() {
        return "duration";
    }

    public String getOffsetAttributeName() {
        return "offset";
    }

    public String getSampleStartTimeAttributeName() {
        return "sampleStartTime";
    }

    public boolean limitDecimalPlaces() {
        return false;
    }
}

class StandardXMLStyle extends XMLStyle {
    
    public final char[] getReferenceChars()
    {
            return new char[] { };
    }

    public final char[][] getEncodingsOfReferenceChars()
    {
            return new char[][] { };
    }
    
    public final char[] getValueReferenceChars()
    {
            return new char[] { '<', '>', '\"', '&' };
    }

    public final char[][] getEncodingsOfValueReferenceChars()
    {
            return new char[][] { { '&', 'l', 't', ';' },
                                  { '&', 'g', 't', ';' },
                                  { '&', 'q', 'u', 'o', 't', ';' },
                                  { '&', 'a', 'm', 'p', ';' } 
                                };
    }

}