package org.logscanner.gui;

import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;

import org.logscanner.Resources;
import org.logscanner.common.gui.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class ExitAction extends BaseAction
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private MessageSourceAccessor messageAccessor;

	public ExitAction()
	{
		super();
	}
	
	@PostConstruct
	public void init()
	{
		init(messageAccessor.getMessage("action.exit.title"), Resources.getIcon("image.exit.16"));
	}

	@Override
	public void actionPerformed0(ActionEvent e)
	{
		System.exit(0);
	}
}
