package org.logscanner.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.cache.Cache;
import org.logscanner.cache.CacheFileInfo;
import org.logscanner.jobs.LocationsReader;
import org.logscanner.util.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jgoodies.common.base.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
@Service
public class CacheManager 
{
	@Autowired
	private AppProperties props;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private JobResultModel jobResultModel;
	
	private Map<String, Cache> caches = new ConcurrentHashMap<>();
	
	@PostConstruct
	public void init()
	{
		jobResultModel.addPropertyChangeListener("jobState", (event) -> {
			if (event.getNewValue() == JobResultModel.JobState.STOPPED)
				caches.forEach((locationCode, cache) -> saveCache(locationCode, cache));
		});
	}
	
	/**
	 * @param locationCode
	 * @param path
	 * @param actualToDate <= getCacheUpdateTime
	 * @return
	 */
	public CacheFileInfo getFileInfo(String locationCode, String path, Date actualToDate)
	{
		Cache cache = findCache(locationCode);
		return getFileInfo(cache, locationCode, path, actualToDate);
	}

	private CacheFileInfo getFileInfo(Cache cache, String locationCode, String path, Date actualToDate)
	{
		Optional<CacheFileInfo> result = Optional.empty();
		Optional.empty();  
		if (cache != null)
		{
			result = cache.getFiles().stream()
								.filter(fi -> {
//											if (StringUtils.contains(fi.getPath(), "cod99_calc.2018-08-10_0.log"))
//												log.info("ZZZ {}", fi.getPath());
											return StringUtils.equals(fi.getPath(), path)
														&& (actualToDate == null || actualToDate.before(fi.getCacheUpdateTime()));
									}
								)
								.findAny();
		}
		return result.isPresent() ? result.get() : null;
	}

	public CacheFileInfo updateFromAttributes(String locationCode, String path, BasicFileAttributes attr)
	{
		Cache cache = findCache(locationCode);
		CacheFileInfo fileInfo = getFileInfo(cache, locationCode, path, null);
		if (fileInfo == null) 
		{ 
			fileInfo = new CacheFileInfo(path);
			cache.addFile(fileInfo);
		}
		FileTime lastModifiedTime = attr.lastModifiedTime();
		if (lastModifiedTime != null)
		{
			Date newLastModifiedTime = new Date(lastModifiedTime.toMillis());
			if (!Objects.equals(newLastModifiedTime, fileInfo.getLastModified()))
			{
				fileInfo.setLastModified(newLastModifiedTime);
				fileInfo.setContentEnd(null);
				cache.changed(fileInfo);
			}
		}
		if (attr.size() >= 0)
		{
			fileInfo.setSize(attr.size());
			cache.changed(fileInfo);
		}
		return fileInfo;
	}
	
	public CacheFileInfo updateFromContent(String locationCode, String path, Date contentStart, Date contentEnd)
	{
		Cache cache = findCache(locationCode);
		CacheFileInfo fileInfo = getFileInfo(locationCode, path, null);
		if (fileInfo == null) 
		{
			fileInfo = new CacheFileInfo(path);
			cache.addFile(fileInfo);
		}
		if (contentStart != null && !Objects.equals(contentStart, fileInfo.getContentStart()))
		{
			fileInfo.setContentStart(contentStart);
			cache.changed(fileInfo);
		}
		if (contentEnd != null && !Objects.equals(contentEnd, fileInfo.getContentEnd()))
		{
			fileInfo.setContentEnd(contentEnd);
			cache.changed(fileInfo);
		}
		return fileInfo;
	}
	
	private Cache findCache(String locationCode)
	{
		Cache result = caches.get(locationCode);
		if (result == null)
		{
			result = loadCache(locationCode);
			if (result == null)
				result = new Cache();
			caches.put(locationCode, result);
		}
		return result;
	}

	private Cache loadCache(String locationCode) 
	{
		Path path = getPathForLocation(locationCode);
		Cache result = null;
		try
		{
			if (Files.exists(path))
			{
				ObjectReader reader = mapper.reader();
				result = reader.readValue(
											reader.getFactory().createParser(path.toFile()), 
											Cache.class
									);
				
			}
		}
		catch (IOException ex)
		{
			log.error("", ex);
			try
			{
				Files.delete(path);
			}
			catch (IOException ex1)
			{
				log.error(ex1.getMessage());
			}
		}
		return result;
	}
	
	private void saveCache(String locationCode, Cache cache)
	{
		if (!cache.isChanged())
			return;
		
		try
		{
			Path path = getPathForLocation(locationCode);
			Files.createDirectories(path.getParent());
			
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(path.toFile(), cache);
		}
		catch (IOException ex)
		{
			log.error("", ex);
		}
	}
	
	private Path getPathForLocation(String locationCode)
	{
		return Paths.get(props.getDataDir(), "data", "files", locationCode, "dir.json");
	}
}
