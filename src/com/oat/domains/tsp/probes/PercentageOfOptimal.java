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
package com.oat.domains.tsp.probes;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.tsp.TSPProblem;
import com.oat.probes.BestScoreProbe;

/**
 * Description: 
 *  
 * Date: 05/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class PercentageOfOptimal extends BestScoreProbe
{
	protected double percentageOfOptimal;
	
	// state
	protected double optimalTourLength;
	

	@Override
	public Object getProbeObservation()
	{
		return new Double(percentageOfOptimal);		
	}	
	
	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		super.solutionEvaluatedEvent(evaluatedSolution);
		
		if(problem.isBetter(bestScore, optimalTourLength))
		{
			throw new RuntimeException("Solution was found "+bestScore+" that is better than the best known solution " + optimalTourLength);
		}
		percentageOfOptimal = ((bestScore - optimalTourLength) / optimalTourLength) * 100.0;
	}
	
    @Override
    public void initialiseBeforeRun(Problem p, Algorithm a) throws InitialisationException
    {
    	super.initialiseBeforeRun(p ,a);
    	optimalTourLength = ((TSPProblem)problem).getSolutionTourLength();
    }
	
	@Override
    public void reset()
    {
    	super.reset();
    	optimalTourLength = Double.NaN;
    	percentageOfOptimal = Double.NaN;
    }
    
	@Override
	public String getName()
	{
		return "Percentage Of Optimal";
	}

	public double getPercentageOfOptimal()
	{
		return percentageOfOptimal;
	}

	public double getOptimalTourLength()
	{
		return optimalTourLength;
	}
}
