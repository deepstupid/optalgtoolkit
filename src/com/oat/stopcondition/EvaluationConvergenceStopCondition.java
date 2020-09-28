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

import java.util.LinkedList;

import com.oat.InvalidConfigurationException;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;

/**
 * Description: Stop condition that checks for convergence of evaluations 
 * That is the same evaluation being returned for a set number of evaluations.
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
public class EvaluationConvergenceStopCondition extends GeneticSolutionEvaluatedStopCondition     
{    
	/**
	 * The number of evaluations to consider for convergence
	 */
	protected int windowSize = 1000;
    
	/**
	 * A queue of ordered evaluations, all of which must be equal for the
	 * stop condition to be triggered
	 */
    protected LinkedList<Double> queue = new LinkedList<Double>();
    
    
    
    public EvaluationConvergenceStopCondition()
    {}
    
    public EvaluationConvergenceStopCondition(int aWindowSize)
    {
    	setWindowSize(aWindowSize);
    }
    

	@Override
	public String getName()
	{
		return "Convergence (Evaluations)";
	}

	@Override
	public boolean mustStopInternal()
	{
		// check if the window size has been filled
		if(queue.size() >= windowSize)
		{
			// check the scores
			for (int i = 1; i < windowSize; i++)
			{
				// search for a difference in the scores
				if(!queue.get(i).equals(queue.get(i-1)))
				{
					// at least two scores are different
					return false;
				}
			}
			
			// no difference in the scores
			return true;
		}		
		
		return false;
	}

	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
		if(windowSize<2)
		{
			throw new InvalidConfigurationException("Convergence window size must be >2: " + windowSize);
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

	public LinkedList<Double> getQueue()
	{
		return queue;
	}
	
	
	@Override
	public void reset()
	{
		super.reset();
		queue.clear();
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		// add the score to the end of the queue
		try
		{
			queue.addLast(evaluatedSolution.getScore());
		}
		catch (SolutionEvaluationException e)
		{
			throw new RuntimeException("Unexpected event, notified of an unevaluated solution : " + evaluatedSolution);
		}
		// shrink the queue to size
		while(queue.size() > windowSize)
		{	
			// remove from the front of the queue
			queue.removeFirst();
		}		
	}
	
}
