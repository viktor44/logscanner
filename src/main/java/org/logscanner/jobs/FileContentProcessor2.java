package org.logscanner.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.logscanner.AppConstants;
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
import org.logscanner.service.JobResultModel;
import org.logscanner.service.LogPatternDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * читаем файл в память, используем reset. используем ArchiveInputStream
 * @author Victor Kadachigov
 */
@Slf4j
public class FileContentProcessor2 implements ItemProcessor<FileInfo, FileData>, StepExecutionListener
{
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
			FileData result = null;
			FileSystemService fileSystemService = fileServiceSelector.select(file.getLocationType());
			try (InputStream inputStream = fileSystemService.getInputStream(file))
			{
				inputStream.mark(Integer.MAX_VALUE);
				FileData fileData = new FileData();
				fileData.setLocationCode(file.getLocationCode());
				fileData.setFilePath(file.getFilePath());
				fileData.setZipPath(getZipPath(file));
				if (match(inputStream, fileData))
				{
					inputStream.reset();
					fileData.setContentReader(new ByteContentReader(IOUtils.toByteArray(inputStream)));
					resultModel.addSelectedFile();
					result = fileData;
				}
			}
			return result;
		}
		catch (FileTooBigException ex)
		{
			log.error(ex.getMessage());
			return null;
		}
		catch (IOException | ArchiveException ex)
		{
			log.error("", ex);
			return null;
		}
	}

    private boolean match(InputStream inputStream, FileData fileData) throws IOException, ArchiveException 
    {
    	boolean result = false;

		log.info("Checking {} {}", fileData.getLocationCode(), fileData.getFilePath());
		
		resultModel.addProcessedFile();
		
    	if (FilenameUtils.isExtension(fileData.getFilePath(), "zip"))
    	{
    		ArchiveInputStream zipInputStream = (new ArchiveStreamFactory()).createArchiveInputStream(ArchiveStreamFactory.ZIP, inputStream);
			ArchiveEntry zipEntry = zipInputStream.getNextEntry();
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
    	Date firstParsedDate = null;
    	Date lastParsedDate = null;
    	boolean lastParsedDateInRange = false;
    	while ((line = reader.readLine()) != null)
    	{
    		Date dt = tryToParseDate(line, dateFormat);
    		
    		boolean dateIsEmpty = dt == null; 
    		boolean dateInRange = false;
    		if (!dateIsEmpty)
    		{
    			if (firstParsedDate == null)
    				firstParsedDate = dt;
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
    	if (dateInRangeWholeFile)
    		resultModel.addAll(list);
    	if (firstParsedDate != null)
    	{
    		cacheManager.updateFromContent(fileData.getLocationCode(), fileData.getFilePath(), firstParsedDate, null);
    	}
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
		FileContentProcessor2 fcp = new FileContentProcessor2();
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

    @AfterStep
	@Override
	public ExitStatus afterStep(StepExecution stepExecution)
	{
		return null;
	}

	@BeforeStep
	@Override
	public void beforeStep(StepExecution stepExecution)
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
