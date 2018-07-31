package org.logscanner.gui;

import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.AppConstants;
import org.logscanner.common.gui.BaseAction;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.exception.BusinessException;
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
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class SearchAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(SearchAction.class);

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

	public SearchAction() {
		super("Искать");
	}

	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception {
		if (resultModel.getJobState() == JobResultModel.JobState.STOPPED)
			start();
		else if (resultModel.getJobState() == JobResultModel.JobState.RUNNED)
			stop();
	}

	private void stop() throws Exception {
		resultModel.stopping();
		jobOperator.stop(searchModel.getExecutionId());
	}
	
	private boolean validate() {
		if (searchModel.getSelectedLocations().isEmpty()) {
			MessageBox.showMessageDialog(null, "Не выбрано ни одного расположения для поиска"); 
			return false;
		}
		if (searchModel.isSaveToFile() && Files.exists(Paths.get(searchModel.getResultPath()))) {
			if (!MessageBox.showConfirmDialog(null, MessageFormat.format("Файл {0} уже существует. Перезаписать?", searchModel.getResultPath()))) 
				return false;
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
				.addLong(AppConstants.JOB_PARAM_SAVE_TO_ARCHIVE, searchModel.isSaveToFile() ? 1L : 0L)
				.addString(AppConstants.JOB_PARAM_OUTPUT_ARCHIVE_NAME, searchModel.getResultPath())
				.addString(AppConstants.JOB_SEARCH_STRING, searchModel.getSearchString())
				.addString(AppConstants.JOB_PARAM_LOCATIONS, StringUtils.join(searchModel.getSelectedLocations(), ','))
				.addString(AppConstants.JOB_PARAM_PATTERN_CODE, searchModel.getPatternCode());
//				.addString(AppConstants.JOB_PARAM_ENCODING, searchModel.getEncoding());
		JobExecution jobExecution = jobLauncher.run(job, jobParamsBuilder.toJobParameters());
		searchModel.setExecutionId(jobExecution.getId());
		
		searchModel.saveDefaults();

		log.info("started");
	}

}
