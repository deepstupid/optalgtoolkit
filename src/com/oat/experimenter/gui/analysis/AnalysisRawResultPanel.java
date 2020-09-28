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

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.LinkedList;

import javax.swing.BorderFactory;

import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.experimenter.RunResult;
import com.oat.gui.ExplortableTablePanel;
import com.oat.utils.GUIUtils;

/**
 * Description: Display raw results 
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
public class AnalysisRawResultPanel extends ExplortableTablePanel
{       
    protected RunResult [] runResults;
	
	
	public AnalysisRawResultPanel()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Raw Results"));		
	}
	
	@Override
	public void clear()
	{
		super.clear();
		runResults = null;
	}
    
	public void populateWithData(RunResult [] results)	
	{
		runResults = results;
		// presume all rows have data in the same order
		String [] header = results[0].toHeaderStrings();
		Object [][] data = new Object[results.length][];
		for (int i = 0; i < data.length; i++)
		{
			data[i] = results[i].toResultObjects();
		}
		populateWithData(data, header);
	}
	
	
	
	
	public RunResult[] getRunResults()
	{
		return runResults;
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
			// load a result
			LinkedList<ExperimentalRun> runs = experiments[0].getRuns();
			ExperimentalRun selectedRun = null;
			// find a completed run
			for(ExperimentalRun run : runs)
			{
				if(run.isCompleted())
				{
					selectedRun = run;
					break;
				}
			}
			if(selectedRun == null)
			{
				throw new RuntimeException("Unable to find a completed run in the first experiment!"); 
			}
			
			RunResult [] results = ExperimentalRunUtils.loadRunResult(experiments[0], selectedRun);
			
			AnalysisRawResultPanel panel = new AnalysisRawResultPanel();
			panel.populateWithData(results);
			GUIUtils.testJFrame(panel, "Raw Results", new Dimension(800, 600));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
