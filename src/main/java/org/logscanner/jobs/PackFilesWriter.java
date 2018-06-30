package org.logscanner.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.logscanner.AppConstants;
import org.logscanner.data.FileData;
import org.logscanner.data.LogEvent;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Victor Kadachigov
 */
public class PackFilesWriter extends AbstractItemStreamItemWriter<FileData>
{
	private static Logger log = LoggerFactory.getLogger(PackFilesWriter.class);

	private StepExecution stepExecution;
	private ZipOutputStream outputStream;

	@Override
	public synchronized void write(List<? extends FileData> items) throws Exception 
	{
		for (FileData fileData : items)
		{
			//log.info("Сохраняю {} -> {}", fileData.getFilePath(), fileData.getZipPath());
			log.info("Сохраняю {}", fileData.getFilePath()); //, Runtime.getRuntime().freeMemory());
			ZipEntry entry = new ZipEntry(fileData.getZipPath());
			outputStream.putNextEntry(entry);
			try (InputStream inputStream = fileData.getContentReader().getInputStream())
			{
				IOUtils.copy(inputStream, outputStream);	
			}
			outputStream.closeEntry();
		}
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException 
	{
		if (!isInitialized()) 
		{
			if (stepExecution.getJobParameters().getLong(AppConstants.JOB_PARAM_SAVE_TO_ARCHIVE) == 1L)
			{
				String resultFile = stepExecution.getJobParameters().getString(AppConstants.JOB_PARAM_OUTPUT_ARCHIVE_NAME);
				File file = new File(resultFile);

				log.info("Файл с результатами {}", file.getAbsolutePath());
				
				try 
				{
					outputStream = new ZipOutputStream(new FileOutputStream(file));
				} 
				catch (FileNotFoundException ex) 
				{
					throw new ItemStreamException(ex.getMessage(), ex);
				}
			}
			else
				outputStream = new ZipOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
		}
	}

	@Override
	public void close() 
	{
		try 
		{
			if (isInitialized())
				outputStream.close();
		} 
		catch (IOException ex) 
		{
			throw new ItemStreamException(ex.getMessage(), ex);
		}
		finally 
		{
			outputStream = null;
		}
	}

    private boolean isInitialized() 
    {
		return outputStream != null;
	}

	@BeforeStep
    public void setStepExecution(StepExecution stepExecution) 
    {
        this.stepExecution = stepExecution;
    }
}
