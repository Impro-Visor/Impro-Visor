/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>:58  2001

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

package jm.midi.event;

/***********************************************************
Description:
The interface VoiceEvt is the "parent" to a 
group of child classes representing MIDI voice event messages. 
These classes will usually be added to a linked list as type
Event.  The child classes instance variable id can be used
to distinguish easily between the child event types.<BR>
<BR>
001 - ATouch<BR>
002 - ChPres<BR>
003 - CChange<BR>
004 - NoteOff<BR>
005 - NoteOn<BR>
006 - PWheel<BR>
007 - PChange<BR>
<BR>
<BR>
<i>//This example shows how to add voice events to a list</i><BR> 
class makeScore<BR>
{   <BR>
<spacer type=horizontal size=20>  List violinPart = new List();<BR>
<spacer type=horizontal size=20>	VoiceEvt voiceEvt = new CChange();<BR>
<spacer type=horizontal size=20>	violinPart.insertAtBack(voiceEvt);<BR>
}<BR>
<BR>
<i>//This example prints the contents of a voice event list<BR>
//using the abstract method print(). </i> <BR>
class play_part<BR>
{<BR>
<spacer type=horizontal size=20>	ListNode node = violinPart.getFirstNode();<BR>
<spacer type=horizontal size=20>	while(node != null)<BR>
<spacer type=horizontal size=20>	{<BR>
<spacer type=horizontal size=40>		VoiceEvt voiceEvt = (VoiceEvt) node.getObject();<BR>
<spacer type=horizontal size=40>		voiceEvt.print();<BR>
<spacer type=horizontal size=20>	}<BR>
}<BR>
@author Andrew Sorensen
************************************************************/

public interface VoiceEvt extends Event
{
	/**Get a voice events MIDI channel*/
	public abstract short getMidiChannel();
	
	/**Set a voice events MIDI channel*/
	public abstract void setMidiChannel(short midiChannel);
}
