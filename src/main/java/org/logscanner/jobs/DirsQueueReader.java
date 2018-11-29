package org.logscanner.jobs;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.AppConstants;
import org.logscanner.data.DirInfo;
import org.logscanner.data.FileInfo;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
public class DirsQueueReader implements ItemReader<FileInfo>, StepExecutionListener 
{
	@Autowired
	private JobResultModel eventQueueHolder;

	private StepExecution stepExecution;
	private Object monitor = new Object();
	private Iterator<FileInfo> iterator;
	
	@Override
	public FileInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException 
	{
		FileInfo result = null;
		synchronized (monitor) 
		{
			if (iterator == null || !iterator.hasNext())
			{
				iterator = null;
				ExecutionContext stepContext = stepExecution.getJobExecution().getExecutionContext();
		        BlockingQueue<DirInfo> queue = (BlockingQueue<DirInfo>)stepContext.get(AppConstants.PROP_DIRS_INFO);
        		if (queue != null)
        		{
        			DirInfo currentDir = queue.poll();
        			if (currentDir != null)
        			{
            			log.info("Processing {} {} {}", currentDir.getLocationCode(), StringUtils.defaultString(currentDir.getHost()), currentDir.getRootPath());
            			iterator = currentDir.getFiles().iterator();
        			}
        		}
			}
			if (iterator != null)
				result = iterator.next();
		}
		return result;
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
        this.iterator = null;
    }

}
