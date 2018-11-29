package org.logscanner;

/**
 * @author Victor Kadachigov
 */
public class AppConstants 
{
	public static final String APP_NAME = "Log Scanner";
	
	public static final String JOB_NAME = "CollectLogs";
	public static final String PROP_DIRS_INFO = "DirsInfo";
	public static final String PROP_COMMON_PATH = "CommonPath";
	
	public static final String JOB_PARAM_ID = "JobId";
	/**
	 * Сохранять найденные логи в архив
	 */
	public static final String JOB_PARAM_SAVE_TO_ARCHIVE = "SaveToArchive";
	/**
	 * Имя архива с логами
	 */
	public static final String JOB_PARAM_OUTPUT_ARCHIVE_NAME = "OutputArchiveName";
	/**
	 * Имя папки с логами
	 */
	public static final String JOB_PARAM_OUTPUT_FOLDER_NAME = "OutputFolderName";
	/**
	 * Кодировка файлов с логами
	 */
	public static final String JOB_PARAM_ENCODING = "Encoding";
	/**
	 * Где искать (идентификаторы)
	 */
	public static final String JOB_PARAM_LOCATIONS = "Locations";
	/**
	 * Искать с ...
	 */
	public static final String JOB_PARAM_FROM = "DateFrom";
	/**
	 * Искать по ...
	 */
	public static final String JOB_PARAM_TO = "DateTo";
	/**
	 * Строка логов для поиска
	 */
	public static final String JOB_SEARCH_STRING = "SearchString";
	/**
	 * 
	 */
	public static final String JOB_PARAM_PATTERN_CODE = "PatternCode";
}
