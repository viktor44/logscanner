package org.logscanner.data;

import java.util.List;

import org.logscanner.util.Named;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Victor Kadachigov
 */
@JsonInclude(Include.NON_NULL)
public class LocationGroup  implements Named
{
	private String code;
	private String name;
	private String description;
	private List<Location> items;
	private List<LocationGroup> groups;
	
	LocationGroup() {
		//for deserialization
	}
	public LocationGroup(String code, String name, String description) {
		this();
		this.code = code;
		this.name = name;
		this.description = description;
	}

	@Override
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Location> getItems() {
		return items;
	}
	public void setItems(List<Location> items) {
		this.items = items;
	}
	public List<LocationGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<LocationGroup> groups) {
		this.groups = groups;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationGroup other = (LocationGroup) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return code + " " + name;
	}
}
