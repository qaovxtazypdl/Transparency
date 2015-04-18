class Transparency
{
	public static void main(String[] args)
	{
		ImageProcessing image = new ImageProcessing("X:\\Desktop\\TEST\\4.jpg", "X:\\Desktop\\TEST\\run.png");
		image.RGBtoALPHA(0xff000000);
		image.OverlayImage();
		image.Save();
	}
}

//todo: brighten image that has been alpha'd. Color tuning.
//todo: better algorithm for scaling images.
//todo": optimize
//todo: parameters - 
//			-auto AUTO determine background color
//			-i input file, -o output file
//			-bg background overlay file
//			-nopre for no precalculated alpha (consistent with image editor formats...)