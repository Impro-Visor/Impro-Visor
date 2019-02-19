Notes on the reharmonization/substitution study files
Robert M. Keller
17 Feburary 2019

In this directory are files created for the purpose of studying chord
substitutions and reharmonization. The following mechanism is used for naming
files:

Title-Orig.ls	  means this is the original leadsheet for the tune of this
                  title, as it comes from a book. Note that the producer of
		  the book has likely extracted the leadsheet from sheet music,
		  which itself may involve a certain amount of interpretation.

Title-MMM.ls	  means this is the leadsheet as reharmonization by musician
		  MMM. For example, MMM could be one of:
		       Champ	    for Champ Champagne
		       David	    for David Hazeltine
		       Frank	    for Frank Mantooth
		  All of the above are professional musicians. David is the
		  only one currently living.

		  In the above, I have not taken measures to capture a specific
		  style. The key signatures are also not indicated. However,
		  these can be played in Impro-Visor to get a feel for the
		  sound. But none of the sheets contain melodies.

Title-Orig-Stripped.ls
Title-MMM-Stripped.ls
		  These are the same leadsheets, but with only the chord parts.
		  They have not been saved by Impro-Visor, but should still
		  load and play. Saving will result in the addition of meta-
		  data, so these should not be re-saved.

Many chords are "slash-chords", For example D/C. These have two purposes:
     a. To indicate inversions. For example G/B would be the first inversion
        in classical theory -- a G chord (G B D) with B rather than G in the
	bass.

     b. To change the chord by adding notes and changing the bass at the same
        time. For example, D/C adds the note C to a D chord (D F# A) and puts
	the C in the bass. It effectively becomes a D7/C.

In addition to slashes used within chords, slashes are used for spacing.
The vertical bar | is a separate between measures. The same amount of
space is assumed between any two successive bars. Chord symbols within
a bar are spread to divide the space evenly. If an uneven division is desired,
/ may be used. For example:
      | C |          There is only one chord in this measure.
      
      | C D |        There are two chords, C at the beginning, and D
                     half-way through.
		     
      | C / / D |    C gets 3/4 of the space, and D 1/4. Each / functions as
                     if it were a spaceholder for a chord.

      | C | / D |    This represents two bars. The C in the first bar extends
                     into the second, taking half the space, while the D
		     takes the other half.

Also, if a bar is empty, it means that the previous chord extends into it:

      | C | | |      This is C extended over a total of 3 bars.


Some chords are more-or-less equivalent, such as C7 vs C9. But some are
altered versions or extensions of a basic chord, such as C7b9. It may
be necessary to program such equivalences explicitly.

Some reharmonizations might be best understood by including the melody notes.
Melody notes are sometimes considered as implied extensions of the chords.

The learning system might require transposing each sheet to 12 keys total. This
is often done. It would be nice if it weren't needed, but not needing it
requires that the system learn to understand transposition on its own.
Transposition can be achieved within Impro-Visor.


		  