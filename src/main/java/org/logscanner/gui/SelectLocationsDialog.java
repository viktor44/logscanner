package org.logscanner.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.logscanner.App;
import org.logscanner.common.gui.BaseDialog;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.common.gui.TableColumnAdjuster;
import org.logscanner.data.Location;
import org.logscanner.data.LocationGroup;
import org.logscanner.service.LocationDao;
import org.logscanner.util.LocationHelper;
import org.logscanner.util.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Victor Kadachigov
 */
public class SelectLocationsDialog extends BaseDialog 
{
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(SelectLocationsDialog.class);

	private JXTreeTable treeTable;
	private LocationsTreeTableModel treeTableModel;
	
	public SelectLocationsDialog(Set<String> selectedLocations)
	{
		super(App.getMainFrame(), "Где искать", true);
		setLocationRelativeTo(null);
		treeTableModel.setSelectedItems(selectedLocations);
	}
	
	@Override
	protected JButton[] createButtons() 
	{
		return new JButton[] { dialogOkButton(), dialogCancelButton("Отмена") };
	}
	
	@Override
	protected JComponent createMainPanel() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		Dimension d = panel.getPreferredSize();
		d.width = 480;
		d.height = 640;
		panel.setPreferredSize(d);
		
		LocationDao locationDao = ServiceHelper.getBean(LocationDao.class);
		treeTableModel = new LocationsTreeTableModel(locationDao.getRootGroup());
//		treeTableModel = new LocationsTreeTableModel(LocationDao.createTestRoot());
		treeTable = new JXTreeTable(treeTableModel);
		treeTable.expandAll();
		treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnAdjuster tca = new TableColumnAdjuster(treeTable);
		tca.adjustColumns();
		panel.add(new JScrollPane(treeTable), BorderLayout.CENTER);
		return panel;
	}

	public Set<String> getSelectedLocations() 
	{
		return treeTableModel.getSelectedItems();
	}
	
	@Override
	protected boolean canClose(String actionCommand)
	{
		if (!Objects.equals(actionCommand, ACTION_OK))
			return true;
		
		Boolean result = null;
		LocationDao locationDao = ServiceHelper.getBean(LocationDao.class);
		List<Location> locations = new ArrayList<>();
		for (String lid : getSelectedLocations())
		{
			Location loc = locationDao.getByCode(lid);
			if (loc == null) // LocationGroup
				continue;
			for (Location l : locations)
			{
				if (Objects.equals(l.getHost(), loc.getHost()) && Objects.equals(l.getPath(), loc.getPath()))
				{
					result = MessageBox.showConfirmDialog(null, "" + l.getCode() + " и " + loc.getCode() + " имеют одинаковые хост и путь. Продолжить?");
					break;
				}
			}
			if (result != null)
				break;
			locations.add(loc);
		}
		return result != null ? result : true;
	}

	private class LocationsTreeTableModel implements TreeTableModel
	{
		private String[] columnNames = { "", "", "Код", "Путь" };
		
		private final LocationGroup root;
		private final EventListenerList eventListeners = new EventListenerList();
		private final Set<String> selectedItems = new HashSet<>();
		
		LocationsTreeTableModel(LocationGroup root)
		{
			this.root = root;
		}
		
		@Override
		public Object getRoot() {
			return root;
		}

		@Override
		public Object getChild(Object parent, int index) {
			Object result = null;
			if (parent instanceof LocationGroup)
			{
				LocationGroup group = (LocationGroup)parent;
				if (group.getGroups() != null)
					result = group.getGroups().get(index);
				else if (group.getItems() != null)
					result = group.getItems().get(index);
			}
			return result;
		}

		@Override
		public int getChildCount(Object parent) 
		{
			int result = 0;
			if (parent instanceof LocationGroup)
			{
				LocationGroup group = (LocationGroup)parent;
				if (group.getGroups() != null)
					result = group.getGroups().size();
				else if (group.getItems() != null)
					result = group.getItems().size();
			}
			return result;
		}

		@Override
		public boolean isLeaf(Object node) 
		{
			return getChildCount(node) == 0;
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) 
		{
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) 
		{
			int result = -1;
			if (parent instanceof LocationGroup)
			{
				LocationGroup group = (LocationGroup)parent;
				if (child instanceof LocationGroup && group.getGroups() != null)
					result = group.getGroups().indexOf(child);
				else if (child instanceof Location && group.getItems() != null)
					result = group.getItems().indexOf(child);
			}
			return result;
		}

		@Override
		public void addTreeModelListener(TreeModelListener l) 
		{
			eventListeners.add(TreeModelListener.class, l);
		}

		@Override
		public void removeTreeModelListener(TreeModelListener l) 
		{
			eventListeners.remove(TreeModelListener.class, l);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			Class<?> result = String.class;
			if (columnIndex == 0)
				result = Boolean.class;
			return result;
		}

		@Override
		public int getColumnCount() 
		{
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) 
		{
			return columnNames[column];
		}

		@Override
		public int getHierarchicalColumn() 
		{
			return 1;
		}

		@Override
		public Object getValueAt(Object node, int column) 
		{
			Object result = null;
			if (node instanceof LocationGroup)
			{
				LocationGroup group = (LocationGroup)node;
				switch (column)
				{
					case 0:
						result = selectedItems.contains(group.getCode());
						break;
					case 2:
						result = group.getCode();
						break;
					case 3:
						result = group.getName();
						break;
				}
			}
			else if (node instanceof Location)
			{
				Location location = (Location)node;
				switch (column)
				{
					case 0:
						result = selectedItems.contains(location.getCode());
						break;
					case 2:
						result = location.getCode();
						break;
					case 3:
						result = location.getName();
						break;
				}
			}
			return result;
		}

		@Override
		public boolean isCellEditable(Object node, int column) 
		{
			return column == 0;
		}

		@Override
		public void setValueAt(Object value, Object node, int column) 
		{
			if (column != 0)
				return;
			
			boolean b = (Boolean)value;
			if (node instanceof LocationGroup)
			{
				LocationGroup group = (LocationGroup)node;
				if (b)
					selectedItems.add(group.getCode());
				else
					selectedItems.remove(group.getCode());
				if (group.getGroups() != null)
					group.getGroups().forEach(item -> setValueAt(value, item, column));
				if (group.getItems() != null)
					group.getItems().forEach(item -> setValueAt(value, item, column));
			}
			else if (node instanceof Location)
			{
				Location location = (Location)node;
				if (b)
					selectedItems.add(location.getCode());
				else
				{
					boolean stop = false;
					String lid = location.getCode();
					while (!stop)
					{
						LocationGroup group = LocationHelper.getParent(root, root, lid);
						stop = group == null;
						if (!stop)
						{
							lid = group.getCode();
							selectedItems.remove(lid);
						}
					}
					selectedItems.remove(location.getCode());
				}
			}
		}

		public Set<String> getSelectedItems() 
		{
			return selectedItems;
		}
		public void setSelectedItems(Set<String> selectedItems) 
		{
			if (selectedItems != null && ! selectedItems.isEmpty())
			{
				this.selectedItems.clear();
				this.selectedItems.addAll(selectedItems);
			}
		}
	}
	
}
