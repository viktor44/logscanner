package org.logscanner.cache;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
public class Cache 
{
	@Getter
	@Setter(value=AccessLevel.NONE)
	@JsonIgnore
	private boolean changed;
	@Getter
	@Setter(value=AccessLevel.PACKAGE)
	private Set<CacheFileInfo> files = new TreeSet<>();

	public void addFile(CacheFileInfo file) 
	{
		files.add(file);
		changed(file);
	}

	public void changed(CacheFileInfo fileInfo)
	{
		fileInfo.setCacheUpdateTime(new Date());
		changed = true;
	}
}
