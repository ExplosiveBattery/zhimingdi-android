#/bin/sh
PATH=/media/vega/0D6C051A0D6C051A/all/Wallpapers
dateStringPrefix="20180501 +"
num=0
dateStringPostfix=" day"

for i in $(echo $PATH/*); do 
	newFileName = ${PATH}/$(/bin/date -d "${dateStringPrefix}${num}${dateStringPostfix}" +%Y%m%d).jpg
	/bin/mv $i $newFileName
done

