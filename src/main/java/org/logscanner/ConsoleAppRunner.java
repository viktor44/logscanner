package org.logscanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.logscanner.data.Location;
import org.logscanner.data.LocationGroup;
import org.logscanner.service.AppProperties;
import org.logscanner.service.LocationDao;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Victor Kadachigov
 */
public class ConsoleAppRunner implements ApplicationRunner, ApplicationContextAware
{
	private ApplicationContext applicationContext;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
	@Autowired
	private AppProperties props;
	
	@Override
	public void run(ApplicationArguments args) throws Exception 
	{
		try 
		{
			JobParametersBuilder jobParamsBuilder = new JobParametersBuilder()
					.addString(AppConstants.JOB_PARAM_OUTPUT_ARCHIVE_NAME, "/Users/victor/temp/zzz.zip")
					.addString(AppConstants.JOB_PARAM_LOCATIONS, "1, 2");
			jobLauncher.run(job, jobParamsBuilder.toJobParameters());
		} 
		finally 
		{
			Map<String, ThreadPoolTaskExecutor> executors = applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);
			for (ThreadPoolTaskExecutor e : executors.values())
				e.shutdown();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException 
	{
		this.applicationContext = applicationContext;
	}
}
