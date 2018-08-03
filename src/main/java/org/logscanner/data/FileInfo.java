package org.logscanner.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;

/**
 * @author Victor Kadachigov
 */
public interface FileInfo extends Serializable
{
	public LocationType getLocationType();
	public String getLocationCode();
//	public InputStream getInputStream() throws IOException;
//	public long length();
	public Path getFile();
	public String getHost();
	public String getFilePath();
//	public Date getLastModified();
//	public Date getCreated();
}
