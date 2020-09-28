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
package com.oat.stopcondition;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;

/**
 * Description: Stop condition that triggers if there is a lack of improvement for a set
 * number of evaluations 
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
public class LackOfImprovementStopCondition extends
		GeneticSolutionEvaluatedStopCondition
{
	/**
	 * The number of evaluations to consider for convergence
	 */
	protected int windowSize = 1000;
	
	// state
	protected long countSinceLastImprovement;
	protected double lastBestScore;
	protected Problem problem;
	
	
	public LackOfImprovementStopCondition()
	{}
	
	public LackOfImprovementStopCondition(int aWindowSize)
	{
		setWindowSize(aWindowSize);
	}
	

	@Override
	public String getName()
	{
		return "No Improvement (Evaluations)";
	}

	@Override
	public boolean mustStopInternal()
	{
		if(countSinceLastImprovement >= windowSize)
		{
			return true;
		}
		
		// do not stop
		return false;
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		double s = evaluatedSolution.getScore();
		
		// check for no recorded score or the new score is better than anything ever
		if(Double.isNaN(lastBestScore) || problem.isBetter(s, lastBestScore))
		{
			// reset the counter
			countSinceLastImprovement = 0;
			// remember the best score
			lastBestScore = s;
		}
		else
		{
			// no improvement
			countSinceLastImprovement++;
		}
	}
	
	@Override
    public void initialiseBeforeRun(Problem p, Algorithm a)
		throws InitialisationException
	{
		super.initialiseBeforeRun(p, a);
		problem = p;
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
		problem = null;
		countSinceLastImprovement = 0;
		lastBestScore = Double.NaN;
	}
	

	@Override
    public void validateConfiguration() throws InvalidConfigurationException
    {
		if(windowSize<2)
		{
			throw new InvalidConfigurationException("Window size must be >= 2: " + windowSize);
		}
    }

	
	
	
	
	public int getWindowSize()
	{
		return windowSize;
	}

	public void setWindowSize(int windowSize)
	{
		this.windowSize = windowSize;
	}
}
