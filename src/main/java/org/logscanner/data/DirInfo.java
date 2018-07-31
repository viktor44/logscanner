package org.logscanner.data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Victor Kadachigov
 */
public class DirInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/** Location Code (unique) */
	private String locationCode;
	private String host;
	private String rootPath;
	private List<FileInfo> files;

	public List<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}
	public String getRootPath()	{
		return rootPath;
	}
	public void setRootPath(String rootDir)	{
		this.rootPath = rootDir;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	
}
