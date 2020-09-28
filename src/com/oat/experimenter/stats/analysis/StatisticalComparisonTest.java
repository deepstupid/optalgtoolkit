/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat.experimenter.stats.analysis;

import com.oat.experimenter.Reportable;
import com.oat.experimenter.StatisticalHypothesisTest;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;

/**
 * Description: A generic tool for analyzing the relationship between populations of results
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
public abstract class StatisticalComparisonTest
	implements Comparable<StatisticalComparisonTest>, Reportable, StatisticalHypothesisTest
{      
    /**
     * Constructor
     */
    public StatisticalComparisonTest()
    {
    	
    }    
    
    public void evaluate(RunStatisticSummary s1, RunStatisticSummary s2)
    	throws AnalysisException
    {
    	if(!supportsTwoPopulations())
    	{	
    		throw new AnalysisException("Two populations are not supported!");
    	}    	
    	// evaluate
    	internalEvaluate(s1, s2);
    }
    
    public void evaluate(RunStatisticSummary [] runStatistics)
    	throws AnalysisException
    {
    	// check if only two statistics were provided
    	if(runStatistics.length == 2 && supportsTwoPopulations())
    	{
    		evaluate(runStatistics[0], runStatistics[1]);
    		return;
    	}
    	if(!supportsNPopulations())
    	{	
    		throw new AnalysisException("More than two populations are not supported!");
    	}    	
    	// evaluate
    	internalEvaluate(runStatistics);
    }
    
    /**
     * Calculate statistics for the two provided populations
     * @param s1
     * @param s2
     */
    protected abstract void internalEvaluate(RunStatisticSummary s1, RunStatisticSummary s2) throws AnalysisException; 
    
    /**
     * Calculate statistics for the set of provided populations
     * @param s
     */
    protected abstract void internalEvaluate(RunStatisticSummary [] s) throws AnalysisException;
    
    /**
     * Whether or not this tool supports two populations of results
     * @return
     */
    public boolean supportsTwoPopulations()
    {
        return true;
    }
    
    /**
     * Whether or not this tool supports >2 populations of results
     * @return
     */
    public boolean supportsNPopulations()
    {
        return true;
    }
    
      
    
    @Override
    public String toString()
    {
        return getName();
    }

	@Override
	public int compareTo(StatisticalComparisonTest o)
	{
		return getName().compareTo(o.getName());
	}  
	
	@Override
	public String nullHypothesisDescription()
	{
		return "Sample populations have the same distribution";
	}
	
	/**
	 * Whether or not the populations are different (NULL hypothesis can be rejected with a pValue of 0.05)
	 * @return
	 */
	public boolean isPopulationsDifferent()
	{
		return canRejectNullHypothesis();
	}
}
