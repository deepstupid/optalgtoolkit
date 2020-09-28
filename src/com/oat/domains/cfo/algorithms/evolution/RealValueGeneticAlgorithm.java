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
package com.oat.domains.cfo.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.domains.cfo.CFOUtils;
import com.oat.utils.EvolutionUtils;


/**
 * Type: RealValueGeneticAlgorithm<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description: Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000.
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 09/01/2007   JBrownlee   Updated to use function to generate initial population
 *                          Modified to use generic elitism strategy
 * 
 * </pre>
 */
public class RealValueGeneticAlgorithm extends Algorithm
{        
    protected long seed = System.currentTimeMillis();
    protected double crossover = 0.95;
    protected double mutation = 0.85;
    protected int popsize = 100;
    protected int boutSize = 2;
    protected int elitism = 1;
    protected double stdev = 0.01;
    

    @Override
    public String getDetails()
    {
        return
        "Real-Valued Genetic Algorithm (RVGA): " +
        "as described in \"Real-valued vectors\" by David Fogel in  Evolutionary Computation 1 - Basic Algorithms and Operations (2000). " +
        "Using uniform-random crossover, " +
        "using Gaussian mutation where the stdev parameter is a ratio of the objective function range in each dimension.";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);        

        // prepare initial population
        LinkedList<CFOSolution> pop = CFOUtils.getRandomPopulationReal(r, (CFOProblemInterface)p, popsize);   
        // evaluate
        p.cost(pop);
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // select the parents of the next generation
            int numToSelect = ((popsize%2)==0) ? popsize : popsize + 1; // must select an even number of parents
            LinkedList<CFOSolution> selected = EvolutionUtils.tournamentSelection(pop, numToSelect, p, r, boutSize);        
            // create the solutions of the next generation
            LinkedList<CFOSolution> children = reproduce(selected, popsize-elitism, (CFOProblemInterface)p, r);            
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

    
    public LinkedList<CFOSolution> reproduce(LinkedList<CFOSolution> pop, int totalChildren, CFOProblemInterface p, Random r)
    {
        LinkedList<CFOSolution> children = new LinkedList<CFOSolution>();
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            CFOSolution p1 = pop.get(i);
            CFOSolution p2 = pop.get(i+1);
            // crossover
            double [][] dd = EvolutionUtils.uniformCrossover(p1.getCoordinate(), p2.getCoordinate(), r, crossover);            
            for (int j = 0; children.size()<totalChildren && j < dd.length; j++)
            {
                // mutate
                CFOUtils.realValueGlobalGaussianMutate(dd[j], p, r, mutation, stdev);
                // create
                CFOSolution c = new CFOSolution(dd[j]);
                //add
                children.add(c);
            }
            
        }
        return children;
    }
    

    
    

    @Override
    public String getName()
    {
        return "Real-Value Genetic Algorithm (RVGA)";
    }


    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // crossover
        if(crossover>1||crossover<0)
        {
            throw new InvalidConfigurationException("Invalid crossover" + crossover);
        }
        // mutation
        if(mutation>1||mutation<0)
        {
            throw new InvalidConfigurationException("Invalid mutation" + mutation);
        }
        // pop size
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize" + popsize);
        }
        // bout size
        if(boutSize>popsize||boutSize<=0)
        {
            throw new InvalidConfigurationException("Invalid boutSize" + boutSize);
        }
        // elitism
        if(elitism>popsize||elitism<0)
        {
            throw new InvalidConfigurationException("Invalid elitism" + elitism);
        }
        // stdev
        if(stdev<0)
        {
            throw new InvalidConfigurationException("Invalid stdev" + stdev);
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

    public double getCrossover()
    {
        return crossover;
    }

    public void setCrossover(double crossover)
    {
        this.crossover = crossover;
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

    public double getStdev()
    {
        return stdev;
    }

    public void setStdev(double stdev)
    {
        this.stdev = stdev;
    }
    
    
}

