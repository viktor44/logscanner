package org.logscanner.data;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.util.Named;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Victor Kadachigov
 */
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class LogPattern implements Named
{
	private String code;
	private String name;
	private String encoding;
	private String[] includes;
	private String datePattern;
	
	LogPattern()
	{
		//for deserialization
	}
	
	public LogPattern(String code, String name)
	{
		this.code = code;
		this.name = name;
	}

	@Override
	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	@Override
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String[] getIncludes()
	{
		return includes;
	}
	public void setIncludes(String[] includes)
	{
		this.includes = includes;
	}
	public String getEncoding()
	{
		return encoding;
	}
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	public String getDescription()
	{
		StringBuilder sb = new StringBuilder()
				.append(name)
				.append(" (")
				.append(encoding)
				.append(", [")
				.append(StringUtils.join(includes, ','))
				.append("])");
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
		LogPattern other = (LogPattern) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	public String getDatePattern()
	{
		return datePattern;
	}
	public void setDatePattern(String datePattern)
	{
		this.datePattern = datePattern;
	}
}
