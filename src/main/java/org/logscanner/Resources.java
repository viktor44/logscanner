package org.logscanner;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.logscanner.data.LocationType;

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
										{"image.excel.16",			"/images/set1/Excel_16x16.png"},
								};
	
	private static ResourceBundle getInstance()
	{
		//return ResourceBundle.getBundle(Resources.class.getName(), new Locale("ru"));
		return ResourceBundle.getBundle(Resources.class.getName(), new Locale("en"));
		//return ResourceBundle.getBundle(Resources.class.getName());
		//return ResourceBundle.getBundle(Resources.class.getName(), Locale.US);
	}
	
	public static String getStr(String key, Object... args)
	{
		String result = getInstance().getString(key);
		if (args.length > 0)
			result = MessageFormat.format(result, args[0]);
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
						{"error.not_implemented",			"Not implemented"},
						{"error.cant_start",				"Application start error"},
						{"error.unsupported_location_type",	"Unsupported location type {0}"},
						{"error.code_is_empty",				"Empty code"},
						{"error.code_already_used",			"Code \"{0}\" already used"},
						{"error.context_is_null",			"Spring context is null"},
						{"dialog.title.error",				"Error"},
						{"dialog.title.warning",			"Warning"},
						{"dialog.title.info",				"Information"},
						{"dialog.title.confirm",			"Confirm"},
						
//Images					
						{"image.select_locations.16", 		loadIcon("image.locations.16")},
						{"image.exit.16",					loadIcon("image.exit.16")},
						{"image.save_to.16",				loadIcon("image.open.16")},
						{"image.excel.16",					loadIcon("image.excel.16")}
						
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
	
	private Icon loadIcon(String key)
	{
		String imagePath = findResourcePath(key);
		URL imageURL = Resources.class.getResource(imagePath);
		if (imageURL == null)
			throw new IllegalArgumentException(MessageFormat.format("Resource \"{0}\" not found", imagePath));
		return new ImageIcon(imageURL);
	}
	
}
