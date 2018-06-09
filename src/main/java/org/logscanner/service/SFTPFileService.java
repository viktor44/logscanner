package org.logscanner.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.LocalFileInfo;
import org.logscanner.data.Location;
import org.logscanner.data.SFTPFileInfo;
import org.logscanner.data.LocationType;
import org.logscanner.util.fs.LocalDirectoryScanner;
import org.logscanner.util.fs.SFTPDirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Victor Kadachigov
 */
@Service
public class SFTPFileService implements FileSystemService
{
	private static final Logger log = LoggerFactory.getLogger(SFTPFileService.class);
	private static final int DEFAULT_PORT = 22;
	
	@Override
	public byte[] readContent(FileInfo file) throws IOException
	{
		return Files.readAllBytes(file.getFile());
	}

	@Override
	public String getRelativePath(FileInfo file, String basePath)
	{
		String path = file.getFile().toString();
		if (path.startsWith(basePath))
		{
			int index = basePath.length();
			if (!basePath.endsWith("/")) index++;
			path = path.substring(index);
		}
		return path;
	}

	@Override
	public List<FileInfo> listFiles(Location location, FilterParams filterParams) throws IOException
	{
		if (location.getType() != LocationType.SFTP)
			throw new IllegalArgumentException("Unsupported location type " + location.getType() + ". Expected " + LocationType.SFTP);
		
		SFTPDirectoryScanner dirScanner = new SFTPDirectoryScanner();
		dirScanner.setHost(location.getHost());
		dirScanner.setPort(location.getPort() != null ? location.getPort() : DEFAULT_PORT);
		dirScanner.setUsername(location.getUser());
		dirScanner.setPassword(location.getPassword());
		dirScanner.setBasedir(location.getPath());
		dirScanner.setIncludes(filterParams.getIncludes());
		List<FileSelector> selectors = new ArrayList<>();
		if (filterParams.getDateFrom() != null)
		{
			DateSelector fromSelector = new DateSelector();
			fromSelector.setCheckdirs(false);
			fromSelector.setWhen(TimeComparison.AFTER);
			fromSelector.setMillis(filterParams.getDateFrom().getTime());
			selectors.add(fromSelector);
		}
		if (filterParams.getDateTo() != null)
		{
			DateSelector toSelector = new DateSelector();
			toSelector.setCheckdirs(false);
			toSelector.setWhen(TimeComparison.BEFORE);
			toSelector.setMillis(filterParams.getDateTo().getTime());
			selectors.add(toSelector);
		}
		if (!selectors.isEmpty())
			dirScanner.setSelectors(selectors.toArray(new FileSelector[selectors.size()]));
		dirScanner.scan();
		Path basedit = dirScanner.getBasedir();
		List<FileInfo> list = new ArrayList<>();
		String includedFiles[] = dirScanner.getIncludedFiles();
		Arrays.stream(includedFiles)
				.forEach(path -> list.add(new SFTPFileInfo(basedit.resolve(path))));
		return list;
	}
}
