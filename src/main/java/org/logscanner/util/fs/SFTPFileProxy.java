package org.logscanner.util.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Victor Kadachigov
 */
public class SFTPFileProxy extends File
{
	private final Path path;
	
	public SFTPFileProxy(Path path) {
		super("");
		this.path = path;
	}

	public String getName() {
		return path.getFileName().toString();
	}

	public String getParent() {
		return path.getParent().toString();
	}

	public File getParentFile() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getPath() {
		return path.toString();
	}

	public boolean isAbsolute() {
		return path.isAbsolute();
	}

	public String getAbsolutePath() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public File getAbsoluteFile() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getCanonicalPath() throws IOException {
		return path.normalize().toRealPath().toString();
	}

	public File getCanonicalFile() throws IOException {
		throw new UnsupportedOperationException("Not implemented");
	}

	public URL toURL() throws MalformedURLException {
		return path.toUri().toURL();
	}

	public URI toURI() {
		return path.toUri();
	}

	public boolean canRead() {
		return Files.isReadable(path);
	}

	public boolean canWrite() {
		return Files.isWritable(path);
	}

	public boolean exists() {
		return Files.exists(path);
	}

	public boolean isDirectory() {
		return Files.isDirectory(path);
	}

	public boolean isFile() {
		return Files.isRegularFile(path);
	}

	public boolean isHidden() {
		try {
			return Files.isHidden(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public long lastModified() {
		try {
			return Files.getLastModifiedTime(path).toMillis();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public long length() {
		try {
			return Files.size(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean createNewFile() throws IOException {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean delete() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void deleteOnExit() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String[] list() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String[] list(FilenameFilter filter) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public File[] listFiles() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public File[] listFiles(FilenameFilter filter) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public File[] listFiles(FileFilter filter) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean mkdir() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean mkdirs() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean renameTo(File dest) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setLastModified(long time) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setReadOnly() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setWritable(boolean writable, boolean ownerOnly) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setWritable(boolean writable) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setReadable(boolean readable, boolean ownerOnly) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setReadable(boolean readable) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean setExecutable(boolean executable) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public boolean canExecute() {
		return Files.isExecutable(path);
	}

	public long getTotalSpace() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public long getFreeSpace() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public long getUsableSpace() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public int compareTo(File pathname) {
		return path.compareTo(pathname.toPath());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	public String toString() {
		return path.toString();
	}

	public Path toPath() {
		return path;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SFTPFileProxy other = (SFTPFileProxy) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
