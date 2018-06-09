package org.logscanner;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Victor Kadachigov
 */
public class Resources extends ListResourceBundle
{
	protected static String IMAGE_SET1[][] = new String[][] 
								{
										{"image.locations.16",		"/images/set1/Open_16x16.png"},
										{"image.exit.16",			"/images/set1/Log Out_16x16.png"},
										{"image.open.16",			"/images/set1/Open_16x16.png"},
								};
	
	private static ResourceBundle getInstance()
	{
		return ResourceBundle.getBundle(Resources.class.getName(), Locale.US);
	}
	
	public static String getStr(String key, Object... args)
	{
		String result = getInstance().getString(key);
		if (args.length > 0)
			result = MessageFormat.format(result, args);
		return result;
	}
	
	public static Icon getIcon(String key)
	{
		return (Icon)getInstance().getObject(key);
	}
	
	@Override
	protected Object[][] getContents()
	{
		return new Object[][] 
				{
//Images					
						{"image.locations.16", 				loadImage("image.locations.16")},
						{"image.exit.16",					loadImage("image.exit.16")},
						{"image.open.16",					loadImage("image.open.16")}
						
        		};

	}

	private String findResourcePath(String key)
	{
		String result = null;
		String map[][] = IMAGE_SET1;
		for (int i = 0; i < map.length && result == null; i++)
		{
			if (map[i][0].equals(key))
				result = map[i][1];
		}
		if (result == null)
			throw new IllegalArgumentException(MessageFormat.format("Key \"{0}\" not found", key));
		return result;
	}
	
	private Icon loadImage(String key)
	{
		String imagePath = findResourcePath(key);
		URL imageURL = Resources.class.getResource(imagePath);
		if (imageURL == null)
			throw new IllegalArgumentException(MessageFormat.format("Resource \"{0}\" not found", imagePath));
		return new ImageIcon(imageURL);
	}
	
}
