package org.logscanner.jobs;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.AppConstants;
import org.logscanner.data.DirInfo;
import org.logscanner.data.FileInfo;
import org.logscanner.data.LogEvent;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Victor Kadachigov
 */
public class DirsQueueReader implements ItemReader<FileInfo> 
{
	private static Logger log = LoggerFactory.getLogger(DirsQueueReader.class);

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
            			log.info("Обрабатываю {} {} {}", currentDir.getLocationCode(), StringUtils.defaultString(currentDir.getHost()), currentDir.getRootPath());
            			iterator = currentDir.getFiles().iterator();
        			}
        		}
			}
			if (iterator != null)
				result = iterator.next();
		}
		return result;
	}

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) 
    {
        this.stepExecution = stepExecution;
        this.iterator = null;
    }

}
