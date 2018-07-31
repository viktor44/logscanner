package org.logscanner.jobs;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.logscanner.AppConstants;
import org.logscanner.data.DirInfo;
import org.logscanner.data.FileInfo;
import org.logscanner.data.FilterParams;
import org.logscanner.data.Location;
import org.logscanner.data.LogEvent;
import org.logscanner.data.LogPattern;
import org.logscanner.service.FileServiceSelector;
import org.logscanner.service.FileSystemService;
import org.logscanner.service.JobResultModel;
import org.logscanner.service.LocalFileService;
import org.logscanner.service.LogPatternDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.privatejgoodies.common.base.Objects;

/**
 * @author Victor Kadachigov
 */
public class DirectoryFilesProcessor implements ItemProcessor<Location, DirInfo>
{
	private static final Logger log = LoggerFactory.getLogger(DirectoryFilesProcessor.class);
	
	@Autowired
	private LogPatternDao patternDao;
	@Autowired
	private FileServiceSelector fileServiceSelector;
	@Autowired
	private JobResultModel resultModel;

	private StepExecution stepExecution;
	private LogPattern pattern;
	private Date dateFrom;
	private Date dateTo;

	@Override
	public DirInfo process(Location location) throws Exception 
	{
		DirInfo result = null;
		FilterParams filterParams = new FilterParams();
		filterParams.setIncludes(pattern.getIncludes());
		filterParams.setDateFrom(dateFrom);
		filterParams.setDateTo(dateTo);
		FileSystemService fileSystemService = fileServiceSelector.select(location.getType());
		List<FileInfo> list = fileSystemService.listFiles(location, filterParams); 
		
		log.info("{} {} выбрано {} файлов", location.getCode(), location.getPath(), list.size());
		
		if (!list.isEmpty())
		{
			resultModel.addFilesToProcess(list.size());
			
			Collections.sort(list, new NameComparator());
			result = new DirInfo();
			result.setLocationCode(location.getCode());
			result.setHost(location.getHost());
			result.setRootPath(location.getPath());
			result.setFiles(list);
			
//			for (FileInfo fi : list)
//			{
//				if (!Objects.equals(fi.getHost(), result.getHost()))
//					throw new RuntimeException("Not equals " + fi.getHost() + " " + result.getLocationCode());
//			}
		}
		
		return result;
	}

	public class NameComparator implements Comparator<FileInfo>
	{
		@Override
		public int compare(FileInfo f1, FileInfo f2)
		{
			return (new CompareToBuilder())
							.append(f1.getFilePath().toLowerCase(), f2.getFilePath().toLowerCase())
							.toComparison();
		}
	}

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) 
    {
        this.stepExecution = stepExecution;
        this.pattern = patternDao.getByCode(stepExecution.getJobParameters().getString(AppConstants.JOB_PARAM_PATTERN_CODE));
        this.dateFrom = stepExecution.getJobParameters().getDate(AppConstants.JOB_PARAM_FROM);
        this.dateTo = stepExecution.getJobParameters().getDate(AppConstants.JOB_PARAM_TO);
    }
}
