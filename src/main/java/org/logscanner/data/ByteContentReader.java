package org.logscanner.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteContentReader implements ContentReader
{
	private final byte[] content;
	
	public ByteContentReader(byte[] content)
	{
		this.content = content;
	}

	@Override
	public InputStream getInputStream()
	{
		return new ByteArrayInputStream(content);
	}
}
