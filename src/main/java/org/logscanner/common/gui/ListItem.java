package org.logscanner.common.gui;

import java.util.Objects;

/**
 * @author Victor Kadachigov
 */
public class ListItem<V> implements Comparable<ListItem<V>>
{
	private V value;
	private String description;

	public ListItem(V value, String description)
	{
		this.value = value;
		this.description = description;
	}

	public V getValue()
	{
		return value;
	}
	
	public String getDescription()
	{
		return description;
	}

	@Override
	public int compareTo(ListItem<V> item)
	{
		return getDescription().compareTo(item.getDescription());
	}

	@Override
	public boolean equals(Object object)
	{
		@SuppressWarnings("rawtypes")
		ListItem item = (ListItem)object;
		return (item == null) ? false : Objects.equals(value, item.getValue());
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String toString()
	{
		return description;
	}
}
