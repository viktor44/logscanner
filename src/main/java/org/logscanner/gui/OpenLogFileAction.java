package org.logscanner.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;

import org.logscanner.Resources;
import org.logscanner.common.gui.BaseAction;
import org.logscanner.data.LogEvent;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class OpenLogFileAction extends BaseAction 
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OpenLogFileAction.class);

	@Autowired
	private JobResultModel resultModel;

	public OpenLogFileAction() 
	{
		super(Resources.getStr("action.open_log.title"));
	}

	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception 
	{
		if (!Desktop.isDesktopSupported())
			throw new UnsupportedOperationException("Can't do this. Desktop is not supported");
		
		LogEvent logEvent = resultModel.getSelectedItem();
		if (logEvent != null)
		{
			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.OPEN))
				throw new UnsupportedOperationException("Can't do open operation");
			desktop.open(new File(logEvent.getPath()));
		}
	}
}
