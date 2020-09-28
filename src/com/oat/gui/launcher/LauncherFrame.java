/*
 Optimization Algorithm Toolkit (OAT)
 http://sourceforge.net/projects/optalgtoolkit
 Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat.gui.launcher;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.oat.gui.GenericOATFrame;


/**
 * Date: 20/08/2007<br/>
 * @author Jason Brownlee
 *
 * Description: 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class LauncherFrame extends GenericOATFrame
{	
	public LauncherFrame()
	{
		super("Launcher");
		setSize(400, 520);
		setResizable(false);
	}
   
	@Override
	protected void prepareGUI()
	{
		LauncherPicturePanel picturePanel = new LauncherPicturePanel();
		LauncherExplorerPanel explorerPanel = new LauncherExplorerPanel();
		LauncherExperimenterPanel experimenterPanel = new LauncherExperimenterPanel();
		
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        // problem
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        p.add(picturePanel, c);        
        
        c.gridx = 0;
        c.gridy = 2;        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(explorerPanel, c);
        
        c.gridx = 0;
        c.gridy = 3;        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(experimenterPanel, c);
        
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
	}
	
	
	
	
  
	
	public static void main(String[] args)
	{
		new LauncherFrame().makeVisible();
	}
}
