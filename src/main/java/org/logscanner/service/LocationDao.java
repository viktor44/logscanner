package org.logscanner.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.logscanner.data.Location;
import org.logscanner.data.LocationGroup;
import org.logscanner.exception.BusinessException;
import org.logscanner.util.LocationHelper;
import org.logscanner.util.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Victor Kadachigov
 */
@Service
public class LocationDao extends DaoSupport
{
	@Autowired
	private AppProperties props;
	@Autowired
	private ObjectMapper mapper;

	private LocationGroup root;
	
	public Location getByCode(String code)
	{
		Location result = LocationHelper.getLocationById(root, root, code);
		return result;
	}
	
	public LocationGroup getGroupByCode(String code)
	{
		LocationGroup result = LocationHelper.getGroupById(root, root, code);
		return result;
	}

	public LocationGroup getParent(String childCode)
	{
		LocationGroup result = LocationHelper.getParent(root, root, childCode);
		return result;
	}

	public LocationGroup getRootGroup()
	{
		return root;
	}
	
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException 
	{
	}
	
	@Override
	protected void initDao() throws Exception 
	{
		ObjectReader reader = mapper.reader();
		LocationGroup rootGroup = reader.readValue(
								reader.getFactory().createParser(props.getLocationsFile().toFile()), 
								LocationGroup.class
						);
		
		LocationHelper.checkCode(toList(rootGroup));
		
		root = rootGroup;
	}
	
	private List<Named> toList(LocationGroup group)
	{
		List<Named> result = new ArrayList<>();
		result.add(group);
		if (group.getItems() != null)
			result.addAll(group.getItems());
		if (group.getGroups() != null)
			group.getGroups().forEach(g -> result.addAll(toList(g)));
		return result;
	}
	
//	public static LocationGroup createTestRoot()
//	{
//		final LocationGroup root = new LocationGroup("ROOT", "ROOT", null);
//		root.setGroups(new ArrayList<>());
//		final LocationGroup group1 = new  LocationGroup("usv-fs", "УСВ ФС", "Описание");
//		group1.setGroups(new ArrayList<>());
//		final LocationGroup group11 = new LocationGroup("usv-fs-mb", "Московский банк", null);
//		group11.setItems(new ArrayList<>());
//		group11.getItems().add(new Location("server1", "\\\\server1\\logs", "server1"));
//		group11.getItems().add(new Location("server2", "\\\\server2\\logs", "Очень очень очень очень длинное описание"));
//		
//		final LocationGroup group12 = new LocationGroup("usv-fs-srb", "Среднерусский банк", null);
//		group12.setItems(new ArrayList<>());
//		group12.getItems().add(new Location("server21", "\\\\server21\\logs", "server21"));
//		final LocationGroup group13 = new LocationGroup("usv-fs-sb", "Северный банк", null);
//		group13.setGroups(new ArrayList<>());
//		group1.getGroups().add(group11);
//		group1.getGroups().add(group12);
//		group1.getGroups().add(group13);
//		final LocationGroup group2 = new LocationGroup("usv-uko", "УСВ УКО", "");
//		final LocationGroup group3 = new LocationGroup("usv-bo", "УСВ БО", "");
//		root.getGroups().add(group1);
//		root.getGroups().add(group2);
//		root.getGroups().add(group3);
//		return root;
//	}
//	
//	public static void main(String[] args) throws IOException
//	{
//		ObjectMapper mapper = new ObjectMapper();
//		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
//		writer.writeValue(new File("/Users/victor/tmp/locations.json"), createTestRoot());
//	}
}
