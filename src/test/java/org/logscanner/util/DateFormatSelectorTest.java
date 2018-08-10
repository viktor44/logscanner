package org.logscanner.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DateFormatSelectorTest
{
	@Test
	public void test() throws Exception
	{
		String fileName = getClass().getName().replace('.', '/') + ".txt";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName))))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (StringUtils.isBlank(line) || line.startsWith("#"))
					continue;

				String format = DateFormatSelector.selectFormat(line);
				assertThat(format)
						.as("Unknown date format in line '" + StringUtils.abbreviate(line, 50) + "'")
						.isNotNull();
			}
		}
	}
}
