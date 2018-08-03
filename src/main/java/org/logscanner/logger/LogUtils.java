package org.logscanner.logger;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Victor Kadachigov
 */
public class LogUtils 
{
	private LogUtils() 
	{
	}

	public static String toLog(Object value) {
		return toLog(value, new StringBuilder()).toString();
	}

	public static StringBuilder toLog(Object value, StringBuilder sb) 
	{
		if (value == null)
			sb.append("null");
		else if (value instanceof String)
			sb.append(stringToLog((String)value));
		else if (isSimpleType(value))
			sb.append(value);
		else
			sb.append(ToStringBuilder.reflectionToString(value, ToStringStyle.SHORT_PREFIX_STYLE));
		return sb;
	}
	
	public static final ToStringStyle SHORT_ARRAY_STYLE = new ShortArrayToStringStyle();  
	
    private static final class ShortArrayToStringStyle extends ToStringStyle 
    {
        private static final long serialVersionUID = 1L;

        ShortArrayToStringStyle() 
        {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
        }

        /**
         * <p>Ensure <code>Singleton</ode> after serialization.</p>
         * @return the singleton
         */
        private Object readResolve() 
        {
            return SHORT_ARRAY_STYLE;
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) 
        {
        	if (value != null && value.getClass().isArray())
        		super.append(buffer, fieldName, value, false);
        	else
        		super.append(buffer, fieldName, value, fullDetail);
        }
    }
	
	/**
	 * @param value
	 * @return позвращает строку в кодировке UTF-8
	 */
	public static String asString(byte value[])
	{
		try
		{
			return (value != null && value.length > 0) ? new String(value, "UTF-8") : null;
		}
		catch (UnsupportedEncodingException ex)
		{
			return "" + ex.getMessage();
		}
	}
	
	/**
	 * Обрезаем очень длинные строки
	 * @param value
	 * @return
	 */
	public static String stringToLog(String value)
	{
		if (StringUtils.length(value) > 200)
			value = value.substring(0, 200) + "...";
		return value;
	}

	private static boolean isSimpleType(Object value)
	{
		return		value instanceof Number
				||	value instanceof String
				||	value instanceof Date
				||	value instanceof Calendar;
	}

	public static String createDurationString(LocalTime start, LocalTime end)
	{
		Duration d = Duration.between(start, end);
		long hours = d.toHours();
		d = d.minusHours(hours);
		long minutes = d.toMinutes();
		d = d.minusMinutes(minutes);
		long seconds = d.getSeconds();
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
}
