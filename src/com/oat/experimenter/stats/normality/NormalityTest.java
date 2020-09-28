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
package com.oat.experimenter.stats.normality;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import com.oat.experimenter.Reportable;
import com.oat.experimenter.StatisticalHypothesisTest;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;

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
public abstract class NormalityTest
	implements Comparable<NormalityTest>, Reportable, StatisticalHypothesisTest
{	

	/**
	 * Perform a normality evaluation on the provided experimental run summary
	 * @param summary
	 * @throws AnalysisException
	 */
    public void evaluate(RunStatisticSummary summary)
		throws AnalysisException
	{		
		// evaluate
    	evaluate(summary.getRawResults(), summary.getMean(), summary.getStdev());
	}
    
    /**
     * Perform a normality evaluation on the provided experimental run summary
     * @param results
     * @throws AnalysisException
     */
    public void evaluate(double [] results)
		throws AnalysisException
	{		
    	// calculate mean
    	double mean = new Mean().evaluate(results);
    	double stdev = new StandardDeviation().evaluate(results);
		// evaluate
		internalEvaluate(results, mean, stdev);
	}
    
    /**
     * Perform a normality evaluation on the provided experimental run summary
     * 
     * @param results
     * @param mean
     * @param stdev
     * @throws AnalysisException
     */
    public void evaluate(double [] results, double mean, double stdev)
		throws AnalysisException
	{		
		// evaluate
		internalEvaluate(results, mean, stdev);
	}
    
    /**
     * Perform a normality evaluation on the provided experimental run summary
     * 
     * @param results
     * @param mean
     * @param stdev
     * @throws AnalysisException
     */
    protected abstract void internalEvaluate(double [] results, double mean, double stdev) throws AnalysisException;    
    

    
    /**
     * Whether or not the population is normally distributed.
     * Specifically whether or not the population differs from a normal 
     * distribution with a significance of at least 95%
     * @return
     */
    public boolean isNormal()
    {
    	return !canRejectNullHypothesis();
    }
    
	@Override
	public int compareTo(NormalityTest o)
	{
		return getName().compareTo(o.getName());
	}
	
	@Override
	public String nullHypothesisDescription()
	{
		return "Sample population distribution is Normal";
	}
	
    @Override
    public String toString()
    {
        return getName();
    }
}
