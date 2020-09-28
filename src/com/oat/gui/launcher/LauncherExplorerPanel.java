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
package com.oat.gui.launcher;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.explorer.gui.entry.ExplorerFrame;
import com.oat.gui.DomainListCellRenderer;
import com.oat.gui.GUIException;

/**
 * Description: 
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
public class LauncherExplorerPanel extends JPanel
	implements ActionListener
{		
	protected JButton launchButton;	
	protected JList domainList;
	
	public LauncherExplorerPanel()
	{
		Domain [] domains = null;
		
		try
		{
			domains = DomainUtils.loadDomainList(DomainUtils.DOMAIN_LIST);
		} 
		catch (Exception e)
		{
			throw new GUIException("Unable to load domain list : " + e.getMessage(), e);
		}
		
		domainList = new JList(domains);
		DomainListCellRenderer renderer = new DomainListCellRenderer();
		domainList.setCellRenderer(renderer);
		domainList.setVisibleRowCount(5);
		domainList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		domainList.setToolTipText("Select one or more problem domains");
		// select all by default
		//domainList.setSelectionInterval(0, domains.length-1);
		
		launchButton = new JButton("Launch");
		launchButton.addActionListener(this);
		launchButton.setToolTipText("Launch explorer interface for selected domains");
		
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        // list
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        add(new JScrollPane(domainList), c);        
        
        // launch
        c.gridx = 3;
        c.gridy = 1;        
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.CENTER;
        add(launchButton, c);
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Explorer"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if(src == launchButton)
		{
			Object [] domains = domainList.getSelectedValues();
			if(domains!=null && domains.length>0)
			{
				Domain [] list = new Domain[domains.length];
				for (int i = 0; i < domains.length; i++)
				{
					list[i] = (Domain)domains[i];
				}
				// launch
				new ExplorerFrame(list).makeVisible();
			}
		}
	}
}
