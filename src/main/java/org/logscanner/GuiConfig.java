package org.logscanner;

import org.logscanner.util.ServiceHelper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Victor Kadachigov
 */
@SpringBootApplication
@Import({
	CommonConfig.class,
	BatchConfig.class 
})
@ComponentScan({ "org.logscanner.service", "org.logscanner.gui" })
public class GuiConfig
{

}
