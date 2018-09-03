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
						{"error.not_implemented",			"Not implemented yet"},
						{"error.log_open_error",			"Log file open error"},		// Ошибка при открытии лог-файла
						
						{"action.file",						"File"},			// Файл
						{"action.help",						"Help"},			// Справка
						{"dialog.title.error",				"Error"},			// Ошибка
						{"dialog.title.warning",			"Warning"},			// Внимание
						{"dialog.title.info",				"Information"},		// Информация
						{"dialog.title.confirm",			"Confirm"},			// Подтверждение
						
						{"dialog.about.title",				"About"},			// О программе
						{"dialog.about.text.version",		"Version {0}"},		// Версия
						{"dialog.about.text.copyright",		"Copyright 2018 by Victor Kadachigov"},
						{"dialog.button.cancel",			"Cancel"},			// Отмена
						
						{"action.copy_text.title",			"Copy text"},		// Копировать текст
						{"action.exit.title",				"Exit"},			// Выход
						{"action.open_log.title",			"Open file"},		// Открыть файл
						{"action.preferences.title",		"Preferences..."},	// Настройки...
						{"action.select_locations.title",	"Where?"},			// Где?
						
						{"results_panel.text.done",			"Done. Work time {0}"},	// "Готово. Работали " 
						
						{"action.search.title",				"Search"},				//  "Искать"
						{"action.search.stop",				"Stop"},				//  "Остановить"
						{"action.search.text.file_exists",	"File {0} already exists. Overwrite?"},	// "Файл {0} уже существует. Перезаписать?"
						{"action.search.text.no_locations",	"No locations selected"},				// Не выбрано ни одного расположения для поиска
						
						{"search_panel.text.from",			"From: "},				// Период с: 
						{"search_panel.text.to",			"to: "},				// по:
						{"search_panel.text.where",			"Where: "},				//Где искать:
						{"search_panel.text.file_mask",		"File mask: "},			// "Маска файлов: "
						{"search_panel.text.result",		"Result: "},			// Результат:
						{"search_panel.text.search",		"Search: "},			// Искать:
						{"search_panel.text.text",			"text:"},				// "текст:"
						
						{"dialog.select_locations.title",	"Where"},				// Где искать
						{"dialog.select_locations.confirm",	"Host and path are equals in {0} and {1}. Continue?"},		// "{0} и {1} имеют одинаковые хост и путь. Продолжить?
						
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
