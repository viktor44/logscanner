package org.logscanner.cache;

import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.logscanner.data.LogPattern;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Victor Kadachigov
 */
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters=true, onlyExplicitlyIncluded=true)
@ToString(doNotUseGetters=true)
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class CacheFileInfo implements Comparable<CacheFileInfo>
{
	@EqualsAndHashCode.Include
	@Setter(value=AccessLevel.PACKAGE)
	private String path;
	private Date cacheUpdateTime;
	private Date lastModified;
	private Date contentStart;
	private Date contentEnd;
	private long size;

	CacheFileInfo()
	{
		this.cacheUpdateTime = DateUtils.addYears(new Date(), -100);
	}
	public CacheFileInfo(String path)
	{
		this.path = Objects.requireNonNull(path);
		this.cacheUpdateTime = new Date();
	}

	public FileTime getLastModifiedAsFileTime() {
		return lastModified != null ? FileTime.fromMillis(lastModified.getTime()) : null;
	}

	@Override
	public int compareTo(CacheFileInfo o)
	{
		return path.compareTo(o.getPath());
	}

}
