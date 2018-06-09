package org.logscanner.common.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.logscanner.exception.BusinessException;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

/**
 * @author Victor Kadachigov
 */
public class MessageBox
{
	private MessageBox() {}
	
	public static void showErrorDialog(Component parentComponent, Throwable exception)
	{
		showErrorDialog(parentComponent, ExceptionUtils.getRootCauseMessage(exception));
	}

	public static void showErrorDialog(Component parentComponent, String message)
	{
		JOptionPane.showMessageDialog(
				parentComponent, 
				message, 
				"Ошибка", 
				JOptionPane.ERROR_MESSAGE
		);
	}

	public static void showWarningDialog(Component parentComponent, String message)
	{
		JOptionPane.showMessageDialog(
				parentComponent, 
				message, 
				"Внимание", 
				JOptionPane.WARNING_MESSAGE
		);
	}

	public static void showMessageDialog(Component parentComponent, String message)
	{
		JOptionPane.showMessageDialog(
				parentComponent, 
				message, 
				"Информация", 
				JOptionPane.INFORMATION_MESSAGE
		);
	}

	public static boolean showConfirmDialog(Component parentComponent, String message)
	{
		int result = JOptionPane.showConfirmDialog(
								parentComponent, 
								message, 
								"Подтверждение", 
								JOptionPane.YES_NO_OPTION
						);
		return result == JOptionPane.YES_OPTION;
	}

	public static void showExceptionDialog(Component parentComponent, String message, Throwable error)
	{
		String msg, text;
		Throwable th = error instanceof BusinessException ? error : ExceptionUtils.getRootCause(error);
		if (th == null)
			th = error;
		if (StringUtils.isNotBlank(message))
		{
			msg = message;
			text = th instanceof BusinessException ? th.getMessage() : ExceptionUtils.getMessage(th);
		}
		else
		{
			msg = th instanceof BusinessException ? th.getMessage() : ExceptionUtils.getMessage(th);
			text = "";
		}
		TaskDialogs.build()
			.instruction(message)
			.text(text)
			.showException(error);
	}

}
