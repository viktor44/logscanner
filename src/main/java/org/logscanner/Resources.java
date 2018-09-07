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
						{"error.not_implemented",			"Not implemented"},
						{"error.cant_start",				"Application start error"},
						{"error.log_open_error",			"Log file open error"},
						{"error.file_too_big",				"File size {0} {1}Mb exceeds maximum {2}Mb"},
						{"error.unsupported_location_type",	"Unsupported location type {0}"},
						{"error.code_is_empty",				"Empty code"},
						{"error.code_already_used",			"Code '{0}' already used"},
						{"error.context_is_null",			"Spring context is null"},
						
						{"action.file",						"File"},
						{"action.help",						"Help"},
						{"dialog.title.error",				"Error"},
						{"dialog.title.warning",			"Warning"},
						{"dialog.title.info",				"Information"},
						{"dialog.title.confirm",			"Confirm"},
						
						{"dialog.about.title",				"About"},
						{"dialog.about.text.version",		"Version {0}"},
						{"dialog.about.text.copyright",		"Copyright 2018 by Victor Kadachigov"},
						{"dialog.button.cancel",			"Cancel"},
						
						{"action.copy_text.title",			"Copy text"},
						{"action.exit.title",				"Exit"},
						{"action.open_log.title",			"Open file"},
						{"action.preferences.title",		"Preferences..."},
						{"action.select_locations.title",	"Where?"},
						
						{"results_panel.text.done",			"Done. Work time {0}"}, 
						{"results_panel.columns",			"Time;File;Text"},
						
						{"action.search.title",				"Search"},
						{"action.search.stop",				"Stop"},
						{"action.search.text.file_exists",	"File {0} already exists. Overwrite?"},
						{"action.search.text.no_locations",	"No locations selected"},
						
						{"search_panel.text.from",			"From: "}, 
						{"search_panel.text.to",			"to: "},
						{"search_panel.text.where",			"Where: "},
						{"search_panel.text.file_mask",		"File mask: "},
						{"search_panel.text.result",		"Result: "},
						{"search_panel.text.search",		"Search: "},
						{"search_panel.text.text",			"text:"},
						
						{"dialog.select_locations.title",	"Where"},
						{"dialog.select_locations.confirm",	"Host and path are equals in {0} and {1}. Continue?"},
						{"dialog.select_locations.columns", ";;Code;Path"},	
						
						{"action.save_to.title",			"Open"},
						
						{"status_panel.status.ready",		"Ready"},
						{"status_panel.status.searching",	"Searching"},
						{"status_panel.status.stopping",	"Stopping"},
						{"status_panel.status.done",		"Done"},
						{"status_panel.text",				"Processed {0} from {1}. Selected {2}"},
						
//Images					
						{"image.select_locations.16", 		loadImage("image.locations.16")},
						{"image.exit.16",					loadImage("image.exit.16")},
						{"image.save_to.16",				loadImage("image.open.16")}
						
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
