package org.logscanner.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.logscanner.Resources;

/**
 * @author Victor Kadachigov
 */
public class ExitAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	public ExitAction()
	{
		super(Resources.getStr("action.exit.title"), Resources.getIcon("image.exit.16"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}
}
