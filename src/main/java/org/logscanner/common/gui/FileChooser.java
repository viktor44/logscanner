package org.logscanner.common.gui;

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.logscanner.Resources;

/**
 * Show confirmation dialog if file already exists
 * 
 * @author Victor Kadachigov
 */
public class FileChooser extends JFileChooser
{
	@Override
	public void approveSelection()
	{
		File f = getSelectedFile();
		if (getDialogType() == SAVE_DIALOG)
		{
			if (StringUtils.isBlank(FilenameUtils.getExtension(f.getName())))
			{
				f = new File(f.getAbsolutePath() + ".pdf");
				setSelectedFile(f);
			}
			if (f.exists())
			{
				
				if (MessageBox.showConfirmDialog(this, MessageFormat.format("Файл {0} уже существует. Перезаписать?", f.getName()))) 
					super.approveSelection();

				return;
			}
		}

		super.approveSelection();
	}
}
