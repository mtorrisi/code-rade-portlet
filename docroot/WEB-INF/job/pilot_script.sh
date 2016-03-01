#!/bin/sh 
#
# myRepast-infection - portlet pilot script
#
# The following script does:
#   o Perform a simulation using the provided parameters
#   o Create a archive containinG the output file.  
# 
# Author: mario.torrisi@ct.infn.it
#

SW_NAME="repast" # Software name 

echo "--------------------------------------------------"
echo "Job execution starts on: '"$(date)"'"

echo "---[HOME directory]-------------------------------"
ls -l $HOME

echo "---[Working directory]-------------------------"
mkdir output
ls -l $(pwd)

/bin/bash $HOME/$SW_NAME/simulation.sh $1 $2 $3 $4 > stdout

#
# Following statement produce the simulation_output file
#
OUTFILE=simulation_output.txt
echo "--------------------------------------------------"  > $OUTFILE
echo "Simulation started at: '"$(date)"'"		  >> $OUTFILE 
echo ""                                                   >> $OUTFILE
echo "#################[  START LOG  ]##################" >> $OUTFILE
echo ""                                                   >> $OUTFILE
cat stdout			                          >> $OUTFILE
echo "#################[   END LOG   ]##################" >> $OUTFILE
echo ""                                                   >> $OUTFILE
echo "Simulation ended at: '"$(date)"'"		          >> $OUTFILE
echo "--------------------------------------------------" >> $OUTFILE
echo ""                                                   >> $OUTFILE

#
# Collect all generated output files into a single tar.gz file
#
tar cvfz myRepast-infection-Files.tar.gz $OUTFILE output/

#!/bin/sh 
#
# code-rade - portlet pilot script
#

## cvmfs stuff
# Environmental variables
export SITE=generic
export OS=u1404
export ARCH=x86_64
export CVMFS_DIR=/cvmfs/fastrepo.sagrid.ac.za/
export MODULE="/usr/bin/modulecmd bash"
export MODULE_VERSION=3.2.10
export MODULEPATH="/etc/environment-modules/modules:/usr/share/modules/versions:/usr/Modules/$MODULE_VERSION/modulefiles:/usr/share/modules/modulefiles"

#load module
$MODULE use $CVMFS_DIR/modules/libraries
$MODULE use $CVMFS_DIR/modules/compilers

# Get the hostname
HOSTNAME=$(hostname -f)

# In order to avoid concurrent accesses to files, the 
# portlet uses filename prefixes like
# <timestamp>_<username>_filename
# for this reason the file must be located before to use it
INFILE=$(ls -1 | grep input_file.java)

echo "--------------------------------------------------"
echo "Job landed on: '"$HOSTNAME"'"
echo "--------------------------------------------------"
echo "Job execution starts on: '"$(date)"'"

echo "---[WN HOME directory]----------------------------"
ls -l $HOME

echo "---[WN Working directory]-------------------------"
ls -l $(pwd)

echo "---[Macro file]---------------------------------"
cat $INFILE
echo

#
# Following statement simulates a produced job file
#
OUTFILE=hostname_output.txt
echo "--------------------------------------------------"  > $OUTFILE
echo "Job landed on: '"$HOSTNAME"'"                       >> $OUTFILE
echo "infile:        '"$INFILE"'"                         >> $OUTFILE
echo "outfile:       '"$OUTFILE"'"                        >> $OUTFILE
echo "SITE:          '"$SITE"'"                           >> $OUTFILE
echo "OS:            '"$OS"'"                             >> $OUTFILE
echo "ARCH:          '"$ARCH"'"                           >> $OUTFILE
echo "CVMFS_DIR:     '"$CVMFS_DIR"'"                      >> $OUTFILE
echo "MODULEPATH:    '"$MODULEPATH"'"                     >> $OUTFILE
echo "MODULE_VERSION:'"$MODULE_VERSION"'"                 >> $OUTFILE
echo "--------------------------------------------------" >> $OUTFILE
echo ""                                                   >> $OUTFILE

#sleep 10

# Producing an output file 
$MODULE avail
$MODULE add jdk/8u66
#java -version

#javac $INFILE
#java  $INFILE
#cat $INFILE >> $OUTFILE

#
# At the end of the script file it's a good practice to 
# collect all generated job files into a single tar.gz file
# the generated archive may include the input files as well
#
tar cvfz code-rade-Files.tar.gz $INFILE $OUTFILE

