package org.logscanner.cache;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.apache.commons.lang3.NotImplementedException;
import org.logscanner.Resources;

/**
 * @author Victor Kadachigov
 */
public class BasicFileAttributesImpl implements BasicFileAttributes
{
	private final FileTime lastModifiedTime;
	private final long size;
	
	public BasicFileAttributesImpl(CacheFileInfo fileInfo) {
		lastModifiedTime = fileInfo.getLastModifiedAsFileTime();
		size = fileInfo.getSize();
	}
	public BasicFileAttributesImpl(FileTime lastModifiedTime, FileTime creationTime, long size) {
		this.lastModifiedTime = lastModifiedTime;
		this.size = size;
	}
	
	@Override
	public FileTime lastModifiedTime() {
		return lastModifiedTime;
	}
	@Override
	public long size() {
		return size;
	}
	@Override
	public boolean isRegularFile() {
		return true;
	}
	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public FileTime creationTime() {
		throw new NotImplementedException(Resources.getStr("error.not_implemented"));
	}
	@Override
	public FileTime lastAccessTime() {
		throw new NotImplementedException(Resources.getStr("error.not_implemented"));
	}
	@Override
	public boolean isSymbolicLink() {
		throw new NotImplementedException(Resources.getStr("error.not_implemented"));
	}
	@Override
	public boolean isOther() {
		throw new NotImplementedException(Resources.getStr("error.not_implemented"));
	}
	@Override
	public Object fileKey() {
		throw new NotImplementedException(Resources.getStr("error.not_implemented"));
	}
}
