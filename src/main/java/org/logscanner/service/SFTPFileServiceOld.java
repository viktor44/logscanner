package org.logscanner.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.NotImplementedException;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.Location;
import org.logscanner.data.SFTPFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * @author Victor Kadachigov
 */
@Service
public class SFTPFileServiceOld implements FileSystemService
{
	private static final Logger log = LoggerFactory.getLogger(SFTPFileServiceOld.class);
	private static final int DEFAULT_PORT = 22;
	
	@Override
	public byte[] readContent(FileInfo file) throws IOException
	{
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public String getRelativePath(FileInfo file, String basePath)
	{
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public List<FileInfo> listFiles(Location location, FilterParams filterParams) throws IOException
	{
		try
		{
	        JSch jsch = new JSch();
	        Session session = jsch.getSession(location.getUser(), location.getHost(), location.getPort() != null ? location.getPort() : DEFAULT_PORT);
	        session.setPassword(location.getPassword());
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        Channel channel = session.openChannel("sftp");
	        channel.connect();
	        List<FileInfo> result = listFiles((ChannelSftp) channel, location.getPath(), filterParams);
	        return result;
		}
		catch (JSchException ex)
		{
			throw new IOException(ex);
		}
	}
	
	private List<FileInfo> listFiles(ChannelSftp channel, String path, FilterParams filterParams) throws IOException
	{
		try
		{
	        List<FileInfo> result = new ArrayList<>();
	        channel.cd(path);
	        EntrySelector selector = new EntrySelector(filterParams);
	        channel.ls(path, selector);
	        List<ChannelSftp.LsEntry> filelist = selector.getList();
	        for (int i = 0; i < filelist.size(); i++) 
	        {
	        	ChannelSftp.LsEntry entry = filelist.get(i);
	        	String fullName = appendName(path, entry.getFilename());
	        	if (!".".equals(entry.getFilename()) && !"..".equals(entry.getFilename()))
		        	log.info(
		        			"{} isBlk: {} isChr: {} isDir: {} isFifo: {} isLink: {} isReg: {} isSock:{}", 
		        			fullName, 
		        			entry.getAttrs().isBlk(),
		        			entry.getAttrs().isChr(),
		        			entry.getAttrs().isDir(),
		        			entry.getAttrs().isFifo(),
		        			entry.getAttrs().isLink(),
		        			entry.getAttrs().isReg(),
		        			entry.getAttrs().isSock()
		        	);
	        	if (entry.getAttrs().isReg())
	        		result.add(new SFTPFileInfo(Paths.get(fullName)));
	        	else if (entry.getAttrs().isDir()
	        				&& !".".equals(entry.getFilename()) 
	        				&& !"..".equals(entry.getFilename()))
	        		result.addAll(listFiles(channel, fullName, filterParams));
	        }
	        return result;
		}
		catch (SftpException ex)
		{
			throw new IOException(ex);
		}
	}
	
	private String appendName(String basePath, String name) 
	{
		if (basePath.endsWith("/"))
			basePath = basePath.substring(0, basePath.length() - 1);
		return basePath + "/" + name;
	}
	
	private class EntrySelector implements ChannelSftp.LsEntrySelector
	{
		private final FilterParams filterParams;
		private final List<ChannelSftp.LsEntry> list = new ArrayList<>();
		
		public EntrySelector(FilterParams filterParams)
		{
			this.filterParams = filterParams;
		}

		@Override
		public int select(LsEntry entry)
		{
			LsEntry result = entry;
			if (filterParams != null)
			{
				Date modifyTime = new Date(((long)entry.getAttrs().getMTime()) * 1000);
				if (filterParams.getDateFrom() != null && modifyTime.compareTo(filterParams.getDateFrom()) < 0)
					result = null;
				if (result != null)
				{
					if (filterParams.getDateTo() != null && modifyTime.compareTo(filterParams.getDateTo()) > 0)
						result = null;
				}
			}
			if (result != null)
				list.add(result);
			return CONTINUE;
		}

		public List<ChannelSftp.LsEntry> getList()
		{
			return list;
		}
		
	}
	
	

}
