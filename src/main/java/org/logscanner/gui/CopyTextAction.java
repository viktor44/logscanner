package org.logscanner.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;

import org.logscanner.common.gui.BaseAction;
import org.logscanner.data.LogEvent;
import org.logscanner.jobs.CopyFilesWriter;
import org.logscanner.service.JobResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
@Component
public class CopyTextAction extends BaseAction
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private JobResultModel resultModel;
	@Autowired
	private MessageSourceAccessor messageAccessor;

	@PostConstruct
	public void init()
	{
		init(messageAccessor.getMessage("action.copy_text.title"), null);
	}

	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception 
	{
		LogEvent logEvent = resultModel.getSelectedItem();
		if (logEvent != null)
		{
			StringSelection stringSelection = new StringSelection(logEvent.getText());
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents(stringSelection, stringSelection);
		}
	}

}
