package org.logscanner.cache;

import java.nio.file.attribute.FileTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class CacheFileInfo 
{
	private String path;
	private Date lastModified;
	private Date created;
	private long size;

	CacheFileInfo()
	{
	}

	public CacheFileInfo(String path)
	{
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	void setPath(String path) {
		this.path = path;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModifiedTime) {
		this.lastModified = lastModifiedTime;
	}
	public FileTime getLastModifiedAsFileTime() {
		return lastModified != null ? FileTime.fromMillis(lastModified.getTime()) : null;
	}
	public Date getCreated() {
		return created;
	}
	public FileTime getCreatedAsFileTime() {
		return created != null ? FileTime.fromMillis(created.getTime()) : null;
	}
	public void setCreated(Date creationTime) {
		this.created = creationTime;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheFileInfo other = (CacheFileInfo) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}