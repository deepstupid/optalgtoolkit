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
package com.oat.explorer.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.oat.Algorithm;
import com.oat.Domain;
import com.oat.Problem;
import com.oat.StopCondition;
import com.oat.explorer.gui.AlgorithmRunStateChangedListener;
import com.oat.explorer.gui.StopConditionChangedListener;
import com.oat.gui.BeanConfigurationFrame;

/**
 * Description: Manage stop conditions for a domain
 *  
 * Date: 04/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class StopConditionControlPanel extends JPanel
	implements ActionListener, AlgorithmRunStateChangedListener, ListSelectionListener
{
	protected LinkedList<StopConditionChangedListener> listeners = new LinkedList<StopConditionChangedListener>();
	
	protected JList stopConditionList;
	protected JButton configButton;
	
	protected final Domain domain; 
	
	
	public StopConditionControlPanel(Domain aDomain)
	{
		domain = aDomain;
		createGUI();
	}
	
	protected void createGUI()
	{
		setName("Stop Conditions");
		
		configButton = new JButton("Config");
		configButton.addActionListener(this);
		
		LinkedList<StopCondition> stopConditions = domain.loadDomainStopConditions();
		Vector<StopCondition> v = new Vector<StopCondition>();
		v.addAll(stopConditions);
		stopConditionList = new JList(v);
		stopConditionList.setVisibleRowCount(4);
		stopConditionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		stopConditionList.addListSelectionListener(this);
		

		JPanel p = new JPanel();
		p.add(configButton);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(stopConditionList), BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), getName()));
		evaluateButtonEnabledState();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if(src == configButton)
		{
			Object [] selected = stopConditionList.getSelectedValues();
			if(selected!=null && selected.length==1)
			{
				StopCondition s = (StopCondition) selected[0];
				
	            if(s.isUserConfigurable())
	            {
	                BeanConfigurationFrame f = new BeanConfigurationFrame(null, s, "Stop Condition Configuration", null);
	                f.setVisible(true);
	            }
			}
		}
	}
	
	public LinkedList<StopCondition> getSelectedStopConditions()
	{
		Object [] selected = stopConditionList.getSelectedValues();
		
		LinkedList<StopCondition> list = new LinkedList<StopCondition>();
		
		if(selected!=null)
		{
			for (int i = 0; i < selected.length; i++)
			{
				list.add((StopCondition)selected[i]);
			}
		}
		
		return list;
	}

	public void enableAll()
	{
		stopConditionList.setEnabled(true);
		evaluateButtonEnabledState();
	}
	public void disableAll()
	{
		stopConditionList.setEnabled(false);
		configButton.setEnabled(false);
	}
	
	
	@Override
	public void algorithmFinishNotify(Problem p, Algorithm a)
	{
		enableAll();		
	}

	@Override
	public void algorithmStartNotify(Problem p, Algorithm a)
	{
		disableAll();		
	}
	
	public void evaluateButtonEnabledState()
	{
		Object [] selected = stopConditionList.getSelectedValues();
		
		// check for disable buttons
		if(selected==null || selected.length!=1)
		{
			configButton.setEnabled(false);
		}
		// only one selected, and has configuration
		else 
		{
			if(((StopCondition)selected[0]).isUserConfigurable())
			{
				configButton.setEnabled(true);
			}
			else
			{
				// one selected by cannot configure
				configButton.setEnabled(false);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		evaluateButtonEnabledState();
		triggerStopConditionSelectionChanged(getSelectedStopConditions());
	}
	
	public void triggerStopConditionSelectionChanged(LinkedList<StopCondition> s)
	{
		for(StopConditionChangedListener l : listeners)
		{
			l.stopConditionSelectionChanged(s);
		}
	}
	
	public void addStopConditionChangedListener(StopConditionChangedListener l)
	{
		listeners.add(l);
	}
	
	public boolean removeStopConditionChangedListener(StopConditionChangedListener l)
	{
		return listeners.remove(l);
	}
}
