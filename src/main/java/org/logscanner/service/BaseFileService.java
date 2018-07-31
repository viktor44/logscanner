package org.logscanner.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.types.selectors.FileSelector;
import org.logscanner.data.ByteContentReader;
import org.logscanner.data.ContentReader;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.logscanner.data.UriContentReader;
import org.logscanner.exception.BusinessException;
import org.logscanner.exception.FileTooBigException;
import org.logscanner.util.fs.LocalDirectoryScanner;
import org.logscanner.util.fs.ModifiedInPeriodSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseFileService implements FileSystemService
{
	private static final Logger log = LoggerFactory.getLogger(BaseFileService.class);
	private static long MAX_FILE_SIZE = 101; //Mb
	
	@Autowired
	private CacheManager cacheManager;
	
	@Override
	public ContentReader readContent(FileInfo file) throws IOException, BusinessException
	{
		Path path = file.getFile();
		long size = Files.size(path);
//		if (size > MAX_FILE_SIZE * 1024 * 1024) 
//			throw new FileTooBigException(
//							"Размер файла " 
//							+ path.toString() + " " + (size / (1024 * 1024))
//							+ "Mb превышает максимальное значение " + MAX_FILE_SIZE + "Mb");
		ContentReader result;
//		result = new ByteContentReader(Files.readAllBytes(path));
		result = new UriContentReader(path.toUri(), size);
		return result;
	}
	
	@Override
	public InputStream getInputStream(FileInfo file) throws IOException, BusinessException
	{
		Path path = file.getFile();
		long size = Files.size(path);
		if (size > MAX_FILE_SIZE * 1024 * 1024) 
			throw new FileTooBigException(
							"Размер файла " 
							+ path.toString() + " " + (size / (1024 * 1024))
							+ "Mb превышает максимальное значение " + MAX_FILE_SIZE + "Mb");
		return new BufferedInputStream(Files.newInputStream(path), (int)size);
	}
	
	protected abstract boolean isSupported(Location location);
	protected abstract LocalDirectoryScanner createDirectoryScanner(Location location);
	protected abstract List<FileInfo> processScannerResults(LocalDirectoryScanner dirScanner, Location location);

	@Override
	public List<FileInfo> listFiles(Location location, FilterParams filterParams) throws IOException
	{
		if (!isSupported(location))
			throw new IllegalArgumentException("Unsupported location type " + location.getType() + ". Expected " + LocationType.LOCAL);
		
		LocalDirectoryScanner dirScanner = createDirectoryScanner(location);
		dirScanner.setBasedir(location.getPath());
		dirScanner.setIncludes(filterParams.getIncludes());

		List<FileSelector> selectors = new ArrayList<>();
		if (filterParams.getDateFrom() != null || filterParams.getDateTo() != null)
			selectors.add(new ModifiedInPeriodSelector0(location.getCode(), filterParams.getDateFrom(), filterParams.getDateTo()));

		if (!selectors.isEmpty())
			dirScanner.setSelectors(selectors.toArray(new FileSelector[selectors.size()]));
		dirScanner.scan();
		List<FileInfo> list = processScannerResults(dirScanner, location); 
		return list;
	}
	
	private class ModifiedInPeriodSelector0 extends ModifiedInPeriodSelector
	{
		private final String locationCode;
		
		public ModifiedInPeriodSelector0(String locationCode, Date from, Date to) 
		{
			super(from, to);
			this.locationCode = locationCode;
		}
		
		@Override
		protected BasicFileAttributes readAttributes(Path path) throws IOException 
		{
			BasicFileAttributes result = cacheManager.readAttributes(locationCode, path.toString());
			if (result == null)
			{
				result = super.readAttributes(path);
				cacheManager.updateAttributes(locationCode, path.toString(), result);
			}
			return result;
		}
	}

}