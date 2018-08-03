package org.logscanner.util.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Victor Kadachigov
 */
public class ModifiedInPeriodSelector extends BaseSelector
{
	private static final Logger log = LoggerFactory.getLogger(ModifiedInPeriodSelector.class);

	private final Date from;
	private final Date to;
	
	public ModifiedInPeriodSelector(Date from, Date to)
	{
		super();
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean isSelected(File basedir, String filename, File file)
	{
		FileTime creationTime = null;
		FileTime lastModifiedTime = null;
		Path path = file.toPath();
		BasicFileAttributes attr = null;
		try
		{
			attr = readAttributes(path);
		}
		catch (IOException ex)
		{
			log.error(ex.getMessage());
			return false;
		}
		creationTime = attr.creationTime();
		lastModifiedTime = attr.lastModifiedTime();
		boolean wrongCreationTime = creationTime == null || creationTime.compareTo(lastModifiedTime) == 0;
		
		boolean result = lastModifiedTime.toInstant().compareTo(from.toInstant()) >= 0 
								&& (wrongCreationTime || creationTime.toInstant().compareTo(to.toInstant()) <= 0);
		
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"); 
//		log.info("isSelected({}) \nfrom: {}, \nto: {}, \ncreated: {}, \nmodified: {}, \nresult: {}", filename, df.format(from), df.format(to), df.format(creationTime.toMillis()), df.format(lastModifiedTime.toMillis()), result);
		
		return result;
	}
	
	protected BasicFileAttributes readAttributes(Path path) throws IOException
	{
		return Files.readAttributes(path, BasicFileAttributes.class);
	}
	
}

