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
public class Resources_ru extends ListResourceBundle
{
	protected static String IMAGE_SET1[][] = new String[][] 
								{
										{"image.locations.16",		"/images/set1/Open_16x16.png"},
										{"image.exit.16",			"/images/set1/Log Out_16x16.png"},
										{"image.open.16",			"/images/set1/Open_16x16.png"},
								};
	
	private static ResourceBundle getInstance()
	{
		return ResourceBundle.getBundle(Resources_ru.class.getName(), Locale.US);
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
//						{"error.not_implemented",			"Not implemented"},
						{"error.cant_start",				"Ошибка запуска приложения"},
						{"error.log_open_error",			"Ошибка при открытии лог-файла"},
						{"error.file_too_big",				"Размер файла {0} {1}Mb превышает максимальное значение {2}Mb"},
//						{"error.unsupported_location_type",	"Unsupported location type {0}"},
//						{"error.code_is_empty",				"Empty code"},
						{"error.code_already_used",			"Code '{0}' already used"},
						{"error.context_is_null",			"Spring context is null"},
						
						{"action.file",						"Файл"},
						{"action.help",						"Справка"},
						{"dialog.title.error",				"Ошибка"},
						{"dialog.title.warning",			"Внимание"},
						{"dialog.title.info",				"Информация"},
						{"dialog.title.confirm",			"Подтверждение"},
						
						{"dialog.about.title",				"О программе"},
						{"dialog.about.text.version",		"Версия {0}"},
						{"dialog.about.text.copyright",		"Copyright 2018 by Victor Kadachigov"},
						{"dialog.button.cancel",			"Отмена"},
						
						{"action.copy_text.title",			"Копировать текст"},
						{"action.exit.title",				"Выход"},
						{"action.open_log.title",			"Открыть файл"},
						{"action.preferences.title",		"Настройки..."},
						{"action.select_locations.title",	"Где?"},
						
						{"results_panel.text.done",			"Готово. Работали {0}"},
						
						{"action.search.title",				"Искать"},
						{"action.search.stop",				"Остановить"},
						{"action.search.text.file_exists",	"Файл {0} уже существует. Перезаписать?"},
						{"action.search.text.no_locations",	"Не выбрано ни одного расположения для поиска"},
						
						{"search_panel.text.from",			"Период с: "},
						{"search_panel.text.to",			"по: "},
						{"search_panel.text.where",			"Где искать: "},
						{"search_panel.text.file_mask",		"Маска файлов: "},
						{"search_panel.text.result",		"Результат: "},
						{"search_panel.text.search",		"Искать: "},
						{"search_panel.text.text",			"текст:"},
						
						{"dialog.select_locations.title",	"Где искать"},
						{"dialog.select_locations.confirm",	"{0} и {1} имеют одинаковые хост и путь. Продолжить?"},
						{"dialog.select_locations.columns", ";;Code;Path"},	
						
						{"action.save_to.title",			"Open"},				// Открыть
						
						{"status_panel.status.ready",		"Ready"},				// Готов
						{"status_panel.status.searching",	"Searching"},			// Идёт поиск
						{"status_panel.status.stopping",	"Stopping"},			// Останавливаю
						{"status_panel.status.done",		"Done"},				// Готово
						{"status_panel.text",				"Processed {0} from {1}. Selected {2}"},	// Обработано {0} из {1}. Выбрано {2}
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
		URL imageURL = Resources_ru.class.getResource(imagePath);
		if (imageURL == null)
			throw new IllegalArgumentException(MessageFormat.format("Resource \"{0}\" not found", imagePath));
		return new ImageIcon(imageURL);
	}
	
}
