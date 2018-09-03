package org.logscanner.gui;

import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.Set;

import org.logscanner.Resources;
import org.logscanner.common.gui.BaseAction;
import org.logscanner.common.gui.BaseDialog;
import org.logscanner.service.SearchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class SelectLocationsAction extends BaseAction 
{
	private static Logger log = LoggerFactory.getLogger(SelectLocationsAction.class);

	@Autowired
	private SearchModel searchModel;
	
	public SelectLocationsAction()
	{
		super(Resources.getStr("action.select_locations.title"), Resources.getIcon("image.locations.16"));
	}
	
	@Override
	public void actionPerformed0(ActionEvent event) 
	{
		SelectLocationsDialog dialog = new SelectLocationsDialog(searchModel.getSelectedLocations());
		dialog.setVisible(true);
		if (Objects.equals(dialog.getCloseAction(), BaseDialog.ACTION_OK))
		{
			Set<String> oldValue = searchModel.getSelectedLocations();
			searchModel.setSelectedLocations(dialog.getSelectedLocations());
			firePropertyChange("locations", oldValue, searchModel.getSelectedLocations());
		}
	}

}
