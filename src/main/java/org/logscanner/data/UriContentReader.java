package org.logscanner.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Victor Kadachigov
 */
public class UriContentReader implements ContentReader
{
	private static final Logger log = LoggerFactory.getLogger(UriContentReader.class);
	private static final int DEFAULT_BUFFER_SIZE = 20 * 1024 * 1024; // 20Mb
	private final URI uri;
	private final long size;
	
	private transient BufferedInputStream inputStream;
	
	public UriContentReader(URI uri, long size)
	{
		this.uri = uri;
		this.size = size;
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		int bufferSize = Math.min((int)size, DEFAULT_BUFFER_SIZE);
		if (inputStream != null)
		{
			// check if closed
			try
			{
				inputStream.reset();	
			}
			catch (IOException ex)
			{
				log.error(ex.getMessage());
				inputStream = null;
			}
		}
		if (inputStream == null)
		{
			inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(uri)), bufferSize);
			inputStream.mark(bufferSize + 1);
		}
		return inputStream;
	}

	@Override
	public void close()
	{
		if (inputStream != null)
		{
			try
			{
				inputStream.close();	
			}
			catch (IOException ex)
			{
				log.error(ex.getMessage());
				inputStream = null;
			}
		}
	}
}
