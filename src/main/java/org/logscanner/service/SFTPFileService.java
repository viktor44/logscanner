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
import org.logscanner.util.fs.ModifiedInPeriodSelector;
import org.logscanner.util.fs.SFTPDirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Victor Kadachigov
 */
@Service
public class SFTPFileService extends BaseFileService
{
	private static final Logger log = LoggerFactory.getLogger(SFTPFileService.class);
	private static final int DEFAULT_PORT = 22;
	
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
	protected boolean isSupported(Location location)
	{
		return location.getType() == LocationType.SFTP;
	}
	
	@Override
	protected LocalDirectoryScanner createDirectoryScanner(Location location)
	{
		SFTPDirectoryScanner dirScanner = new SFTPDirectoryScanner();
		dirScanner.setHost(location.getHost());
		dirScanner.setPort(location.getPort() != null ? location.getPort() : DEFAULT_PORT);
		dirScanner.setUsername(location.getUser());
		dirScanner.setPassword(location.getPassword());
		return dirScanner;
	}
	
	@Override
	protected List<FileInfo> processScannerResults(LocalDirectoryScanner dirScanner, Location location)
	{
		Path basedir = dirScanner.getBasedir();
		List<FileInfo> list = new ArrayList<>();
		String includedFiles[] = dirScanner.getIncludedFiles();
		Arrays.stream(includedFiles)
				.forEach(path -> list.add(new SFTPFileInfo(location.getCode(), location.getHost(), basedir.resolve(path))));
		return list;
	}
}
