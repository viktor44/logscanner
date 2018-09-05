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

import org.logscanner.Resources;

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

	@Override
	public String getName() {
		return path.getFileName().toString();
	}

	@Override
	public String getParent() {
		return path.getParent().toString();
	}

	@Override
	public File getParentFile() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public String getPath() {
		return path.toString();
	}

	@Override
	public boolean isAbsolute() {
		return path.isAbsolute();
	}

	@Override
	public String getAbsolutePath() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public File getAbsoluteFile() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public String getCanonicalPath() throws IOException {
		return path.normalize().toRealPath().toString();
	}

	@Override
	public File getCanonicalFile() throws IOException {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public URL toURL() throws MalformedURLException {
		return path.toUri().toURL();
	}

	@Override
	public URI toURI() {
		return path.toUri();
	}

	@Override
	public boolean canRead() {
		return Files.isReadable(path);
	}

	@Override
	public boolean canWrite() {
		return Files.isWritable(path);
	}

	@Override
	public boolean exists() {
		return Files.exists(path);
	}

	@Override
	public boolean isDirectory() {
		return Files.isDirectory(path);
	}

	@Override
	public boolean isFile() {
		return Files.isRegularFile(path);
	}

	@Override
	public boolean isHidden() {
		try {
			return Files.isHidden(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long lastModified() {
		try {
			return Files.getLastModifiedTime(path).toMillis();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long length() {
		try {
			return Files.size(path);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean createNewFile() throws IOException {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean delete() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public void deleteOnExit() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public String[] list() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public String[] list(FilenameFilter filter) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public File[] listFiles() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public File[] listFiles(FilenameFilter filter) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public File[] listFiles(FileFilter filter) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean mkdir() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean mkdirs() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean renameTo(File dest) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setLastModified(long time) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setReadOnly() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setWritable(boolean writable, boolean ownerOnly) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setWritable(boolean writable) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setReadable(boolean readable, boolean ownerOnly) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setReadable(boolean readable) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean setExecutable(boolean executable) {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public boolean canExecute() {
		return Files.isExecutable(path);
	}

	@Override
	public long getTotalSpace() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public long getFreeSpace() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
	public long getUsableSpace() {
		throw new UnsupportedOperationException(Resources.getStr("error.not_implemented"));
	}

	@Override
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

	@Override
	public String toString() {
		return path.toString();
	}

	@Override
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
