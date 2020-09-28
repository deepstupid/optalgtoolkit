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
public class ExamplePairwiseNonParametric
{
	
	public static void main(String[] args)
	{
		
		// load results from file		
		/*
		String [] filenames = new String[]
		{
				"experiments/file1.csv", 
				"experiments/file2.csv", 
				"experiments/file3.csv"
		};		
    	// load the files
    	// each file contains n repeats (lines), with m probes (variables on a line)
    	// [file][lines][variables]		
    	double [][][] results = new double [filenames.length][][];    	
    	for (int i = 0; i < results.length; i++)
		{
    		try
			{
				results[i] = FileUtils.loadCSV(filenames[i]);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Something unexpected happened in the example: " + e.getMessage(), e);
			}
		}*/
		
    	
		// create some random results that differ
		// 3 'files'		
		double [][][] results = new double[3][][]; 
		Random r = new Random(1);		
		for (int file = 0; file < results.length; file++)
		{
			// 30 'lines'
			double fileOffset = (file+1) * 10;
			results[file] = new double[30][];
			for (int line = 0; line < results[file].length; line++) // lines
			{
				// 4 'variables' per line
				results[file][line] = new double[4];
				for (int variable = 0; variable < results[file][line].length; variable++)
				{
					results[file][line][variable] = (r.nextDouble() * fileOffset);
				}
			}
		}
		
		
    	// select one of the probes to do a test on
    	int selectedProble = 0;
    	// calculate stats
		ExamplePairwiseNonParametric example = new ExamplePairwiseNonParametric();
    	example.pairwiseMatrixComparisonParametric(results, selectedProble);
	}
	
    public void pairwiseMatrixComparisonParametric(double [][][] results, int selectedProble)
    {
    	// convert into samples we can work with
    	double [][] samples = new double[results.length][];
    	for (int file = 0; file < samples.length; file++)
		{
    		samples[file] = new double[results[file].length];
    		// process all lines of the current file getting the variable we are interested in
    		for (int line = 0; line < samples[file].length; line++)
			{
    			samples[file][line] = results[file][line][selectedProble];
			}
		}
    	
    	// do some stats
    	MannWhitneyUTest [][] testsResilts = pairwiseMatrixComparisonParametric(samples);
    	
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
    
    
    
    public MannWhitneyUTest [][] pairwiseMatrixComparisonParametric(double [][] results)
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
					tests[x][y].evaluate(results[x], results[y]);
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
