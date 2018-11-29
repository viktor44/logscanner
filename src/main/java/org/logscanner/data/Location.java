package org.logscanner.data;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.util.Named;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Location implements Named
{
	@EqualsAndHashCode.Include
	private String code;
	private String path;
	private String description;
	private String host;
	private Integer port;
	private String user;
	private String password;
	private LocationType type = LocationType.LOCAL;
	
	Location() {
		//for deserialization
	}
	public Location(String code, String path, String description) {
		this();
		this.code = code;
		this.path = path;
		this.description = description;
	}

	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer();
		if (type == LocationType.LOCAL)
		{
			sb.append(path);
		}
		else
		{
			sb.append(type.toString().toLowerCase()).append("://");
			if (user != null)
				sb.append(user).append('@');
			sb.append(host);
			if (path != null)
				sb.append(path);
		}
		
		if (StringUtils.isNotBlank(description))
			sb.append(" (").append(description).append(")");
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return code + ": " + getName();
	}
}
