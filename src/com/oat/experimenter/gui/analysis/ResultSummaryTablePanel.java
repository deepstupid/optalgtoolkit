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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.gui.ExplortableTablePanel;
import com.oat.gui.GUIException;

/**
 * Description: Dispaly results for a run (means and standard deviations)
 *  
 * Date: 31/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ResultSummaryTablePanel extends ExplortableTablePanel
{
	protected boolean byAlgorithm = true;
	protected NumberFormat f = DecimalFormat.getInstance();
	
	
	public ResultSummaryTablePanel()
	{}	
	
	public void populate(ExperimentalRun [] runs, RunStatisticSummary [] summaries)
	{
		LinkedList<ExperimentalRun> list = new LinkedList<ExperimentalRun>();
		for (int i = 0; i < runs.length; i++)
		{
			list.add(runs[i]);
		}
		
		// use the matrix to do some heavy lifting
		ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
		// extract all problems and algorithms
		try
		{
			matrix.populateFromRunList(list);
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Error preparing matrix: " + e.getMessage(), e);
		}
		// sort algorithms and problems
		Collections.sort(matrix.getAlgorithms());
		Collections.sort(matrix.getProblems());
		// create an ordered run matrix
		ExperimentalRun [][] experimentalRunsMatrix = null;		
		try
		{
			experimentalRunsMatrix = matrix.fromUnorderedFlatRunListToMatrix(list);
		}
		catch (ExperimentException e)
		{
			throw new GUIException("Error preparing matrix: " + e.getMessage(), e);
		}
				
		
		// populate the table
		
		if(byAlgorithm)
		{
			// headers
			Vector<String> headers = new Vector<String>();
			headers.add("");
			for (int i = 0; i < experimentalRunsMatrix[0].length; i++)
			{
				headers.add(experimentalRunsMatrix[0][i].getAlgorithm().getName());
			}		
			setHeaders(headers);
	        // rows
	        for (int i = 0; i < experimentalRunsMatrix.length; i++)
	        {
	        	Vector<String> row = new Vector<String>();
	        	row.add(experimentalRunsMatrix[i][0].getProblem().getName());
	        	// get all run id's for all algorithms on this problem
	        	for (int j = 0; j < experimentalRunsMatrix[i].length; j++)
				{
	        		RunStatisticSummary s = locateSummaryByRunId(experimentalRunsMatrix[i][j].getId(), summaries);
	        		row.add(prepareCellValue(s));
				}        	
	        	addRow(row);
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
			setHeaders(headers);
			// rows
	        for (int i = 0; i < experimentalRunsMatrix[0].length; i++)
	        {
	        	Vector<String> row = new Vector<String>();
	        	row.add(experimentalRunsMatrix[0][i].getAlgorithm().getName());
	        	// get all run id's for all problems on this algorithm
	        	for (int j = 0; j < experimentalRunsMatrix.length; j++)
				{	        		
	        		RunStatisticSummary s = locateSummaryByRunId(experimentalRunsMatrix[j][i].getId(), summaries);
	        		row.add(prepareCellValue(s));
	        		
				}        	
	        	addRow(row);
	        }
		}		
	}
	
	public String prepareCellValue(RunStatisticSummary s)
	{
		return f.format(s.getMean()) + " ("+f.format(s.getStdev())+")";
	}
	

	public static RunStatisticSummary locateSummaryByRunId(String id, RunStatisticSummary [] allSummaries)
	{
		for (int i = 0; i < allSummaries.length; i++)
		{
			if(allSummaries[i].getExperimentalRunName().equals(id))
			{
				return allSummaries[i];
			}
		}
		
		throw new GUIException("Unable to locate summary for run id " + id);
	}

	public boolean isByAlgorithm()
	{
		return byAlgorithm;
	}


	public void setByAlgorithm(boolean byAlgorithm)
	{
		this.byAlgorithm = byAlgorithm;
	}	
}
