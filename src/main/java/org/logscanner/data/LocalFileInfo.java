package org.logscanner.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Victor Kadachigov
 */
public class LocalFileInfo implements FileInfo
{
	private final String locationCode;
	private final transient Path file;
	
	public LocalFileInfo(String locationCode, Path file)
	{
		this.locationCode = locationCode;
		this.file = file;
	}
	
	@Override
	public String getFilePath()
	{
		return file.toString();
	}
	@Override
	public Path getFile() 
	{
		return file;
	}
	@Override
	public LocationType getLocationType() 
	{
		return LocationType.LOCAL;
	}
	@Override
	public String getHost()
	{
		return null;
	}
	@Override
	public String getLocationCode() 
	{
		return locationCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocalFileInfo [locationCode=");
		builder.append(locationCode);
		builder.append(", file=");
		builder.append(file);
		builder.append("]");
		return builder.toString();
	}
}
