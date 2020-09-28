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
package com.oat.experimenter.gui.analysis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.gui.runs.RunListPanel;
import com.oat.experimenter.gui.runs.RunMatrixListPanel;
import com.oat.gui.GUIException;
import com.oat.utils.GUIUtils;

/**
 * Description: Displays the runs of an experiment to a user so they can select 
 * runs for analysis
 *  
 * Date: 30/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class AnalysisRunList extends JPanel
{
	public final static int MATRIX = 0;
	public final static int LIST = 1;
	
	protected Experiment experiment;		
	protected ExperimentalRunMatrix runMatrix;	
	
	protected JTabbedPane jtp;
	
	protected RunListPanel runListPanel;
	protected RunMatrixListPanel matrixListPanel;
	                                 
	public AnalysisRunList()
	{
		createGUI();
	}
	
	public AnalysisRunList(Experiment aExperiment)
	{
		this();
		populateWithExperiment(aExperiment);
	}
	
	protected void createGUI()
	{
		matrixListPanel = new RunMatrixListPanel();
		runListPanel = new RunListPanel();
		
		jtp = new JTabbedPane();
		jtp.add("Matrix", matrixListPanel);
		jtp.add("List", runListPanel);
				
		setLayout(new BorderLayout());
		add(jtp, BorderLayout.CENTER);	
	}
	

	
	public ExperimentalRun [] getSelectedRuns()
	{
		int mode = jtp.getSelectedIndex();
		
		if(mode == MATRIX)
		{
			return matrixListPanel.getSelectedRuns();			
		}
		else if(mode == LIST)			
		{
			return runListPanel.getSelectedRuns();
		}
		
		throw new RuntimeException("Unknown selected tab: " + mode);
	}
	
	
	public void populateWithExperiment(Experiment aExperiment)
	{
		experiment = aExperiment;		
		runMatrix = new ExperimentalRunMatrix();		
		try
		{
			runMatrix.populateFromRunList(experiment.getRuns());
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Unable to prepare run matrix: " + e.getMessage(), e);
		}
		
		matrixListPanel.setRunMatrix(runMatrix, experiment.getRuns());
		runListPanel.populateWithExperiment(experiment);
	}	
	
	/**
	 * Test out this thing
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			// home
			File home = ExperimentUtils.getDefaultHomeDirectory();
			// load all experiments
			Experiment [] experiments = ExperimentUtils.loadExperiments(home);
			
			AnalysisRunList panel = new AnalysisRunList();
			panel.populateWithExperiment(experiments[0]);
			GUIUtils.testJFrame(panel, "Analysis Run Lists", new Dimension(800, 600));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
