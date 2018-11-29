package org.logscanner.jobs;

import java.util.List;

import org.logscanner.data.FileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
public class LogWriter implements ItemWriter<FileData>, StepExecutionListener
{
	@Override
	public void write(List<? extends FileData> items) throws Exception 
	{
//		if (items.size() > 0)
//			log.info("{} write: {}", hashCode(), items.get(0).getFiles().get(0).getAbsolutePath());
//		for (int i = 0; i < items.size(); i++)
//			log.info("{} write {}: {}", hashCode(), i, items.get(i));
		items.forEach(item -> {
			log.info("{} -> {}", item.getFilePath(), item.getZipPath());
		});
	}

	@Override
	public void beforeStep(StepExecution stepExecution)
	{
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution)
	{
		return null;
	}
}
