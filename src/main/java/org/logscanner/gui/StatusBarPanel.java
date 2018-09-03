package org.logscanner.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXStatusBar;
import org.logscanner.Resources;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class StatusBarPanel extends JPanel
{
	private static Logger log = LoggerFactory.getLogger(StatusBarPanel.class);

	@Autowired
	private JobResultModel resultModel;
	
	private JXStatusBar statusBar;
	private JLabel statusLabel;
	private JLabel progressLabel;
	
	@PostConstruct
	public void init()
	{
		setLayout(new BorderLayout());
		
		statusBar = new JXStatusBar();
		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		JXStatusBar.Constraint c1 = new JXStatusBar.Constraint(); 
		c1.setFixedWidth(100);
		statusLabel = new JLabel(Resources.getStr("status_panel.status.ready"));
		statusBar.add(statusLabel, c1);
		JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(); 
		progressLabel = new JLabel("");
		statusBar.add(progressLabel, c2);

		add(statusBar, BorderLayout.CENTER);
		
		resultModel.addPropertyChangeListener(
				"jobState",
				new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent event)
					{
						switch ((JobResultModel.JobState)event.getNewValue())
						{
							case RUNNED:
								statusLabel.setText(Resources.getStr("status_panel.status.searching"));
								break;
							case STOPPING:
								statusLabel.setText(Resources.getStr("status_panel.status.stopping"));
								break;
							case STOPPED:
								statusLabel.setText(Resources.getStr("status_panel.status.done"));
								break;
						}
					}
				}
		);

		resultModel.addPropertyChangeListener(
				new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent event)
					{
						if (!StringUtils.equalsAny(event.getPropertyName(), "filesToProcess", "processedFiles", "selectedFiles"))
							return;
						if (Objects.equals(event.getPropertyName(), "filesToProcess") && (Integer)event.getNewValue() == 0)
							progressLabel.setText("");
						else
						{
							int processed = "processedFiles".equals(event.getPropertyName()) ? (Integer)event.getNewValue() : resultModel.getProcessedFiles();
							int selected = "selectedFiles".equals(event.getPropertyName()) ? (Integer)event.getNewValue() : resultModel.getSelectedFiles();
							int total = "filesToProcess".equals(event.getPropertyName()) ? (Integer)event.getNewValue() : resultModel.getFilesToProcess();
							progressLabel.setText(Resources.getStr("status_panel.text", processed, total, selected));
						}
					}
				}
		);
	}

}
