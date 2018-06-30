package org.logscanner.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXStatusBar;
import org.logscanner.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Victor Kadachigov
 */
@Component
public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private SearchPanel searchPanel;
	@Autowired
	private ResultsPanel resultsPanel;
	@Autowired
	private StatusBarPanel statusBarPanel;
	@Autowired
	private AboutAction aboutAction;
	@Autowired
	private PreferencesAction preferencesAction;
	
	public MainFrame()
	{
		super(AppConstants.APP_NAME);
	}
	
	@PostConstruct
	public void init()
	{
        setSize(800, 600);
		
        setLocationRelativeTo(null);

		setJMenuBar(createMenuBar());
		setContentPane(createContentPane());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private Container createContentPane() 
	{
		JPanel result = new JPanel(new BorderLayout());
		result.setBorder(BorderFactory.createEmptyBorder(3,3, 3, 3));
		result.add(searchPanel, BorderLayout.NORTH);
		result.add(resultsPanel, BorderLayout.CENTER);
		result.add(statusBarPanel, BorderLayout.SOUTH);
		
//		JXStatusBar statusBar = new JXStatusBar();
//		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
//		JLabel statusLabel = new JLabel("Ready");
//		JXStatusBar.Constraint c1 = new JXStatusBar.Constraint(); 
//		c1.setFixedWidth(100);
//		statusBar.add(statusLabel, c1);
//		result.add(statusBar, BorderLayout.SOUTH);

		return result;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Файл");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		menuBar.add(fileMenu);

		fileMenu.add(preferencesAction);
		fileMenu.add(new JSeparator());
		fileMenu.add(new ExitAction());

		JMenu helpMenu = new JMenu("Справка");
		fileMenu.setMnemonic(KeyEvent.VK_H);
		
		menuBar.add(helpMenu);
		
		helpMenu.add(aboutAction);

		return menuBar;
	}
}
