package org.logscanner.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.lang3.NotImplementedException;

import lombok.Getter;

/**
 * @author Victor Kadachigov
 */
public class SFTPFileInfo extends LocalFileInfo
{
	@Getter
	private final String host;
	
	public SFTPFileInfo(String locationCode, String host, Path path)
	{
		super(locationCode, path);
		this.host = host;
	}

	@Override
	public LocationType getLocationType() 
	{
		return LocationType.SFTP;
	}
}
