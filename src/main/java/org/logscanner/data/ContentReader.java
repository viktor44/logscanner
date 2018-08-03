package org.logscanner.data;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Victor Kadachigov
 */
public interface ContentReader extends AutoCloseable
{
	public InputStream getInputStream() throws IOException;
	@Override
	public void close();
}
