package org.logscanner.data;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.util.Named;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class LogPattern implements Named
{
	@EqualsAndHashCode.Include
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
}
