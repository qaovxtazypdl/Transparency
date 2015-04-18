class Transparency
{
	public static void main(String[] args)
	{
		ImageProcessing image = new ImageProcessing("X:\\Desktop\\TEST\\1.jpg");
		image.RGBtoALPHA(0);
		image.Save();
	}
}

//todo: brighten image that has been alpha'd
//todo: overlay onto second argument image if exist.
//todo: parameters - 
//			-auto AUTO determine background color
//			-i input file, -o output file
//			-bg background overlay file