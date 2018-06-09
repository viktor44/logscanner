package org.logscanner.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.annotation.PostConstruct;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.logscanner.AppConstants;
import org.logscanner.common.gui.BaseDialog;
import org.logscanner.service.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class AboutDialog extends BaseDialog
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private AppProperties props;

	public AboutDialog()
	{
		super(null, "О программе", false);
	}
	
	@PostConstruct
	public void init()
	{
		super.init();
	    setSize(450, 260);
	}

	@Override
	protected JComponent createMainPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = null;
		
		JLabel appNameLabel = new JLabel(AppConstants.APP_NAME);
		Font appNameFont = appNameLabel.getFont();
		appNameLabel.setFont(new Font(appNameFont.getName(), Font.BOLD, 20));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.ipady = 20;
		panel.add(appNameLabel, c);
		
		JLabel versionLabel = new JLabel("Версия " + props.getVersion());
		versionLabel.setFont(new Font(appNameFont.getName(), Font.BOLD, 14));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(versionLabel, c);

		JLabel copyrightLabel = new JLabel("Copyright 2018 by Victor Kadachigov");
		copyrightLabel.setFont(new Font(appNameFont.getName(), Font.PLAIN, 14));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(copyrightLabel, c);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.PAGE_END;
		c.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), c);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 1.0;
		panel.add(Box.createVerticalGlue(), c);

		return panel;
	}

	@Override
	protected JButton[] createButtons()
	{
		return null;
	}
}
