package org.logscanner.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.types.selectors.FileSelector;
import org.logscanner.data.ByteContentReader;
import org.logscanner.data.ContentReader;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.LocalFileInfo;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.logscanner.exception.BusinessException;
import org.logscanner.exception.FileTooBigException;
import org.logscanner.util.fs.LocalDirectoryScanner;
import org.logscanner.util.fs.ModifiedInPeriodSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFileService implements FileSystemService
{
	private static final Logger log = LoggerFactory.getLogger(BaseFileService.class);
	private static long MAX_FILE_SIZE = 101; //Mb
	
	@Override
	public ContentReader readContent(FileInfo file) throws IOException, BusinessException
	{
		Path path = file.getFile();
		long size = Files.size(path);
		if (size > MAX_FILE_SIZE * 1024 * 1024) 
			throw new FileTooBigException(
							"Размер файла " 
							+ path.toString() + " " + (size / (1024 * 1024))
							+ "Mb превышает максимальное значение " + MAX_FILE_SIZE + "Mb");
		ContentReader result;
		result = new ByteContentReader(Files.readAllBytes(file.getFile()));
		return result;
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
			selectors.add(new ModifiedInPeriodSelector(filterParams.getDateFrom(), filterParams.getDateTo()));

		if (!selectors.isEmpty())
			dirScanner.setSelectors(selectors.toArray(new FileSelector[selectors.size()]));
		dirScanner.scan();
		List<FileInfo> list = processScannerResults(dirScanner, location); 
		return list;
	}

}
