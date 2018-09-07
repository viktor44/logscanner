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
						{"results_panel.columns",			"Время;Файл;Строка"},
						
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
						{"dialog.select_locations.columns", ";;Код;Путь"},	
						
						{"action.save_to.title",			"Открыть"},
						
						{"status_panel.status.ready",		"Готов"},
						{"status_panel.status.searching",	"Идёт поиск"},
						{"status_panel.status.stopping",	"Останавливаю"},
						{"status_panel.status.done",		"Готово"},
						{"status_panel.text",				"Обработано {0} из {1}. Выбрано {2}"},
        		};

	}
}
