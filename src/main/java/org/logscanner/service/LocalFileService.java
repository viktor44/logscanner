package org.logscanner.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.data.FileInfo;
import org.logscanner.data.LocalFileInfo;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.logscanner.util.fs.LocalDirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Victor Kadachigov
 */
@Service
public class LocalFileService extends BaseFileService
{
	private static final Logger log = LoggerFactory.getLogger(LocalFileService.class);
	
	@Override
	public String getRelativePath(FileInfo file, String basePath)
	{
		Path commonPath = Paths.get(basePath);
		Path path = Paths.get(file.getFilePath());
		if (StringUtils.isNotBlank(basePath))
		{
			path = commonPath.relativize(path);
		}
		String result = path.toString();
		
		if (result.startsWith("\\\\"))
			result = result.substring(2);
		else 
		{
			int index = result.indexOf(':');
			if (index > 0)
				result = result.substring(index + 1);
		}
		return result;
	}
	
	@Override
	protected LocalDirectoryScanner createDirectoryScanner(Location location)
	{
		return new LocalDirectoryScanner();
	}
	
	@Override
	protected boolean isSupported(Location location)
	{
		return location.getType() == LocationType.LOCAL;
	}
	
	@Override
	protected List<FileInfo> processScannerResults(LocalDirectoryScanner dirScanner, Location location)
	{
		List<FileInfo> list = new ArrayList<>();
		String includedFiles[] = dirScanner.getIncludedFiles();
		Arrays.stream(includedFiles)
				.forEach(path -> {
						LocalFileInfo fileInfo = new LocalFileInfo(location.getCode(), Paths.get(location.getPath(), path));
						list.add(fillAttributes(fileInfo));
				});
		return list;
	}
}
