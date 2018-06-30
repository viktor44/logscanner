package org.logscanner.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXStatusBar;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusBarPanel extends JPanel
{
	private static Logger log = LoggerFactory.getLogger(StatusBarPanel.class);

	@Autowired
	private JobResultModel resultModel;
	
	private JXStatusBar statusBar;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	
	@PostConstruct
	public void init()
	{
		setLayout(new BorderLayout());
		
		statusBar = new JXStatusBar();
		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		JXStatusBar.Constraint c1 = new JXStatusBar.Constraint(); 
		c1.setFixedWidth(100);
		statusLabel = new JLabel("Готов");
		statusBar.add(statusLabel, c1);
		//JLabel label = new JLabel("Хм...");
		JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(); 
		c2.setFixedWidth(200);
		progressBar = new JProgressBar();
		progressBar.setBorder(BorderFactory.createEmptyBorder());
		statusBar.add(progressBar, c2);

		add(statusBar, BorderLayout.CENTER);
		
		resultModel.addPropertyChangeListener(
				"jobState",
				new PropertyChangeListener()
				{
					@Override
					public void propertyChange(PropertyChangeEvent event)
					{
						//log.info("{}", event);
						switch ((JobResultModel.JobState)event.getNewValue())
						{
							case RUNNED:
								statusLabel.setText("Идёт поиск");
								break;
							case STOPPING:
								statusLabel.setText("Останавливаю");
								break;
							case STOPPED:
								statusLabel.setText("Готово");
								break;
						}
					}
				}
		);
	}

}
