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
package com.oat.domains.bfo.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.EpochAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.BFOUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: SimpleGeneticAlgorithm<br/>
 * Date: 06/12/2006<br/>
 * <br/>
 * Description: Genetic Algorithm (GA)
 * Copy of the implementation for func opt, modified to work with binary problem 
 * Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000.
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 09/01/2007   JBrownlee   Modified to use generic elitism strategy
 * 20/08/2007	JBrownlee	Updated to support automatic configuration
 * </pre>
 */
public class GeneticAlgorithm extends EpochAlgorithm<BFOSolution>
	implements AutomaticallyConfigurableAlgorithm
{    
	// user parameters
    protected long seed;
    protected double crossover;
    protected double mutation;
    protected int popsize;
    protected int boutSize;
    protected int elitism;    
    
    // state
    protected Random rand;
    
    
    public GeneticAlgorithm()
    {
    	// default configuration
    	automaticConfiguration(128, this);
    }
    
    @Override
    public String getDetails()
    {
    	StringBuffer b = new StringBuffer();
    	b.append(getName()+"\n");
    	b.append("As described in Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000.\n");
    	b.append("Tournament Selection without reselection, One-point crossover.");    	
    	return b.toString();    	
    }
  
    @Override
    public void automaticallyConfigure(Problem problem)
    {
    	automaticConfiguration(((BFOProblemInterface)problem).getBinaryStringLength(), this);
    }
    
    public static void automaticConfiguration(int stringLength, GeneticAlgorithm algorithm)
    {
    	algorithm.setSeed(System.currentTimeMillis());
    	algorithm.setCrossover(0.98);
    	algorithm.setPopsize(stringLength);
    	algorithm.setMutation(1.0 / stringLength);
    	algorithm.setBoutSize(2);
    	algorithm.setElitism(0);
    }
    
    @Override
	protected LinkedList<BFOSolution> internalExecuteEpoch(Problem problem, LinkedList<BFOSolution> population)    
	{        
        // select
        int numToSelect = ((popsize%2)==0) ? popsize : popsize + 1; // must select an even number of parents
        LinkedList<BFOSolution> selected = EvolutionUtils.tournamentSelection(population, numToSelect, problem, rand, boutSize);            
        // reproduce
        LinkedList<BFOSolution> children = BFOUtils.genericAlgorithmReproduce(selected, popsize-elitism, mutation, crossover, rand);            
        // return
        return children;
	}
    
    @Override
    protected void internalPostEvaluation(Problem problem, LinkedList<BFOSolution> oldPopulation, LinkedList<BFOSolution> newPopulation)
    {
        // add the best solutions from the last generation to the next generation
        if(elitism > 0)
        {
            EvolutionUtils.elitism(oldPopulation, newPopulation, popsize, problem);
        }
    }    
    
	@Override
	protected LinkedList<BFOSolution> internalInitialiseBeforeRun(Problem problem)
	{
		// reused
        rand = new Random(seed);
        
        LinkedList<BFOSolution> pop = new LinkedList<BFOSolution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            pop.add(new BFOSolution(RandomUtils.randomBitString(rand, ((BFOProblemInterface)problem).getBinaryStringLength())));
        }      
        
        return pop;
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
        // crossover
        if(crossover>1||crossover<0)
        {
            throw new InvalidConfigurationException("Invalid crossover " + crossover);
        }
        // mutation
        if(mutation>1||mutation<0)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutation);
        }
        // pop size
        if(popsize<=0||popsize>1000000)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // bout size
        if(boutSize>popsize||boutSize<=0)
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
}

