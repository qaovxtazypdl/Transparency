import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Transparency
{
	//argument constants.
	private static final String NAME = "Transparency";
	private static final String ARG_I = "-i";
	private static final String ARG_O = "-o";
	private static final String ARG_BG = "-b";
	private static final String ARG_PRE = "-p";
	private static final String ARG_DIM = "-d";
	private static final String ARG_COL = "-c";
	private static final String ARG_NDB = "-n";
	private static final String ARG_HELP = "-?";
	
	/*
	 * Prints usage information.
	 */
	private static void usage()
	{
		System.out.printf("USAGE:\n");
		System.out.printf("java %s %s inputFile [%s r g b] [%s outputFile] [%s bgFile1 bgFile2 ...] [%s] [%s width height]\n\n", NAME, ARG_I, ARG_COL, ARG_O, ARG_BG, ARG_PRE, ARG_DIM);
		System.out.printf("%s\t\t\tPath to the input file.\n\n", ARG_I);
		System.out.printf("%s\t\t\tThe RGB value to de-blend into transparency.\n", ARG_COL);
		System.out.printf("\t\t\tOptional. Defaults to 0,0,0 (black).\n\n");
		System.out.printf("%s\t\t\tPath to the output file. Optional.\n", ARG_O);
		System.out.printf("\t\t\tDefaults to input file + \"_out\".\n\n");
		System.out.printf("%s\t\t\tList of paths to background files to blend into\n", ARG_BG);
		System.out.printf("\t\t\tin order. Optional.\n\n");
		System.out.printf("%s\t\t\tUse premultiplied argb processing. Optional.\n\n", ARG_PRE);
		System.out.printf("%s\t\t\tDimensions of the output. Optional.\n", ARG_DIM);
		System.out.printf("\t\t\tDefaults to the dimensions of the input.\n\n");
		System.out.printf("%s\t\t\tDon't attempt to deblend image first. Instead, blend\n", ARG_NDB);
		System.out.printf("\t\t\tinput directly with any backgrounds. Optional.\n\n");

	}
	
	/*
	 * Gets arguments into a mapping between flag and arguments
	 * 
	 *@args: input argument array.
	 *
	 *@return: Returns a mapping between flag and arguments
	 */
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
		if(args.length == 0)
		{
			usage();
			return;
		}
		
		Map<String, List<String>> argumentMap = getArgs(args);
		boolean premult = false;
		boolean noDeblend = false;
		int width = 0;
		int height = 0;
		String inputName = "";
		String outputName = "";
		int argb = 0xff000000;
		List<String> backgrounds = new ArrayList<String>();
		
		//check for input file.
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
			else if(arg.equals(ARG_COL))
			{
				List<String> rgblist = argumentMap.get(arg);
				if(rgblist.size() != 3) 
				{
					System.out.println("Invalid rgb argument.");
					usage();
					return;
				}
				else
				{
					try
					{
						int r = Integer.parseInt(rgblist.get(0));
						int g = Integer.parseInt(rgblist.get(1));
						int b = Integer.parseInt(rgblist.get(2));
						argb = (0xff << 24) | (r << 16) | (g << 8) | (b);
					}
					catch (NumberFormatException e)
					{
						System.out.println("Invalid rgb argument.");
						usage();
						return;
					}
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
			else if(arg.equals(ARG_DIM))
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
			else if(arg.equals(ARG_PRE))
			{
				premult = true;
			}
			else if(arg.equals(ARG_NDB))
			{
				noDeblend = true;
			}
			else
			{
				System.out.println("Invalid argument " + arg + ".");
				usage();
				return;
			}
		}
		
		//initialize processor
		ImageProcessing image = new ImageProcessing(inputName, premult, width, height);
		
		//make transparency
		if(!noDeblend)
		{
			image.unblendImage(argb);
			image.save(outputName, "_layer");
		}
		
		//blend backgrounds
		for(String bgName : backgrounds)
		{
			image.blendImage(bgName);
		}
		if(!backgrounds.isEmpty())
		{
			image.save(outputName, "_blended");
		}
	}
}

//implement handling transparent input.
