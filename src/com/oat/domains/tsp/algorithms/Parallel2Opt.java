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
package com.oat.domains.tsp.algorithms;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.domains.tsp.TSPUtils;
import com.oat.utils.EvolutionUtils;

/**
 * Type: Parallel2Opt<br/>
 * Date: 9/07/2006<br/>
 * <br/>
 * Description: Simple two-opt based greedy search
 * <br/>
 * @author Daniel Angus
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   Jbrownlee   Modified to be simpler, use utilities for elite selection
 *                          Fixed explicit score checking, and remvoed iterator usage - 
 *                          did in three lines what was implemented in like 20
 *                          Random moved to method variable rather than instance variable
 *                          Removed mutation parameter, not used
 * </pre>                          
 */
public class Parallel2Opt extends Algorithm
{   
    protected long seed = System.currentTimeMillis();    
    protected int popsize = 100; 
    
    @Override
    public String getDetails()
    {
        return "2-opt with a population of solutions, accept if better.";
    }

    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {             
        Random r = new Random(seed);
        TSPSolution start = TSPUtils.generateNearestNeighbourSolution((TSPProblem)p, r);
        LinkedList<TSPSolution> pop = new LinkedList<TSPSolution>();
        pop.add(start);
        // generate initial population
        while(pop.size() < popsize)
        {
            TSPSolution s = new TSPSolution(start);
            EvolutionUtils.mutatePermutation(s.getPermutation(), r, 0.50); // 50% chance of mutation
            pop.add(s);
        }         
        // evaluate
        p.cost(pop);
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            // notify listeners
            triggerIterationCompleteEvent(p, pop);
        	LinkedList<TSPSolution> tmp = new LinkedList<TSPSolution>();
            // create the next generation
            for(TSPSolution s : pop)
            {
                // create new instance
                TSPSolution child = new TSPSolution(s); // copy constructor
                // mutate with two-opt procedure
                TSPUtils.twoOpt(child.getPermutation(), r, 1.0); // procedure is assured
                // add to population
                tmp.add(child);
            }
            // evaluate the population
            p.cost(tmp);
            // perform elitest selection - only if there is evals left
            if(p.canEvaluate())
            {
                pop.addAll(tmp);
                EvolutionUtils.elitistSelectionStrategy(pop, popsize, p);
            }
        }
    }
    
    @Override
    public String getName()
    {
        return "Parallel 2-opt";
    }

    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // popsize
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
    }
    

    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public int getPopsize()
    {
        return popsize;
    }

    public void setPopsize(int popsize)
    {
        this.popsize = popsize;
    }
}

