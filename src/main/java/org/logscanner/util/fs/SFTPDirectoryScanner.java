package org.logscanner.util.fs;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.tools.ant.BuildException;

import com.github.robtimus.filesystems.sftp.SFTPEnvironment;

/**
 * @author Victor Kadachigov
 */
public class SFTPDirectoryScanner extends LocalDirectoryScanner {

	private String host;
	private int port = 22;
	private String username;
	private String password;
	
	private FileSystem fs;
	
	@Override
	protected void init() {
		try {
//			URI uri = URI.create("sftp://" + (username != null ? (username + "@") : "")  + host + ":" + port);
			URI uri = URI.create("sftp://" + host + ":" + port);
			SFTPEnvironment env = new SFTPEnvironment()
					.withConfig("StrictHostKeyChecking", "no");
			if (username != null)
				env.withUsername(username);
			if (password != null)
				env.withPassword(password.toCharArray());
			try {
				fs = FileSystems.newFileSystem(URI.create("sftp://" + host + ":" + port), env);	
			} catch (FileSystemAlreadyExistsException ex1) {
				fs = FileSystems.getFileSystem(URI.create("sftp://" + (username != null ? (username + "@") : "")  + host + ":" + port));
			}        
			
		} catch (Exception ex) {
			throw new BuildException(ex);
		}
		if (basedirStr != null)
			basedir = fs.getPath(basedirStr);
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    /**
     * Test whether a file should be selected.
     *
     * @param name the filename to check for selecting.
     * @param file the java.io.File object for this filename.
     * @return <code>false</code> when the selectors says that the file
     *         should not be selected, <code>true</code> otherwise.
     */
    protected boolean isSelected(final String name, final Path file) {
    	File basedirFile = new SFTPFileProxy(basedir);
        return selectors == null
                || Stream.of(selectors).allMatch(sel -> sel.isSelected(basedirFile, name, new SFTPFileProxy(file)));
    }
}
