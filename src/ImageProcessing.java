import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.lang.Math;


class ImageProcessing
{
	private BufferedImage _image, _bg;
	private String _imageName, _bgName;
	
	private int _height, _bgHeight;
	private int _width, _bgWidth;
	
	private int _timesSaved;
	private int _imageType;
	private boolean _premultiply;
	
	public ImageProcessing(String imageName, boolean premultiply)
	{
		_premultiply = premultiply;
		if(_premultiply)
		{
			_imageType = BufferedImage.TYPE_INT_ARGB_PRE;
		}
		else
		{
			_imageType = BufferedImage.TYPE_INT_ARGB;
		}
		
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
		
		if(_image.getType() != _imageType)
		{
			BufferedImage argbImage = new BufferedImage(_width, _height, _imageType);
	        for(int w = 0; w < _width; w++)
	        {
		        for(int h = 0; h < _height; h++)
		        {
		        	argbImage.setRGB(w, h, (_image.getRGB(w, h) | 0xff000000)); //will break on transparent input
		        }
	        }
	        _image = argbImage;
		}
	}
	
	//returns how "different" the color is. Closer it is, more transparent it will be.
	private int getColorDifference(int base_r, int base_g, int base_b, int r, int g, int b) {
		return Math.max(Math.abs(base_r - r), Math.max(Math.abs(base_g - g), Math.abs(base_b - b))); //max brightness metric
    	//return (int) (0.2126 * Math.abs(base_r - r) + 0.7152 * Math.abs(base_g - g) + 0.0722 * Math.abs(base_b - b)); //luminance metric
	}
	
	public void RGBtoALPHA(int base_rgb) {
		if(_image == null) return;
		int base_a = (base_rgb >>> 24) & 0xff;
    	int base_r = (base_rgb >>> 16) & 0xff;
    	int base_g = (base_rgb >>> 8) & 0xff;
    	int base_b = (base_rgb) & 0xff;
    	
        for(int w = 0; w < _width; w++) {
	        for(int h = 0; h < _height; h++) {
            	int argb = _image.getRGB(w, h);

            	int a = (argb >>> 24) & 0xff;
            	int r = (argb >>> 16) & 0xff;
            	int g = (argb >>> 8) & 0xff;
            	int b = (argb) & 0xff;
            	int newa = getColorDifference(base_r, base_g, base_b, r, g, b); //choose an a.

            	if(_premultiply)
            	{
                	r = r * a / 0xff;
                	g = g * a / 0xff;
                	b = b * a / 0xff;
            		double a_ratio = (double)newa / 0xff;

                	//input = transparency + (backgd * (1.0-transparency.a))
                	//transparency = input - (backgd * (1.0-transparency.a))
            		a = (int) ((a - (1-a_ratio) * base_a));
            		r = (int) ((r - (1-a_ratio) * base_r));
            		g = (int) ((g - (1-a_ratio) * base_g));
            		b = (int) ((b - (1-a_ratio) * base_b));

            		r = r > 0xff? 0xff : r;
            		b = b > 0xff? 0xff : b;
            		g = g > 0xff? 0xff : g;
            		a = a > 0xff? 0xff : a;
            		
                	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	}
            	else
            	{
                	double base_a_ratio = (double)a / 0xff;
            		double a_ratio = (double)newa / 0xff;
            		
                	//input = transparency.a * transparency + (backgd * (1.0-transparency.a))
                	//transparency = input - ((1-transparency.a) * backgd) / transparency.a
            		a = (int) ((base_a_ratio * a - (1-a_ratio) * base_a));
            		r = (int) ((base_a_ratio * r - (1-a_ratio) * base_r) / a_ratio);
            		g = (int) ((base_a_ratio * g - (1-a_ratio) * base_g) / a_ratio);
            		b = (int) ((base_a_ratio * b - (1-a_ratio) * base_b) / a_ratio);

            		r = r > 0xff? 0xff : r;
            		b = b > 0xff? 0xff : b;
            		g = g > 0xff? 0xff : g;
            		a = a > 0xff? 0xff : a;
            		
                	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	}

            	//enter the r' back into the pixel.
            	_image.setRGB(w,h, argb);
	        }
        }
	}
	
	public void OverlayImage(String bgName) {
		_bgName = bgName;
		_bg = null;
		File inputFile = new File(_bgName);
		try {
			_bg = ImageIO.read(inputFile);
		} catch (IOException e) {
			System.out.println("Could not read input image.");
			return;
		}
			
		_bgHeight = _bg.getHeight();
		_bgWidth = _bg.getWidth();
		
		if(_bg.getType() != _imageType)
		{
			BufferedImage argbImage = new BufferedImage(_bgWidth, _bgHeight, _imageType);
	        for(int w = 0; w < _bgWidth; w++)
	        {
		        for(int h = 0; h < _bgHeight; h++)
		        {
		        	argbImage.setRGB(w, h, (_bg.getRGB(w, h) | 0xff000000)); //will break on transparent input
		        }
	        }
	        _bg = argbImage;
		}
		if(_bg == null) return;
		
        for(int w = 0; w < _bgWidth; w++) {
	        for(int h = 0; h < _bgHeight; h++) {
            	int bg_argb = _bg.getRGB(w, h);
            	int bg_a = (bg_argb >>> 24) & 0xff;
            	int bg_r = (bg_argb >>> 16) & 0xff;
            	int bg_g = (bg_argb >>> 8) & 0xff;
            	int bg_b = (bg_argb) & 0xff;
            	int src_argb = GetScaledPixel(_image, _bgWidth, _bgHeight, w, h);
            	int src_a = (src_argb >>> 24) & 0xff;
            	int src_r = (src_argb >>> 16) & 0xff;
            	int src_g = (src_argb >>> 8) & 0xff;
            	int src_b = (src_argb) & 0xff;
            	int argb = 0;
            	
            	if(_premultiply)
            	{
                	//pix = transparency + (backgd * (1.0-transparency.a))
                	int a = src_a + bg_a * (0xff - src_a) / 0xff;
                	int r = src_r + bg_r * (0xff - src_a) / 0xff;
                	int g = src_g + bg_g * (0xff - src_a) / 0xff;
                	int b = src_b + bg_b * (0xff - src_a) / 0xff;
                	
                	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	}
            	else
            	{
                	//pix = transparency.a * transparency + (backgd * (1.0-transparency.a))
                	double a_ratio = (double) src_a / 0xff;
                	int a = (int) (src_a + (1 - a_ratio) * bg_a);
                	int r = (int) (a_ratio * src_r + (1 - a_ratio) * bg_r);
                	int g = (int) (a_ratio * src_g + (1 - a_ratio) * bg_g);
                	int b = (int) (a_ratio * src_b + (1 - a_ratio) * bg_b);
                	
                	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	}
            	
            	//enter the r' back into the pixel.
            	_bg.setRGB(w,h, argb);
	        }
        }
        //swap the new image to the main position.
        _image = _bg;
        _width = _bgWidth;
        _height = _bgHeight;
	}
	
	public boolean Save(String outName)
	{
        boolean success = true;
		if(outName.contains("."))
		{
			outName = outName.substring(0, outName.lastIndexOf('.'));
		}

		try {
			if(_image != null)
			{
		        File fileName = new File(outName + "_" + _timesSaved++ + ".png");
				success &= ImageIO.write(_image, "png", fileName);
			}
			else 
			{
				success = false;
			}
		} catch (IOException e) {
			System.out.println("Could not save processed image.");
			success = false;
		}
		return success;
	}
	
	public boolean Save()
	{
		return Save(_imageName.substring(0, _imageName.lastIndexOf('.')) + "_default");
	}
	
	//get a better algorithm for zoomins.
	private int GetScaledPixel(BufferedImage image, int scaledMaxX, int scaledMaxY, int x, int y) {
		//TODO:
		return image.getRGB(x, y);
	}
}