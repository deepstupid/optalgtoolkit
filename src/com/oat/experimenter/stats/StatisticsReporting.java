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
package com.oat.experimenter.stats;

import java.text.DecimalFormat;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.experimenter.ExperimentalRunMatrix;

/**
 * Description: 
 *  
 * Date: 22/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class StatisticsReporting
{
    
    /**
     * Returns a matrix of results of algorithms by problems with means and standard deviations
     * @param summary
     * @param matrix
     * @return
     */
    public static String getSummaryStatisticReport(
    		RunStatisticSummary [][] summary, 
    		ExperimentalRunMatrix matrix)
    {
    	StringBuffer b = new StringBuffer(5*1024);
    	DecimalFormat f = (DecimalFormat) DecimalFormat.getInstance();    	
    	
		LinkedList<Problem> problems = matrix.getProblems();
		LinkedList<Algorithm> algorithms = matrix.getAlgorithms();
		
		// header line for all algorithms
		b.append("\t"); // for the problem title column
		for (int i = 0; i < algorithms.size(); i++)
		{			
			b.append(algorithms.get(i).getClass().getSimpleName());
			if(i!=algorithms.size()-1)
			{
				b.append("\t");
			}
		}
		b.append("\n");
		// process all problems
		for (int i = 0; i < summary.length; i++)
		{
			b.append(problems.get(i).getClass().getSimpleName());
			b.append("\t");
			// process all algorithm results for problem
			for (int j = 0; j < summary[i].length; j++)
			{
				b.append(f.format(summary[i][j].getMean()));
				b.append(" (");
				b.append(f.format(summary[i][j].getStdev()));
				b.append(")");
				if(j!=summary[i].length-1)
				{
					b.append("\t");
				}
			}
			if(i!=summary.length-1)
			{
				b.append("\n");
			}					
		}
		
		return b.toString();
    }
       
    
    public static String reportToString(String [][] report)
    {
    	StringBuffer b = new StringBuffer();

    	for (int i = 0; i < report.length; i++)
		{
			b.append(report[i][0]);
			b.append(":\t");
			b.append(report[i][1]);
			b.append("\n");
		}
    	
    	return b.toString();
    }
}
