package org.logscanner.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Victor Kadachigov
 */
@Getter
@Setter
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
}
