download: https://www.dropbox.com/s/i2zohq8oa0jmcop/Transparency.jar?dl=0

Demo
=========
java -jar Transparency.jar -i Feathers.png -c 0 0 255 -b background.jpg

Source feather
![demo before](http://i.imgur.com/LvC6FFP.png)

Isolated foreground:
![demo after](http://i.imgur.com/S6OBofz.png)

Blended into a background image:
![demo after](http://i.imgur.com/xEZ8WxW.png)

Mileage may vary. Best results when background is solid colors, and differentiable from foreground easily.


USAGE:
=========
java Transparency -i inputFile [-c r g b] [-o outputFile] [-b bgFile1 bgFile2 ...] [-p] [-d width height]
or
java -jar Transparency.jar -i inputFile [-c r g b] [-o outputFile] [-b bgFile1 bgFile2 ...] [-p] [-d width height] if you have a .jar
-i			Path to the input file.

-c			The RGB value to de-blend into transparency.
			Optional. Defaults to 0,0,0 (black).

-o			Path to the output file. Optional.
			Defaults to input file + "_out".

-b			List of paths to background files to blend into
			in order. Optional.

-p			Use premultiplied argb processing. Optional.

-d			Dimensions of the output. Optional.
			Defaults to the dimensions of the input.

-n			Don't attempt to deblend image first. Instead, blend
			input directly with any backgrounds. Optional.



Current issues:
If the input file is a file with transparency ... stuff goes wrong.
