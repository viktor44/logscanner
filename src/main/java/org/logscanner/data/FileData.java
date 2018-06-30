package org.logscanner.data;

/**
 * @author Victor Kadachigov
 */
public class FileData 
{
	private String location;
	private String filePath;
	private String zipPath;
	private ContentReader contentReader;
	
	public String getZipPath() {
		return zipPath;
	}
	public void setZipPath(String zipPath) {
		this.zipPath = zipPath;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public ContentReader getContentReader() {
		return contentReader;
	}
	public void setContentReader(ContentReader contentReader) {
		this.contentReader = contentReader;
	}
}
