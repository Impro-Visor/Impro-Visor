#! /bin/bash

clear				#Clear terminal window

#Prompt password input upon start, for later sudo commands
if [[ $UID != 0 ]]; then
    echo
    echo "*********************************"
    echo
    echo
    echo "Please run this script with sudo:"
    echo "sudo $0 $*"
    echo "Do this by typing the above and pressing enter. Then enter your password."
    echo
    echo
    echo "**********************************"
    exit 1
fi


#Inform user of programs to be installed
DIALOG=${DIALOG=dialog}

$DIALOG --title " To Be Installed" --clear \
        --yesno "For audio input, you need Java, SuperCollider with the Tartini plugin, cmake, a c compiler, and an FFT library. Continue?" 10 30

case $? in
  0)
    echo "You chose yes. Installations commencing";;
  1)
    echo "You chose no. Exiting now"; exit;;
  255)
    echo "ESC pressed. Exiting now"; exit;;
esac


#Things to be installed:
#Java
#SuperCollider
#clang/gcc (tested on clang, 3.2) - need to check if already installed
#cmake //assume installs make
#libfftw3*

#Installation portion


#Java installation
echo "Checking for java"
javaCheck(){
if hash java 2>/dev/null; then
	echo "Java already installed"
else 
	echo "Java not installed"
	while true; do
    		read -p "OK to purge openjdk and install Java 7? 
			Press y or n and then enter - " yn
    		case $yn in
        		[Yy]* ) echo "You chose yes"; break;;
        		[Nn]* ) echo "You chose no"; exit;;
        		* ) echo "Please answer yes or no.";;
    		esac
	done
	#If user agrees to download, download
	sudo apt-get purge openjdk*			#Purge
	sudo add-apt-repository ppa:webupd8team/java 	
	sudo apt-get update 
	sudo apt-get install oracle-java7-installer
	echo "Java 7 now installed"
fi
}
javaCheck


#SuperCollider installation <3
echo "Checking for SuperCollider install"
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys FABAEF95
sudo add-apt-repository ppa:supercollider/ppa
sudo apt-get update
sudo apt-get install supercollider supercollider-dev supercollider-common supercollider-server supercollider-ide
echo "SuperCollider installed and up-to-date"


#DONT FORCE USER TO INSTALL
#Check for installations of clang
#TODO give option for already present cxx compiler
echo "Checking for c compiler"
cCompilerCheck (){
	if hash clang 2>/dev/null; then
		echo "C compiler already installed: you have clang"
	elif hash gcc 2>/dev/null; then
		echo "C compiler already installed: you have gcc"
	elif hash tcc 2>/dev/null; then
		echo "C compiler already installed: you have tcc"
	else
		while true; do
    			read -p "OK to install clang (used for SuperCollider 
			plugin)? Press y or n and enter - " yn
    			case $yn in
        			[Yy]* ) echo "You chose yes"; break;;
        			[Nn]* ) echo "You chose no"; exit;;
        			* ) echo "Please answer yes or no.";;
    			esac
		done
		echo "Installing clang"
		sudo apt-get install clang	#install clang if no other
		echo "Clang installed"
fi
}
cCompilerCheck


#Check for installations of cmake
echo "Checking for cmake"
cmakeCheck (){
	if hash cmake 2>/dev/null; then
		echo "Cmake already installed, no installation required"
	else
		echo "Installing cmake"
		sudo apt-get install cmake	#cmake installed
		echo "Cmake installed"
	fi
}
cmakeCheck


#Install libfftw3
echo "Checking for libfftw3"
fftw3Check (){
	echo "Checking for/installing libfftw3 - for calculating 
		fast-fourier transforms"
	sudo apt-get install libfftw3*
	echo "Libfftw3 items installed/updated"
}
fftw3Check
	
#Install plugins in home and renaming
echo "Installing necessary plugins for SuperCollider"
cd ~
wget http://iweb.dl.sourceforge.net/project/sc3-plugins/Source%20Release/sc3-plugins-src-2012-05-26.tgz	#get from website
echo "Extracting"
tar xvzf sc3-plugins-src-2012-05-26.tgz
rm sc3-plugins-src-2012-05-26.tgz
cd ~
echo "Renaming plugins folder"
mv sc3-plugins-src-2012-05-26 sc3plugins	#rename for later

#Compiling, renaming, moving plugin files.
#Assume already extracted sc3plugins plugin folder to home
echo "Compiling, renaming, moving plugin files"
cd sc3plugins
mkdir build
cd build
cmake -DCMAKE_INSTALL_PREFIX=/usr -DSC_PATH=/usr/include/SuperCollider/ ..      
##HAD SOME ISSUES with fftw3 lib stuff not found
echo "Installation in progress"
make
sudo make install


#navigate to usr/share/SuperCollider/Extensions/SC3plugins.
#If Extensions doesn't exist, create folder for it
#and also SC3plugins folder
#ExtensionDirectory=~/../../usr/share/SuperCollider/Extensions
#dirCheck1(){
#if [ -d "$ExtensionDirectory" ]; then
#	echo "Extensions folder present"
#else
#	echo "Extensions folder not present, creating now"
#	c
#	mkdir ~/../../usr/share/SuperCollider/Extensions
#fi
#}
#dirCheck1

#SC3pluginsDirectory=~/../../usr/share/SuperCollider/Extensions/SC3plugins
#dirCheck2(){
#if [ -d "$SC3pluginsDirectory" ]; then
#	echo "Plugins folder present"
#else
#	echo "Plugins folder not present, creating now"
#	mkdir ~/../../usr/share/SuperCollider/Extensions/SC3plugins
#fi
#}

echo "Cleaning up files"
cd ~/../../usr/share/SuperCollider/Extensions/SC3plugins
sudo mv PitchDetection ..
sudo rm -r *
cd ..
sudo mv PitchDetection SC3plugins
sudo mv ~/sc3plugins/build/source/PitchDetection.so .


echo "All done! For help on audio input, please see the help menu
in ImproVisor."





