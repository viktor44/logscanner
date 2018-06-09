package org.logscanner.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.data.LogEvent;
import org.logscanner.service.JobResultModel;
import org.oxbow.swingbits.dialog.task.TaskDialog;
import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class ResultsPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ResultsPanel.class);
	
	@Autowired
	private JobResultModel resultModel;
	@Autowired
	private CopyTextAction copyTextAction;
	@Autowired
	private OpenLogFileAction openLogFileAction;
	
	ResultsTableModel resultsTableModel;
	
	public ResultsPanel()
	{
		super();
	}
	
	@PostConstruct
	public void init()
	{
		setLayout(new BorderLayout());
		
		resultsTableModel = new ResultsTableModel();
		JTable table = new JTable(resultsTableModel)
		{
			@Override
			public JPopupMenu getComponentPopupMenu() 
			{
				JPopupMenu result = null;
				Point p = getMousePosition();
				if (p != null)
				{
					int row = rowAtPoint(p);
					if (row >= 0)
					{
						if (!isRowSelected(row)) 
							setRowSelectionInterval(row, row);
						result = super.getComponentPopupMenu();
					}
				}
				return result;
			}
		};
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		TableColumn column = null;
		for (int i = 0; i < resultsTableModel.getColumnCount(); i++) {
		    column = table.getColumnModel().getColumn(i);
		    switch (i)
		    {
		    	case 1: // Файл
		    		column.setPreferredWidth(200);
		    		break;
		    	case 2: // Строка
		    		column.setPreferredWidth(800);
		    		break;
		    	default:
		    		column.setPreferredWidth(100);
		    }
		}

		add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new JMenuItem(openLogFileAction));
		popupMenu.add(new JMenuItem(copyTextAction));
		
		table.setComponentPopupMenu(popupMenu);
		table.getSelectionModel().addListSelectionListener(resultModel);
		
		resultModel.addPropertyChangeListener(
				"jobState",
				new PropertyChangeListener() 
				{
					@Override
					public void propertyChange(PropertyChangeEvent event) 
					{
						if (event.getNewValue() == JobResultModel.JobState.STOPPED)
						{
					        EventQueue.invokeLater(() -> {
					        	if (resultModel.isSuccess())
					        		MessageBox.showMessageDialog(
						        			null, 
				        					"Готово. Работали " 
				        						+ createDurationString(resultModel.getStartTime(), resultModel.getEndTime())
					        		);
					        	else
					        		MessageBox.showExceptionDialog(null, "Ошибка", resultModel.getError());
					        });
						}
					}
				}
		);
	}
	
	private static String createDurationString(LocalTime start, LocalTime end)
	{
		Duration d = Duration.between(start, end);
		long hours = d.toHours();
		d = d.minusHours(hours);
		long minutes = d.toMinutes();
		d = d.minusMinutes(minutes);
		long seconds = d.getSeconds();
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	public static void main(String[] args)
	{
		LocalTime s = LocalTime.now()
							.minus(Duration.ofHours(2))
							.minus(Duration.ofMinutes(27))
							.minus(Duration.ofSeconds(15));
		LocalTime e = LocalTime.now();
		System.out.println("ZZZZZZZ: " + createDurationString(s, e));
	}
	
	private class ResultsTableModel extends AbstractTableModel implements ListDataListener
	{
		private static final long serialVersionUID = 1L;
		
		private String[] columnNames = { "Время", "Файл", "Строка" };
		
		public ResultsTableModel()
		{
			resultModel.addListDataListener(this);
		}
		
		@Override
		public int getRowCount() 
		{
			return resultModel.getSize();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			Object result = null;
			LogEvent logEvent = resultModel.getElementAt(rowIndex);
			if (logEvent != null)
			{
				switch (columnIndex)
				{
					case 0:
						result = logEvent.getLogTime();
						break;
					case 1:
//						result = StringUtils.abbreviateMiddle(logEvent.getPath(), "...", 50);
						result = logEvent.getPath();
						break;
					case 2:
						result = StringUtils.abbreviate(logEvent.getText(), "...", 500);
						break;
					default:
						result = "";
				}
			}
			return result;
		}

		@Override
		public void intervalAdded(ListDataEvent event) 
		{
			fireTableRowsInserted(event.getIndex0(), event.getIndex1());
		}

		@Override
		public void intervalRemoved(ListDataEvent event) 
		{
			fireTableRowsDeleted(event.getIndex0(), event.getIndex1());
		}

		@Override
		public void contentsChanged(ListDataEvent event) 
		{
			fireTableDataChanged();
		}
		
	}
}
