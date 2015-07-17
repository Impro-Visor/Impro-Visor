(

GUI.qt;          // Platform independent GUI system
MIDIClient.init; // initialize the MIDIClient on Server

~onOrOff     = false;
~pitch       = 0;
~changePitch = 0;
~lastPitch   = 0;
~resolution  = 7;
~currentRes  = 0;
~highThresh  = -22;
~lowThresh   = -40;
~delay       = 0.1;
~median      = 63;
~currentDir  = thisProcess.nowExecutingPath.dirname; // seemingly platform independent (Check TODO)


/*
    * * * * * * * * * * *
   * - - - - - - - - - - *
  * -       GUI         - *
  * -       CODE        - *
   *  - - - - - - - - -  *
    * * * * * * * * * * *
*/

Window.closeAll; // Remove once development and debugging is complete (TODO)

~midiWarning = {
	//TO DO
};

~audioWarning = {
	//TO DO
};

~gooey = {
	arg x_pos = (100), y_pos = (75), width = (500), height = (500);
	var w, deviceBtn, configBtn, loadConfigBtn, saveBtn, tab_width, tab_height, tab_x, tab_y,
	levelSlider, resolutionSlider, midis, audios, deviceView, configView, stageBounds,
	audioInBtn, midiOutBtn, highThreshBox, lowThreshBox, resBox, delayBox, volumeSlider, delaySlider;

	tab_width   = 150;
	tab_height  = 30;
	tab_x       = 100;
	tab_y       = 20;
	stageBounds = Rect(0, y_pos + (y_pos / 6), width, height - (height / 3));

	w             = Window("Pitch Tracker", Rect(x_pos, y_pos, width, height), false);
	deviceBtn     = Button(w, Rect(tab_x, 20,  tab_width, tab_height)).states_([["Device"], ["Device Options"]]);
	loadConfigBtn = Button(w, Rect(tab_x, 450, tab_width, tab_height)).states_([["Load Config File"]]);
	saveBtn       = Button(w, Rect(tab_x + tab_width + 15, 450, tab_width, tab_height)).states_([["Save"]]);
	configBtn     = Button(w, Rect(tab_x + tab_width + 15, 20,  tab_width, tab_height))
	.states_([["Config"], ["Configuration Options"]]);

	// Global View
	deviceView = CompositeView(w, stageBounds).background_(Color.fromHexString("#FFFFFF"));
	configView = CompositeView(w, stageBounds).background_(Color.fromHexString("#FFFFFF"));

	// Device View Elements
	StaticText(deviceView, Rect(tab_x, tab_x - 20, 300, 30)).string_("Audio Input Device");
	StaticText(deviceView, Rect(tab_x, tab_x + 50, 300, 30)).string_("MIDI Output Device");
	audioInBtn   = PopUpMenu(deviceView, Rect(tab_x, tab_x, 300, 30));
	midiOutBtn   = PopUpMenu(deviceView, Rect(tab_x, tab_x + 70, 300, 30));

	StaticText(w, Rect(tab_x, 100, 300, 40)).string_("Input Volume");
	volumeSlider = LevelIndicator(w, Rect(tab_x, 125, 300, 30));

	// Config View Elements
	StaticText(configView, Rect(tab_x, tab_x - 25, 300, 30)).string_("Levels");
	StaticText(configView, Rect(tab_x - 50, tab_x + 50, 300, 30)).string_("Resolution");
	StaticText(configView, Rect(tab_x - 50, tab_x - 25, 30, 30)).string_("Low");
	StaticText(configView, Rect(width - 75, tab_x - 25, 30, 30)).string_("High");
	StaticText(configView, Rect(tab_x - 50, tab_x + 125, 300, 30)).string_("Delay");
	lowThreshBox     = NumberBox(configView, Rect(tab_x - 50, tab_x, 30, 30));
	highThreshBox    = NumberBox(configView, Rect(width - 75, tab_x, 30, 30));
	resBox           = NumberBox(configView, Rect(width - 75, tab_x + 70, 30, 30));
	delayBox         = NumberBox(configView, Rect(width - 75, tab_x + 145, 30, 30));
	levelSlider      = RangeSlider(configView, Rect(tab_x, tab_x, 300, 30));
	resolutionSlider = Slider(configView, Rect(tab_x - 50, tab_x + 70, 350, 30));
	delaySlider      = Slider(configView, Rect(tab_x - 50, tab_x + 145, 350, 30));


	// Attaching actions to Buttons
	deviceBtn.action = {
		arg view, char;

		deviceBtn.value = 1;
		configBtn.value = 0;

		deviceView.visible = true;
		configView.visible = false;
	};

	levelSlider.action = {
		lowThreshBox.value  = (levelSlider.lo * 80 - 80).asInt;
		highThreshBox.value = (levelSlider.hi * 80 - 80).asInt;

		~highThresh = highThreshBox.value;
		~lowThresh = lowThreshBox.value;
		~threshTest.set("highThresh", ~highThresh, "lowThresh", ~lowThresh);
	};

	highThreshBox.action = {
		~highThresh = highThreshBox.value;
		~threshTest.set("highThresh", ~highThresh);
	};

	lowThreshBox.action = {
		~lowThresh = lowThreshBox.value;
		~threshTest.set("lowThresh", ~lowThresh);
	};

	delaySlider.action = {
		delayBox.value = (delaySlider.value / 4);
		~delay = delayBox.value;
		~median = (((~delay * 311).asInt * 2) + 1);
		~freqTrack.set("delayTime", ~delay, "median", ~median);
		~threshTest.set("delayTime", ~delay);
	};

	delayBox.action = {
		if ( delayBox.value > 0.25, { delayBox.value = 0.25 });
		if ( delayBox.value < 0,    { delayBox.value = 0 });

		delaySlider.value = delayBox.value * 4;

		~delay = delayBox.value;
		~median = (((~delay * 311).asInt * 2) + 1);
		~freqTrack.set("delayTime", ~delay, "median", ~median);
		~threshTest.set("delayTime", ~delay);
	};

	lowThreshBox.action  = {
		if ((lowThreshBox.value > highThreshBox.value), {
			lowThreshBox.value = highThreshBox.value;
		});

		if ((lowThreshBox.value < -80), {
			lowThreshBox.value = -80;
		});

		levelSlider.lo = 1 - (lowThreshBox.value / -80);
	};

	highThreshBox.action = {
		if ((highThreshBox.value < lowThreshBox.value), {
			highThreshBox.value = lowThreshBox.value;
		});

		if ((highThreshBox.value > 0), {
			highThreshBox.value = 0;
		});

		levelSlider.hi = 1 - (highThreshBox.value / -80);
	};

	configBtn.action = {
		arg view, char;

		configBtn.value = 1;
		deviceBtn.value    = 0;
		configView.visible = true;
		deviceView.visible = false;
	};

	saveBtn.action = {
		f = File(~currentDir +/+ "configure.txt", "w");

		f.write(lowThreshBox.value  + "\n");
		f.write(highThreshBox.value + "\n");
		f.write(resBox.value        + "\n");
		f.write(delayBox.value      + "\n");

		"Sucessfully saved!".postln;

		f.close;
	};

	loadConfigBtn.action = {
		arg view, char, f, data;

		f = File(~currentDir +/+ "configure.txt", "r");
		if (f.isOpen, {


			//lowThresh
			~lowThresh = f.getLine.asFloat;
			~threshTest.set("lowThresh", ~lowThresh);

			levelSlider.lo = (((~lowThresh) + 80) / 80);
			lowThreshBox.value = ~lowThresh;

			//highThresh
			~highThresh = f.getLine.asFloat;
			~threshTest.set("highThresh", ~highThresh);

			levelSlider.hi = (((~highThresh) + 80) / 80);
			highThreshBox.value = ~highThresh;

			//resolution
			~resolution = f.getLine.asFloat;

			resolutionSlider.value = ~resolution / 100;
			resBox.value = ~resolution;

			//delay
			~delay = f.getLine.asFloat;
			~threshTest.set("delayTime", ~delay);
			~median = (((~delay * 311).asInt * 2) + 1);
			~freqTrack.set("delayTime", ~delay, "median", ~median);

			delaySlider.value = ~delay * 4;
			delayBox.value = ~delay;

			/*"\n".postln;
			"GLOBALS:".postln;
			" ------------------------".postln;
			("lowThresh =" + ~lowThresh).postln;
			("highThresh =" + ~highThresh).postln;
			("resolution =" + ~resolution).postln;
			("delay =" + ~delay).postln;
			("median =" + ~median).postln;
			" ------------------------".postln;
			"\n".postln;
			"ARGS:".postln;
			" ------------------------".postln;
			~threshTest.get(\lowThresh, { arg value; ("lowThresh = " + value).postln; });
			~threshTest.get(\highThresh, { arg value; ("highThresh = " + value).postln; });
			~threshTest.get(\delayTime, { arg value; ("threshDelayTime = " + value).postln; });
			~freqTrack.get(\delayTime, { arg value; ("freqDelayTime = " + value).postln; });
			~freqTrack.get(\median, { arg value; (("median = " + value) + "\n" + "------------------------").postln; });*/



		});

		f.close;
	};

	resolutionSlider.action = {
		var val;

		val = ControlSpec(0, 100, \linear, 1.00).map(resolutionSlider.value);
		resBox.value_(val);
		~resolution = resBox.value;
	};

	resBox.action = {
		var val;
		if ((resBox.value > 100), {
			resBox.value = 100;
		});

		if ((resBox.value < 0), {
			resBox.value = 0;
		});

		resolutionSlider.value = resBox.value / 100;
	};

	audioInBtn.action = {
		s.options.inDevice = ServerOptions.inDevices[audioInBtn.value];
		s.quit;
		s.waitForBoot(~backEnd);
	};

	midiOutBtn.action = {
		m = MIDIOut(midiOutBtn.value);
	};

	// initialization parameters
	midis = [];
	MIDIClient.destinations.do({
		arg item, arr;

		midis = midis.add(item.device + item.name);
	});
	midiOutBtn.items_(midis);
	audioInBtn.items_(ServerOptions.inDevices);

	~oscr5.remove;
	~oscr5 = OSCresponder(nil, 'amp', {
		|time, resp, msg, vol|

		vol = msg[3];
		{ volumeSlider.value = vol }.defer;
	}).add;

	lowThreshBox.value  = ~lowThresh;
	highThreshBox.value = ~highThresh;
	levelSlider.lo      = (~lowThresh + 80) / 80;
	levelSlider.hi      = (~highThresh + 80) / 80;

	deviceBtn.value     = 1;
	deviceView.visible  = true;
	configView.visible  = false;

	midiOutBtn.value = -1;
	audioInBtn.value = -1;

	delaySlider.value = ~delay * 4;
	delayBox.value    = ~delay;

	resolutionSlider.value = ~resolution / 100;
	resBox.value           = ~resolution;

	w.background_(Color.fromHexString("#C0D1FF"));
	w.front.alwaysOnTop_(true);
	w.onClose = { "Keep that code tight!".postln; s.quit; 0.exit; };

}.value;

~backEnd = {
	~freqTrack = SynthDef(\freqTrack, {
		arg threshold = (0.93), median = (63), framesize = (512), overlap = (256), delayTime = (0.10);
		var freq, in, tart, changeFreq;

		/*

		FreqTrack: Pipe in to Pitch
		calculates the MIDI number of the input sound

		*/


		in         = SoundIn.ar(0);
		tart       = Tartini.kr(in, threshold, framesize, 0, overlap);
		freq       = Median.kr(median, tart).cpsmidi.round;
		changeFreq = DelayN.kr(Median.kr((median + 4), tart).cpsmidi.round, delaytime: delayTime);

		SendReply.ar(Impulse.ar(50), 'pitch', [freq]);
		SendReply.ar(Impulse.ar(50), 'changePitch', [changeFreq]);
	}).play(args: [\median, ~median, \delayTime, ~delay]);


	~threshTest = SynthDef(\threshTest, {
		arg highThresh = -22, lowThresh = -40, delayTime = (0.10);
		var amp, highTrig, lowTrig, in, sig;

		in  = SoundIn.ar(0);
		sig = DelayN.ar(in, delaytime: delayTime);
		amp = Amplitude.kr(sig).ampdb; // return Amplitude to to Decibals
		amp = K2A.ar(amp);             // to improve resolution of amplitude tracking control -> audio rate


		highTrig = amp >= highThresh;
		lowTrig  = amp <= lowThresh;

		SendReply.ar(Impulse.ar(50), 'highTrigVal', [highTrig]); // Respective triggers for noteOn
		SendReply.ar(Impulse.ar(50), 'lowTrigVal', [lowTrig]);   // and noteOff
		SendReply.ar(Impulse.ar(30), 'amp', (((RunningSum.kr(Amplitude.kr(in).ampdb, 50) / 50) + 80)/80));
	}).play(args: [\highThresh, ~highThresh, \lowThresh, ~lowThresh, \delayTime, ~delay]);

	/*

	OSC Responder (event emitters) for OSC messages
	serve the purpose of dispatching anonymous functions

	*/

	~oscr4.remove;
	~oscr4 = OSCresponder(nil, 'pitch', {
		|time, resp, msg|

		~currentRes = ~currentRes -1;
		if(not(msg[3] == ~pitch), {
			~pitch = msg[3]; // save pitch to global variable ~pitch
		});

	}).add;

	~oscr3.remove;
	~oscr3 = OSCresponder(nil, 'changePitch', {
		|time, resp, msg|

		~changePitch = msg[3];
		if ((~currentRes <= 0) && ~onOrOff && not(~lastPitch == ~changePitch) &&
			not(~lastPitch == ~pitch) && (~pitch == ~changePitch), {
				"note on slur, pitch ".post;
				~pitch.postln;
				~lastPitch.postln;
				if(~onOrOff,
					m.noteOff(1, ~lastPitch, 60);
				);
				m.noteOn(1, ~pitch, 60);
				~lastPitch = ~pitch;
				~currentRes = ~resolution;
		});

	}).add;

	~oscr1.remove;
	~oscr1 = OSCresponder(nil, 'highTrigVal', {
		|time, resp, msg|

		if((msg[3] == 1) && not(~onOrOff) && (~currentRes <= 0), {
			"note on, pitch ".post;
			~pitch.postln;
			m.noteOn(1, ~pitch, 60);
			~lastPitch = ~pitch;
			~onOrOff = true;
			~currentRes = ~resolution;
		});

	}).add;

	~oscr2.remove;
	~oscr2 = OSCresponder(nil, 'lowTrigVal', {
		|time, resp, msg|

		if((msg[3] == 1) && (~onOrOff) , {
			"note off".postln;
			m.noteOff(1, ~lastPitch, 60);
			~onOrOff = false;
		});
	}).add;

}

)