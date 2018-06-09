package org.logscanner;

import org.logscanner.util.ServiceHelper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author Victor Kadachigov
 */
@SpringBootApplication
@Import({
	CommonConfig.class,
	BatchConfig.class 
})
@ComponentScan({ "org.logscanner.service" })
public class ConsoleConfig
{
	@Bean
	ApplicationRunner applicationRunner(ApplicationArguments args)
	{
		return new ConsoleAppRunner();
	}
}
