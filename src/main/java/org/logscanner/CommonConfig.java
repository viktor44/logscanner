package org.logscanner;

import java.nio.file.attribute.FileTime;

import org.logscanner.util.ServiceHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Victor Kadachigov
 */
@Configuration
public class CommonConfig 
{
	@Bean
	ServiceHelper serviceHelper()
	{
		return new ServiceHelper();
	}
	
	@Bean
	ObjectMapper objectMapper()
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
		mapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
		return mapper;
	}

}
