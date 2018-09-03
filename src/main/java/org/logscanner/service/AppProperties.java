package org.logscanner.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.data.Location;
import org.logscanner.data.LocationGroup;
import org.logscanner.data.LogPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

/**
 * @author Victor Kadachigov
 */
@Component
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class AppProperties 
{
	private static final Logger log = LoggerFactory.getLogger(AppProperties.class);

	@JsonIgnore
	@Autowired
	private ObjectMapper mapper;
	@JsonIgnore
	@Value("${version}")
	private String version;
	@JsonIgnore
	private Path preferencesDir;
	@JsonIgnore
	private Path locationsFile;
	@JsonIgnore
	private Path settingsFile;
	@JsonIgnore
	private Path patternsFile;
	@JsonIgnore
	private FileTime settingsFileChangeTime;

	private int settingsVersion = 1;
	private int maxResults = 100000;
	private String defaultPatternCode;
	private String defaultDir;
	private Boolean defaultSaveToFile;
	private String locale = "en";
	private String dataDir;
	
	@PostConstruct
	public void init()
	{
		try 
		{
			AppDirs appDirs = AppDirsFactory.getInstance();
			preferencesDir = Paths.get(appDirs.getUserConfigDir("LogScanner", null, "", true));
			if (Files.notExists(preferencesDir))
				Files.createDirectories(preferencesDir);
			
			locationsFile = preferencesDir.resolve("locations.json");
			if (Files.notExists(locationsFile))
				createDefaultLocations();

			patternsFile = preferencesDir.resolve("patterns.json");
			if (Files.notExists(patternsFile))
				createDefaultPatterns();
			
			settingsFile = preferencesDir.resolve("settings.json");
			if (Files.exists(settingsFile))
				settingsFileChangeTime = Files.getLastModifiedTime(settingsFile);
			else
				createDefaultSettings();
			loadSettings();
			
			if (StringUtils.isEmpty(dataDir))
				dataDir = appDirs.getUserDataDir("LogScanner", null, "", false);
		} 
		catch (IOException ex) 
		{
			throw new BeanInitializationException("Initialization of AppProperties failed", ex);
		}
	}
	
	@PreDestroy
	public void close()
	{
		try
		{
			if (settingsFileChangeTime != null 
					&& Files.getLastModifiedTime(settingsFile).compareTo(settingsFileChangeTime) == 0)
				saveSettings();
		}
		catch (Exception ex)
		{
			log.error("", ex);
		}
	}
	
	private void createDefaultPatterns() throws IOException
	{
		List<LogPattern> list = new ArrayList<>();
		LogPattern p = new LogPattern("all", "Все файлы");
		p.setIncludes(new String[] {"**/*.*"});
		p.setEncoding("UTF-8");
		list.add(p);
		
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		writer.writeValue(patternsFile.toFile(), list);
	}

	private void createDefaultLocations() throws IOException
	{
		final LocationGroup root = new LocationGroup("ROOT", "ROOT", null);
		root.setGroups(new ArrayList<>());
		final LocationGroup group1 = new  LocationGroup("Group1", "Group 1", "Group 1 description");
		group1.setGroups(new ArrayList<>());
		final LocationGroup group11 = new LocationGroup("NetworkGroup", "Network Group", null);
		group11.setItems(new ArrayList<>());
		group11.getItems().add(new Location("server1", "\\\\server1\\logs", "server1 description"));
		group11.getItems().add(new Location("server2", "\\\\server2\\logs", "server2 description"));
		
		final LocationGroup group12 = new LocationGroup("LocalDirGroup", "Local Dir Group", null);
		group12.setItems(new ArrayList<>());
		group12.getItems().add(new Location("temp", "c:\\temp", "temp dir description"));
		
		group1.getGroups().add(group11);
		group1.getGroups().add(group12);
		
		final LocationGroup group2 = new  LocationGroup("Group2", "Group 2", "Group 2 description");
		final LocationGroup group3 = new  LocationGroup("Group3", "Group 3", "Group 3 description");
		root.getGroups().add(group1);
		root.getGroups().add(group2);
		root.getGroups().add(group3);
		
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		writer.writeValue(locationsFile.toFile(), root);
	}

	private void createDefaultSettings() throws IOException 
	{
		saveSettings();
	}

	private void saveSettings() throws IOException
	{
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		writer.writeValue(settingsFile.toFile(), this);
	}

	private void loadSettings() throws IOException 
	{
		ObjectReader reader = mapper.reader();
		AppProperties p = reader.readValue(
									reader.getFactory().createParser(settingsFile.toFile()), 
									AppProperties.class
							);
		maxResults = p.maxResults;
		defaultDir = p.defaultDir;
		defaultPatternCode = p.defaultPatternCode;
		defaultSaveToFile = p.defaultSaveToFile;
		locale = p.locale;
		if (StringUtils.isNotBlank(p.dataDir))
			dataDir = p.dataDir; 
	}

	public Path getPreferencesDir()
	{
		return preferencesDir;
	}
	
	public Path getLocationsFile() 
	{
		return locationsFile;
	}
	
	public String getVersion()
	{
		return version;
	}

	public int getMaxResults()
	{
		return maxResults;
	}
	
	public Path getPatternsFile()
	{
		return patternsFile;
	}

	public String getDefaultPatternCode()
	{
		return defaultPatternCode;
	}
	public void setDefaultPatternCode(String defaultPatterCode)
	{
		this.defaultPatternCode = defaultPatterCode;
	}

	public String getDefaultDir()
	{
		return defaultDir;
	}
	public void setDefaultDir(String defaultDir)
	{
		this.defaultDir = defaultDir;
	}

	public Boolean getDefaultSaveToFile()
	{
		return defaultSaveToFile;
	}
	public void setDefaultSaveToFile(Boolean defaultSaveToFile)
	{
		this.defaultSaveToFile = defaultSaveToFile;
	}

	public String getLocale()
	{
		return locale;
	}

	public String getDataDir()
	{
		return dataDir;
	}
}
