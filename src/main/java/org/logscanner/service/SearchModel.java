package org.logscanner.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSetMetaData;
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

import lombok.Getter;

/**
 * @author Victor Kadachigov
 */
@Component
public class SearchModel implements Serializable
{
	public static final int SAVE_TYPE_FILE = 1;
	public static final int SAVE_TYPE_FOLDER = 2;
	
	@Autowired
	private AppProperties props;

	@Getter
	private LocalDate fromDate;
	@Getter
	private LocalTime fromTime;
	@Getter
	private LocalDate toDate;
	@Getter
	private LocalTime toTime;
	@Getter
	private boolean saveResults;
	@Getter
	private int saveType = SAVE_TYPE_FOLDER;
	@Getter
	private String resultFile;
	@Getter
	private String resultFolder;
	@Getter
	private Set<String> selectedLocations = new HashSet<>();
//	private List<String> searchStrings;
	@Getter
	private String searchString;
	@Getter
	private String patternCode;
	@Getter
	private Long executionId;
	private PropertyChangeSupport propertyChangeSupport;
	
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
		saveResults = props.getDefaultSaveToFile() != null ? props.getDefaultSaveToFile() : false;
		if (StringUtils.isNotBlank(props.getDefaultDir())) 
		{
			resultFile = Paths.get(props.getDefaultDir(), "result.zip").toString();
			resultFolder = Paths.get(props.getDefaultDir()).toString();
		}
		else
			resultFile = "result.zip";
		patternCode = null;// props.getDefaultPatternCode();
	}
	
	public void setResultFile(String resultFile) {
		String oldResulFile = this.resultFile;
		this.resultFile = resultFile;
		firePropertyChange("resultFile", oldResulFile, resultFile);
	}
	public void setSelectedLocations(Set<String> selectedLocations) {
		Set<String> oldSelectedLocations = new HashSet<>(this.selectedLocations);
		this.selectedLocations.clear();
		this.selectedLocations.addAll(selectedLocations);
		firePropertyChange("selectedLocations", oldSelectedLocations, selectedLocations);
	}
	public void setSearchString(String searchString) {
		String oldSearchString = this.searchString;
		this.searchString = searchString;
		firePropertyChange("searchString", oldSearchString, searchString);
	}
	public void setSaveResults(boolean saveResults) {
		boolean oldSaveResults = this.saveResults;
		this.saveResults = saveResults;
		firePropertyChange("saveToFile", oldSaveResults, saveResults);
	}

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) 
    {
        if (propertyChangeSupport == null)
            propertyChangeSupport = new SwingPropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) 
    {
        if (propertyChangeSupport == null)
            return;
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) 
    {
        if (propertyChangeSupport == null)
            propertyChangeSupport = new SwingPropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) 
    {
        if (propertyChangeSupport == null)
            return;
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChangeSupport == null) 
        	return;
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
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

	public void setFromDate(LocalDate newFromDate) {
		LocalDate oldFromDate = this.fromDate;
		this.fromDate = newFromDate;
		firePropertyChange("fromDate", oldFromDate, newFromDate);

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

	public void setFromTime(LocalTime fromTime) {
		LocalTime oldFromTime = this.fromTime;
		this.fromTime = fromTime;
		firePropertyChange("fromTime", oldFromTime, fromTime);
	}

	public void setToDate(LocalDate toDate) {
		LocalDate oldToDate = this.toDate;
		this.toDate = toDate;
		firePropertyChange("toDate", oldToDate, toDate);
	}

	public void setToTime(LocalTime toTime) {
		LocalTime oldToTime = this.toTime;
		this.toTime = toTime;
		firePropertyChange("toTime", oldToTime, toTime);
	}

	public void setExecutionId(Long executionId) {
		Long oldExecutionId = this.executionId;
		this.executionId = executionId;
		firePropertyChange("executionId", oldExecutionId, executionId);
	}

	public void setPatternCode(String patternCode) {
		String oldPatternCode = this.patternCode;
		this.patternCode = patternCode;
		firePropertyChange("patternCode", oldPatternCode, patternCode);
	}

	public void saveDefaults() {
		props.setDefaultPatternCode(patternCode);
		props.setDefaultSaveToFile(saveResults);
		if (saveResults) {
			Path p = Paths.get(resultFile);
			if (p.getParent() != null)
				props.setDefaultDir(p.getParent().toString());
		}
	}

	public void setResultFolder(String resultFolder) {
		String oldResultFolder = this.resultFolder;
		this.resultFolder = resultFolder;
		firePropertyChange("resultFolder", oldResultFolder, resultFolder);
	}

	public void setSaveType(int saveType) {
		int oldSaveType = this.saveType;
		this.saveType = saveType;
		firePropertyChange("saveType", oldSaveType, saveType);
	}
}
