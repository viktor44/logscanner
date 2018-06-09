package org.logscanner.gui;

import java.awt.event.ActionEvent;

import org.logscanner.common.gui.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class AboutAction extends BaseAction
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private AboutDialog aboutDialog;

	public AboutAction()
	{
		super("О программе");
	}
	
	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception
	{
		aboutDialog.setVisible(true);
	}
}
