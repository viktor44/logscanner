package org.logscanner.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.swing.AbstractListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.logscanner.data.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
@Scope("singleton")
public class JobResultModel extends AbstractListModel<LogEvent> implements JobExecutionListener, ListSelectionListener //, ExceptionHandler
{
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(JobResultModel.class);

	public static enum JobState
	{
		RUNNED,
		STOPPING,
		STOPPED;
	}
	
	@Autowired
	private AppProperties props;
	
	private CircularFifoQueue<LogEvent> queue;
	private JobState jobState = JobState.STOPPED;
	private PropertyChangeSupport changeSupport;
	private LocalTime startTime;
	private LocalTime endTime;
	private int selectedItemIndex;
	private Throwable error;
	private AtomicInteger filesToProcess = new AtomicInteger();
	private AtomicInteger processedFiles = new AtomicInteger();
	private AtomicInteger selectedFiles = new AtomicInteger();
	
	@PostConstruct
	void init()
	{
		queue = new CircularFifoQueue<>(props.getMaxResults());
	}
	
    public synchronized void add(LogEvent event)
	{
		queue.add(event);
		int size = queue.size();
		if (size == queue.maxSize())
			fireContentsChanged(this, 0, size);
		else
			fireIntervalAdded(this, size - 1, size);
	}

    public synchronized void addAll(Collection<LogEvent> events)
	{
    	if (events == null || events.isEmpty())
    		return;
    	
		queue.addAll(events);
		int size = queue.size();
		if (size == queue.maxSize())
			fireContentsChanged(this, 0, size);
		else
			fireIntervalAdded(this, Math.max(0, size - events.size()), size);
	}

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) 
    {
        if (changeSupport == null)
            changeSupport = new SwingPropertyChangeSupport(this);
        changeSupport.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) 
    {
        if (changeSupport == null)
            return;
        changeSupport.removePropertyChangeListener(listener);
    }
    
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) 
    {
        if (changeSupport == null)
            changeSupport = new SwingPropertyChangeSupport(this);
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) 
    {
        if (changeSupport == null)
            return;
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) 
    {
        if (changeSupport == null) 
        	return;
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public synchronized void clear()
	{
		int size = queue.size();
		queue.clear();
		fireIntervalRemoved(this, 0, size);
		error = null;
		startTime = null;
		endTime = null;
	}
	
	@Override
	public int getSize() 
	{
		return queue.size();
	}

	@Override
	public LogEvent getElementAt(int index) 
	{
		return index < queue.size() ? queue.get(index) : null;
	}

	public void stopping() 
	{
		setJobState(JobState.STOPPING);
	}

	@Override
	public void beforeJob(JobExecution jobExecution) 
	{
		log.info("beforeJob({})", jobExecution);
		clear();
		startTime = jobExecution.getStartTime()
						.toInstant()
	      				.atZone(ZoneId.systemDefault())
	      				.toLocalTime();
		setJobState(JobState.RUNNED);
	}

	@Override
	public void afterJob(JobExecution jobExecution) 
	{
		log.info("afterJob({})", jobExecution);
		endTime = jobExecution.getEndTime()
						.toInstant()
		  				.atZone(ZoneId.systemDefault())
		  				.toLocalTime();
		if (jobExecution.getStatus() == BatchStatus.FAILED || jobExecution.getStatus() == BatchStatus.UNKNOWN)
		{
			List<Throwable> list = jobExecution.getAllFailureExceptions();
			if (!list.isEmpty())
				error = list.get(list.size() - 1);
		}
		setJobState(JobState.STOPPED);
	}

	public JobState getJobState() 
	{
		return jobState;
	}
	private void setJobState(JobState jobState) 
	{
		firePropertyChange("jobState", this.jobState, jobState);
		this.jobState = jobState;
	}

	public LocalTime getStartTime() 
	{
		return startTime;
	}

	public LocalTime getEndTime() 
	{
		return endTime;
	}

	@Override
	public void valueChanged(ListSelectionEvent event) 
	{
//		log.info("valueChanged({})", event);
		selectedItemIndex = event.getFirstIndex();
	}
	
	public LogEvent getSelectedItem()
	{
		return (selectedItemIndex >= 0) ? queue.get(selectedItemIndex) : null;
	}

	public Throwable getError() 
	{
		return error;
	}
	
	public boolean isSuccess()
	{
		return endTime != null && error == null;
	}

	public int getFilesToProcess()
	{
		return filesToProcess.intValue();
	}
	public void addFilesToProcess(int value)
	{
		firePropertyChange("filesToProcess", filesToProcess.intValue(), filesToProcess.intValue() + value);
		filesToProcess.addAndGet(value);
	}

	public int getProcessedFiles()
	{
		return processedFiles.intValue();
	}
	public void addProcessedFile()
	{
		firePropertyChange("processedFiles", processedFiles.intValue(), processedFiles.intValue() + 1);
		processedFiles.incrementAndGet();
	}

	public int getSelectedFiles()
	{
		return selectedFiles.intValue();
	}
	public void addSelectedFile()
	{
		firePropertyChange("selectedFiles", selectedFiles.intValue(), selectedFiles.intValue() + 1);
		selectedFiles.incrementAndGet();
	}
	
}
