package org.logscanner.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

public class DateFormatSelector
{
	private static String[] formats = new String[] 
										{
												"yyyy-MM-dd HH:mm:ss,S",
												"yyyy-MM-dd HH:mm:ss.S",
												"yyyy-MM-dd HH:mm:ss:S",
												"yyyy-MM-dd HH:mm:ss",
												"dd.MM.yy HH:mm:ss,S",
												"dd.MM.yy HH:mm:ss.S",
												"dd.MM.yy HH:mm:ss:S",
												"dd.MM.yy HH:mm:ss",
										};

	public static String selectFormat(String dateString)
	{
		String result = null;
		for (String s : formats)
		{
			FastDateFormat dateFormat = FastDateFormat.getInstance(s);
			try
			{
				Date d = dateFormat.parse(dateString);
				if (d != null)
				{
					result = s;
					break;
				}
			}
			catch (ParseException | NumberFormatException ex)
			{
			}
		}
		return result;
	}
}
