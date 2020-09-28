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
package com.oat.stopcondition;

import com.oat.InvalidConfigurationException;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;

/**
 * Type: EvaluationsStopCondition<br/>
 * Date: 30/07/2007<br/>
 * <br/>
 * Description: Listens to solution evaluations, stop condition checks if
 * the number of evaluations equals or exceeds a predefined maximum number of evaluations
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class EvaluationsStopCondition extends GeneticSolutionEvaluatedStopCondition
	implements SolutionEvaluationListener
{
	/**
	 * Maximum number of evaluations supported
	 */
	protected long maxEvaluations = 1000L;
	
	/**
	 * Number of evaluations executed 
	 */
	protected long evaluationsCount;	
	
	public EvaluationsStopCondition(){}
	
	public EvaluationsStopCondition(long aMaxEvaluations)
	{
		setMaxEvaluations(aMaxEvaluations);
	}
	

	@Override
	public String getName()
	{
		return "Total Evaluations";
	}

	@Override
	public boolean mustStopInternal()
	{
		if(evaluationsCount >= maxEvaluations)
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	 public void reset()
	{
		super.reset();
		evaluationsCount = 0;
	}

	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
		if(maxEvaluations<1)
		{
			throw new InvalidConfigurationException("MaxEvaluations must be >=1: " + maxEvaluations);
		}		
	}

	public long getMaxEvaluations()
	{
		return maxEvaluations;
	}

	public void setMaxEvaluations(long maxEvaluations)
	{
		this.maxEvaluations = maxEvaluations;
	}

	public long getEvaluationsCount()
	{
		return evaluationsCount;
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		evaluationsCount++;		
	}

}
