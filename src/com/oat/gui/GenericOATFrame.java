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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.oat.Version;
import com.oat.utils.GUIUtils;

/**
 * Description: Generic OAT frame with menu items and GUI creation skeleton
 * To make visible safely, create and then call makeVisible()
 *  
 * Date: 21/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class GenericOATFrame extends JFrame
	implements ActionListener
{
    protected JMenuItem aboutMenuItem;
    protected JMenuItem exitMenuItem;
    
    protected AboutDialog aboutDialog;
	
	public GenericOATFrame(String frameTitle)
	{
		super(Version.NAME_SHORT + " v"+Version.VERSION + " - " + frameTitle);		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new InternalWindowAdapter());
		prepareMenuBar();
		prepareGUI();
	}
	
    protected void prepareMenuBar()
    {
        aboutDialog = new AboutDialog(this);
        JMenuBar menuBar = new JMenuBar();  
        
        // file
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        // exit
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);
        // help
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);        
        // about
        aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.addActionListener(this);
        helpMenu.add(aboutMenuItem);           
        // Install the menu bar in the frame
        setJMenuBar(menuBar);
    }
    
    /**
     * Responds to menu events
     * @param ae
     */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if(src == aboutMenuItem)
        {
            aboutDialog.setVisible(true);
        }
        else if(src == exitMenuItem)
        {
        	// ensure the wind behaves as we have specified elsewhere
        	processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }
	
    /**
     * Create the GUI components of the frame
     */
	protected abstract void prepareGUI();	
	
	/**
	 * Overriden to ensure the frame is always centred
	 */
    @Override
    public void setVisible(boolean b)
    {
        if(b)
        {
          GUIUtils.centerComponent(this);
        }
        super.setVisible(b);
    }
	
    /**
     * Called by any thread to make this frame visable
     */
    public void makeVisible()
    {
        Runnable run = new Runnable()
        {
            public void run()
            {
                setVisible(true);
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    /**
     * 
     * Description: Listeners for a close event to save GUI properties 
     *  
     * Date: 21/08/2007<br/>
     * @author Jason Brownlee 
     *
     * <br/>
     * <pre>
     * Change History
     * ----------------------------------------------------------------------------
     * 
     * </pre>
     */
    protected class InternalWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent we)
        {
            // TODO
        }
    }
}
