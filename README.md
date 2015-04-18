# Transparency
Small tool that makes an image transparent by hue. Option to overlay resulting file over a background.

USAGE: java Transparency -i inputFile [-c r g b] [-o outputFile] [-b bgFile1 bgFile2 ...] [-p] [-d width height]

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

A better method of scaling should be implemented in the future.
