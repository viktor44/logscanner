package org.logscanner.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.logscanner.Resources;
import org.logscanner.cache.CacheFileInfo;
import org.logscanner.data.ByteContentReader;
import org.logscanner.data.ContentReader;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.LocalFileInfo;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.logscanner.data.UriContentReader;
import org.logscanner.exception.BusinessException;
import org.logscanner.exception.FileTooBigException;
import org.logscanner.util.fs.LocalDirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author Victor Kadachigov
 */
public abstract class BaseFileService implements FileSystemService
{
	private static final Logger log = LoggerFactory.getLogger(BaseFileService.class);
	private static long MAX_FILE_SIZE = 101; //Mb
	
	@Autowired
	protected CacheManager cacheManager;
	@Autowired
	private MessageSourceAccessor messageAccessor;
	
	@Override
	public ContentReader readContent(FileInfo file, ReaderType reader) throws IOException, BusinessException
	{
		Path path = file.getFile();
		long size = Files.size(path);
		ContentReader result = null;
		switch (reader)
		{
			case IN_MEMORY:
				if (size > MAX_FILE_SIZE * 1024 * 1024) 
					throw new FileTooBigException(
									messageAccessor.getMessage(
											"error.file_too_big", 
											new String[] { path.toString(), String.valueOf(size / (1024 * 1024)), String.valueOf(MAX_FILE_SIZE) }
									)
							);
				result = new ByteContentReader(Files.readAllBytes(path));
				break;
			case URI:
				result = new UriContentReader(path.toUri(), size);
				break;
			case AUTO:
				throw new NotImplementedException(Resources.getStr("error.not_implemented"));
		}
		
		return result;
	}
	
	@Override
	public InputStream getInputStream(FileInfo file) throws IOException, BusinessException
	{
		return readContent(file, ReaderType.URI).getInputStream();
	}
	
	protected abstract boolean isSupported(Location location);
	protected abstract LocalDirectoryScanner createDirectoryScanner(Location location);
	protected abstract List<FileInfo> processScannerResults(LocalDirectoryScanner dirScanner, Location location);

	@Override
	public List<FileInfo> listFiles(Location location, FilterParams filterParams) throws IOException
	{
		if (!isSupported(location))
			throw new IllegalArgumentException(Resources.getStr("error.unsupported_location_type", location.getType()));
		
		LocalDirectoryScanner dirScanner = createDirectoryScanner(location);
		dirScanner.setBasedir(location.getPath());
		dirScanner.setIncludes(filterParams.getIncludes());

		List<FileSelector> selectors = new ArrayList<>();
		if (filterParams.getDateFrom() != null || filterParams.getDateTo() != null)
			selectors.add(new ModifiedInPeriodSelector(location.getCode(), filterParams.getDateFrom(), filterParams.getDateTo()));

		if (!selectors.isEmpty())
			dirScanner.setSelectors(selectors.toArray(new FileSelector[selectors.size()]));
		dirScanner.scan();
		List<FileInfo> list = processScannerResults(dirScanner, location); 
		return list;
	}
	
	public class ModifiedInPeriodSelector extends BaseSelector
	{
		private final String locationCode;
		private final Date from;
		private final Date to;
		
		public ModifiedInPeriodSelector(String locationCode, Date from, Date to)
		{
			this.locationCode = locationCode;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean isSelected(File basedir, String filename, File file)
		{
			Date contentStart = null;
			Date lastModifiedTime = null;
			Path path = file.toPath();
			CacheFileInfo cacheFileInfo = cacheManager.getFileInfo(locationCode, path.toString());
			if (cacheFileInfo == null)
			{
				try
				{
					BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
					cacheManager.updateFromAttributes(locationCode, path.toString(), attr);
					lastModifiedTime = new Date(attr.lastModifiedTime().toMillis());
				}
				catch (IOException ex)
				{
					log.error(ex.getMessage());
					return false;
				}
			}
			else
			{
				lastModifiedTime = cacheFileInfo.getLastModified();
				contentStart = cacheFileInfo.getContentStart();
			}
			
			boolean result = lastModifiedTime.compareTo(from) >= 0 
									&& (contentStart == null || contentStart.compareTo(to) <= 0);
			
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"); 
//			log.info("isSelected({}) \nfrom: {}, \nto: {}, \ncreated: {}, \nmodified: {}, \nresult: {}", filename, df.format(from), df.format(to), df.format(creationTime.toMillis()), df.format(lastModifiedTime.toMillis()), result);
			
			return result;
		}
	}

	protected FileInfo fillAttributes(LocalFileInfo fileInfo)
	{
//		CacheFileInfo attr = cacheManager.getFileInfo(fileInfo.getLocationCode(), fileInfo.getFilePath());
//		fileInfo.setLastModified(attr.getLastModified());
//		fileInfo.setCreated(attr.getCreated());
		return fileInfo;
	}
}
