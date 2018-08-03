package org.logscanner.cache;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @author Victor Kadachigov
 */
public class BasicFileAttributesImpl implements BasicFileAttributes
{
	private final FileTime lastModifiedTime;
	private final FileTime creationTime;
	private final long size;
	
	public BasicFileAttributesImpl(CacheFileInfo fileInfo) {
		lastModifiedTime = fileInfo.getLastModifiedAsFileTime();
		creationTime = fileInfo.getCreatedAsFileTime();
		size = fileInfo.getSize();
	}
	public BasicFileAttributesImpl(FileTime lastModifiedTime, FileTime creationTime, long size) {
		this.lastModifiedTime = lastModifiedTime;
		this.creationTime = creationTime;
		this.size = size;
	}
	
	@Override
	public FileTime lastModifiedTime() {
		return lastModifiedTime;
	}
	@Override
	public FileTime creationTime() {
		return creationTime;
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
	public FileTime lastAccessTime() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean isSymbolicLink() {
		throw new UnsupportedOperationException("Not implemented");
	}
	@Override
	public boolean isOther() {
		throw new UnsupportedOperationException("Not implemented");
	}
	@Override
	public Object fileKey() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
