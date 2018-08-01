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
import org.logscanner.util.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class CacheManager 
{
	private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

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
	
	public BasicFileAttributes readAttributes(String locationCode, String path) throws IOException
	{
		Optional<CacheFileInfo> fileInfo = findFileInfo(locationCode, path);
		return fileInfo.isPresent() ? new BasicFileAttributesImpl(fileInfo.get()) : null;
	}

	public void updateAttributes(String locationCode, String path, BasicFileAttributes attr) throws IOException
	{
		Optional<CacheFileInfo> opt = findFileInfo(locationCode, path);
		CacheFileInfo fileInfo = opt.isPresent() ? opt.get() : new CacheFileInfo(path);
		FileTime creationTime = attr.creationTime();
		FileTime lastModifiedTime = attr.lastModifiedTime();
		boolean wrongCreationTime = creationTime == null || (lastModifiedTime != null && creationTime.compareTo(lastModifiedTime) == 0);
		if (!wrongCreationTime)
			fileInfo.setCreated(new Date(creationTime.toMillis()));
		if (lastModifiedTime != null)
			fileInfo.setLastModified(new Date(lastModifiedTime.toMillis()));
		if (attr.size() >= 0)
			fileInfo.setSize(attr.size());

		Cache cache = findCache(locationCode);
		cache.addFile(fileInfo);
	}
	
	private Optional<CacheFileInfo> findFileInfo(String locationCode, String path) 
	{
		Optional<CacheFileInfo> result = Optional.empty();  
		Cache cache = findCache(locationCode);
		if (cache != null)
			result = cache.getFiles().stream()
						.filter(fi -> StringUtils.equals(fi.getPath(), path))
						.findAny();
		return result;
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
			throw new RuntimeException(ex);
		}
	}
	
	private Path getPathForLocation(String locationCode)
	{
		return Paths.get(props.getDataDir(), "files", locationCode, "dir.json");
	}
	
	public static class BasicFileAttributesImpl implements BasicFileAttributes
	{
		private final FileTime lastModifiedTime;
		private final FileTime creationTime;
		private final long size;
		
		public BasicFileAttributesImpl(CacheFileInfo fileInfo) {
			lastModifiedTime = fileInfo.getLastModifiedAsFileTime();
			creationTime = fileInfo.getCreatedAsFileTime();
			size = fileInfo.getSize();
		}
		public BasicFileAttributesImpl(FileTime lastModifiedTime, FileTime creationTime, long size) {
			this.lastModifiedTime = lastModifiedTime;
			this.creationTime = creationTime;
			this.size = size;
		}
		
		@Override
		public FileTime lastModifiedTime() {
			return lastModifiedTime;
		}
		@Override
		public FileTime creationTime() {
			return creationTime;
		}
		@Override
		public long size() {
			return size;
		}
		@Override
		public boolean isRegularFile() {
			return true;
		}
		@Override
		public boolean isDirectory() {
			return false;
		}

		@Override
		public FileTime lastAccessTime() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public boolean isSymbolicLink() {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override
		public boolean isOther() {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override
		public Object fileKey() {
			throw new UnsupportedOperationException("Not implemented");
		}
	}
}
