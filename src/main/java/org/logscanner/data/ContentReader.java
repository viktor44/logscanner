package org.logscanner.data;

import java.io.IOException;
import java.io.InputStream;

public interface ContentReader
{
	public InputStream getInputStream() throws IOException;
}
