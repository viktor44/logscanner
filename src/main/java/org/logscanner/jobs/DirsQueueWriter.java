package org.logscanner.jobs;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.logscanner.AppConstants;
import org.logscanner.data.DirInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

/**
 * @author Victor Kadachigov
 */
public class DirsQueueWriter implements ItemWriter<DirInfo> 
{
	private static Logger log = LoggerFactory.getLogger(DirsQueueWriter.class);

	private StepExecution stepExecution;

	@Override
	public void write(List<? extends DirInfo> items) throws Exception 
	{
		ExecutionContext stepContext = stepExecution.getExecutionContext();
        BlockingQueue<DirInfo> queue = (BlockingQueue<DirInfo>)stepContext.get(AppConstants.PROP_DIRS_INFO);
        if (queue == null)
        {
        	synchronized (this) 
        	{
        		queue = (BlockingQueue<DirInfo>)stepContext.get(AppConstants.PROP_DIRS_INFO);
        		if (queue == null)
        		{
                	queue = new ArrayBlockingQueue<>(100);
                	stepContext.put(AppConstants.PROP_DIRS_INFO, queue);
        		}
			}
        }
        for (DirInfo di : items)
        	queue.put(di);
	}

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) 
    {
        this.stepExecution = stepExecution;
    }
}
