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
	
	public ImageProcessing(String imageName, String bgName)
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
		
		if(_image.getType() != BufferedImage.TYPE_INT_ARGB_PRE)
		{
			BufferedImage argbImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB_PRE);
	        for(int w = 0; w < _width; w++)
	        {
		        for(int h = 0; h < _height; h++)
		        {
		        	argbImage.setRGB(w, h, (_image.getRGB(w, h) | 0xff000000)); //will break on transparent input
		        }
	        }
	        _image = argbImage;
		}
		
		_bgName = bgName;
		_bg = null;
		inputFile = new File(_bgName);
		try {
			_bg = ImageIO.read(inputFile);
		} catch (IOException e) {
			System.out.println("Could not read input image.");
			return;
		}
			
		_bgHeight = _bg.getHeight();
		_bgWidth = _bg.getWidth();
		
		if(_bg.getType() != BufferedImage.TYPE_INT_ARGB_PRE)
		{
			BufferedImage argbImage = new BufferedImage(_bgWidth, _bgHeight, BufferedImage.TYPE_INT_ARGB_PRE);
	        for(int w = 0; w < _bgWidth; w++)
	        {
		        for(int h = 0; h < _bgHeight; h++)
		        {
		        	argbImage.setRGB(w, h, (_bg.getRGB(w, h) | 0xff000000)); //will break on transparent input
		        }
	        }
	        _bg = argbImage;
		}
	}
	
	//returns how "different" the color is. Closer it is, more transparent it will be.
	private int getColorDifference(int base_r, int base_g, int base_b, int r, int g, int b) {
		return Math.max(Math.abs(base_r - r), Math.max(Math.abs(base_g - g), Math.abs(base_b - b)));
    	//return (int) (Math.abs(base_r - r) + Math.abs(base_g - g) + Math.abs(base_b - b));
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


            	//////////////////////////COLOR TUNING - PREMULTIPLIED//////////////////////////       
            	/*r = r * a / 0xff;
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
        		a = a > 0xff? 0xff : a;*/
            	//////////////////////////COLOR TUNING - PREMULTIPLIED//////////////////////////
            	
            	//////////////////////////COLOR TUNING - nonpremultiuplied//////////////////////
            	
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
        		
            	//////////////////////////COLOR TUNING - nonpremultiuplied//////////////////////

            	//enter the r' back into the pixel.
            	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	_image.setRGB(w,h, argb);
	        }
        }
	}
	
	//get a better algorithm for zoomins.
	private int GetScaledPixel(BufferedImage image, int scaledMaxX, int scaledMaxY, int x, int y) {
		//TODO:
		return image.getRGB(x, y);
	}
	
	public void OverlayImage() {
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
            	
            	//////////////////////////COLOR TUNING - PREMULTIPLIED//////////////////////////       
            	//pix = transparency + (backgd * (1.0-transparency.a))
            	/*
            	int a = src_a + bg_a * (0xff - src_a) / 0xff;
            	int r = src_r + bg_r * (0xff - src_a) / 0xff;
            	int g = src_g + bg_g * (0xff - src_a) / 0xff;
            	int b = src_b + bg_b * (0xff - src_a) / 0xff;
            	*/
            	//////////////////////////COLOR TUNING - PREMULTIPLIED//////////////////////////
            	
            	//////////////////////////COLOR TUNING - nonpremultiuplied//////////////////////
            	//pix = transparency.a * transparency + (backgd * (1.0-transparency.a))
            	double a_ratio = (double) src_a / 0xff;
            	int a = (int) (src_a + (1 - a_ratio) * bg_a);
            	int r = (int) (a_ratio * src_r + (1 - a_ratio) * bg_r);
            	int g = (int) (a_ratio * src_g + (1 - a_ratio) * bg_g);
            	int b = (int) (a_ratio * src_b + (1 - a_ratio) * bg_b);
            	
            	/*
        		          a + (1-a_ratio) * base_a = (base_a_ratio * a);
        		a_ratio * r + (1-a_ratio) * base_r = (base_a_ratio * r);
        		a_ratio * g + (1-a_ratio) * base_g = (base_a_ratio * g);
        		a_ratio * b + (1-a_ratio) * base_b = (base_a_ratio * b);
        		*/
            	//////////////////////////COLOR TUNING - nonpremultiuplied//////////////////////
            	
            	int argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	_bg.setRGB(w,h, argb);
	        }
        }
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
		        File deblendedFileName = new File(outName + "_deblended.png");
				success &= ImageIO.write(_image, "png", deblendedFileName);
			}
			if(_bg != null)
			{
				File outputFile = new File(outName + ".png");
				success &= ImageIO.write(_bg, "png", outputFile);
			}
			if(_image == null || _bg == null) 
				success = false;
		} catch (IOException e) {
			System.out.println("Could not save processed image.");
			success = false;
		}
		return success;
	}
	
	public boolean Save()
	{
		return Save(_imageName.substring(0, _imageName.lastIndexOf('.')) + "_temp");
	}
}