package org.logscanner.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.logscanner.AppConstants;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.jobs.CopyFilesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Victor Kadachigov
 */
@Slf4j
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
	private ExitAction exitAction;
	@Autowired
	private PreferencesAction preferencesAction;
	@Autowired
	private MessageSourceAccessor messageAccessor;
	
	@PostConstruct
	public void init()
	{
		MessageBox.changeTitles(
				messageAccessor.getMessage("dialog.title.error"), 
				messageAccessor.getMessage("dialog.title.warning"),
				messageAccessor.getMessage("dialog.title.info"),
				messageAccessor.getMessage("dialog.title.confirm")
		);
		
		setTitle(AppConstants.APP_NAME);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		List<Image> icons = new ArrayList<Image>();
		icons.add(toolkit.createImage(MainFrame.class.getResource("/images/app1/log_16.png")));
		icons.add(toolkit.createImage(MainFrame.class.getResource("/images/app1/log_24.png")));
		icons.add(toolkit.createImage(MainFrame.class.getResource("/images/app1/log_32.png")));
		icons.add(toolkit.createImage(MainFrame.class.getResource("/images/app1/log_64.png")));
		icons.add(toolkit.createImage(MainFrame.class.getResource("/images/app1/log_128.png")));
		setIconImages(icons);

		setSize(800, 600);
		
        setLocationRelativeTo(null);

		setJMenuBar(createMenuBar());
		setContentPane(createContentPane());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new ShowLogKeyEventDispatcher());
	}

	private Container createContentPane() 
	{
		JPanel result = new JPanel(new BorderLayout());
		result.setBorder(BorderFactory.createEmptyBorder(3,3, 3, 3));
		result.add(searchPanel, BorderLayout.NORTH);
		result.add(resultsPanel, BorderLayout.CENTER);
		result.add(statusBarPanel, BorderLayout.SOUTH);
		return result;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(messageAccessor.getMessage("action.file"));
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		menuBar.add(fileMenu);

		fileMenu.add(preferencesAction);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitAction);

		JMenu helpMenu = new JMenu(messageAccessor.getMessage("action.help"));
		fileMenu.setMnemonic(KeyEvent.VK_H);
		
		menuBar.add(helpMenu);
		
		helpMenu.add(aboutAction);

		return menuBar;
	}
	
	private class ShowLogKeyEventDispatcher implements KeyEventDispatcher
	{
		@Override
		public boolean dispatchKeyEvent(KeyEvent event) 
		{
			// Ctrl + Alt + L
			if (event.getID() == KeyEvent.KEY_RELEASED && event.getKeyCode() == 76 && event.isControlDown() && event.isAltDown())
			{
				try 
				{
					Path logPath = Paths.get(System.getProperty("java.io.tmpdir"), "logscanner.log");
					if (Files.exists(logPath))
						Desktop.getDesktop().open(logPath.toFile());
				}
				catch (Exception ex) 
				{
					log.error("", ex);
					MessageBox.showExceptionDialog(null, messageAccessor.getMessage("error.log_open_error"), ex);
				}
			}
			return false;
		}
	}
}
