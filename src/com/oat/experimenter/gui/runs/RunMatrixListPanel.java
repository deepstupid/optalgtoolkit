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
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import com.oat.domains.cfo.algorithms.ParallelHillclimbingAlgorithm;
import com.oat.domains.cfo.algorithms.evolution.EvolutionStrategies;
import com.oat.domains.cfo.algorithms.evolution.EvolutionaryProgramming;
import com.oat.domains.cfo.problems.dejong.TestFunctionF1;
import com.oat.domains.cfo.problems.dejong.TestFunctionF2;
import com.oat.domains.cfo.problems.dejong.TestFunctionF3;
import com.oat.domains.cfo.problems.dejong.TestFunctionF4;
import com.oat.domains.cfo.problems.dejong.TestFunctionF5;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.gui.GUIException;
import com.oat.gui.GenericDefaultTableModel;
import com.oat.utils.GUIUtils;

/**
 * Description: Display a run matrix either by problem or by algorithm
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
public class RunMatrixListPanel extends JPanel
{	
	/**
	 * Current run matrix being worked on
	 */
	protected ExperimentalRunMatrix runMatrix;
	
	/**
	 * Current experimental runs derived from the matrix
	 */
	protected ExperimentalRun [][] experimentalRunsMatrix;
	protected LinkedList<ExperimentalRun> experimentalRuns;
		
    /**
     * Table of experimental runs
     */
    protected JTable runTable;
    /**
     * Data model for the table
     */
    protected GenericDefaultTableModel runTableModel;
    
    /**
     * True - algorithms across the top, problems down the side
     * otherwise problems across the top and algorithms down the side
     */
    protected boolean populateByAlgorithm = true;
	
    
	/**
	 * Default Constructor
	 */
	public RunMatrixListPanel()
	{
		createGUI();
	}
	
	public void addSelectionChangeListener(ListSelectionListener listener)
	{
		runTable.getSelectionModel().addListSelectionListener(listener);
	}
	
	/**
	 * Creates the GUI
	 */
	protected void createGUI()
	{
        runTableModel = new GenericDefaultTableModel();            
        runTable = new JTable(runTableModel);
        runTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        runTable.setColumnSelectionAllowed(true);
        runTable.setRowSelectionAllowed(true);
        runTable.setCellSelectionEnabled(true);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(runTable), BorderLayout.CENTER);  
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Experimental Run Matrix"));
	}
	
	
	public void clear()
	{
		runMatrix = null;
		experimentalRunsMatrix = null;
		experimentalRuns = null;
		clearTable();
	}
	
	protected void clearTable()
	{
		//  ensure no selection change event is triggered
        runTable.clearSelection();        
        // clear
        while(runTableModel.getRowCount()!=0)
        {
        	runTableModel.removeRow(0);
        }
        // lazy - reset the model
        runTableModel = new GenericDefaultTableModel();        
        runTable.setModel(runTableModel); 
        runTableModel.fireTableDataChanged();
	}
	
	
	public void setRunMatrixFromExperiment(Experiment experiment)
	{
		ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
		if(experiment.getRunsDefined() > 0)
		{
			// populate
			try
			{
				matrix.populateFromRunList(experiment.getRuns());
			}
			catch (ExperimentException e)
			{
				throw new GUIException("Unable to prepare run matrix: " + e.getMessage(), e);
			}
		}
		
		setRunMatrix(matrix);
	}
	
	/**
	 * Suitable for displaying runs where the run lists displayed is derived each time
	 * the table is populated, suitable for editing/creating a matrix,not for static display
	 * @param matrix
	 */
	public void setRunMatrix(ExperimentalRunMatrix matrix)
	{
		// store
		runMatrix = matrix;
		deriveRunList = true;
		// populate if possible
		loadTableWithMatrix();
	}
	
	protected boolean deriveRunList;
	
	/**
	 * Suitable for displaying runs
	 * Suitable for static display
	 * @param matrix
	 * @param runList
	 */
	public void setRunMatrix(ExperimentalRunMatrix matrix, LinkedList<ExperimentalRun> runList)
	{
		// store
		runMatrix = matrix;
		experimentalRuns = runList;
		deriveRunList = false;
		// populate if possible
		loadTableWithMatrix();
	}
	
	
	public void loadTableWithMatrix()
	{
		clearTable();
		
		if(runMatrix.isEmpty())
		{
			return; // do nothing
		}
		
		if(deriveRunList)
		{
			// calculate run list
			experimentalRuns = runMatrix.toRunList();
		}
		// convert to matrix
		try
		{
			// [problems]->[algorithms for problem]
			experimentalRunsMatrix = runMatrix.fromFlatRunListToMatrix(experimentalRuns);
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Problem preparing run matrix data: " + e.getMessage(), e);
		}
		
		//
		// populate by algorithm * problems
		//
		if(populateByAlgorithm)
		{
			// headers
			Vector<String> headers = new Vector<String>();
			headers.add("");
			for (int i = 0; i < experimentalRunsMatrix[0].length; i++)
			{
				headers.add(experimentalRunsMatrix[0][i].getAlgorithm().getName());
			}		
			runTableModel.setColumnIdentifiers(headers);
			runTable.getTableHeader().setReorderingAllowed(false);
	        // rows
	        for (int i = 0; i < experimentalRunsMatrix.length; i++)
	        {
	        	Vector<String> row = new Vector<String>();
	        	row.add(experimentalRunsMatrix[i][0].getProblem().getName());
	        	// get all run id's for all algorithms on this problem
	        	for (int j = 0; j < experimentalRunsMatrix[i].length; j++)
				{
	        		row.add(experimentalRunsMatrix[i][j].getId());
	        		//row.add(experimentalRuns[i][j].getAlgorithm().getName()+","+experimentalRuns[i][j].getProblem().getName());
				}        	
	        	runTableModel.addRow(row);
	        }
		}
		else
		{
			// headers
			Vector<String> headers = new Vector<String>();
			headers.add("");
			for (int i = 0; i < experimentalRunsMatrix.length; i++)
			{
				headers.add(experimentalRunsMatrix[i][0].getProblem().getName());
			}		
			runTableModel.setColumnIdentifiers(headers);
			runTable.getTableHeader().setReorderingAllowed(false);
			// rows
	        for (int i = 0; i < experimentalRunsMatrix[0].length; i++)
	        {
	        	Vector<String> row = new Vector<String>();
	        	row.add(experimentalRunsMatrix[0][i].getAlgorithm().getName());
	        	// get all run id's for all problems on this algorithm
	        	for (int j = 0; j < experimentalRunsMatrix.length; j++)
				{
	        		row.add(experimentalRunsMatrix[j][i].getId());
	        		//row.add(experimentalRuns[j][i].getAlgorithm().getName()+","+experimentalRuns[j][i].getProblem().getName());
				}        	
	        	runTableModel.addRow(row);
	        }
		}
	}
	
	
	public ExperimentalRun [] getSelectedRuns()
	{
		LinkedList<String> selectedIds = new LinkedList<String>(); 
		// scan the table for selected things
		for (int i = 0; i < runTable.getRowCount(); i++)
		{
			for (int j = 0; j < runTable.getColumnCount(); j++)
			{
				if(runTable.isCellSelected(i, j))
				{
					// always ignore first column
					if(j != 0)
					{						
						String id = (String) runTableModel.getValueAt(i, j);
						selectedIds.add(id);
					}
				}
			}
		}
		
		if(selectedIds.isEmpty())
		{
			return null;
		}
		
		// get the experiments for id
		ArrayList<ExperimentalRun> selectedExperiments = new ArrayList<ExperimentalRun>();
		for (String id : selectedIds)
		{
			boolean found = false;
			
			for (int i = 0; !found && i < experimentalRuns.size(); i++)
			{
				if(experimentalRuns.get(i).getId().equals(id))
				{
					selectedExperiments.add(experimentalRuns.get(i));
					found = true; // stop searching
				}
			}
			if(!found)
			{
				throw new GUIException("Unable to locate run with id: " + id);
			}
		}
		
		return selectedExperiments.toArray(new ExperimentalRun[selectedExperiments.size()]);
	}
    
    	
	
	public boolean isPopulateByAlgorithm()
	{
		return populateByAlgorithm;
	}

	public void setPopulateByAlgorithm(boolean populateByAlgorithm)
	{
		this.populateByAlgorithm = populateByAlgorithm;
	}

	public static void main(String[] args)
	{
		try
		{
			// create a dummy matrix
			ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
			matrix.setRepeats(30);
			// algorithms				
			matrix.addAlgorithm(new EvolutionStrategies());
			matrix.addAlgorithm(new ParallelHillclimbingAlgorithm());
			matrix.addAlgorithm(new EvolutionaryProgramming());
			// problems
			matrix.addProblem(new TestFunctionF1());
			matrix.addProblem(new TestFunctionF2());
			matrix.addProblem(new TestFunctionF3());
			matrix.addProblem(new TestFunctionF4());
			matrix.addProblem(new TestFunctionF5());
			
			
			// list runs for the first experiment
			RunMatrixListPanel panel = new RunMatrixListPanel();			
			// populate
			panel.setPopulateByAlgorithm(true);
			panel.setRunMatrix(matrix);			

			GUIUtils.testJFrame(panel, "Matrix List Panel", new Dimension(640, 480));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}
