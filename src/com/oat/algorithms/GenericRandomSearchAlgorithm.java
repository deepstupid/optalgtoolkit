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
package com.oat.algorithms;

import java.util.LinkedList;
import java.util.Random;

import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.EpochAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;

/**
 * Description: Generic random search 
 *  
 * Date: 10/09/2007<br/>
 * @author Jason Brownlee
 * @param <S> 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class GenericRandomSearchAlgorithm<S extends Solution> extends EpochAlgorithm<S>
	implements AutomaticallyConfigurableAlgorithm
{
	// a small number
	protected static final int epochSize = 100;
	
	/**
	 * Seed used to initialize the random number generated for each run
	 */
	protected long seed;
	
	// state
	protected Random rand;
	
	
	
	public GenericRandomSearchAlgorithm()
	{
		automaticallyConfigure(this);
	}
	

	@Override
	public void automaticallyConfigure(Problem problem)
	{
		automaticallyConfigure(this);		
	}
	
	public static void automaticallyConfigure(GenericRandomSearchAlgorithm a)
	{
		a.setSeed(System.currentTimeMillis());
	}

	@Override
	protected LinkedList<S> internalExecuteEpoch(Problem problem,
			LinkedList<S> population)
	{
		LinkedList<S> pop = new LinkedList<S>();
		for (int i = 0; i < epochSize; i++)
		{
			pop.add(generateRandomSolution(rand, problem));
		}		
		return pop;
	}

	@Override
	protected LinkedList<S> internalInitialiseBeforeRun(Problem problem)
	{
		rand = new Random(seed);
		LinkedList<S> pop = new LinkedList<S>();
		for (int i = 0; i < epochSize; i++)
		{
			pop.add(generateRandomSolution(rand, problem));
		}		
		return pop;
	}
	
	protected abstract S generateRandomSolution(Random rand, Problem problem);

	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<S> oldPopulation, LinkedList<S> newPopulation)
	{}

	@Override
	public void validateConfiguration() throws InvalidConfigurationException
	{}

	@Override
	public String getName()
	{
		return "Random Search";
	}	
	
    @Override
    public String getDetails()
    {
        return "Generates random solutions in the domain";
    }

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}	
}
