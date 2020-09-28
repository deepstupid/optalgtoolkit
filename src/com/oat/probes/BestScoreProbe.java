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
 * Description: Collects the best score of a run 
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
public class BestScoreProbe extends GenericSolutionEvaluatedProbe
{
    protected double bestScore;
    protected Problem problem;
    
	@Override
	public String getName()
	{
		return "Best Score";
	}

	@Override
	public Object getProbeObservation()
	{
		return new Double(bestScore);
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		try
		{
			double s = evaluatedSolution.getScore();
			// check for no score
			if(Double.isNaN(bestScore))
			{
				bestScore = s;
			}
			else if(problem.isBetter(s, bestScore))
			{
				bestScore = s;
			}
		}
		catch (SolutionEvaluationException e)
		{
			throw new RuntimeException("Unexpected event, notified of an unevaluated solution : " + evaluatedSolution);
		}
	}
	
	@Override
	public void cleanupAfterRun(Problem p, Algorithm a)	
		throws InitialisationException
	{
		super.cleanupAfterRun(p, a);
		problem = null;
	}
    
	@Override
    public void reset()
    {
    	super.reset();
    	bestScore = Double.NaN;
    	problem = null;
    }
    
    
    @Override
    public void initialiseBeforeRun(Problem p, Algorithm a) throws InitialisationException
    {
    	super.initialiseBeforeRun(p ,a);
    	problem = p;
    }

	public double getBestScore()
	{
		return bestScore;
	}

	public Problem getProblem()
	{
		return problem;
	}    
}
