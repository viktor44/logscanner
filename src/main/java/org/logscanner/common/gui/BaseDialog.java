package org.logscanner.common.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Victor Kadachigov
 */
public abstract class BaseDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	public static final String ACTION_OK		= "action_ok";
	public static final String ACTION_CANCEL	= "action_cancel";
	public static final int DEFAULT_BUTTON_WIDTH = 100;
	
	private String closeAction;
	
	public BaseDialog(Frame owner, String title, boolean doInit)
	{
		super(owner, title, true);
		if (doInit)
			init();
	}
	
	protected void init()
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setContentPane(createContentPane());
		
		setLocationRelativeTo(getParent());
		
		pack();
	}

	protected abstract JButton[] createButtons();
	protected abstract JComponent createMainPanel();
	
	public String getCloseAction()
	{
		return closeAction;
	}
	void setCloseAction(String closeAction)
	{
		this.closeAction = closeAction;
	}

	protected JComponent createContentPane()
	{
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(createMainPanel(), BorderLayout.CENTER);
		
		JButton[] buttons = createButtons();
		if (buttons != null && buttons.length > 0)
		{
			Box buttonsPanel = Box.createHorizontalBox();
			buttonsPanel.add(Box.createHorizontalGlue());
			for (int i = 0; i < buttons.length; i++)
			{
				buttonsPanel.add(buttons[i]);
				if (StringUtils.defaultString(buttons[i].getActionCommand()).equals(BaseDialog.ACTION_OK))
					getRootPane().setDefaultButton(buttons[i]);
				if (i < buttons.length - 1)
					buttonsPanel.add(Box.createHorizontalStrut(5));
			}
			panel.add(buttonsPanel, BorderLayout.SOUTH);
		}
		
		return panel;
	}
	
	protected boolean canClose(String actionCommand)
	{
		return true;
	}
	
	private static class CancelButton extends JButton
	{
		public CancelButton(String text)
		{
			super(text);
			
			Dimension d = getPreferredSize();
			d.width = DEFAULT_BUTTON_WIDTH;
			setPreferredSize(d);

			setActionCommand(BaseDialog.ACTION_CANCEL);

	        Action cancelKeyAction = new AbstractAction() 
	        {
	            @Override
				public void actionPerformed(ActionEvent e) 
	            {
	                ((CancelButton)e.getSource()).fireActionPerformed(e);
	            }
	        }; 
	    	KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke((char)KeyEvent.VK_ESCAPE);
	    	InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    	ActionMap actionMap = getActionMap();
	    	if (inputMap != null && actionMap != null) 
	    	{
	    	    inputMap.put(cancelKeyStroke, BaseDialog.ACTION_CANCEL);
	    	    actionMap.put(BaseDialog.ACTION_CANCEL, cancelKeyAction);
	    	}
	    
	        addActionListener(new CloseDialogActionListener());
		}

	}
	
	protected JButton dialogCancelButton(String text)
	{
        return new CancelButton(text);
	}

	protected JButton dialogOkButton()
	{
        JButton button = createButton("OK");
        button.setActionCommand(BaseDialog.ACTION_OK);
        button.addActionListener(new CloseDialogActionListener());
        return button;
	}
	
	protected JButton createButton(String text)
	{
		JButton button = new JButton(text);
		Dimension d = button.getPreferredSize();
		d.width = DEFAULT_BUTTON_WIDTH;
		button.setPreferredSize(d);
		return button;
	}
	
	private static class CloseDialogActionListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			Object source = event.getSource(); 
			if (source == null || !(source instanceof Component))
				return;
			BaseDialog dialog = (BaseDialog)SwingUtilities.getAncestorOfClass(BaseDialog.class, (Component)event.getSource());
			if (dialog != null)
			{
				if (dialog.canClose(event.getActionCommand()))
				{
					dialog.setCloseAction(event.getActionCommand());
					dialog.setVisible(false);	
				}
			}
		}
	}
}

