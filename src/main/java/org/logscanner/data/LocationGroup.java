package org.logscanner.data;

import java.util.List;

import org.logscanner.util.Named;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Victor Kadachigov
 */
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters=true, onlyExplicitlyIncluded=true)
@JsonInclude(Include.NON_NULL)
public class LocationGroup  implements Named
{
	@EqualsAndHashCode.Include
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
	public String toString() {
		return code + " " + name;
	}
}
