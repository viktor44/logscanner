package org.logscanner.common.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.logscanner.exception.BusinessException;
import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Victor Kadachigov
 */
public abstract class BaseAction extends AbstractAction
{
	private static Logger log = LoggerFactory.getLogger(BaseAction.class);

	public BaseAction(String name) 
    {
    	super(name);
    }
	public BaseAction(String name, Icon icon) 
    {
    	super(name, icon);
    	putValue(Action.SHORT_DESCRIPTION, name);
    }
	
	@Override
	public final void actionPerformed(ActionEvent event)
	{
		try
		{
			actionPerformed0(event);
		}
		catch (Exception ex)
		{
			handleError(ex);
		}
	}
	
	protected void handleError(Throwable error)
	{
		log.error("", error);
		if (error instanceof BusinessException)
			TaskDialogs.error(null, error.getMessage(), null);
		else
			TaskDialogs.showException(error);
	}

	protected abstract void actionPerformed0(ActionEvent event) throws Exception;
}
