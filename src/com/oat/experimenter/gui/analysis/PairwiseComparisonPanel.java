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
import java.util.LinkedList;
import java.util.Vector;

import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.gui.ExplortableTablePanel;
import com.oat.gui.GUIException;
import com.oat.utils.BeanUtils;

/**
 * Description: 
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
public class PairwiseComparisonPanel extends ExplortableTablePanel
{
	protected NumberFormat f = DecimalFormat.getInstance();
	
	/**
	 * Default true, expects to compare n algorithms on a problem, if false
	 * it compares n problems on an algorithm
	 */
	protected boolean compareAlgorithms = true;
	
	
	public PairwiseComparisonPanel()
	{}
	
	
	public void populate(
			ExperimentalRun [] runs,
			RunStatisticSummary [] summaries,
			StatisticalComparisonTest statisticalTest)
		throws GUIException
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
		
		// validate the mode
		if(compareAlgorithms)
		{
			if(matrix.getProblems().size() > 1 || matrix.getAlgorithms().size() < 2)			
			{			
				throw new GUIException("Must select runs across multiple algorithms on one problem.");
			}
		}
		else
		{
			if(matrix.getAlgorithms().size() > 1 || matrix.getProblems().size() < 2)			
			{			
				throw new GUIException("Must select runs across multiple problems on one algorithm.");
			}
		}				
		// validate the statistical test
		if(!statisticalTest.supportsTwoPopulations())
		{
			throw new GUIException("The selected statistical test does not support 2 populations");
		}	
		
		populateInternal(runs, summaries, statisticalTest);
	}
	
	
	protected void populateInternal(
			ExperimentalRun [] runs,
			RunStatisticSummary [] summaries,
			StatisticalComparisonTest statisticalTest)
	{		
		// runs id's as headers
		Vector<String> headers = new Vector<String>();
		headers.add("");
		for (int i = 0; i < runs.length; i++)
		{
			headers.add(runs[i].getId());
		}			
		setHeaders(headers);
		
		// rows
		for (int i = 0; i < runs.length; i++)
		{
			Vector<String> v = new Vector<String>();
			// header for the row
			v.add(runs[i].getId());
			
			// columns
			for (int j = 0; j < runs.length; j++)
			{
				if(j<=i)
				{
					v.add("");
				}
				else
				{
					StatisticalComparisonTest test = executeTest(statisticalTest, summaries[i], summaries[j]);
					if(test == null)
					{
						v.add("ERROR");
					}
					else
					{
						v.add(prepareCellValue(test));
					}
				}
			}
			
			addRow(v);
		}
					
	}
	
	public String prepareCellValue(StatisticalComparisonTest test)
	{
		return test.isPopulationsDifferent() + " ("+f.format(test.getPValue())+")";
	}
	
	protected StatisticalComparisonTest executeTest(
			StatisticalComparisonTest aTestPrototype, 
			RunStatisticSummary s1,
			RunStatisticSummary s2)
	{
		StatisticalComparisonTest test = BeanUtils.beanCopy(aTestPrototype);
		try
		{
			test.evaluate(s1 ,s2);
		}
		catch (AnalysisException e)
		{
			return null;
		}
		return test;
	}
	


	public boolean isCompareAlgorithms()
	{
		return compareAlgorithms;
	}


	public void setCompareAlgorithms(boolean compareAlgorithms)
	{
		this.compareAlgorithms = compareAlgorithms;
	}
	
	
	
}
