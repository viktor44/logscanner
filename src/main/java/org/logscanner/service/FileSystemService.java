package org.logscanner.service;

import java.io.IOException;
import java.util.List;

import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.Location;
import org.logscanner.data.LogPattern;

/**
 * @author Victor Kadachigov
 */
public interface FileSystemService 
{
	public byte[] readContent(FileInfo file) throws IOException;
	public String getRelativePath(FileInfo file, String basePath);
	public List<FileInfo> listFiles(Location location, FilterParams filterParams) throws IOException;
}
