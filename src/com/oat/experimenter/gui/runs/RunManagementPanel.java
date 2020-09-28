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
package com.oat.experimenter.gui.runs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.gui.GUIException;
import com.oat.gui.GenericOATModalDialog;
import com.oat.utils.GUIUtils;

/**
 * Description: 
 *  
 * Date: 29/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class RunManagementPanel extends JPanel
{	
	protected RunMatrixListPanel runMatrixList;	
	protected RunListPanel runList;	
	
	protected RunListControlPanel listControlPane;
	protected RunMatrixListControlPanel matrixControlPanel;
	
	protected JTabbedPane jtp;
	
	/**
	 * Current experiment being worked on
	 */
	protected Experiment experiment;

	/**
	 * The current experimental run matrix being worked on
	 */
	protected ExperimentalRunMatrix runMatrix;
	
	
	
	/**
	 * Prepare with the provided experiment
	 */
	public RunManagementPanel(Experiment aExperiment)
	{		
		this();
		populateFromExperiment(aExperiment);
	}
	/**
	 * Default Constructor
	 */
	public RunManagementPanel()
	{
		createGUI();
	}

	
	/**
	 * Construct the GUI
	 */
	public void createGUI()
	{
		runList = new RunListPanel();
		listControlPane = new RunListControlPanel();
		runList.add(listControlPane, BorderLayout.SOUTH);
		
		runMatrixList = new RunMatrixListPanel();
		matrixControlPanel = new RunMatrixListControlPanel();
		runMatrixList.add(matrixControlPanel, BorderLayout.SOUTH);
		
		jtp = new JTabbedPane();
		jtp.add("Run Matrix", runMatrixList);
		jtp.add("Run List", runList);	
		
        setLayout(new BorderLayout());
        add(jtp, BorderLayout.CENTER);
	}
	
	public void populateFromExperiment(Experiment aExperiment)
	{
		experiment = aExperiment;
		runMatrix = new ExperimentalRunMatrix();
		if(experiment.getRunsDefined() > 0)
		{
			// populate
			try
			{
				runMatrix.populateFromRunList(experiment.getRuns());
			}
			catch (ExperimentException e)
			{
				throw new GUIException("Unable to prepare run matrix: " + e.getMessage(), e);
			}
		}
		// populate lists		
		runList.populateWithExperiment(experiment);
		runMatrixList.setRunMatrix(runMatrix);
		// enable/disable things
		matrixControlPanel.populateFromExperiment(experiment);
		
		// do not manage runs if none defined
		if(aExperiment.getRunsDefined()>0)
		{
			jtp.setEnabledAt(1,true);
		}
		else
		{
			jtp.setEnabledAt(1,false);
		}
	}
	
	/**
	 * Called when something happens somewhere
	 */
	public void reloadLists()
	{
		if(experiment.getRunsDefined()>0)
		{
			jtp.setEnabledAt(1,true);
		}
		else
		{
			jtp.setEnabledAt(1,false);
		}
		
		runList.loadAllRunsIntoTable();
		runMatrixList.loadTableWithMatrix();
		matrixControlPanel.populateFromExperiment(experiment);

	}
	
	protected class RunMatrixListControlPanel extends JPanel 
		implements ActionListener
	{
		protected JButton editRunMatrix;
		protected JButton deleteAllResults;
		
		public RunMatrixListControlPanel()
		{
			createGUI();
		}
		public void createGUI()
		{
			editRunMatrix = new JButton("Edit Run Schedule");
			editRunMatrix.addActionListener(this);			
			add(editRunMatrix);
			
			deleteAllResults = new JButton("Delete All Results");
			deleteAllResults.addActionListener(this);			
			add(deleteAllResults);
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			
			if(src == editRunMatrix)
			{				
				// prepare panel
				RunMatrixDetailsPanel panel = new RunMatrixDetailsPanel();
				panel.populateGUIFromRunSchedule(experiment, runMatrix);				
				// prepare frame
				GenericOATModalDialog frame = new GenericOATModalDialog(panel,  "Run Schedule");
				frame.setVisible(true);
				// we may have changed the state of some of the runs
				// reload both lists
				reloadLists();
			}
			else if(src == deleteAllResults)
			{
				try
				{
					ExperimentalRunUtils.deleteAllRunResults(experiment);
				}
				catch(Exception e2)
				{
					JOptionPane.showMessageDialog(this, "Error deleting all runs: " + e2.getMessage(), "Error Deleting", JOptionPane.ERROR_MESSAGE);
				}		
				reloadLists();
			}
		}		
		
		public void populateFromExperiment(Experiment aExperiment)
		{
			if(aExperiment.getRunsCompleted()>0)
			{
				editRunMatrix.setEnabled(false);
				deleteAllResults.setEnabled(true);
			}
			else
			{
				deleteAllResults.setEnabled(false);
				editRunMatrix.setEnabled(true);
			}
		}
	}
	
	
	protected class RunListControlPanel extends JPanel 
		implements ActionListener
	{
		protected JButton executeSelectedButton;
		protected JButton editSelectedButton;

		public RunListControlPanel()
		{
			createGUI();
		}

		public void createGUI()
		{
			executeSelectedButton = new JButton("Execute Selected");
			editSelectedButton = new JButton("Edit Selected");
			executeSelectedButton.addActionListener(this);
			editSelectedButton.addActionListener(this);
			add(executeSelectedButton);
			add(editSelectedButton);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();

			ExperimentalRun[] selectedRuns = runList.getSelectedRuns();

			if (src == executeSelectedButton)
			{
				if (selectedRuns == null || selectedRuns.length == 0)
				{
					JOptionPane.showMessageDialog(this, "Please select a single experimental run to execute.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
				} 
				else
				{
					RunExecutionPanel panel = new RunExecutionPanel();
					panel.setExecutionTask(experiment, selectedRuns); // set the runs
					GenericOATModalDialog frame = new GenericOATModalDialog(panel, "Run Execution");
					frame.setVisible(true);
					// we may have changed the state of some of the runs
					// reload both lists
					reloadLists();
				}							
			} 
			else if (src == editSelectedButton)
			{
				if (selectedRuns == null || selectedRuns.length > 1)
				{
					JOptionPane.showMessageDialog(this,
							"Please select a single experimental run to edit.",
							"Invalid Selection", JOptionPane.WARNING_MESSAGE);
				} 
				else
				{
					// prepare panel
					RunDetailsPanel panel = new RunDetailsPanel();
					panel.setAllowDelete(false);
					panel.setAllowEditChangeAlgorithm(false);
					panel.setAllowEditChangeProblem(false);
					panel.prepareForUpdate(experiment, selectedRuns[0]);
					// prepare frame
					GenericOATModalDialog frame = new GenericOATModalDialog(panel, "Experiemntal Run Details");
					frame.setVisible(true);
					// we have edited the configuration of a run, just refresh the list
					runList.loadAllRunsIntoTable();
				}
			}
		}
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			// home
			File home = ExperimentUtils.getDefaultHomeDirectory();
			// load all experiments
			Experiment [] experiments = ExperimentUtils.loadExperiments(home);
			
			
			// list runs for the first experiment
			RunManagementPanel panel = new RunManagementPanel();			
			// populate
			panel.populateFromExperiment(experiments[0]);

			GUIUtils.testJFrame(panel, "Run Management", new Dimension(800, 600));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}
