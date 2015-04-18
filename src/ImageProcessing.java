import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.lang.Math;

class ImageProcessing
{
	//Image objects.
	private BufferedImage _image, _bg, _output;
	
	//output image size
	private int _outputWidth, _outputHeight;
	
	//Image names for the main image object and the background.
	private String _inputName;
	
	//Whether or not we are using premultiplied alpha mode.
	private boolean _premultiply;
	
	//The data type of the image. ARGB or ARGB + PREMULTIPLIED
	private int _imageType;
	
	/*
	 * Constructor. Creates the object with a starting image and a premultiply flag.
	 * 
	 * @imageName: Path to the initial image to open.
	 * @premultiply: a flag to indicate if we are using the premultiplied alpha ARGB mode.
	 * 				 this mode is more efficient (slightly) but transparency not easily
	 * 				 compatible with windows image editors I've tried.
	 * @outputWidth,Height: dimensions to scale the outputted image to.
	 */
	public ImageProcessing(String imageName, boolean premultiply, int outputWidth, int outputHeight)
	{
		//set premult flag
		_premultiply = premultiply;
		if(_premultiply)
		{
			_imageType = BufferedImage.TYPE_INT_ARGB_PRE;
		}
		else
		{
			_imageType = BufferedImage.TYPE_INT_ARGB;
		}
		
		_image = openImage(imageName);
		_inputName = imageName;
		
		if(outputWidth == 0 || outputHeight == 0)
		{
			outputWidth = _image.getWidth();
			outputHeight = _image.getHeight();
		}
		_outputWidth = outputWidth;
		_outputHeight = outputHeight;
		_output = copyAndFormatImage(_image, _outputWidth, _outputHeight);
	}
	
	/*
	 * Unblends the current image and makes a specified color gradient transparent.
	 * 
	 * @base_rgb: the main color to make transparent.
	 */
	public void unblendImage(int base_rgb) {
		if(_image == null) return;
		
		//get base argb
		int base_a = (base_rgb >>> 24) & 0xff;
    	int base_r = (base_rgb >>> 16) & 0xff;
    	int base_g = (base_rgb >>> 8) & 0xff;
    	int base_b = (base_rgb) & 0xff;
    	
    	//for each pixel
        for(int w = 0; w < _outputWidth; w++) {
	        for(int h = 0; h < _outputHeight; h++) {
            	int argb = getScaledPixel(_image, _outputWidth, _outputHeight, w, h);

            	//get image argb
            	int a = (argb >>> 24) & 0xff;
            	int r = (argb >>> 16) & 0xff;
            	int g = (argb >>> 8) & 0xff;
            	int b = (argb) & 0xff;
            	int newa = getColorDifference(base_r, base_g, base_b, r, g, b); //choose an a.

            	//reverse calculate blending
            	if(_premultiply)
            	{
            		//premult
                	r = r * a / 0xff;
                	g = g * a / 0xff;
                	b = b * a / 0xff;
                	
                	//input = transparency + (backgd * (1.0-transparency.a))
                	//transparency = input - (backgd * (1.0-transparency.a))
            		a = a - (0xff - newa) * base_a / 0xff;
            		r = r - (0xff - newa) * base_r / 0xff;
            		g = g - (0xff - newa) * base_g / 0xff;
            		b = b - (0xff - newa) * base_b / 0xff;
            		
                	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	}
            	else
            	{
            		if(newa != 0)
             		{
 	                	//input = transparency.a * transparency + (backgd * (1.0-transparency.a))
 	                	//transparency = input - ((1-transparency.a) * backgd) / transparency.a
 	            		r = (a * r - (0xff - newa) * base_r) / newa;
 	            		g = (a * g - (0xff - newa) * base_g) / newa;
 	            		b = (a * b - (0xff - newa) * base_b) / newa;
 	
 	            		r = r > 0xff? 0xff : r;
 	            		b = b > 0xff? 0xff : b;
 	            		g = g > 0xff? 0xff : g;
             		}
            		
                	argb = (newa << 24) | (r << 16) | (g << 8) | (b);
            	}

            	//enter the final pixel back into the output pixel
            	_output.setRGB(w,h, argb);
	        }
        }
	}
	
	/*
	 * Blends the current _output image over a background image.
	 * 
	 * @bgName: name of the image file to overlay over.
	 */
	public void blendImage(String bgName) {
		//opens the backgound image to blend into.
		_bg = openImage(bgName);
		
		if(_bg == null) return;
		
		//for each pixel
        for(int w = 0; w < _outputWidth; w++) {
	        for(int h = 0; h < _outputHeight; h++) {
            	int argb = 0;
            	
            	//get background argb, scaled
            	int bg_argb = getScaledPixel(_bg, _outputWidth, _outputHeight, w, h);
            	int bg_a = (bg_argb >>> 24) & 0xff;
            	int bg_r = (bg_argb >>> 16) & 0xff;
            	int bg_g = (bg_argb >>> 8) & 0xff;
            	int bg_b = (bg_argb) & 0xff;
            	
            	//get source argb, scaled.
            	int src_argb = getScaledPixel(_output, _outputWidth, _outputHeight, w, h);
            	int src_a = (src_argb >>> 24) & 0xff;
            	int src_r = (src_argb >>> 16) & 0xff;
            	int src_g = (src_argb >>> 8) & 0xff;
            	int src_b = (src_argb) & 0xff;
            	
            	//apply blending to the pixel.
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
                	int a = (src_a *  0xff + (0xff - src_a) * bg_a) / 0xff;
                	int r = (src_a * src_r + (0xff - src_a) * bg_r) / 0xff;
                	int g = (src_a * src_g + (0xff - src_a) * bg_g) / 0xff;
                	int b = (src_a * src_b + (0xff - src_a) * bg_b) / 0xff;
                	
                	argb = (a << 24) | (r << 16) | (g << 8) | (b);
            	}
            	
            	//enter the r' back into the pixel.
            	_output.setRGB(w,h, argb);
	        }
        }
	}
	
	/*
	 * Saves the current image into a png file with a selected name.
	 * 
	 * @outName: filename to save as.
	 * @qualifier: the string to insert at the end of the file name, before extension.
	 * 
	 * @return: returns whether save succeeded.
	 */
	public boolean save(String outName, String qualifier)
	{
        boolean success = true;
        if(outName.equals("")) 
        {
        	outName = _inputName;
        }
        
		if(outName.contains("."))
		{
			outName = outName.substring(0, outName.lastIndexOf('.'));
		}

		try {
			if(_output != null)
			{
		        File fileName = new File(outName + qualifier + ".png");
				success &= ImageIO.write(_output, "png", fileName);
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
	
	/*
	 * Saves the current image into a png file.
	 * 
	 * @outName: filename to save as.
	 * 
	 * @return: returns whether save succeeded.
	 */
	public boolean save(String outName)
	{
		return save(outName, "");
	}
	
	/*
	 * Saves the current image into a png file.
	 * 
	 * @return: returns whether save succeeded.
	 */
	public boolean save()
	{
		return save(_inputName, "_out");
	}
	
	
	/*
	 * Opens an image with the specified imagename into a buffer.
	 * @imageName: Path to the image to open.
	 */
	private BufferedImage openImage(String imageName)
	{
		//open the image and load into buffer.
		BufferedImage image = null;
		File inputFile = new File(imageName);
		try {
			image = ImageIO.read(inputFile);
		} catch (IOException e) {
			System.out.println("Could not read input image.");
			return image;
		}
		
		//convert type if needed.
		if(image.getType() != _imageType)
		{
	        image = copyAndFormatImage(image);
		}
		return image;
	}
	
	/*
	 * Returns a copy of the image passed in.
	 * 
	 * @source: the source image to copy
	 * 
	 * @return: a copy of source.
	 */
	private BufferedImage copyAndFormatImage(BufferedImage source)
	{
		return copyAndFormatImage(source, source.getWidth(), source.getHeight());
	}
	
	
	/*
	 * Returns a copy of the image passed in and scales up or down if needed.
	 * 
	 * @source: the source image to copy
	 * @width/height: the size of the resulting copy.
	 * 
	 * @return: a copy of source, scaled to specified width and height.
	 */
	private BufferedImage copyAndFormatImage(BufferedImage source, int width, int height)
	{
		BufferedImage argbImage = new BufferedImage(width, height, _imageType);
        for(int w = 0; w < width; w++)
        {
	        for(int h = 0; h < height; h++)
	        {
	        	argbImage.setRGB(w, h, getScaledPixel(source, width, height, w, h)); //TODO: handle transparent input
	        }
        }
		return argbImage;
	}
	
	/*
	 * Returns how "different" the color is to the base as an alpha value. 
	 * Closer the colors are, more transparent the resulting pixel will be.
	 * 
	 * @base_r/g/b: the base rgb components.
	 * @r/g/b: the color we are currently looking at.
	 * 
	 * @return: the difference metric as alpha value.
	 */
	private int getColorDifference(int base_r, int base_g, int base_b, int r, int g, int b) {
		//max brightness metric
		return Math.max(Math.abs(base_r - r), Math.max(Math.abs(base_g - g), Math.abs(base_b - b))); 
		//luminance metric
    	//return (int) (0.2126 * Math.abs(base_r - r) + 0.7152 * Math.abs(base_g - g) + 0.0722 * Math.abs(base_b - b));
	}
	
	/*
	 * Gets the pixel in the corresponsing scaled location in the image. (no scale blending)
	 * 
	 * @image: the image souce for getting the pixels
	 * @scaledMaxX/Y maximum width and height in the scaled coordinate system
	 * @x/y pixel at x,y in the scaled coordinate system to retrieve.
	 * 
	 * @return: the argb value of the pixel selected.
	 */
	private int getScaledPixel(BufferedImage image, int scaledMaxX, int scaledMaxY, int x, int y) {
		return image.getRGB(image.getWidth() * x / scaledMaxX, image.getHeight() * y / scaledMaxY);
	}
}