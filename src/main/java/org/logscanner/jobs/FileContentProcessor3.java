package org.logscanner.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.logscanner.AppConstants;
import org.logscanner.cache.BasicFileAttributesImpl;
import org.logscanner.cache.CacheFileInfo;
import org.logscanner.data.ByteContentReader;
import org.logscanner.data.FileData;
import org.logscanner.data.FileInfo;
import org.logscanner.data.LogEvent;
import org.logscanner.data.LogPattern;
import org.logscanner.exception.FileTooBigException;
import org.logscanner.logger.Logged;
import org.logscanner.logger.Logged.Level;
import org.logscanner.service.CacheManager;
import org.logscanner.service.FileServiceSelector;
import org.logscanner.service.FileSystemService;
import org.logscanner.service.FileSystemService.ReaderType;
import org.logscanner.service.JobResultModel;
import org.logscanner.service.LocalFileService;
import org.logscanner.service.LogPatternDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * читаем файл в память, используем reset. используем ZipInputStream
 * @author Victor Kadachigov
 */
public class FileContentProcessor3 implements ItemProcessor<FileInfo, FileData> 
{
	private static Logger log = LoggerFactory.getLogger(FileContentProcessor3.class);

	@Autowired
	private JobResultModel resultModel;
	@Autowired
	private LogPatternDao patternDao;
	@Autowired
	private FileServiceSelector fileServiceSelector;
	@Autowired
	private CacheManager cacheManager;

	private StepExecution stepExecution;
	private String commonPrefix;
	private String searchString;
	private String encoding;
	private String datePattern;
	private Date dateFrom;
	private Date dateTo;
	
	@Override
	@Logged(level = Level.DEBUG)
	public FileData process(FileInfo file) throws Exception 
	{
		try 
		{
			resultModel.addProcessedFile();

			FileData result = null;
			FileSystemService fileSystemService = fileServiceSelector.select(file.getLocationType());
			
			FileData fileData = new FileData();
			fileData.setLocationCode(file.getLocationCode());
			fileData.setFilePath(file.getFilePath());
			fileData.setZipPath(getZipPath(file));
			if (checkBeforeRead(file))
			{
				fileData.setContentReader(fileSystemService.readContent(file, ReaderType.URI));
				result = fileData;
			}
			else
			{
				try (InputStream inputStream = fileSystemService.getInputStream(file))
				{
					inputStream.mark(Integer.MAX_VALUE);
					if (match(inputStream, fileData))
					{
						inputStream.reset();
						fileData.setContentReader(new ByteContentReader(IOUtils.toByteArray(inputStream)));
						result = fileData;
					}
				}
			}
			if (result != null)
				resultModel.addSelectedFile();
			return result;
		}
		catch (FileTooBigException ex) 
		{
			log.error(ex.getMessage());
			return null;
		}
		catch (IOException ex)
		{
			log.error("", ex);
			return null;
		}
	}

    private boolean checkBeforeRead(FileInfo fileInfo)
	{
    	boolean result = false;
    	if (StringUtils.isEmpty(searchString))
    	{
    		CacheFileInfo cacheFileInfo = cacheManager.getFileInfo(fileInfo.getLocationCode(), fileInfo.getFilePath());
    		if (cacheFileInfo != null)
    		{
    			Date contentStart = cacheFileInfo.getContentStart();
    			Date contentEnd = cacheFileInfo.getContentEnd();
    			if (contentStart != null && contentEnd != null)
    				result = contentStart.compareTo(dateTo) <= 0 && contentEnd.compareTo(dateFrom) >= 0;
    			else if (contentStart != null)
    				result = contentStart.compareTo(dateFrom) >= 0 && contentStart.compareTo(dateTo) <= 0;
       			else if (contentEnd != null)
      				result = contentEnd.compareTo(dateFrom) >= 0 && contentEnd.compareTo(dateTo) <= 0;
    		}
    	}
		return result;
	}

	private boolean match(InputStream inputStream, FileData fileData) throws IOException 
    {
    	boolean result = false;

		log.info("Проверяю {} {}", fileData.getLocationCode(), fileData.getFilePath());
		
    	if (FilenameUtils.isExtension(fileData.getFilePath(), "zip"))
    	{
    		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null && !result)
			{
				if (!zipEntry.isDirectory())
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, encoding));
					result = match(reader, fileData);
				}
				zipEntry = zipInputStream.getNextEntry();
			}
    	}
    	else
    	{
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
			result = match(reader, fileData);
    	}

		return result;
	}
    
    private boolean match(BufferedReader reader, FileData fileData) throws IOException
    {
    	boolean result = false;
    	String line;
    	List<LogEvent> list = new ArrayList<>();
    	FastDateFormat dateFormat = StringUtils.isNotEmpty(datePattern) 
    								? FastDateFormat.getInstance(datePattern)
    								: null;
    	boolean dateInRangeWholeFile = false;
    	Date contentStart = null;
    	Date contentEnd = null;
    	Date lastParsedDate = null;
    	boolean lastParsedDateInRange = false;
    	while ((line = reader.readLine()) != null)
    	{
    		Date dt = tryToParseDate(line, dateFormat);
    		
    		boolean dateIsEmpty = dt == null; 
    		boolean dateInRange = false;
    		if (!dateIsEmpty)
    		{
    			if (contentStart == null)
    				contentStart = dt;
    			dateInRange = dt.compareTo(dateFrom) >= 0 && dt.compareTo(dateTo) <= 0;
    			dateInRangeWholeFile |= dateInRange;
    			lastParsedDate = dt;
    			lastParsedDateInRange = dateInRange; 

        		if (dt.compareTo(dateFrom) > 0 && dt.compareTo(dateTo) > 0)
        			break;
    		}
    		
    		if (dateIsEmpty || dateInRange)
    		{
    			if (searchString != null)
    			{
    	    		if (StringUtils.contains(line, searchString)
    	    				&& (dateInRange || lastParsedDate == null || lastParsedDateInRange))
    	    		{
    	    			list.add(new LogEvent(dt != null ? dt : lastParsedDate, "server1", fileData.getFilePath(), line));
    	    			result = true;
    	    		}
    			}
    			else if (dateInRange)
    			{
    				result = true;
    				break;
    			}
    		}
    	}
    	if (line == null) //we reach end of file
    		contentEnd = lastParsedDate;
    	if (dateInRangeWholeFile)
    		resultModel.addAll(list);
    	if (contentStart != null || contentEnd != null) 
    		cacheManager.updateFromContent(fileData.getLocationCode(), fileData.getFilePath(), contentStart, contentEnd);
    	result |= lastParsedDate == null; // we can't check date at all
    	return result;
    }

	private Date tryToParseDate(String line, FastDateFormat dateFormat) 
	{
		Date result = null;
		if (dateFormat != null)
		{
			try
			{
				result = dateFormat.parse(line);
			}
			catch (ParseException | NumberFormatException ex)
			{
				//Can't parse date. DO NOTHING.
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception
	{
		String s;
		s = "2018-05-17 14:18:43,682 [INFO ] [ru.sbrf.depositpf.contract.service.PerformedOperationService] [[deposit-contract]-server-rejected-thread-14] - Сохранили";
		s = "2018-05-17 14:18:43,682 ";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");
		FileContentProcessor fcp = new FileContentProcessor();
		Date d = df.parse(s); // fcp.tryToParseDate(s, df);
		System.out.println("ZZZZ: " + (d != null ? df.format(d) : "null"));
	}

	private String getZipPath(FileInfo file) 
    {
		String result = null;
		if (file.getHost() != null)
			result = file.getHost() + (file.getFilePath().startsWith("/") ? "" : "/") + file.getFilePath();
		else
		{
			FileSystemService fileSystemService = fileServiceSelector.select(file.getLocationType());
			result = fileSystemService.getRelativePath(file, commonPrefix);
		}
		return result;
	}

	private byte[] readContent(File file) throws IOException 
	{
		try (InputStream is = new BufferedInputStream(new FileInputStream(file)))
		{
			return IOUtils.toByteArray(is, file.length());
		}
	}

	@BeforeStep
    public void setStepExecution(StepExecution stepExecution) 
    {
        this.stepExecution = stepExecution;
		commonPrefix = stepExecution.getJobExecution().getExecutionContext().getString(AppConstants.PROP_COMMON_PATH);
    	searchString = stepExecution.getJobParameters().getString(AppConstants.JOB_SEARCH_STRING);
    	LogPattern logPattern = patternDao.getByCode(stepExecution.getJobParameters().getString(AppConstants.JOB_PARAM_PATTERN_CODE)); 
    	encoding = logPattern.getEncoding();
    	datePattern = logPattern.getDatePattern();
    	if (StringUtils.isEmpty(encoding))
    		encoding = "UTF-8";
    	dateFrom = stepExecution.getJobParameters().getDate(AppConstants.JOB_PARAM_FROM);
    	dateTo = stepExecution.getJobParameters().getDate(AppConstants.JOB_PARAM_TO);
    }
}
