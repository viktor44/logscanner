package org.logscanner.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.NotImplementedException;

/**
 * @author Victor Kadachigov
 */
public class SFTPFileInfo implements FileInfo
{
	private final transient Path path;
	private final String host;
	
	public SFTPFileInfo(String host, Path path)
	{
		this.host = host;
		this.path = path;
	}

	@Override
	public String getFilePath()
	{
		return getFile().toString();
	}

	@Override
	public Path getFile() 
	{
		return path;
	}

	@Override
	public LocationType getLocationType() 
	{
		return LocationType.SFTP;
	}

	@Override
	public String getHost()
	{
		return host;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SFTPFileInfo [path=");
		builder.append(path);
		builder.append(", host=");
		builder.append(host);
		builder.append("]");
		return builder.toString();
	}

}
