import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Transparency
{
	private static final String ARG_I = "-i";
	private static final String ARG_O = "-o";
	private static final String ARG_BG = "-bg";
	private static final String ARG_PRE = "-pre";
	private static final String ARG_BLEND = "-blendonly";
	private static final String ARG_D = "-d";
	private static final String ARG_HELP = "-?";
	
	private static void test()
	{
		ImageProcessing image4 = new ImageProcessing("X:\\Desktop\\TEST\\4.jpg", false, 0, 0);
		image4.unblendImage(0xff000000);
		image4.save();
		image4.blendImage("X:\\Desktop\\TEST\\run.png");		
		image4.save();
		
		ImageProcessing image1 = new ImageProcessing("X:\\Desktop\\TEST\\1.jpg", true, 0, 0);
		image1.unblendImage(0xff000000);
		image1.save();
		image1.blendImage("X:\\Desktop\\TEST\\run.png");		
		image1.save();
		
		ImageProcessing image2 = new ImageProcessing("X:\\Desktop\\TEST\\2.jpg", false, 0, 0);
		image2.unblendImage(0xff000000);
		image2.save();
		image2.blendImage("X:\\Desktop\\TEST\\run.png");		
		image2.save();
		
		ImageProcessing image3 = new ImageProcessing("X:\\Desktop\\TEST\\3.jpg", true, 0, 0);
		image3.unblendImage(0xff000000);
		image3.save();
		image3.blendImage("X:\\Desktop\\TEST\\run.png");		
		image3.save();
	}
	
	private static void usage()
	{
		System.out.println("You did it wrong.");
	}
	
	private static Map<String, List<String>> getArgs(String[] args)
	{
		Map<String, List<String>> argumentMap = new HashMap<String, List<String>>();
		
		int i = 0;
		while(i < args.length)
		{
			String argument = args[i];
			if(argument.startsWith("-"))
			{
				i++;
				List<String> arguments = new ArrayList<String>();
				while(i < args.length && !args[i].startsWith("-"))
				{
					arguments.add(args[i]);
					i++;
				}
				argumentMap.put(argument, arguments);
			}
			else
			{
				usage();
				break;
			}
		}
		
		return argumentMap;
	}
	
	public static void main(String[] args)
	{
		//test();
		Map<String, List<String>> argumentMap = getArgs(args);
		
		boolean premult = false;
		boolean blendOnly = false;
		int width = 0;
		int height = 0;
		String inputName = "";
		String outputName = "";
		List<String> backgrounds = new ArrayList<String>();
		
		if (!argumentMap.containsKey(ARG_I))
		{
			System.out.println("Input file not found.");
			usage();
			return;
		}
		
		//display usage if requested or input file not found, else process the arguments.
		for (String arg : argumentMap.keySet())
		{
			if(arg.equals(ARG_HELP))
			{
				usage();
				return;
			}
			else if (arg.equals(ARG_I))
			{
				List<String> names = argumentMap.get(arg);
				if(names.size() != 1) 
				{
					System.out.println("Invalid input file argument.");
					usage();
					return;
				}
				else
				{
					inputName = names.get(0);
				}
			}
			else if(arg.equals(ARG_O))
			{
				List<String> names = argumentMap.get(arg);
				if(names.size() != 1) 
				{
					System.out.println("Invalid output file argument.");
					usage();
					return;
				}
				else
				{
					outputName = names.get(0);
				}
			}
			else if(arg.equals(ARG_BG))
			{
				backgrounds = argumentMap.get(arg);
			}
			else if(arg.equals(ARG_D))
			{
				List<String> sizes = argumentMap.get(arg);
				if(sizes.size() != 2) 
				{
					System.out.println("Invalid size argument.");
					usage();
					return;
				}
				else
				{
					try
					{
						width = Integer.parseInt(sizes.get(0));
						height = Integer.parseInt(sizes.get(1));
					}
					catch (NumberFormatException e)
					{
						System.out.println("Invalid size argument.");
						usage();
						return;
					}
				}
			}
			else if(arg.equals(ARG_BLEND))
			{
				blendOnly = true;
			}
			else if(arg.equals(ARG_PRE))
			{
				premult = true;
			}
			else
			{
				System.out.println("Invalid argument " + arg + ".");
				usage();
				return;
			}
		}
		
		ImageProcessing image = new ImageProcessing(inputName, premult, width, height);
	}
}

//TODO: scale mode blending for upscaling
//TODO: parameters - 
//			-i input file, -o output file     											(1)
//			-bg background overlay file       											(1)
//			-pre for premultiplied alpha (inconsistent with image editor formats...) 	(0)
//		    -overlay to overlay an existing file directly.							 	(0)
//			-osize specify output file dimensions									 	(2)
//          -?																			(0)
		
//i o bg pre blendonly osize

