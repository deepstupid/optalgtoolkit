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
package com.oat.experimenter.examples;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;

/**
 * Description: Example of pair-wise non-parametric comparisons between samples
 *  
 * Date: 27/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExamplePairwiseNonParametric2
{
	
	public static void main(String[] args)
	{
		
		// load results from file		
		String [] filenames = new String[]
		{
				"experiments/file1.csv", 
				"experiments/file2.csv", 
				"experiments/file3.csv"
		};	
		
		// load and calculate basic stats
		int selectedProble = 0;
		RunStatisticSummary [] results = new RunStatisticSummary[filenames.length];
/*		for (int i = 0; i < results.length; i++)
		{
			results[i] = new RunStatisticSummary();
			try
			{
				results[i].calculate(filenames[i], selectedProble);
			}
			catch (AnalysisException e)
			{
				throw new RuntimeException("Something unexpected happened in the example: " + e.getMessage(), e);
			}
		}*/
		
    	
		// create some random results that differ
		// 3 'files'
		double [][][] rawResults = new double[results.length][][]; 
		Random r = new Random(1);		
		for (int file = 0; file < rawResults.length; file++)
		{
			// 30 'lines'
			double fileOffset = (file+1) * 10;
			rawResults[file] = new double[30][];
			for (int line = 0; line < rawResults[file].length; line++) // lines
			{
				// 4 'variables' per line
				rawResults[file][line] = new double[4];
				for (int variable = 0; variable < rawResults[file][line].length; variable++)
				{
					rawResults[file][line][variable] = (r.nextDouble() * fileOffset);
				}
			}
		}
		
		for (int i = 0; i < results.length; i++)
		{
			results[i] = new RunStatisticSummary();
			try
			{
				results[i].calculate(rawResults[i], "R"+i, selectedProble);				
			}
			catch (AnalysisException e)
			{
				throw new RuntimeException("Something unexpected happened in the example: " + e.getMessage(), e);
			}			
			
			System.out.println("> Mean: " + results[i].getMean());
		}
		
    	// calculate stats
		ExamplePairwiseNonParametric2 example = new ExamplePairwiseNonParametric2();
    	example.pairwiseMatrixComparisonParametric(results);
	}
	
    public void pairwiseMatrixComparisonParametric(RunStatisticSummary [] results)
    {    	
    	// do some stats
    	MannWhitneyUTest [][] testsResilts = executeTests(results);
    	
    	// print 'is different' matrix
    	System.out.println("is-different matrix");
    	for (int x = 0; x < testsResilts.length; x++)
		{
			for (int y = 0; y < testsResilts[x].length; y++)
			{
				System.out.print(testsResilts[x][y].isPopulationsDifferent() + ", ");
			}
			System.out.println();
		}
    	
    	System.out.println();  
    	
    	// print p-value matrix
    	System.out.println("p-value matrix");
    	NumberFormat f = DecimalFormat.getInstance();
    	for (int x = 0; x < testsResilts.length; x++)
		{
			for (int y = 0; y < testsResilts[x].length; y++)
			{
				System.out.print(f.format(testsResilts[x][y].getPValue()) + ", ");
			}
			System.out.println();
		}
    }
    
    
    
    public MannWhitneyUTest [][] executeTests(RunStatisticSummary [] results)
    {
    	MannWhitneyUTest [][] tests = new MannWhitneyUTest[results.length][results.length];
    	
    	// across the top
    	for (int x = 0; x < tests.length; x++)
		{
			for (int y = 0; y < tests[x].length; y++)
			{
				tests[x][y] = new MannWhitneyUTest();
				try
				{
					tests[x][y].evaluate(results[x].getRawResults(), results[y].getRawResults());
				}
				catch (AnalysisException e)
				{
					throw new RuntimeException("Something unexpected happened in the example: " + e.getMessage(), e);
				}
			}
		}
    	
    	return tests;
    }
}
