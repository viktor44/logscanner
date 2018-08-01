package org.logscanner.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.logscanner.data.ContentReader;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.Location;
import org.logscanner.exception.BusinessException;
import org.logscanner.service.FileSystemService.ReaderType;

/**
 * @author Victor Kadachigov
 */
public interface FileSystemService 
{
	public enum ReaderType
	{
		IN_MEMORY,
		URI,
		AUTO
	}
	
	public ContentReader readContent(FileInfo file, ReaderType reader) throws IOException, BusinessException;
	public InputStream getInputStream(FileInfo file) throws IOException, BusinessException;
	public String getRelativePath(FileInfo file, String basePath);
	public List<FileInfo> listFiles(Location location, FilterParams filterParams) throws IOException;
}
