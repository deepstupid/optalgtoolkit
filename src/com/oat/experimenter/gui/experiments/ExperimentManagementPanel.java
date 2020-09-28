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
package com.oat.experimenter.gui.experiments;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.gui.analysis.AnalysisManagementPanel;
import com.oat.experimenter.gui.runs.RunManagementPanel;
import com.oat.gui.GUIException;
import com.oat.gui.GenericOATModalDialog;
import com.oat.utils.GUIUtils;

/**
 * Description: 
 *  
 * Date: 28/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExperimentManagementPanel extends JPanel
	implements ActionListener, ListSelectionListener
{
	protected ExperimentListPanel listPanel;	
	protected File home;
	
	protected JButton editButton;
	protected JButton deleteButton;
	protected JButton newButton;
	protected JButton manageRunsButton;
	protected JButton analysisButton;
	
	/**
	 * default constructor
	 */
	public ExperimentManagementPanel()
	{
		createGUI();
	}
	
	/**
	 * creates the GUI
	 */
	protected void createGUI()
	{
		try
		{
			home = ExperimentUtils.getDefaultHomeDirectory();
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Error retreiving home directory: " + e.getMessage(), e);			
		}		
		listPanel = new ExperimentListPanel(home);
		listPanel.addListSelectionListener(this);
		
		// prepare control panel
		editButton = new JButton("Edit");
		newButton = new JButton("New");
		deleteButton = new JButton("Delete");
		manageRunsButton = new JButton("Manage Runs");
		analysisButton = new JButton("Analysis");
		
		editButton.addActionListener(this);
		newButton.addActionListener(this);
		deleteButton.addActionListener(this);
		manageRunsButton.addActionListener(this);		
		analysisButton.addActionListener(this);
		
		// disable all functions by default, except new experiment of course
		editButton.setEnabled(false);
		deleteButton.setEnabled(false);
		manageRunsButton.setEnabled(false);
		analysisButton.setEnabled(false);
		
		JPanel p = new JPanel();
		p.add(newButton);
		p.add(editButton);
		p.add(deleteButton);
		p.add(manageRunsButton);
		p.add(analysisButton);
		
		setLayout(new BorderLayout());
		add(listPanel, BorderLayout.CENTER);
		add(p, BorderLayout.SOUTH);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if(src == newButton)
		{
			ExperimentDetailsPanel details = new ExperimentDetailsPanel(home);
			GenericOATModalDialog frame = new GenericOATModalDialog(details, "Experiment Management");			
			frame.setVisible(true);
			// returns after finish, update the list
			listPanel.loadAllExperimentsIntoTable();
		}
		else if(src == editButton)
		{
			Experiment experiment = listPanel.getSelectedExperiment();
			if(experiment == null)
			{
				JOptionPane.showMessageDialog(this, "Please select an experiment to edit.", "No Experiment Selected", JOptionPane.WARNING_MESSAGE);
			}
			else
			{	
				ExperimentDetailsPanel details = new ExperimentDetailsPanel(experiment, home);
				GenericOATModalDialog frame = new GenericOATModalDialog(details, "Experiment Management");			
				frame.setVisible(true);
				// returns after finish, update the list
				listPanel.loadAllExperimentsIntoTable();
			}
		}
		else if(src == deleteButton)
		{
			Experiment experiment = listPanel.getSelectedExperiment();
			if(experiment == null)
			{
				JOptionPane.showMessageDialog(this, "Please select an experiment to delete.", "No Experiment Selected", JOptionPane.WARNING_MESSAGE);
			}
			else if(!experiment.canDelete())
			{
				JOptionPane.showMessageDialog(this, "Unable to delete the selected experiment, delete results first.", "Unable To Delete", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				try
				{
					experiment.delete();
				}
				catch (ExperimentException e1)
				{
					throw new GUIException("Error deleting experiment: " + experiment.getName(), e1);
				}
				// returns after finish, update the list
				listPanel.loadAllExperimentsIntoTable();
			}
		}		
		else if(src == manageRunsButton)
		{
			Experiment experiment = listPanel.getSelectedExperiment();
			if(experiment == null)
			{
				JOptionPane.showMessageDialog(this, "Please select an experiment to manage runs.", "No Experiment Selected", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				JDialog frame = new JDialog((JFrame)null, "Run Management", true);
				RunManagementPanel runManagement = new RunManagementPanel(experiment);
				frame.setSize(800, 600);
				frame.add(runManagement);
				GUIUtils.centerComponent(frame);
				frame.setVisible(true);
				// returns after finish, update the list
				listPanel.loadAllExperimentsIntoTable();
			}
		}
		else if(src == analysisButton)
		{
			Experiment experiment = listPanel.getSelectedExperiment();
			if(experiment == null)
			{
				JOptionPane.showMessageDialog(this, "Please select an experiment to analyze.", "No Experiment Selected", JOptionPane.WARNING_MESSAGE);
			}
			else if(experiment.getRunsCompleted()==0)
			{
				JOptionPane.showMessageDialog(this, "Please select an experiment with at least one completed run to analyze.", "No Experiment Selected", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				// Analyze an experiment of runs
				JDialog frame = new JDialog((JFrame)null, "Analyze", true);
				AnalysisManagementPanel runAnalysis = new AnalysisManagementPanel(experiment);
				frame.setSize(800, 600);
				frame.add(runAnalysis);
				GUIUtils.centerComponent(frame);
				frame.setVisible(true);
				// cannot change runs in the analysis, so no need to reload anything
			}
		}
	}
	
	

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Experiment experiment = listPanel.getSelectedExperiment();
		if(experiment == null)
		{
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			manageRunsButton.setEnabled(false);
			analysisButton.setEnabled(false);
		}
		else
		{
			// generic
			editButton.setEnabled(true);
			manageRunsButton.setEnabled(true);
			
			if(experiment.canDelete())
			{
				deleteButton.setEnabled(true);
			}
			else
			{
				deleteButton.setEnabled(false);
			}
			
			// analysis
			if(experiment.getRunsCompleted()>0)
			{
				analysisButton.setEnabled(true);
			}
			else
			{
				analysisButton.setEnabled(false);
			}
		}
	}

	/**
	 * Entry
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{			
			ExperimentManagementPanel panel = new ExperimentManagementPanel();
			GUIUtils.testJFrame(panel, "Experiment Management", new Dimension(640, 480));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
