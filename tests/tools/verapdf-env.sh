#!/bin/bash
#Description to use: this script needs to provide 'path  to install veraPDF' as first argument

#Provided path to download veraPDF
timestamp=$1
cd $timestamp

echo "#Downloading veraPDF, latest dev version ... unziping to $PWD" 
curl -LO https://software.verapdf.org/dev/verapdf-installer.zip
unzip verapdf-installer.zip

#Definition of the variable, pointing to the veraPDF directory with installation files
veradir=$(ls | grep verapdf-greenfield)
cd "$veradir"

#Definition of the Env variable
export veraPATH="${timestamp}/../verapdf"
echo "veraPATH: $veraPATH" 

#Prepare  of the file: auto-install.xml
# curl -LO  https://raw.githubusercontent.com/veraPDF/veraPDF-regression-tests/integration/TOOLS/auto-install.xml
# OR
#cp /home/test/auto-install.xml ./

curl -LO  https://raw.githubusercontent.com/veraPDF/veraPDF-regression-tests/integration/tools/auto-install.xml

#Definition of the 'PATH' to install veraPDF using the auto-install.xml file
sed -i '5 c\        <installpath>'$(echo $veraPATH)'</installpath>' ./auto-install.xml

veraizpack=$(ls | grep verapdf-izpack-installer)
echo "veraizpack: $veraizpack"

#Performing installation
echo "Performing installation veraPDF ... "
java -jar "$veraizpack" auto-install.xml

#Checking version
currentIFS=$IFS 
IFS='-'
read -r -a array <<< "$veradir"
VERSION="${array[2]}"
IFS=$currentIFS

echo "veraPDF version: $VERSION"
echo "veraPATH: $veraPATH" 
echo "CurrentgDIR: $PWD"
