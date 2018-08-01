package org.logscanner.data;

/**
 * @author Victor Kadachigov
 */
public class FileData 
{
	private String locationCode;
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
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String location) {
		this.locationCode = location;
	}
	public ContentReader getContentReader() {
		return contentReader;
	}
	public void setContentReader(ContentReader contentReader) {
		this.contentReader = contentReader;
	}
}
