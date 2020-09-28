/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006  Jason Brownlee

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.oat.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oat.utils.GUIUtils;

/**
 * Description: 
 *  
 * Date: 06/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GenericOATModalDialog extends JDialog
	implements FinishedNotificationEventListener
{
	protected final JPanel contentPanel;

	
	
	public GenericOATModalDialog(JPanel aContent)
	{
		this(null, aContent, aContent.getName());
	}
    
	public GenericOATModalDialog(JPanel aContent, String aTitle)
	{
		this(null, aContent, aTitle);
	}
	
    public GenericOATModalDialog(Frame frame, JPanel aContent, String aTitle)
    {
        // prepare the frame
        super(frame, aTitle, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new MyWindowAdapter());
        setResizable(false);
        contentPanel = aContent;
        // prepare the GUI
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
    	if(contentPanel instanceof FinishedEventNotifier)
    	{
    		((FinishedEventNotifier)contentPanel).addNotificationListener(this);
    	}
    	
        add(contentPanel, BorderLayout.CENTER);
        pack(); // suitable frame size for the bean configuration
    }
    
    @Override
    public void setVisible(boolean b)
    {
        if(b)
        {
          GUIUtils.centerComponent(this);
        }
        super.setVisible(b);
    }
    
    
    protected class MyWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent we)
        {
            // TODO anythings
        }
    }

	public JPanel getContentPanel()
	{
		return contentPanel;
	}

	@Override
	public void finishedEvent()
	{	
		// we are done here
		dispose();
	}
	
	
}
