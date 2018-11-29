package org.logscanner.data;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Victor Kadachigov
 */
@Getter
@Setter
public class FileData 
{
	private String locationCode;
	private String filePath;
	private String zipPath;
	private ContentReader contentReader;
}
