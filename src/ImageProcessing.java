import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.lang.Math;


class ImageProcessing
{
	private BufferedImage _image;
	private String _imageName;
	
	private int _height;
	private int _width;
	
	public ImageProcessing(String imageName)
	{
		_imageName = imageName;
		_image = null;
		File inputFile = new File(_imageName);
		try {
			_image = ImageIO.read(inputFile);
		} catch (IOException e) {
			System.out.println("Could not read input image.");
			return;
		}
			
		_height = _image.getHeight();
		_width = _image.getWidth();
		
		if(_image.getType() != BufferedImage.TYPE_INT_ARGB)
		{
			BufferedImage argbImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB);
	        for(int w = 0; w < _width; w++)
	        {
		        for(int h = 0; h < _height; h++)
		        {
		        	argbImage.setRGB(w, h, _image.getRGB(w, h));
		        }
	        }
	        _image = argbImage;
		}
	}
	
	//returns how "different" the color is. Closer it is, more transparent it will be.
	private int getColorDifference(int rgb, int r, int g, int b) {
    	int base_b = (rgb) & 0xff;
    	int base_g = (rgb >>> 8) & 0xff;
    	int base_r = (rgb >>> 16) & 0xff;
    	
    	return (int) (0.2126 * Math.abs(base_r - r) + 0.7152 * Math.abs(base_g - g) + 0.0722* Math.abs(base_b - b));
	}
	
	public void RGBtoALPHA(int rgb) {
		if(_image == null) return;

        for(int w = 0; w < _width; w++)
        {
	        for(int h = 0; h < _height; h++)
	        {
            	int pixel = _image.getRGB(w, h);
            	int b = (pixel) & 0xff;
            	int g = (pixel >>> 8) & 0xff;
            	int r = (pixel >>> 16) & 0xff;
            	int a = (pixel >>> 24) & 0xff;
            	
            	int closeness = getColorDifference(rgb, r, g, b);
            	
            	pixel = (pixel & 0x00ffffff) | (closeness << 24);
            	_image.setRGB(w,h, pixel);
	        }
        }
	}
	
	public boolean Save(String outName)
	{
		if(_image == null) return false;
        File outputfile = new File(outName);
		try {
	        return ImageIO.write(_image, "png", outputfile);
		} catch (IOException e) {
			System.out.println("Could not save processed image.");
			return false;
		}
	}
	
	public boolean Save()
	{
		return Save(_imageName.substring(0, _imageName.lastIndexOf('.')) + "_temp.png");
	}
}