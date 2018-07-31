package org.logscanner.cache;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cache 
{
	@JsonIgnore
	private boolean changed;
	private Set<CacheFileInfo> files = new HashSet<>();

	public Set<CacheFileInfo> getFiles() 
	{
		return files;
	}
	void setFiles(Set<CacheFileInfo> files) 
	{
		this.files = files;
	}
	
	public void addFile(CacheFileInfo file) 
	{
		files.add(file);
		changed = true;
	}
	
	public boolean isChanged()
	{
		return changed;
	}
}
