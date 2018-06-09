package org.logscanner.data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Victor Kadachigov
 */
public class DirInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String location;
	private String rootPath;
	private List<FileInfo> files;

	public List<FileInfo> getFiles() 
	{
		return files;
	}
	public void setFiles(List<FileInfo> files) 
	{
		this.files = files;
	}
	public String getRootPath() 
	{
		return rootPath;
	}
	public void setRootPath(String rootDir) 
	{
		this.rootPath = rootDir;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
