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
package com.oat.domains.tsp.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.domains.tsp.TSPUtils;
import com.oat.utils.EvolutionUtils;

/**
 * Type: GeneticAlgorithm<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description: Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000.
 * <br/>
 * @author Jason Brownlee
 * 
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   Jbrownlee   Random moved to method variable rather than instance variable
 * 09/01/2007   JBrownlee   Modified to use generic elitism strategy
 *           
 * </pre>
 */
public class GeneticAlgorithm extends Algorithm
	implements AutomaticallyConfigurableAlgorithm
{        
    protected long seed;
    protected double mutation;
    protected int popsize;
    protected int boutSize;
    protected int elitism;
    
    
    
    public GeneticAlgorithm()
    {
    	automaticConfiguration(100, this);
    }   
    
    @Override
    public void automaticallyConfigure(Problem problem)
    {
    	automaticConfiguration(((TSPProblem)problem).getTotalCities(), this);
    }
    
    public static void automaticConfiguration(int numComponents, GeneticAlgorithm algorithm)
    {
    	algorithm.setSeed(System.currentTimeMillis());
    	algorithm.setMutation(1.0 / numComponents); // 1 in N
    	algorithm.setPopsize(Math.min(numComponents, 200)); // N
    	algorithm.setBoutSize(2); // small, binary
    	algorithm.setElitism(0); // off
    }
    
    
    
    @Override
    public String getDetails()
    {
        return "As described in Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000." +
        "using a permutation representation, " +
        "population is initialised with mutated version of the nearest neighbour solution, " +
        "uses tournament selection without re-selection, " +
        "using edge recombination, " +
        "using 2-opt mutation (always accept mutated solution).";
    }

    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {              
        Random r = new Random(seed);
        TSPSolution start = TSPUtils.generateNearestNeighbourSolution((TSPProblem)p, r);
        LinkedList<TSPSolution> pop = new  LinkedList<TSPSolution>();
        pop.add(start);
        // generate initial population
        while(pop.size() < popsize)
        {
            TSPSolution s = new TSPSolution(start);
            EvolutionUtils.mutatePermutation(s.getPermutation(),r,mutation);
            pop.add(s);
        } 
        
        // evaluate
        p.cost(pop);        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p, pop);
            // select
            int numToSelect = ((popsize%2)==0) ? popsize : popsize + 1; // must select an even number of parents
            LinkedList<TSPSolution> selected = EvolutionUtils.tournamentSelection(pop, numToSelect, p, r, boutSize);        
            // reproduce
            LinkedList<TSPSolution> children = reproduce(selected, popsize-elitism, r);
            // evaluate
            p.cost(children);
            // add the best solutions from the last generation to the next generation
            if(elitism > 0)
            {
                EvolutionUtils.elitism(pop, children, popsize, p);
            }
            // replace the last generation with the next generation
            pop = children;
        }
    }

    
    public LinkedList<TSPSolution> reproduce(LinkedList<TSPSolution> pop, int totalChildren, Random r)
    {             
        LinkedList<TSPSolution> children = new LinkedList<TSPSolution>();
        
        // always expect two children from two parents
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            TSPSolution p1 = pop.get(i);
            TSPSolution p2 = pop.get(i+1);
            
            for (int j = 0; children.size()<totalChildren && j < 2; j++)
            {
                TSPSolution c = TSPUtils.edgeRecombination(p1, p2, r);
                TSPUtils.twoOpt(c.getPermutation(), r, mutation);
                children.add(c);
            }
        }
        
        return children;
    }    

    @Override
    public String getName()
    {
        return "Genetic Algorithm (GA)";
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
        // boutsize
        if(boutSize>popsize||boutSize<0)
        {
            throw new InvalidConfigurationException("Invalid boutSize " + boutSize);
        }
        // elitism
        if(elitism>popsize||elitism<0)
        {
            throw new InvalidConfigurationException("Invalid elitism " + elitism);
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


    public int getBoutSize()
    {
        return boutSize;
    }


    public void setBoutSize(int boutSize)
    {
        this.boutSize = boutSize;
    }


    public int getElitism()
    {
        return elitism;
    }


    public void setElitism(int elitism)
    {
        this.elitism = elitism;
    }
}

