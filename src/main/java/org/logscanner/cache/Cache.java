package org.logscanner.cache;

import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Victor Kadachigov
 */
public class Cache 
{
	@JsonIgnore
	private boolean changed;
	private Set<CacheFileInfo> files = new TreeSet<>();

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
	public void changed()
	{
		changed = true;
	}
}
