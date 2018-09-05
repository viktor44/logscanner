package org.logscanner.service;

import org.logscanner.Resources;
import org.logscanner.data.Location;
import org.logscanner.data.LocationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Victor Kadachigov
 */
@Service
public class FileServiceSelector 
{
	@Autowired
	private LocalFileService local;
	@Autowired
	private SFTPFileService sftp;
	
	public FileSystemService select(LocationType locationType)
	{
		FileSystemService result = null;
		
		switch (locationType)
		{
			case LOCAL:
				result = local;
				break;
			case SFTP:
				result = sftp;
				break;
			default:
				throw new UnsupportedOperationException(Resources.getStr("error.unsupported_location_type", locationType));
		}
		
		return result;
	}
}
