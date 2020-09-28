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
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.gui.GenericDefaultTableModel;
import com.oat.utils.GUIUtils;

/**
 * Description: A list of experimental runs for an experiment
 *  
 * Date: 24/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class RunListPanel extends JPanel
	implements ListSelectionListener
{
    public final static String [] TABLE_HEADINGS = 
    {
    	"Id.", 
    	"Problem", 
    	"Algorithm",
    	"Repeats", 
    	"Completed"
    };
    
    /**
     * Table of experimental runs
     */
    protected JTable runTable;
    /**
     * Data model for the table
     */
    protected GenericDefaultTableModel runTableModel;
    /**
     * Experiment that contains all the runs
     */
    protected Experiment experiment;    
	
	/**
	 * Prepare the list with the runs in the specified experiment
	 * @param aExperiment
	 */
	public RunListPanel(Experiment aExperiment)
	{
		this();
		populateWithExperiment(aExperiment);
	}
	
	/**
	 * Default
	 */
	public RunListPanel()
	{
		prepareGUI();
	}	
	
	/**
	 * Prepare the GUI
	 */
	protected void prepareGUI()
	{        
        runTableModel = new GenericDefaultTableModel();
        runTableModel.setColumnIdentifiers(TABLE_HEADINGS);        
        runTable = new JTable(runTableModel);
        runTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        runTable.getSelectionModel().addListSelectionListener(this);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(runTable), BorderLayout.CENTER);  
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Experimental Run List"));
	}
	
	/**
	 * Loads all experimental runs from the experiment and populates the table
	 */
	public void loadAllRunsIntoTable()
	{
		// do nothing but refresh
		refreshTableDetails();
	}
	
	/**
	 * Adds the provided experimental run as a row to the table
	 * @param e
	 */
    protected void addRow(ExperimentalRun r)
    {
        Vector<Object> v = new Vector<Object>(TABLE_HEADINGS.length);        
        
        v.add(r.getId());
        v.add(r.getProblem().getName());
        v.add(r.getAlgorithm().getName());
        v.add(r.getRepeats());
        //v.add((r.getCompletionDate()==null) ? null : r.getCompletionDate().toString());
        v.add(r.getCompletionDate());
        
        runTableModel.addRow(v);
    }   
   
	/**
	 * Deletes all rows in the table and re-populates the table with the current experimental run list
	 */
	public void refreshTableDetails()
	{
		// clear the table
		clearTable();
        // sort by name
		LinkedList<ExperimentalRun> runList = experiment.getRuns();
        Collections.sort(runList);
        // display
        for (ExperimentalRun run : runList)
        {
            addRow(run);
        }
	}
	
	/**
	 * Empties the table of all data 
	 */
	public void clearTable()
	{
		//  ensure no selection change event is triggered
        runTable.clearSelection();        
        // clear
        while(runTableModel.getRowCount()!=0)
        {
        	runTableModel.removeRow(0);
        }
	}		
	
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        int row = runTable.getSelectedRow();
        if(row != -1)
        {
            // TODO respond to the selection change
        }
    } 
    
    /**
     * Return the selected experimental run
     * If more than one experiment is selected,
     * 
     * @return
     */
    public ExperimentalRun [] getSelectedRuns()
    {
    	int [] selections = runTable.getSelectedRows();
    	
    	if(selections == null || selections.length==0)
    	{
    		return null;
    	}
    	
    	ExperimentalRun [] runs = new ExperimentalRun[selections.length];
    	for (int i = 0; i < runs.length; i++)
		{
    		runs[i] = experiment.getRuns().get(selections[i]);
		}
    	return runs;
    }
    
    public Experiment getExperiment()
	{
		return experiment;
	}

	public void populateWithExperiment(Experiment aExperiment)
	{
		experiment = aExperiment;
		loadAllRunsIntoTable();
	}

	/**
	 * Test
	 * 
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
			// list runs for the first experiment
			RunListPanel panel = new RunListPanel(experiments[0]);
			
			GUIUtils.testJFrame(panel, "Experimental Run List", new Dimension(640, 480));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
