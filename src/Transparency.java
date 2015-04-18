class Transparency
{
	public static void main(String[] args)
	{
		ImageProcessing image = new ImageProcessing("X:\\Desktop\\TEST\\4.jpg", true);
		image.unblendImage(0xff000000);
		image.save();
		image.blendImage("X:\\Desktop\\TEST\\run.png");		
		image.save();

	}
}

//TODO: maybe more formats than png
//TODO: optimize
//TODO: better algorithm for scaling images. scale images. handle output size
//TODO: parameters - 
//			-auto AUTO determine background color
//			-i input file, -o output file
//			-bg background overlay file
//			-pre for premultiplied alpha (inconsistent with image editor formats...)
//		    -overlay to overlay an existing file directly.
//			-osize specify output file dimensions