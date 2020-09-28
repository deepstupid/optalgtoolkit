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
 * Type: GreedySearch<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description: Simple mutation based greedy search
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   Jbrownlee   Modified to be simpler, use utilities for elite selection
 *                          Random moved to method variable rather than instance variable
 * 
 * </pre>
 */
public class GreedySearch extends Algorithm
{
    // parameters
    protected long seed = System.currentTimeMillis();    
    protected double mutation = 0.005;
    protected int popsize = 100;
    
    
    @Override
    public String getDetails()
    {
        return 
        "Starts with a nearest neighbour solution and a population of mutated variations, " +
        "uses swap mutation to produce progeny, only accepting the n best solutions from the union of parents and children populations.";
    }
    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // mutation
        if(mutation>1||mutation<0)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutation);
        }
        // popsize
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {        
        // initial solution is a greedy solution
        Random r = new Random(seed);  
        TSPSolution start = TSPUtils.generateNearestNeighbourSolution((TSPProblem)p, r);
        LinkedList<TSPSolution> pop = new  LinkedList<TSPSolution>();
        pop.add(start);
        // generate initial population
        while(pop.size() < popsize)
        {
            TSPSolution s = new TSPSolution(start);
            EvolutionUtils.mutatePermutation(s.getPermutation(), r, mutation);
            pop.add(s);
        }
        p.cost(pop);        
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            LinkedList<TSPSolution> tmp = new LinkedList<TSPSolution>();
            for (int i = 0; i < pop.size(); i++)
            {
                TSPSolution s = new TSPSolution(pop.get(i));
                EvolutionUtils.mutatePermutation(s.getPermutation(),r,mutation);
                tmp.add(s);
            }
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
        return "Greedy Search";
    }


    public long getSeed()
    {
        return seed;
    }


    public void setSeed(long seed)
    {
        this.seed = seed;
    }


    public double getMutation()
    {
        return mutation;
    }


    public void setMutation(double mutation)
    {
        this.mutation = mutation;
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
