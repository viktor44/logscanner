package org.logscanner.jobs;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.logscanner.AppConstants;
import org.logscanner.data.FileData;
import org.logscanner.logger.Logged;
import org.logscanner.logger.Logged.Level;
import org.logscanner.service.SearchModel;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CopyFilesWriter implements ItemWriter<FileData>, StepExecutionListener
{
	private StepExecution stepExecution;
	private boolean skipStep;
	private String resultFolder;

	@Override
	@Logged(level = Level.DEBUG)
	public void write(List<? extends FileData> items) throws Exception
	{
		if (skipStep) return;
		
		Path baseDir = Paths.get(resultFolder); 
		for (FileData fileData : items)
		{
			Path filePath = baseDir.resolve(fileData.getZipPath());
			try (InputStream inputStream = fileData.getContentReader().getInputStream())
			{
				log.info("Saving {} to {}", fileData.getFilePath(), filePath);
				Files.createDirectories(filePath.getParent());
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);	
			}
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution)
	{
		return null;
	}

	@Override
	public void beforeStep(StepExecution stepExecution)
	{
        this.stepExecution = stepExecution;
        this.skipStep = stepExecution.getJobParameters().getLong(AppConstants.JOB_PARAM_SAVE_TO_ARCHIVE) != SearchModel.SAVE_TYPE_FOLDER;
        this.resultFolder = stepExecution.getJobParameters().getString(AppConstants.JOB_PARAM_OUTPUT_FOLDER_NAME);
        
	}

}
