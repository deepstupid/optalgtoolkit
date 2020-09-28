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
package com.oat.probes;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;

/**
 * 
 * Description: Collects the best solution of a run
 *  
 * Date: 03/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BestSolutionProbe extends GenericSolutionEvaluatedProbe
{
    protected Solution bestSolution;
    protected Problem problem;
    
	@Override
	public String getName()
	{
		return "Best Solution";
	}

	@Override
	public Object getProbeObservation()
	{
		return bestSolution;
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		try
		{
			if(bestSolution==null || problem.isBetter(evaluatedSolution, bestSolution))
			{
				bestSolution = evaluatedSolution;
			}
		}
		catch (SolutionEvaluationException e)
		{
			throw new RuntimeException("Unexpected event, notified of an unevaluated solution : " + evaluatedSolution);
		}			
	}
    
	@Override
    public void reset()
    {
    	super.reset();
    	bestSolution = null;
    	problem = null;
    }
    
    
    @Override
    public void initialiseBeforeRun(Problem p, Algorithm a) throws InitialisationException
    {
    	super.initialiseBeforeRun(p ,a);
    	problem = p;
    }

	

	public Solution getBestSolution()
	{
		return bestSolution;
	}

	public Problem getProblem()
	{
		return problem;
	}    
}
