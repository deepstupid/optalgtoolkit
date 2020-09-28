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
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.gui.GUIException;
import com.oat.gui.GenericDefaultTableModel;
import com.oat.utils.GUIUtils;

/**
 * Description: A list of experiments
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
public class ExperimentListPanel extends JPanel
	implements ListSelectionListener
{
    public final static String [] TABLE_HEADINGS = 
    {
    	"Name", 
    	"Domain", 
    	"Date", 
    	"Runs Defined", 
    	"Runs Completed"
    };
    
    /**
     * Table of experiments
     */
    protected JTable experimentTable;
    /**
     * Data model for the table
     */
    protected GenericDefaultTableModel experimentTableModel;
    /**
     * Base or home directory for all experiments
     */
    protected File homeDirectory;    
    /**
     * List of all experiments in the table
     */
    protected LinkedList<Experiment> experiments;
	
	/**
	 * Prepare the list with the home directory where all experiments are located
	 * @param aHomeDirectory
	 */
	public ExperimentListPanel(File aHomeDirectory)
	{
		homeDirectory = aHomeDirectory;
		experiments = new LinkedList<Experiment>();
		prepareGUI();
		// make an attempt to load the experiments
		loadAllExperimentsIntoTable();
	}
	
	/**
	 * Prepare the GUI
	 */
	protected void prepareGUI()
	{        
        experimentTableModel = new GenericDefaultTableModel();
        experimentTableModel.setColumnIdentifiers(TABLE_HEADINGS);        
        experimentTable = new JTable(experimentTableModel);
        experimentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        experimentTable.getSelectionModel().addListSelectionListener(this);        
        
        setLayout(new BorderLayout());
        add(new JScrollPane(experimentTable), BorderLayout.CENTER);  
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Experiment List"));
	}
	
	/**
	 * Loads all experiments from the home directory and populates the table
	 */
	public void loadAllExperimentsIntoTable()
	{
        Experiment [] eList = null;
        try
		{
			eList = ExperimentUtils.loadExperiments(homeDirectory);
		} 
        catch (ExperimentException e1)
		{
        	throw new GUIException("Error loading experiments: " + e1.getMessage(), e1);
		}
        // clear the list
        experiments.clear();
        // build the list
        for (Experiment e : eList)
        {
            experiments.add(e);
        }
        // refresh the table
        refreshTableDetails();
	}
	
	/**
	 * Adds the provided experiment as a row to the table
	 * @param e
	 */
    protected void addRow(Experiment e)
    {
        Vector<Object> v = new Vector<Object>(TABLE_HEADINGS.length);        
        
        v.add(e.getName());
        v.add(e.getDomain());
        v.add((e.getLastModified()!=null) ? e.getLastModified() : "");
        v.add(e.getRunsDefined());
        v.add(e.getRunsCompleted());
        
        experimentTableModel.addRow(v);
    }   
   
	/**
	 * Deletes all rows in the table and re-populates the table with the current experiment list
	 */
	public void refreshTableDetails()
	{
		// clear the table
		clearTable();
        // sort by name
        Collections.sort(experiments);
        // display
        for (Experiment e : experiments)
        {
            addRow(e);
        }
	}
	
	/**
	 * Empties the table of all data 
	 */
	public void clearTable()
	{
		//  ensure no selection change event is triggered
        experimentTable.clearSelection();        
        // clear
        while(experimentTableModel.getRowCount()!=0)
        {
        	experimentTableModel.removeRow(0);
        }
	}		
	
	
	public void addListSelectionListener(ListSelectionListener l)
	{
		experimentTable.getSelectionModel().addListSelectionListener(l);
	}
	
	public void removeListSelectionListener(ListSelectionListener l)
	{
		experimentTable.getSelectionModel().removeListSelectionListener(l);
	}
	
	
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        int row = experimentTable.getSelectedRow();
        if(row != -1)
        {
            // TODO respond to the selection change
        }
    } 
    
    /**
     * Return the selected experiment
     * @return
     */
    public Experiment getSelectedExperiment()
    {
        int row = experimentTable.getSelectedRow();
        if(row != -1)
        {
        	return experiments.get(row);
        }
        
        return null;
    }
    
    public File getHomeDirectory()
	{
		return homeDirectory;
	}

	public LinkedList<Experiment> getExperiments()
	{
		return experiments;
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
			File home = ExperimentUtils.getDefaultHomeDirectory();
			ExperimentListPanel panel = new ExperimentListPanel(home);
			
			GUIUtils.testJFrame(panel, "Experiment List", new Dimension(640, 480));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
