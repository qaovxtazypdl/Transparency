class Transparency
{
	public static void main(String[] args)
	{
		ImageProcessing image = new ImageProcessing("test.png");
		image.RGBtoALPHA(0);
		image.Save();
	}
}

//todo: make sure image is converted to ARGB form first.
//todo: brighten image that has been alpha'd
//todo: overlay onto second argument image if exist.