package org.logscanner.gui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.AppConstants;
import org.logscanner.common.gui.BaseAction;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.exception.BusinessException;
import org.logscanner.jobs.CopyFilesWriter;
import org.logscanner.service.JobResultModel;
import org.logscanner.service.SearchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
@Component
public class SearchAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	@Autowired
	private SearchModel searchModel;
	@Autowired
	private JobResultModel resultModel;
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private JobOperator jobOperator;
	@Autowired
	private Job job;
	@Autowired
	private MessageSourceAccessor messageAccessor;

	@PostConstruct
	public void init()
	{
		init(messageAccessor.getMessage("action.search.title"), null);
	}

	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception 
	{
		if (resultModel.getJobState() == JobResultModel.JobState.STOPPED)
			start();
		else if (resultModel.getJobState() == JobResultModel.JobState.RUNNED)
			stop();
	}

	private void stop() throws Exception 
	{
		resultModel.stopping();
		jobOperator.stop(searchModel.getExecutionId());
	}
	
	private boolean validate() throws IOException, BusinessException
	{
		if (searchModel.getSelectedLocations().isEmpty()) {
			throw new BusinessException(messageAccessor.getMessage("action.search.text.no_locations")); 
		}
		if (searchModel.isSaveResults()) {
			switch (searchModel.getSaveType()) {
				case SearchModel.SAVE_TYPE_FILE:
					if (Files.exists(Paths.get(searchModel.getResultFile()))  
							&& !MessageBox.showConfirmDialog(
									null, 
									messageAccessor.getMessage("action.search.text.file_exists", new String[] { searchModel.getResultFile() })
								)) {
						return false;
					}
					break;
				case SearchModel.SAVE_TYPE_FOLDER:
					Path p = Paths.get(searchModel.getResultFolder());
					if (Files.exists(p)) {
						if (!Files.isDirectory(p)) {
							throw new BusinessException(messageAccessor.getMessage("action.search.text.not_a_folder", new String[] { searchModel.getResultFolder() }));
						}
						if (Files.list(p).findAny().isPresent()
								&& !MessageBox.showConfirmDialog(
										null, 
										messageAccessor.getMessage("action.search.text.not_empty", new String[] { searchModel.getResultFile() })
									)) {
							return false;
						}						
					}
					break;
				default:
					throw new IllegalStateException("Wrong save type " + searchModel.getSaveType());
			}
		}
		return true;
	}

	private void start() throws Exception {
		if (!validate())
			return;
		
		JobParametersBuilder jobParamsBuilder = new JobParametersBuilder()
				.addString(AppConstants.JOB_PARAM_ID, UUID.randomUUID().toString())
				.addDate(AppConstants.JOB_PARAM_FROM, Date.from(searchModel.getFrom().atZone(ZoneId.systemDefault()).toInstant()))
				.addDate(AppConstants.JOB_PARAM_TO, Date.from(searchModel.getTo().atZone(ZoneId.systemDefault()).toInstant()))
				.addLong(AppConstants.JOB_PARAM_SAVE_TO_ARCHIVE, searchModel.isSaveResults() ? searchModel.getSaveType() : 0L)
				.addString(AppConstants.JOB_SEARCH_STRING, searchModel.getSearchString())
				.addString(AppConstants.JOB_PARAM_LOCATIONS, StringUtils.join(searchModel.getSelectedLocations(), ','))
				.addString(AppConstants.JOB_PARAM_PATTERN_CODE, searchModel.getPatternCode());
//				.addString(AppConstants.JOB_PARAM_ENCODING, searchModel.getEncoding());
		switch (searchModel.isSaveResults() ? searchModel.getSaveType() : 0)
		{
			case SearchModel.SAVE_TYPE_FILE:
				jobParamsBuilder = jobParamsBuilder.addString(AppConstants.JOB_PARAM_OUTPUT_ARCHIVE_NAME, searchModel.getResultFile());
				break;
			case SearchModel.SAVE_TYPE_FOLDER:
				jobParamsBuilder = jobParamsBuilder.addString(AppConstants.JOB_PARAM_OUTPUT_FOLDER_NAME, searchModel.getResultFolder());
				break;
			default:
				// Do nothing
		}
		JobExecution jobExecution = jobLauncher.run(job, jobParamsBuilder.toJobParameters());
		searchModel.setExecutionId(jobExecution.getId());
		
		searchModel.saveDefaults();

		log.info("started");
	}

}
