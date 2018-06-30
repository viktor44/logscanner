package org.logscanner;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.spi.FileSystemProvider;
import java.util.ServiceLoader;

import javax.swing.UIManager;

import org.apache.commons.lang3.NotImplementedException;
import org.logscanner.common.gui.MessageBox;
import org.logscanner.gui.MainFrame;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * @author Victor Kadachigov
 */
public class App 
{
	private static MainFrame mainFrame;
	
	public static void main(String[] args) 
	{
		//MacOs application name: -Xdock:name="Log Scanner"
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
//		ServiceLoader.load(FileSystemProvider.class)
//			.forEach(service -> System.out.println("ZZZ: " + service)); 
//		ServiceLoader.load(FileSystemProvider.class, ClassLoader.getSystemClassLoader())
//			.forEach(service -> System.out.println("XXX: " + service)); 
	
//		System.getProperties().forEach( (k,v) -> { System.out.println("" + k + " : " + v); } );
		
    	try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
    	
		boolean consoleApp = false;
		boolean configError = false;

		OptionParser parser = new OptionParser();
		parser.accepts("c", "console run");

		try 
		{
			OptionSet options = parser.parse(args);
			consoleApp = options.has("c");
		}
		catch (OptionException ex) 
		{
			StringWriter helpWriter = new StringWriter();
			try {
				parser.printHelpOn(helpWriter);
			} catch (IOException ex1) {
				System.out.println(ex1.getMessage());
			}
			System.out.println("\n\n" + ex.getMessage() + "\n\n\n" + helpWriter.toString());
			configError  = true;
		}

		if (configError)
			System.exit(-1);
		
		if (consoleApp)
		{
			throw new NotImplementedException("Not implemented yet");
//			SpringApplication app = new SpringApplication(ConsoleConfig.class);
//			app.run(args);
		}
		else
		{
			try 
			{
				ConfigurableApplicationContext ctx = new SpringApplicationBuilder(GuiConfig.class)
						.headless(false)
						.web(WebApplicationType.NONE)
						.run(args);
				
		        EventQueue.invokeLater(() -> {
		        	createAndShowGUI(ctx.getBean(MainFrame.class));
		        });
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
				MessageBox.showExceptionDialog(null, "Ошибка запуска приложения", ex);
				System.exit(0);
			}
		}
	}

	private static void createAndShowGUI(MainFrame frame) 
	{
        mainFrame = frame;
        mainFrame.setVisible(true);
    }

	public static MainFrame getMainFrame() 
	{
		return mainFrame;
	}
}
