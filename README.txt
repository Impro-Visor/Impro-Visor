
Welcome to Impro-Visor (Improvisation Advisor) Version 8.11,
from Prof. Bob Keller at Harvey Mudd College, May 31, 2016.

Release notes for this version may be found at the end.

If you need help, please post to the Yahoo! impro-visor user group:

    http://launch.groups.yahoo.com/group/impro-visor/

From the group, you may also obtain The Imaginary Book which contains a
large number of chords-only leadsheets, as well as other useful
resources.

Impro-Visor is free and runs on any platform that supports Java 1.7 or
later, including:
 
    Windows (XP, 2000, Vista, 7, 8, 10)
    MacOSX
    Linux 

Java virtual machines are currently bundled in the release for Windows and
MacOSX, but not for Linux.

The official information site for Impro-Visor is:

    http://www.cs.hmc.edu/~keller/jazz/improvisor

which is the same as

    http://www.impro-visor.com

The official download site for Impro-Visor is sourceForge:

    http://sourceforge.net/projects/impro-visor/

Download the installer that is provided for your platform, 
then launch the installer.

The sources are currently maintained on github:

    https://github.com/Impro-Visor/Impro-Visor

Once the program is installed, there should be a launcher

    Impro-Visor

that will run the program proper. The first time the program is run it
will set up a folder in your user home for your personal version of
various files.

Alternatively, you may run by double-clicking the file: 

    improvisor.jar

which is a Java archive. (Sometimes Windows will try to unpack this as an
archive, in which case you would need to do java -jar improvisor.jar on
the command line instead.)

You will need to adjust MIDI settings to get sound on your system.
The Impro-Visor MIDI control panel is identified by a black circular icon 
(representing a MIDI connector) on the right side of the upper icon bar. 
Set the Output to one of:
    Gervill
    Microsoft GS Wavetable SW Synth  
    Microsoft MIDI Mapper
or
    some external synthesizer.

Gervill is built into Java by Oracle Corporation, but it is inferior to the
earlier Java synthesizer. We recommend using one of the following instead:

    On Windows: VirtualMidiSynth from
        http://coolsoft.altervista.org/en/virtualmidisynth
    You will need to download a sound font in .sf2 format. We recommend 
    following the instructions at
        https://musescore.org/en/handbook/soundfont 

    On MacOSX or Windows: Kontakt5 Player from
        https://www.native-instruments.com/en/products/komplete/samplers/
            kontakt-5-player/free-download/
    You will also need to download the free Factory Instruments.

You can also use any external MIDI instrument, such as most digital pianos.


You cannot start the application by clicking on individual leadsheet files.
Instead these files must be opened from within Impro-Visor.
 

I am pleased to acknowledge contributions from the following developers:

 Stephen Jones, Aaron Wolin, David Morrison, Martin Hunt, 
 Steven Gomez, Jim Herold, Brandy McMenamy, Sayuri Soejima, Emma Carlson, 
 Jon Gillick, Kevin Tang, Stephen Lee, Chad Waters, John Goodman, Lasconic, 
 Julia Botev, Michael Carney, Paul Hobbs, Ryan Wieghard, Amos Byon, 
 Zachary Merritt, Xanda Schofield, August Toman-Yih, David Halpern,
 Jack Davison, Audrey Musselman-Brown, Kevin Choi, Brian Howell, 
 Caitlin Chen, Nicolas Chung, Anna Turner, Hayden Blauzvern, 
 Nate Tarrh, Brian Kwak, Kelly Lee, Willem Engen, Eric Chicot,
 Mark Heimann, Nava Dallal, Carli Lessard, Alex Putman, Becki Yukman
 Errick Jackson, Nathan Kim, Zachary Kondak, Mikayla Konst,  
 Baunnee Martinez, Daniel Scanteianu, Amelia Sheppard, and David Siah

We hope you enjoy using the program. 

Sincerely,

Robert M. (Bob) Keller, Impro-Visor Project Director
Professor of Computer Science
Harvey Mudd College
Claremont, CA 91711

Please send issues to: keller@cs.hmc.edu

===============================================================================
Release notes for Impro-Visor 8.11

This release fixes a bug in Active Improvisation, namely that the main window
controls become disabled if the Active Improvisation window is opened more
than once.

It also adds a MIDI setting to correct for input delay in the case that the
audio Pitch Tracker is being used to convert audio to MIDI.

===============================================================================
Release notes for Impro-Visor 8.1

The newest feature is Active Improvisation, in which Impro-Visor will react
to what the user plays on a MIDI instrument, such as a keyboard. Still 
present is Passive Trading, which was formerly available. 

Now there are some new forms of passive improvisation:

    Division: Which sub-divides the interval between successive notes of
    a generated line, in some cases multiple times (as set by the Division 
    control panel).

    One way to get a line for Division is to use Guide Tones, and there is
    now a facility for generating them.

    Guide Tone and Transform: Which applies a transform from a 
    Transformational Grammar to a guide tone line.

    Interval Learning: which generates based on a Markov Chain derived from
    statistics taken from a solo.

    Theme Weaving: which reuses one or more themes specified by the user
    among lines generated by a Grammar.

There is a new Fluid Voicing Editor for generating piano-oriented chord 
voicings based on parameters settable by the user.

There is now a range filter for midi input, so that, for example one could
play the left-hand on a piano and have only the right-hand notes entered
(assuming they are in the specified range).

Starting note can be displayed in the roadmap if melody is present in the
leadsheet. Also styles can be displayed on the roadmap (in case a tune
has multiple styles).

Bugs fixed include:

    Certain types of bricks no longer get mangled in the roadmap preview.

    Voicing keyboard no longer fails to display certain voicings during
    playback.

The number of widgets in the main window has been reduced for a simplified
interface. The functionality of removed widgets is available through other
menus, such as the View menu.

===============================================================================
Release notes for Impro-Visor 7.0

There is a new grammar learning method based on relative pitches.

There is new solo generation method based on transformations from a basic
melody.

Trading can now be done with any grammar, not just ones designated for trading.

The former Solo Generator has been enhanced to become the Theme Weaver. There
is still a bit of work to be done on it.

There is a menu for quantize a melody to a specified number of sub-divisions
of a beat. This can be used to clean up melodies that are played by a human
in real-time on a MIDI instrument.

There are some new elements, such as arpeggiation, in the style pattern editor.

Individual style patterns (rather than just whole styles) can be named and
included by reference rather than by literally copying them.

The section sub-divider interface has been changed slightly.

===============================================================================
Release notes for Impro-Visor 6.0

Now the pitch of the note is displayed to the left of the arrow-head note
cursor. This may be turned off in the View menu.

There is an alternate cursor in the shape of note head. The note head will
have a line through it if the note is on a line, and no line if the note is
on a space.

The Style/Section editor has been changed to allow greater flexiblity.

Sections may be added and removed on the staff by using a shift-click.

There is an option in the View menu to display Major 7 chords using a delta, 
and minor 7 flat 5 chords using a phi symbol, as is done in some leadsheets 
such as Aebersold and the Real Book.

There is a virtual keyboard for entering notes on the screen. It has an
advising option that indicates whether a note is a chord tone or color tone,
as well as some other options. This is opened as Advising Keyboard from the
Utilities menu or with control-shift-K. 

There is now an option within Import MIDI Tracks from File to infer chords.

Now the Style Extractor does not require a leadsheet file to specify chords;
extraction is done only form MIDI files. There are some added styles that were
extracted from MIDI files generated by ChordPulse.

The grammar formalism contains some new constructs, including the ability
to specify relative pitches (rather than just abstract notes), and other
built-ins, such as for generating George Garzone's triadic melodies. There
is also a grammar for Jerry Bergonzi's method of improvisation, described in
his book Melodic Structures.

Now a new leadsheet can be constructed from a roadmap.

The roadmap analysis algorithm has been changed to use harmonic tempo.

Now the Improv button will start improvisation over the selection and loop
continuously, until the button is toggled off.

There is a **preliminary** facility for audio input, however it requires 
co-installation of SuperCollider with the Tartini plug-in. Audio is converted
to MIDI, which Impro-Visor can input in real-time.

===============================================================================

Release notes for Impro-Visor 5.16 

Improves MIDI recording, allowing resolution to be set. 

MIDI recording can now be done in conjunction with improvisation.

Memory leakage during recording is greatly reduced.

Re-organizes grammars for trading, in the form trade-4-A-chord, etc.
Here 4 is the number of bars traded, A means the computer goes first,
B means the user goes first. 'chord' means that tones are constrainted to
chord tones and approach tones. 'color' means that color tones and
scale tones are also used.

The layout of the main window is changed slightly for better ergonomics.

===============================================================================

Release notes for Impro-Visor 5.15 

Improves handling of MIDI track importing.

Improves style extraction from MIDI capabilities.

===============================================================================

Release notes for Impro-Visor 5.14 (pre-release)

Bug fix: Fixes MusicXML export that was broken in 5.12, 5.13.

Adds preliminary MIDI file import. Now a MIDI file can be loaded and played
as such. Single tracks can be imported as Impro-Visor melodies.
(We do not import an entire leadsheet with chord symbols, etc.)
Sysex events in the MIDI file are ignored.

Improves Style Extraction editor and repairs some long standing problems
in that area.

Replaces Generate button with Improvise toggle button. Now improvisation
will continue until the button is toggled a second time. Playback stop is
still through the stop button or the K key.


===============================================================================

Release notes for Impro-Visor 5.13

Bug fix: Selecting New Leadsheet (control-N) caused the program to hang.

===============================================================================

Release notes for Impro-Visor 5.12

Added volume specification options to style specifications. (Use Vnnn where
nnn is an integer between 0 and 127 to control volume in notes following
this term, up to the next V setting.)

Changed Style Editor and Piano Roll Editor to accommodate volumes.

Changed the layout of the Piano Roll Editor so that controls are at the top.

Now the percussion instrument names can be names rather than numbers as before.
Numbers are still accepted, but when style files are written, names will be
used.

Now MIDI channels can be assigned (in the Mixer panel), rather than be
confined to fixed settings (melody = 1, chords = 4, bass = 7, drums = 10).

Now there is an option to send MIDI Bank 0 Select before notes. This is
set in the MIDI Preferences panel.

Now each percussion instrument is assigned a separate MIDI track. This can
be useful if the MIDI output is used as input to a Digital Audio Workstation,
for example.

Updated style files to use names for percussion instruments, added some 
volume settings, and removed some redundant or unwanted patterns.

Fixed a problem in rendering bass lines, which was causing the bass instrument
to move out of range.

Fixed a problem with saving styles containing weights with decimal points
which would cause them to fail to load.


===============================================================================

Release notes for Impro-Visor 5.11

Fixes a bug that prevented roadmaps from opening.

===============================================================================

Release notes for Impro-Visor 5.10

-------------------------------------------------------------------------------
The Style Editor workings have been greatly improved.  Looping now works without
having to set an inter-loop delay.  Copying and pasting of large groups of 
cells is silent, as is creation of a pianoroll.

-------------------------------------------------------------------------------
The shortcut for creating a pianoroll for a column is now control-shift-click,
rather than shift-click as before.  (Shift-click is used to extend a multi-cell
selection.)

-------------------------------------------------------------------------------
A bug was fixed in style rendering for playback.  It only arose in certain 
styles, such as una-mas and senor-blues.

-------------------------------------------------------------------------------
A bug was fixed wherein MIDI input entered during count-in would cause
the program to lock up.

-------------------------------------------------------------------------------
There is a remaining problem with MIDI input.  If used for a long time, 
memory will fill up and the program will start slowing down and eventually
need to be restarted.  Usually this won't happen until after a couple of
choruses have been entered. The problem is being worked.

===============================================================================

Release notes for Impro-Visor 5.09 (pre-release for 5.10)

-------------------------------------------------------------------------------
Windows users: You may need to set your MIDI settings (identified by the
black circular icon) in Impro-Visor the first time you use this release. Use

    Microsoft GS Wavetable SW Synth  
if not using other MIDI devices. Use

    Microsoft MIDI Mapper

if using other MIDI devices. The setting

    Java Sound Synthesizer 

might not work.

Impro-Visor should remember your setting the next time you launch.

-------------------------------------------------------------------------------
Added a new "push" element to style specifications, so that a chord can be
struck before it appears in the leadsheet. The swing style is the only one
currently using this feature. The former swing style has been renamed 
swing-square-comp.

-------------------------------------------------------------------------------
The Section and Style Settings have been reworked. Now a style for any section
other than the first can be specified as "Use style of previous section".
It will appear as an asterisk in the style position. The point of this
feature is that the style of an entire leadsheet can be changed without
changing the styles of each section individually. This is important for
roadmaps, since they will tend to use more sections to indicate harmonic
phrasing.

-------------------------------------------------------------------------------
Some problems with MIDI selection have been worked out. It is believed that
changing MIDI instruments will no longer wedge the program. 

NOTE: Any MIDI instruments used must be in place and running before launching 
Impro-Visor. This includes software and hardware instruments.

-------------------------------------------------------------------------------
Preference settings have been changed to 1-click. Icons for the various
preferences are found at the right end of the icon tool bar. They are,
left to right:

    Global settings (a picture of the Earth globe)

    Leadsheet settings (a leadsheet image)

    Chorus settings(a treble clef and time signature)

    Style and section settings (stylized note symbols)

    MIDI settings (a MIDI connector cross-section)

    Contour drawing settings (a pencil)

Except for MIDI, these are the same icons as in previous versions. Once the
preference window is opened, the icons inside can be used to select the
various types of preference, as before.

-------------------------------------------------------------------------------
In the Roadmap window, the option of selecting play-on-click. This means
that clicking a brick will immediately play that brick.

-------------------------------------------------------------------------------
The number of measures per line for roadmaps is now saved with the leadsheet.
The default is 8. Currently this number can only be set by starting with a
roadmap and creating a leadsheet, or by editing the text of the leadsheet
using the textual editor (or an external editor).

-------------------------------------------------------------------------------
The small status indicator in the tool bar has been replaced with text
having green background in the uppermost menu-bar. Some improvements have
been made in the information conveyed by the status indicator.

-------------------------------------------------------------------------------
The button for toggling note beaming has been replaced with a checkbox in the
View Menu.

-------------------------------------------------------------------------------
A Recur button and Lead Beats spinner have been added next the Generate button.
We are gradually moving toward the ability to have Impro-Visor generate
choruses indefinitely. Currently this works by generating the next chorus 
just before the current chorus ends. The default setting is 1.05 beats before.
This setting is touchy, in that if it is not just right, the next chorus will
start too early or too late. The amount of beats required will depend on the
tempo and the complexity of the generating grammar.

-------------------------------------------------------------------------------
When a selection or chorus is being played, the slot construction lines are
temporarily removed. The chord symbols are still shown in red.

-------------------------------------------------------------------------------
A few new grammars have been added, including some for trading twos and eights,
with either the computer first ("My") or the player first ("Your"). I also
added a "Woody Shaw" grammar and a "Wes Montgomery" grammar derived from one
each of their respective solos. Also, there is a Chord+Approach grammar that
yields more "inside" melodies, as it does not introduce color tones 
intentionally.

-------------------------------------------------------------------------------
Impro-Visor will now remember the last grammar used, and re-open with that
grammar.

-------------------------------------------------------------------------------
Impro-Visor will now remember the last style edited, and re-open the style
editor with that style.

-------------------------------------------------------------------------------
Fixed a bug in interpreting the textual leadsheet notation, wherein multiple
dots on a note were wrongly interpreted. For example c2.. is now equivalent to
c2+4+8

-------------------------------------------------------------------------------
Fixed a bug in the style editor wherein the checkboxes were being ignored.
(The checkboxes are an indication not to save the instruments of those rows.)

-------------------------------------------------------------------------------
Fixed a bug in the lick generator wherein parameters of the grammar, such
as pitch range, were not being set unless the lick generator control panel
is opened.

-------------------------------------------------------------------------------
The recovery from a bad leadsheet file is somewhat improved. It is possible 
to escape the endless cycle caused by restarting with a bad leadsheet.

-------------------------------------------------------------------------------
The number of bars per line was increased from 15 to 64.

-------------------------------------------------------------------------------
The keyboard display, if used, now continues to update after the first
chorus.

-------------------------------------------------------------------------------

End of release notes for Impro-Visor 5.09

-------------------------------------------------------------------------------
