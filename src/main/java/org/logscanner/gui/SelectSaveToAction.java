package org.logscanner.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.Resources;
import org.logscanner.common.gui.BaseAction;
import org.logscanner.service.SearchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Выбираем путь к файлу в который сохраняем результаты
 * 
 * @author Victor Kadachigov
 */
@Component
public class SelectSaveToAction extends BaseAction
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SelectSaveToAction.class);
	
	@Autowired
	private SearchModel searchModel;

	public SelectSaveToAction()
	{
		super(Resources.getStr("action.save_to.title"), Resources.getIcon("image.save_to.16"));
	}
	
	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (StringUtils.isNotBlank(searchModel.getResultPath()))
			fileChooser.setSelectedFile(new File(searchModel.getResultPath()));
		fileChooser.setFileFilter(createFileFilter());
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			searchModel.setResultPath(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	protected FileFilter createFileFilter()
	{
		return new FileNameExtensionFilter("Zip (*.zip)", "zip");
	}
}
