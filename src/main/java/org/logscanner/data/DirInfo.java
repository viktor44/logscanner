package org.logscanner.data;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Victor Kadachigov
 */
@Getter
@Setter
public class DirInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/** Location Code (unique) */
	private String locationCode;
	private String host;
	private String rootPath;
	private List<FileInfo> files;
}
