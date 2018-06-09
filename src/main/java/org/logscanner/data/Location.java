package org.logscanner.data;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.util.Named;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Victor Kadachigov
 */
@JsonInclude(Include.NON_NULL)
public class Location implements Named
{
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
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
		Location other = (Location) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return code + ": " + getName();
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public LocationType getType() {
		return type;
	}
	public void setType(LocationType type) {
		this.type = type;
	}
}
