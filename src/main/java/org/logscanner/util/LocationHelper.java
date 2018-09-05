package org.logscanner.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.Resources;
import org.logscanner.data.Location;
import org.logscanner.data.LocationGroup;
import org.logscanner.data.LogPattern;
import org.logscanner.exception.BusinessException;

/**
 * @author Victor Kadachigov
 */
public class LocationHelper 
{
	public static Location getLocationById(LocationGroup root, LocationGroup group, String id)
	{
		Location result = null;
		if (group.getItems() != null)
		{
			Iterator<Location> iterator = group.getItems().iterator();
			while (result == null && iterator.hasNext())
			{
				Location l = iterator.next();
				if (Objects.equals(l.getCode(), id))
					result = l;
			}
		}
		if (result == null && group.getGroups() != null)
		{
			Iterator<LocationGroup> iterator = group.getGroups().iterator();
			while (result == null && iterator.hasNext())
				result = getLocationById(root, iterator.next(), id);
		}
		return result;
	}

	public static LocationGroup getGroupById(LocationGroup root, LocationGroup group, String id)
	{
		LocationGroup result = null;
		if (Objects.equals(group.getCode(), id))
			result = group;
		if (result == null && group.getGroups() != null)
		{
			Iterator<LocationGroup> iterator = group.getGroups().iterator();
			while (result == null && iterator.hasNext())
				result = getGroupById(root, iterator.next(), id);
		}
		return result;
	}

	public static LocationGroup getParent(LocationGroup root, LocationGroup group, String childId)
	{
		LocationGroup result = null;
		if (group.getItems() != null)
		{
			Iterator<Location> iterator = group.getItems().iterator();
			while (result == null && iterator.hasNext())
			{
				Location l = iterator.next();
				if (Objects.equals(l.getCode(), childId))
					result = group;
			}
		}
		if (result == null && group.getGroups() != null)
		{
			Iterator<LocationGroup> iterator = group.getGroups().iterator();
			while (result == null && iterator.hasNext())
			{
				LocationGroup g = iterator.next();
				if (Objects.equals(g.getCode(), childId))
					result = group;
				else
					result = getParent(root, g, childId);
			}
		}
		return result;
	}
	
	public static void checkCode(List<? extends Named> list) throws BusinessException
	{
		Set<Named> set = new HashSet<>();
		for (Named item : list)
		{
			if (StringUtils.isBlank(item.getCode()))
				throw new BusinessException(Resources.getStr("error.code_is_empty"));
			if (!set.add(item))
				throw new BusinessException(Resources.getStr("error.code_already_used", item.getCode()));
		}
		
	}
}
