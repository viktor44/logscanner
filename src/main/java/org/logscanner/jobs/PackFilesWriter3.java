package org.logscanner.jobs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.lang3.NotImplementedException;
import org.logscanner.AppConstants;
import org.logscanner.Resources;
import org.logscanner.data.FileData;
import org.logscanner.logger.Logged;
import org.logscanner.logger.Logged.Level;
import org.logscanner.service.SearchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * using commons-compress
 * @author Victor Kadachigov
 */
@Slf4j
public class PackFilesWriter3 extends AbstractItemStreamItemWriter<FileData> implements StepExecutionListener
{
	private StepExecution stepExecution;
	private ParallelScatterZipCreator zipCreator;
	private ZipArchiveOutputStream outputStream;
	private Set<String> files;
	private boolean skipStep;

	@Override
	@Logged(level = Level.DEBUG)
	public void write(List<? extends FileData> items) throws Exception 
	{
		if (skipStep) return;
		
		for (FileData fileData : items)
		{
			String zipPath = fileData.getZipPath();
			if (files.contains(zipPath))
			{
				log.warn("File {} is already in archive. Skipping", zipPath);
				continue;
			}
			files.add(zipPath);
			log.info("Saving {} to {}", fileData.getFilePath(), zipPath);
			
			try (ScatterZipOutputStream os = ScatterZipOutputStream.fileBased(File.createTempFile("zip", ".notzip")))
			{
				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(zipPath);
				zipArchiveEntry.setMethod(ZipArchiveEntry.DEFLATED);
				os.addArchiveEntry(
						ZipArchiveEntryRequest.createZipArchiveEntryRequest(
								zipArchiveEntry,
								new InputStreamSupplier() 
								{
									@Override
									public InputStream get() 
									{
										try
										{
											return fileData.getContentReader().getInputStream();
										}
										catch (IOException ex)
										{
											throw new RuntimeException(ex);
										}
									}
								}
						)
				);
				
				synchronized (outputStream)
				{
					os.writeTo(outputStream);	
				}
			}
		}
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException 
	{
		if (!isInitialized()) 
		{
			files = new ConcurrentSkipListSet<>();
			String resultFile = stepExecution.getJobParameters().getString(AppConstants.JOB_PARAM_OUTPUT_ARCHIVE_NAME);
			File file = new File(resultFile);

			log.info("Result file {}", file.getAbsolutePath());
			
			try 
			{
				zipCreator = new ParallelScatterZipCreator();
				outputStream = new ZipArchiveOutputStream(file);
			} 
			catch (IOException ex) 
			{
				throw new ItemStreamException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public void close() 
	{
		try 
		{
			if (outputStream != null)
				outputStream.close();
		} 
		catch (IOException ex) 
		{
			throw new ItemStreamException(ex.getMessage(), ex);
		}
		finally 
		{
			zipCreator = null;
			outputStream = null;
			files = null;
		}
	}

    private boolean isInitialized() 
    {
		return skipStep || outputStream != null;
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
        this.skipStep = stepExecution.getJobParameters().getLong(AppConstants.JOB_PARAM_SAVE_TO_ARCHIVE) != SearchModel.SAVE_TYPE_FILE;
	}
}
