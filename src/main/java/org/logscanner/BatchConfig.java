package org.logscanner;

import org.logscanner.jobs.ConfigFileReader;
import org.logscanner.jobs.DirectoryFilesProcessor;
import org.logscanner.jobs.DirsQueueReader;
import org.logscanner.jobs.DirsQueueWriter;
import org.logscanner.jobs.FileContentProcessor;
import org.logscanner.jobs.FileContentProcessor2;
import org.logscanner.jobs.FileContentProcessor3;
import org.logscanner.jobs.LogWriter;
import org.logscanner.jobs.PackFilesWriter;
import org.logscanner.jobs.PackFilesWriter2;
import org.logscanner.jobs.PackFilesWriter3;
import org.logscanner.service.AppProperties;
import org.logscanner.service.JobResultModel;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Victor Kadachigov
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer
{
	public static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
	
    @Autowired
    private JobBuilderFactory jobs;
	@Autowired
	private StepBuilderFactory steps;
	
	public AppProperties appProperties()
	{
		return new AppProperties();
	}

	@Bean
	public Job job(
					@Qualifier("readDirectoriesStep") Step step1,
					@Qualifier("copyFilesStep") Step step2,
					JobResultModel resultModel
				)
	{
		return jobs.get(AppConstants.JOB_NAME)
					.start(step1)
					.next(step2)
					.listener(resultModel)
					.incrementer(new RunIdIncrementer())
					.preventRestart()
					.build();
	}
	
	@Bean
	protected Step readDirectoriesStep(
						@Qualifier("configFileReader") ItemReader<? extends Object> reader,
						@Qualifier("directoryFilesProcessor") ItemProcessor<? super Object, ? extends Object> processor,
						@Qualifier("dirsQueueWriter") ItemWriter<? super Object> writer,
						@Qualifier("readDirectoriesTaskExecutor") TaskExecutor taskExecutor
					)
	{
		ExecutionContextPromotionListener promotionListener = new ExecutionContextPromotionListener();
		promotionListener.setKeys(new String[] { AppConstants.PROP_DIRS_INFO, AppConstants.PROP_COMMON_PATH });

		return steps.get("readDirectoriesStep")
						.chunk(1)
						.reader(reader)
						.processor(processor)
						.writer(writer)
						.taskExecutor(taskExecutor)
						.listener(promotionListener)
						.build();
	}

	@Bean
	protected ItemWriter<? extends Object> logWriter() 
	{
		return new LogWriter();
	}

	@Bean
	protected ItemWriter<? extends Object> dirsQueueWriter() 
	{
		return new DirsQueueWriter();
	}

	@Bean
	protected ItemProcessor<? extends Object, ? extends Object> directoryFilesProcessor() 
	{
		return new DirectoryFilesProcessor();
	}
	
	@Bean
	protected ItemReader<? extends Object> configFileReader()
	{
		return new ConfigFileReader();
	}
	
	@Bean
	ThreadPoolTaskExecutor readDirectoriesTaskExecutor()
	{
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(DEFAULT_THREADS);
        executor.setMaxPoolSize(DEFAULT_THREADS);
        executor.setThreadNamePrefix("readDirs");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();

        return executor;
	}

	@Bean
	protected Step copyFilesStep(
						@Qualifier("dirsQueueReader") ItemReader<? extends Object> reader,
						@Qualifier("fileContentProcessor") ItemProcessor<? super Object, ? extends Object> processor,
						@Qualifier("packFilesWriter") ItemWriter<? super Object> writer,
						@Qualifier("copyFilesTaskExecutor") TaskExecutor taskExecutor
					)
	{
		ExecutionContextPromotionListener promotionListener = new ExecutionContextPromotionListener();
		promotionListener.setKeys(new String[] { AppConstants.PROP_DIRS_INFO, AppConstants.PROP_COMMON_PATH });

		return steps.get("copyFilesStep")
						.chunk(1)
						.reader(reader)
						.processor(processor)
						.writer(writer)
						.taskExecutor(taskExecutor)
						.listener(promotionListener)
						.build();
	}
	
	@Bean
	protected ItemWriter<? extends Object> packFilesWriter()
	{
		return new PackFilesWriter3();
	}
	
	@Bean
	protected ItemProcessor<? extends Object, ? extends Object> fileContentProcessor() 
	{
		return new FileContentProcessor();
	}

	@Bean
	ThreadPoolTaskExecutor copyFilesTaskExecutor()
	{
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(DEFAULT_THREADS);
        executor.setMaxPoolSize(DEFAULT_THREADS);
        executor.setThreadNamePrefix("copyFiles");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();

        return executor;
	}
	
	@Override
	protected JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());		
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

//	@Bean
//	TaskExecutor jobLauncherTaskExecutor()
//	{
//		return new SimpleAsyncTaskExecutor();
//	}
//	
//	@Bean
//	JobLauncher jobLauncher(
//						JobRepository jobRepository, 
//						@Qualifier("jobLauncherTaskExecutor") TaskExecutor taskExecutor
//				)
//	{
//		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//		jobLauncher.setJobRepository(jobRepository);
//		jobLauncher.setTaskExecutor(taskExecutor);
//		return jobLauncher;
//	}

	@Bean
	ItemReader<? extends Object> dirsQueueReader()
	{
		return new DirsQueueReader();
	}

	@Bean
	public SimpleJobOperator jobOperator(JobExplorer jobExplorer, JobLauncher jobLauncher,
			ListableJobLocator jobRegistry, JobRepository jobRepository) 
	{
		SimpleJobOperator factory = new SimpleJobOperator();
		factory.setJobExplorer(jobExplorer);
		factory.setJobLauncher(jobLauncher);
		factory.setJobRegistry(jobRegistry);
		factory.setJobRepository(jobRepository);
		return factory;
	}
}
