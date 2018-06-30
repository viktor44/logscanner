package org.logscanner.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.logscanner.common.gui.ListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class SearchModel 
{
	private static Logger log = LoggerFactory.getLogger(SearchModel.class);
	
	@Autowired
	private AppProperties props;

	private LocalDate fromDate;
	private LocalTime fromTime;
	private LocalDate toDate;
	private LocalTime toTime;
	private boolean saveToFile;
	private String resultPath;
	private Set<String> selectedLocations;
	private List<String> searchStrings;
	private String searchString;
	private String patternCode;
	private Long executionId;
	private PropertyChangeSupport changeSupport;
	
	public SearchModel()
	{
	}
	
	@PostConstruct
	public void init()
	{
		fromDate = LocalDate.now();
		fromTime = LocalTime.of(0, 0, 0);
		toDate = LocalDate.now();
		toTime = LocalTime.now();
		selectedLocations = new HashSet<>();
		saveToFile = props.getDefaultSaveToFile() != null ? props.getDefaultSaveToFile() : false;
		if (StringUtils.isNotBlank(props.getDefaultDir()))
			resultPath = Paths.get(props.getDefaultDir(), "result.zip").toString();
		else
			resultPath = "result.zip";
		patternCode = props.getDefaultPatternCode();
	}
	
	public String getResultPath() {
		return resultPath;
	}
	public void setResultPath(String resultPath) {
		firePropertyChange("resultPath", this.resultPath, resultPath);
		this.resultPath = resultPath;
	}
	public Set<String> getSelectedLocations() {
		return selectedLocations;
	}
	public void setSelectedLocations(Set<String> selectedLocations) {
		firePropertyChange("selectedLocations", this.selectedLocations, selectedLocations);
		this.selectedLocations = selectedLocations;
	}
	public List<String> getSearchStrings() {
		return searchStrings;
	}
	public void setSearchStrings(List<String> searchStrings) {
		firePropertyChange("searchStrings", this.searchStrings, searchStrings);
		this.searchStrings = searchStrings;
	}
	
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		firePropertyChange("searchString", this.searchString, searchString);
		this.searchString = searchString;
	}
	
	public boolean isSaveToFile() {
		return saveToFile;
	}
	public void setSaveToFile(boolean saveToFile) {
		firePropertyChange("saveToFile", this.saveToFile, saveToFile);
		this.saveToFile = saveToFile;
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
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null) 
        	return;
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    
    public LocalDateTime getFrom()
    {
    	LocalDateTime result = LocalDateTime.of(fromDate, fromTime);
    	return result;
    }
    
    public LocalDateTime getTo()
    {
    	LocalDateTime result = LocalDateTime.of(toDate, toTime);
    	return result;
    }

    @Override
    public String toString() {
    	return ToStringBuilder.reflectionToString(this);
    }

	public LocalDate getFromDate() {
		return fromDate;
	}
	public void setFromDate(LocalDate newFromDate) {
		firePropertyChange("fromDate", this.fromDate, newFromDate);
		this.fromDate = newFromDate;

		LocalDate newToDate = this.toDate;
		if (fromDate != null) // && fromDate.compareTo(toDate) > 0
		{
			setToDate(fromDate);
			if (fromDate.compareTo(LocalDate.now()) == 0)
				setToTime(LocalTime.now());
			else
				setToTime(LocalTime.of(23, 59, 59));
		}
	}

	public LocalTime getFromTime() {
		return fromTime;
	}
	public void setFromTime(LocalTime fromTime) {
		firePropertyChange("fromTime", this.fromTime, fromTime);
		this.fromTime = fromTime;
	}

	public LocalDate getToDate() {
		return toDate;
	}
	public void setToDate(LocalDate toDate) {
		firePropertyChange("toDate", this.toDate, toDate);
		this.toDate = toDate;
	}

	public LocalTime getToTime() {
		return toTime;
	}
	public void setToTime(LocalTime toTime) {
		firePropertyChange("toTime", this.toTime, toTime);
		this.toTime = toTime;
	}

	public Long getExecutionId() {
		return executionId;
	}
	public void setExecutionId(Long executionId) {
		firePropertyChange("executionId", this.executionId, executionId);
		this.executionId = executionId;
	}

	public String getPatternCode() {
		return patternCode;
	}
	public void setPatternCode(String patternCode) {
		firePropertyChange("patternCode", this.patternCode, patternCode);
		this.patternCode = patternCode;
	}

	public void saveDefaults()
	{
		props.setDefaultPatternCode(patternCode);
		props.setDefaultSaveToFile(saveToFile);
		if (saveToFile)
		{
			Path p = Paths.get(resultPath);
			props.setDefaultDir(p.getParent().toString());
		}
	}
}
