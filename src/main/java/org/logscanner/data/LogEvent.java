package org.logscanner.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author Victor Kadachigov
 */
public class LogEvent 
{
	private Date logTime;
	private String locationName;
	private String path;
	private String text;
	
	public LogEvent(String text) {
		this.text = text;
	}

	public LogEvent(Date logTime, String locationName, String path, String text) {
		this.logTime = logTime;
		this.locationName = locationName;
		this.path = path;
		this.text = text;
	}
	
	public Date getLogTime() {
		return logTime;
	}
	public void setLogTime(Date time) {
		this.logTime = time;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
