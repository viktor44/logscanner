package org.logscanner.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;

import org.logscanner.common.gui.BaseAction;
import org.logscanner.jobs.CopyFilesWriter;
import org.logscanner.service.AppProperties;
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
public class PreferencesAction extends BaseAction 
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private AppProperties props;
	@Autowired
	private MessageSourceAccessor messageAccessor;

	@PostConstruct
	public void init()
	{
		init(messageAccessor.getMessage("action.preferences.title"), null);
	}

	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception 
	{
		if (!Desktop.isDesktopSupported())
			throw new UnsupportedOperationException("Can't do this. Desktop is not supported");
		
		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported(Desktop.Action.OPEN))
				throw new UnsupportedOperationException("Can't do open operation");
		
		desktop.open(props.getPreferencesDir().toFile());
	}
}
