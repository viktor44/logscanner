package org.logscanner.gui;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * @author Victor Kadachigov
 */
public class GuiHelper
{
	public static JButton createToolBarButton(Action action)
	{
		JButton result = new JButton(action);
		result.setHideActionText(true);
		return result;
	}
	
	public static Font createBoldFont(Font originalFont)
	{
		return new Font(originalFont.getName(), Font.BOLD, originalFont.getSize());
	}
}
