package org.logscanner.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.listener.CompositeStepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;

public class ResultFilesWriter<T> extends CompositeItemWriter<T> implements StepExecutionListener
{
	private CompositeStepExecutionListener listeners = new CompositeStepExecutionListener(); 
	
	public ResultFilesWriter(List<ItemWriter<? super T>> writers)
	{
		super();
		setDelegates(writers);
	}
	
	@Override
	public void setDelegates(List<ItemWriter<? super T>> delegates)
	{
		for (ItemWriter<? super T> w : delegates)
			if (w instanceof StepExecutionListener)
				listeners.register((StepExecutionListener)w); 
		super.setDelegates(delegates);
	}

	@BeforeStep
	@Override
	public void beforeStep(StepExecution stepExecution)
	{
		listeners.beforeStep(stepExecution);
	}

	@AfterStep
	@Override
	public ExitStatus afterStep(StepExecution stepExecution)
	{
		return listeners.afterStep(stepExecution);
	}

}
