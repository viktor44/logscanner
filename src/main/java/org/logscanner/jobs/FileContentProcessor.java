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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.logscanner.AppConstants;
import org.logscanner.data.FileData;
import org.logscanner.data.FileInfo;
import org.logscanner.data.LogEvent;
import org.logscanner.service.FileServiceSelector;
import org.logscanner.service.FileSystemService;
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
 * @author Victor Kadachigov
 */
public class FileContentProcessor implements ItemProcessor<FileInfo, FileData> 
{
	private static Logger log = LoggerFactory.getLogger(FileContentProcessor.class);

	@Autowired
	private JobResultModel resultModel;
	@Autowired
	private LogPatternDao patternDao;
	@Autowired
	private FileServiceSelector fileServiceSelector;

	private StepExecution stepExecution;
	private String commonPrefix;
	private String searchString;
	private String encoding;
	private Date dateFrom;
	private Date dateTo;
	
	@Override
	public FileData process(FileInfo file) throws Exception 
	{
		try 
		{
			FileSystemService fileSystemService = fileServiceSelector.select(file.getLocationType());
			FileData fileData = new FileData();
			fileData.setFilePath(file.getFilePath());
			fileData.setContent(fileSystemService.readContent(file));
			fileData.setZipPath(getZipPath(file));
			FileData result = null;
			if (match(fileData))
				result = fileData;
			return result;
		}
		catch (IOException ex)
		{
			log.error("", ex);
			return null;
		}
	}

    private boolean match(FileData fileData) throws IOException 
    {
    	boolean result = false;
    	result = StringUtils.isBlank(searchString);
    	if (!result)
    	{
			log.info("Проверяю {}", fileData.getFilePath());
			
        	if (FilenameUtils.isExtension(fileData.getFilePath(), "zip"))
        	{
        		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(fileData.getContent())))
        		{
        			ZipEntry zipEntry = zipInputStream.getNextEntry();
        			while (zipEntry != null && !result)
        			{
        				BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, encoding));
       					result = match(reader, fileData);
        				zipEntry = zipInputStream.getNextEntry();
        			}
        		}
        	}
        	else
        	{
				try (BufferedReader reader = new BufferedReader(
									new InputStreamReader(
											new FileInputStream(fileData.getFilePath()), encoding
									)
							)
					)
				{
					result = match(reader, fileData);
				}
        	}
    	}
		return result;
	}
    
    private boolean match(BufferedReader reader, FileData fileData) throws IOException
    {
    	boolean result = false;
    	String line;
    	List<LogEvent> list = new ArrayList<>();
    	while ((line = reader.readLine()) != null)
    	{
    		Date dt = tryToParseDate(line);
    		boolean b = dt == null || (dt.compareTo(dateFrom) >= 0 && dt.compareTo(dateTo) <= 0);
    		if (b)
    			b = StringUtils.contains(line, searchString);
    		if (b)
    		{
    			list.add(new LogEvent(dt, "server1", fileData.getFilePath(), line));
    		}
    		result |= b;
    	}
    	resultModel.addAll(list);
    	return result;
    }

	private Date tryToParseDate(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getZipPath(FileInfo file) 
    {
		FileSystemService fileSystemService = fileServiceSelector.select(file.getLocationType());
		return fileSystemService.getRelativePath(file, commonPrefix);
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
    	encoding = patternDao.getByCode(stepExecution.getJobParameters().getString(AppConstants.JOB_PARAM_PATTERN_CODE)).getEncoding();
    	if (StringUtils.isEmpty(encoding))
    		encoding = "UTF-8";
    	dateFrom = stepExecution.getJobParameters().getDate(AppConstants.JOB_PARAM_FROM);
    	dateTo = stepExecution.getJobParameters().getDate(AppConstants.JOB_PARAM_TO);
    }
}
