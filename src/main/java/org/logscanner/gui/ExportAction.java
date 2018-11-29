package org.logscanner.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.logscanner.Resources;
import org.logscanner.common.gui.BaseAction;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.data.LogEvent;
import org.logscanner.service.AppProperties;
import org.logscanner.service.JobResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Component
public class ExportAction extends BaseAction 
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private AppProperties props;
	@Autowired
	private MessageSourceAccessor messageAccessor;
	@Autowired
	private JobResultModel resultModel;

	@PostConstruct
	public void init()
	{
		init(messageAccessor.getMessage("action.export_result.title"), Resources.getIcon("image.excel.16"));
	}

	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (StringUtils.isNotBlank(props.getDefaultDir()))
			fileChooser.setCurrentDirectory(new File(props.getDefaultDir()));
		fileChooser.setFileFilter(new FileNameExtensionFilter("TXT (*.txt)", "txt"));
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			if (file.exists()  
					&& !MessageBox.showConfirmDialog(
							null, 
							messageAccessor.getMessage("action.search.text.file_exists", new String[] { file.getAbsolutePath() }))) 
				return;

			doExport(file);

			if (Desktop.isDesktopSupported()
					&& MessageBox.showConfirmDialog(
							null, 
							messageAccessor.getMessage("action.export_result.text.open_file", new String[] { file.getAbsolutePath() })))
				Desktop.getDesktop().open(file);
		}
		
	}

	private void doExport(File file) throws IOException
	{
		
		SortedSet<LogEvent> events = new TreeSet<>(
				new Comparator<LogEvent>()
				{
					@Override
					public int compare(LogEvent event1, LogEvent event2)
					{
						return (new CompareToBuilder())
									.append(event1.getPath(), event2.getPath())
									.append(event1.getLogTime(), event2.getLogTime())
									.toComparison();
					}
				}
		); 
		events.addAll(resultModel.getEvents());
		try (FileWriter writer = new FileWriter(file))
		{
			String path = null;
			for (LogEvent event : events)
			{
				if (!Objects.equals(path, event.getPath()))
				{
					path = event.getPath();
					writer.write("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
					writer.write(path);
					writer.write("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n\n");
				}
				writer.write(event.getText());
				writer.write('\n');
			}
		}
	}
}
