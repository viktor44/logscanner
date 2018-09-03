package org.logscanner.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

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
public class CopyTextAction extends BaseAction
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CopyTextAction.class);
	
	@Autowired
	private JobResultModel resultModel;

	public CopyTextAction() 
	{
		super(Resources.getStr("action.copy_text.title"));
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
