package org.logscanner.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UriContentReader implements ContentReader
{
	private static final int DEFAULT_BUFFER_SIZE = 10 * 1024 * 1024; // 10Mb
	private final URI uri;
	private final long size;
	
	public UriContentReader(URI uri, long size)
	{
		this.uri = uri;
		this.size = size;
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return new BufferedInputStream(Files.newInputStream(Paths.get(uri)), Math.min((int)size, DEFAULT_BUFFER_SIZE));
	}
}
