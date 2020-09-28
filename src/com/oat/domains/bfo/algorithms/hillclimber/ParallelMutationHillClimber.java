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
package com.oat.domains.bfo.algorithms.hillclimber;

import java.util.Random;

import com.oat.Algorithm;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: ParallelMutationHillClimber<br/>
 * Date: 03/07/2007<br/>
 * <br/>
 * Description: Parallel Mutation Hill Climber, a parallel version of the mutation hill climber
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * 
 * </pre>
 */
public class ParallelMutationHillClimber extends Algorithm
	implements AutomaticallyConfigurableAlgorithm	
{   
    /**
     * Random number seed, default is system time 
     */
    protected long seed;
    /**
     * Mutation rate, default is 0.0333, should be 1/L, where L is string length
     */
    protected double mutationRate;
    /**
     * Population size, should be L, where L is the number of bits
     */
    protected int populationSize;
    
    
    
    public ParallelMutationHillClimber()
    {
    	automaticallyConfigure(30, this);
    }    
            
    @Override
	public void automaticallyConfigure(Problem problem)
	{
    	automaticallyConfigure(((BFOProblemInterface)problem).getBinaryStringLength(), this);		
	}
    
    
    public static void automaticallyConfigure(int stringLength, ParallelMutationHillClimber a)
    {
    	a.setSeed(System.currentTimeMillis());
    	a.setMutationRate(1.0/stringLength);
    	a.setPopulationSize(stringLength);
    }
    
    
    
    
    @Override
    public String getDetails()
    {
        return "A population of points, where each point is independant and replaced if a mutated version produces an improvement. " +
        "Mutation rate should be 1/L, and population size should be L";
    }
    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        if(mutationRate>1||mutationRate<0)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutationRate);
        }
        else if(populationSize<=0)
        {
            throw new InvalidConfigurationException("Invalid populationSize " + populationSize);
        }
    }
    
    
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        // prepare the points
        BFOSolution [] pop = new BFOSolution[populationSize];
        for (int i = 0; i < pop.length; i++)
        {
            pop[i] = new BFOSolution(RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength()));
        }
        p.cost(pop);
        
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // process each member of the population
            for (int i = 0; p.canEvaluate() && i < pop.length; i++)
            {
                // copy
                boolean [] mutant = ArrayUtils.copyArray(pop[i].getBitString());
                // mutate
                EvolutionUtils.binaryMutate(mutant, r, mutationRate);
                // create
                BFOSolution newPoint = new BFOSolution(mutant);
                // evaluate
                p.cost(newPoint);
                // compare, accept if better or the same
                if(p.isBetterOrSame(newPoint, pop[i]))
                {
                    pop[i] = newPoint;
                }
            }
        }
    }


    @Override
    public String getName()
    {
        return "Parallel Mutation Hill Climber";
    }

    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public double getMutationRate()
    {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate)
    {
        this.mutationRate = mutationRate;
    }

    public int getPopulationSize()
    {
        return populationSize;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }    
}
