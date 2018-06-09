package org.logscanner.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.LocalFileInfo;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.logscanner.util.fs.LocalDirectoryScanner;
import org.springframework.stereotype.Service;

/**
 * @author Victor Kadachigov
 */
@Service
public class LocalFileService implements FileSystemService
{

	@Override
	public byte[] readContent(FileInfo file) throws IOException
	{
		return Files.readAllBytes(file.getFile());
	}

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
	public List<FileInfo> listFiles(Location location, FilterParams filterParams)
	{
		if (location.getType() != LocationType.LOCAL)
			throw new IllegalArgumentException("Unsupported location type " + location.getType() + ". Expected " + LocationType.LOCAL);
		
		LocalDirectoryScanner dirScanner = new LocalDirectoryScanner();
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
		List<FileInfo> list = new ArrayList<>();
		String includedFiles[] = dirScanner.getIncludedFiles();
		Arrays.stream(includedFiles)
				.forEach(path -> list.add(new LocalFileInfo(Paths.get(location.getPath(), path))));
		return list;
	}
	
//	@Override
//	public List<FileInfo> listFiles(Location location, FilterParams filterParams)
//	{
//		LocalFileInfo root = new LocalFileInfo(new File(location.getPath())); 
//		List<FileInfo> list = listFileTree(root, createFilter(filterParams));
//		return list;
//	}
//	
//	private List<FileInfo> listFileTree(LocalFileInfo dir, FileFilter filter)
//	{
////		log.info("==> listFileTree({})", dir.getAbsoluteFile());
//		
//	    if (dir == null)
//	        return Collections.emptyList();
//	    
//	    File[] dirFiles = dir.getFile().listFiles(filter);
//	    if (dirFiles == null)
//	    	return Collections.emptyList();
//	    
//	    List<FileInfo> fileTree = new ArrayList<FileInfo>();
//	    for (File entry : dirFiles) 
//	    {
//	        if (entry.isFile()) 
//	        	fileTree.add(new LocalFileInfo(entry));
//	        else if (!Files.isSymbolicLink(entry.toPath()))
//	        {
//	        	List<FileInfo> list = listFileTree(new LocalFileInfo(entry), filter);
//	        	if (!list.isEmpty())
//	        		fileTree.addAll(list);
//	        }
//	    }
//	    return fileTree;
//	}
//	
//
//	private FileFilter createFilter(FilterParams filterParams) 
//	{
//		IOFileFilter wildcardFilter = (filterParams.getIncludes() != null && filterParams.getIncludes().length > 0)
//											? new WildcardFileFilter(filterParams.getIncludes(), IOCase.INSENSITIVE)
//											: TrueFileFilter.TRUE;
//		Date from = filterParams.getDateFrom();
//		Date to = filterParams.getDateTo();
//		IOFileFilter dateFilter = TrueFileFilter.TRUE;
//		if (from != null && to != null)
//			dateFilter = FileFilterUtils.and(
//								FileFilterUtils.ageFileFilter(from, false),
//								FileFilterUtils.ageFileFilter(to, true)
//						);
//		else if (from != null)
//			dateFilter = FileFilterUtils.ageFileFilter(from, false);
//		else if (to != null)
//			dateFilter = FileFilterUtils.ageFileFilter(to, true);
//		return  FileFilterUtils.or(
//						DirectoryFileFilter.DIRECTORY,
//						FileFilterUtils.and(dateFilter, wildcardFilter)
//				);
//	}
	
}
