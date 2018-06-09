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
	private final transient Path file;
	
	public LocalFileInfo(Path file)
	{
		this.file = file;
	}
	
	@Override
	public String getFilePath()
	{
		return file.toString();
	}

	public Path getFile() 
	{
		return file;
	}

	@Override
	public LocationType getLocationType() {
		return LocationType.LOCAL;
	}
}
