/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.lickgen.transformations;

import imp.data.MelodyPart;
import imp.gui.Notate;
import imp.lickgen.LickGen;
import polya.Polylist;

/**
 *
 * @author Notsoplenk
 */
public class TransformationTesting {
    
    public Transform transform;
    
    public TransformationTesting(LickGen lickGen, Notate notate)
    {
        String graceNote = 
"(substitution\n" +
"	(name gracenote)\n" +
"	(type embellishment)\n" +
"	(weight 1)\n" +
"\n" +
"	// transformation for 1 note ascending tuple\n" +
"	(transformation\n" +
"		(description single-ascending-tuple)\n" +
"		(weight 3)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"					(not (triplet? n1))\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 16 n1) \n" +
"				(set-duration 	16\n" +
"(transpose-chromatic -1/2 n2))\n" +
"				n2))\n" +
"	// transformation for 1 note ascending triplet\n" +
"	(transformation\n" +
"		(description single-ascending-triplet)\n" +
"		(weight 3)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"					(triplet? n1)\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 16/3 n1) \n" +
"				(set-duration 	16/3\n" +
"(transpose-chromatic -1/2 n2))\n" +
"				n2))\n" +
"\n" +
"	// transformation for 1 note descending tuple\n" +
"	(transformation\n" +
"		(description single-descending-tuple)\n" +
"		(weight 3)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(not (triplet? n1))\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 16 n1) \n" +
"				(set-duration 	16\n" +
"(transpose-chromatic 1/2 n2))\n" +
"				n2))	\n" +
"// transformation for 1 note descending triplet\n" +
"	(transformation\n" +
"		(description single-descending-tuple)\n" +
"		(weight 3)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(triplet? n1)\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 16/3 n1) \n" +
"				(set-duration 	16/3\n" +
"(transpose-chromatic 1/2 n2))\n" +
"				n2))	\n" +
"\n" +
"	// transformation for 2 notes ascending\n" +
"	(transformation\n" +
"		(description double-ascending)\n" +
"		(weight 2)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(not (triplet? n1))\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 8 n1) \n" +
"				(set-duration 	16\n" +
"						(transpose-chromatic -1 n2)\n" +
"(transpose-chromatic -1/2 n2))\n" +
"				n2))\n" +
"\n" +
"// transformation for 2 notes descending\n" +
"	(transformation\n" +
"		(description double-descending)\n" +
"		(weight 2)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(not (triplet? n1))\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 8 n1) \n" +
"				(set-duration 	16\n" +
"						(transpose-chromatic 1 n2)\n" +
"(transpose-chromatic 1/2 n2))\n" +
"				n2))\n" +
"\n" +
"	// transformation for 3 notes ascending\n" +
"	(transformation\n" +
"		(description triple-ascending)\n" +
"		(weight 1)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"	(not (triplet? n1))\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 8 n1)\n" +
"(set-duration 16/3\n" +
" 	(transpose-chromatic -3/2 n2)\n" +
"	(transpose-chromatic -1 n2)\n" +
"	(transpose-chromatic -1/2 n2))\n" +
"				n2))\n" +
"\n" +
"// transformation for 3 notes descending\n" +
"	(transformation\n" +
"		(description triple-descending)\n" +
"		(weight 1)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and\n" +
"(= 	(note-category n2) \n" +
"       		C)\n" +
"(not (triplet? n1))\n" +
"	(duration>= n1 8)\n" +
"	(duration>= n2 8)))\n" +
"\n" +
"		(target-notes 	(subtract-duration 8 n1)\n" +
"(set-duration 16/3\n" +
" 	(transpose-chromatic 3/2 n2)\n" +
"	(transpose-chromatic 1 n2)\n" +
"	(transpose-chromatic 1/2 n2))\n" +
"				n2)))";
String mordent = "(substitution\n" +
"	(name mordant)\n" +
"	(type motif)\n" +
"	(weight 4)\n" +
"	// transformation for 1 step apart, 2 notes uneven\n" +
"	(transformation\n" +
"		(description one-step-two-notes-uneven)\n" +
"		(weight 1)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition (and	(=	1\n" +
"							(pitch-	n1 n2))\n" +
"						(not (triplet? n1))\n" +
"						(duration>= n1 4)\n" +
"						(duration>= n2 8)))\n" +
"\n" +
"(target-notes	(subtract-duration 8 n1)\n" +
"		(set-duration 16\n" +
"			(transpose-diatonic 2 n1)\n" +
"			n1)\n" +
"		n2))\n" +
"// transformation for 1 step apart, 2 notes even\n" +
"	(transformation\n" +
"		(description one-step-two-notes-even)\n" +
"		(weight 1)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition (and	(=	1\n" +
"							(pitch- n1 n2))\n" +
"						(not (triplet? n1))\n" +
"						(duration>= n1 4)\n" +
"						(duration>= n2 8)))\n" +
"\n" +
"(target-notes	(subtract-duration 8/3 n1)\n" +
"		(set-duration 16/3\n" +
"			(transpose-diatonic 2 n1)\n" +
"			n1)\n" +
"		n2))\n" +
"// transformation for 1 step apart, 3 notes just above\n" +
"	(transformation\n" +
"		(description one-step-three-notes-just-above)\n" +
"		(weight 1)\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition (and	(=	1\n" +
"							(pitch- n1 n2))\n" +
"						(not (triplet? n1))\n" +
"						(duration>= n1 4)\n" +
"						(duration>= n2 8)))\n" +
"\n" +
"(target-notes	(subtract-duration 8+16 n1)\n" +
"		(set-duration 16\n" +
"			(transpose-chromatic 1/2  n1)\n" +
"			n1\n" +
"(transpose-chromatic -1/2 n1))\n" +
"		n2))\n" +
"// transformation for 2 steps apart, 3 notes just above\n" +
"	(transformation\n" +
"		(description two-step-three-notes-just-above)\n" +
"		(weight 1)\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition (and	(=	2\n" +
"							(pitch-	 n1 n2))\n" +
"						(not (triplet? n1))\n" +
"						(not (= (chord-family n1) dominant))\n" +
"						(duration>= 	(duration n1)\n" +
"							4)\n" +
"						(duration>=	(duration n2)\n" +
"							8)))\n" +
"\n" +
"(target-notes	(subtract-duration 8+16 n1)\n" +
"		(set-duration 16\n" +
"			(transpose-diatonic 2  n1)\n" +
"			n1\n" +
"(transpose-diatonic 2 n2))\n" +
"		n2))\n" +
"// transformation for 2 steps apart, 3 notes above then below\n" +
"	(transformation\n" +
"		(description two-step-three-notes-above-below)\n" +
"		(weight 4)\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition (and	(=	1\n" +
"							(pitch- n1 n2))\n" +
"						(not (triplet? n1))\n" +
"						(not (= (chord-family n1) dominant))\n" +
"						(duration>= n1 4)\n" +
"						(duration>= n2 8)))\n" +
"\n" +
"(target-notes	(subtract-duration 8+16 n1)\n" +
"		(set-duration 16\n" +
"			(transpose-diatonic 2  n1)\n" +
"			n1\n" +
"(transpose-chromatic -1/2 n2))\n" +
"		n2)))";

String arpeggio = "(substitution\n" +
"	(name triplet-arpeggio)\n" +
"	(type motif)\n" +
"	(weight 1)\n" +
"	// transformation for ascending arpeggio\n" +
"	(transformation\n" +
"		(description ascending)\n" +
"		(weight 1)\n" +
"\n" +
"		(source-notes n1 n2 n3)\n" +
"\n" +
"		(guard-condition (and	(member	(relative-pitch n2)\n" +
"								(1 3))\n" +
"						(=	(duration n2)\n" +
"							4)\n" +
"						(pitch< n2 n3)\n" +
"(not	(=\n" +
"(chord-family n2)\n" +
"dominant))))\n" +
"		\n" +
"		(target-notes	(subtract-duration 8 n1)\n" +
"				(set-duration	8\n" +
"						(transpose-diatonic -2 n2))\n" +
"				(scale-duration 	1/3\n" +
"							n2\n" +
"							(transpose-diatonic 3 n2)\n" +
"							(transpose-diatonic 5 n2))\n" +
"n3))\n" +
"	// transformation for descending arpeggio\n" +
"	(transformation\n" +
"		(description descending)\n" +
"		(weight 1)\n" +
"\n" +
"		(source-notes n1 n2)\n" +
"\n" +
"		(guard-condition (and	(member	(relative-pitch n2)\n" +
"								(1 3))\n" +
"						(=	(duration n2)\n" +
"							4)\n" +
"						(pitch< n1 n2)\n" +
"(not	(=\n" +
"(chord-family n2)\n" +
"dominant))))\n" +
"		\n" +
"		(target-notes	(subtract-duration 8 n1)\n" +
"				(set-duration	 8\n" +
"						(transpose-diatonic -1 n2))\n" +
"				(scale-duration 	1/3\n" +
"							(transpose-diatonic 5 n2)\n" +
"							(transpose-diatonic 3 n2)\n" +
"							(transpose-diatonic 1 n2)))))";

String chromaticPassing = "(substitution\n" +
"	(name chromatic-passing-tone)\n" +
"	(type embellishment)\n" +
"	(weight 1)\n" +
"	//transformation for down\n" +
"	(transformation\n" +
"		(description down)\n" +
"		(weight 1)\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition	(and	(=	(note-category n2)\n" +
"							C)\n" +
"(=	(pitch-	 n1 n2)\n" +
"	1)\n" +
"(duration>=	n1\n" +
"8)))\n" +
"\n" +
"		(target-notes	(scale-duration	1/2\n" +
"							n1\n" +
"	(transpose-chromatic -1/2 n1))\n" +
"				n2))\n" +
"//transformation for up\n" +
"	(transformation\n" +
"		(description down)\n" +
"		(weight 1)\n" +
"		(source-notes n1 n2)\n" +
"		\n" +
"		(guard-condition	(and	(=	(note-category n2)\n" +
"							C)\n" +
"(=	(pitch-	 n2 n1)\n" +
"	1)\n" +
"(duration>=	n1\n" +
"8)))\n" +
"\n" +
"		(target-notes	(scale-duration	1/2\n" +
"							n1\n" +
"	(transpose-chromatic 1/2 n1))\n" +
"				n2)))";

String blues = "(substitution\n" +
"	(name blues-notes)\n" +
"	(type motif)\n" +
"	(weight 3)\n" +
"	(transformation\n" +
"		(description thirds-fifths-sevenths)\n" +
"		(weight 1)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (and	(or	(= (relative-pitch n1) 3)\n" +
"							(= (relative-pitch n1) 5)\n" +
"							(= (relative-pitch n1) 7))\n" +
"(=	(chord-family n1)\n" +
"dominant)\n" +
"(duration>= n1 8)))\n" +
"		(target-notes (transpose-chromatic -1/2 n1))))";
String identities = "(substitution\n" +
"	(name identity-motif)\n" +
"	(type motif)\n" +
"	(weight 2)\n" +
"	(transformation\n" +
"		(description do-nothing)\n" +
"		(weight 1)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (= n1 n1))\n" +
"		(target-notes n1)))\n" +
"\n" +
"(substitution\n" +
"	(name identity-embellishment)\n" +
"	(type embellishment)\n" +
"	(weight 2)\n" +
"	(transformation\n" +
"		(description do-nothing)\n" +
"		(weight 1)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (= n1 n1))\n" +
"		(target-notes n1)))";

String alt9th = "(substitutions\n" +
"	(name altered-dominant-9th)\n" +
"	(weight 1)\n" +
"	(type motif)\n" +
"	(transformation\n" +
"		(description alter)\n" +
"		(weight 1)\n" +
"		(source-notes n1 n2)\n" +
"		(guard-condition	(and	(= (relative-pitch n1) 3)\n" +
"						(duration>= n2 8)\n" +
"						(duration<= n2 4)\n" +
"(= 	(chord-family n1)\n" +
"dominant)))\n" +
"		(target-notes	n1\n" +
"				(set-relative-pitch b9 n2))))";

String splittingQuarter = "(substitution\n" +
"	(name split-half)\n" +
"	(type motif)\n" +
"	(weight 5)\n" +
"(transformation\n" +
"		(description triplets-up)\n" +
"		(weight 3)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (and (not (= (chord-family n1) dominant))\n" +
"(= (duration n1) 2)))\n" +
"		(target-notes (scale-duration 1/3\n" +
"							n1\n" +
"							(transpose-diatonic 2 n1)\n" +
"(transpose-diatonic 3 n1))))\n" +
"(transformation\n" +
"		(description triplets-up-1-2-4)\n" +
"		(weight 3)\n" +
"		(source-notes n1 n2)\n" +
"		(guard-condition (and	(= (duration n1) 2)\n" +
"(not (= (chord-family n1) dominant))\n" +
"(pitch<= n1 n2)))\n" +
"		(target-notes (scale-duration 1/3\n" +
"							n1\n" +
"							(transpose-diatonic 2 n1)\n" +
"(transpose-diatonic 4 n1))))\n" +
"(transformation\n" +
"		(description triplets-down)\n" +
"		(weight 3)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (and (not (= (chord-family n1) dominant))\n" +
"(= (duration n1) 2)))\n" +
"		(target-notes (scale-duration 1/3\n" +
"							n1\n" +
"							(transpose-diatonic -1 n1)\n" +
"(transpose-diatonic -2 n1)))))"
        + "(substitution\n" +
"	(name split-quarter)\n" +
"	(type motif)\n" +
"	(weight 2)\n" +
"	(transformation\n" +
"		(description eighths-up)\n" +
"		(weight 3)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (= (duration n1) 4))\n" +
"		(target-notes (scale-duration 1/2 \n" +
"							n1\n" +
"							(transpose-diatonic 2 n1))))\n" +
"	(transformation\n" +
"		(description eighths-up-3rd)\n" +
"		(weight 3)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (and	(not (= (chord-family n1) dominant))\n" +
"(= (duration n1) 4)))\n" +
"		(target-notes (scale-duration 1/2 \n" +
"							n1\n" +
"							(transpose-diatonic 3 n1))))\n" +
"(transformation\n" +
"		(description eighths-down)\n" +
"		(weight 3)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (= (duration n1) 4))\n" +
"		(target-notes (scale-duration 1/2 \n" +
"							n1\n" +
"							(transpose-diatonic -1 n1))))\n" +
"(transformation\n" +
"		(description triplets-up)\n" +
"		(weight 1)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (and (not (= (chord-family n1) dominant))\n" +
"(= (duration n1) 4)))\n" +
"		(target-notes (scale-duration 1/3\n" +
"							n1\n" +
"							(transpose-diatonic 2 n1)\n" +
"(transpose-diatonic 3 n1))))\n" +
"(transformation\n" +
"		(description triplets-up-1-2-4)\n" +
"		(weight 1)\n" +
"		(source-notes n1 n2)\n" +
"		(guard-condition (and	(= (duration n1) 4)\n" +
"(not (= (chord-family n1) dominant))\n" +
"(pitch<= n1 n2)))\n" +
"		(target-notes (scale-duration 1/3\n" +
"							n1\n" +
"							(transpose-diatonic 2 n1)\n" +
"(transpose-diatonic 4 n1))))\n" +
"(transformation\n" +
"		(description triplets-down)\n" +
"		(weight 1)\n" +
"		(source-notes n1)\n" +
"		(guard-condition (and (not (= (chord-family n1) dominant))\n" +
"(= (duration n1) 4)))\n" +
"		(target-notes (scale-duration 1/3\n" +
"							n1\n" +
"							(transpose-diatonic -1 n1)\n" +
"(transpose-diatonic -2 n1)))))";

String triplet_emb = "(substitution\n" +
"	(name triplet-embellish)\n" +
"	(type embellishment)\n" +
"	(weight 1)\n" +
"	(transformation\n" +
"		(description below-trill-from-small-note)\n" +
"		(weight 2)\n" +
"		(source-notes n1 n2 n3)\n" +
"		(guard-condition 	(and	(= (duration n1) 8)\n" +
"						(= (duration n2) 4)\n" +
"						(not (= (chord-family n1) dominant))\n" +
"(not 	(=	(relative-pitch n1)\n" +
"(relative-pitch n3)))))\n" +
"		(target-notes	n1\n" +
"				(set-duration 8/3 	(transpose-diatonic -1 n1)\n" +
"							n1\n" +
"							(transpose-diatonic -1 n1))))\n" +
"(transformation\n" +
"		(description below-down-from-small-note)\n" +
"		(weight 2)\n" +
"		(source-notes n1 n2)\n" +
"		(guard-condition 	(and	(= (duration n1) 8)\n" +
"						(= (duration n2) 4)\n" +
"						(not (= (chord-family n1) dominant))\n" +
"(pitch< n1 n2)))\n" +
"		(target-notes	n1\n" +
"				(set-duration 8/3 	(transpose-diatonic -1 n1)\n" +
"							(transpose-diatonic -1 n2)\n" +
"							(transpose-diatonic -1 n1))))\n" +
"(transformation\n" +
"		(description below-up-from-small-note)\n" +
"		(weight 2)\n" +
"		(source-notes n1 n2)\n" +
"		(guard-condition 	(and	(= (duration n1) 8)\n" +
"						(= (duration n2) 4)\n" +
"						(not (= (chord-family n1) dominant))\n" +
"(pitch> n1 n2)))\n" +
"		(target-notes	n1\n" +
"				(set-duration 8/3 	(transpose-diatonic -1 n1)\n" +
"							(transpose-diatonic 2 n2)\n" +
"							(transpose-diatonic -1 n1)))))";
        //Polylist polysub = Polylist.PolylistFromString(sub);
        //Substitution subcreated = new Substitution(polysub.assoc("substitution"));
        
        transform = new Transform(mordent+arpeggio+graceNote+identities+chromaticPassing+splittingQuarter+triplet_emb);
        
    }
    
}
