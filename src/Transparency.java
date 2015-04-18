import java.util.List;
import java.util.ArrayList;

class Transparency
{
	private static void test()
	{
		
		ImageProcessing image4 = new ImageProcessing("X:\\Desktop\\TEST\\4.jpg", false);
		image4.unblendImage(0xff000000);
		image4.save();
		image4.blendImage("X:\\Desktop\\TEST\\run.png");		
		image4.save();
		
		ImageProcessing image1 = new ImageProcessing("X:\\Desktop\\TEST\\1.jpg", true);
		image1.unblendImage(0xff000000);
		image1.save();
		image1.blendImage("X:\\Desktop\\TEST\\run.png");		
		image1.save();
		
		ImageProcessing image2 = new ImageProcessing("X:\\Desktop\\TEST\\2.jpg", false);
		image2.unblendImage(0xff000000);
		image2.save();
		image2.blendImage("X:\\Desktop\\TEST\\run.png");		
		image2.save();
		
		ImageProcessing image3 = new ImageProcessing("X:\\Desktop\\TEST\\3.jpg", true);
		image3.unblendImage(0xff000000);
		image3.save();
		image3.blendImage("X:\\Desktop\\TEST\\run.png");		
		image3.save();
	}
	
	public static void main(String[] args)
	{
		test();
	}
}

//TODO: scale mode blending for upscaling
//TODO: parameters - 
//			-i input file, -o output file
//			-bg background overlay file
//			-pre for premultiplied alpha (inconsistent with image editor formats...)
//		    -overlay to overlay an existing file directly.
//			-osize specify output file dimensions

//i o bg pre overlay osize

