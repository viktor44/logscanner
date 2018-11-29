package org.logscanner.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.annotation.PostConstruct;
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
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Выбираем путь к папке в которую сохраняем результаты
 * 
 * @author Victor Kadachigov
 */
@Component
public class SelectSaveToFolderAction extends BaseAction
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private SearchModel searchModel;
	@Autowired
	private MessageSourceAccessor messageAccessor;

	@PostConstruct
	public void init()
	{
		init(messageAccessor.getMessage("action.save_to.title"), Resources.getIcon("image.save_to.16"));
	}
	
	@Override
	protected void actionPerformed0(ActionEvent event) throws Exception
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (StringUtils.isNotBlank(searchModel.getResultFolder()))
			fileChooser.setSelectedFile(new File(searchModel.getResultFolder()));
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			searchModel.setResultFolder(fileChooser.getSelectedFile().getAbsolutePath());
	}
}
