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
package com.oat.domains.psp.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.psp.PSPProblem;
import com.oat.domains.psp.PSPSolutionRelative;
import com.oat.domains.psp.PSPUtils;
import com.oat.utils.EvolutionUtils;


/**
 * Type: GeneticAlgorithm<br/>
 * Date: 12/12/2006<br/>
 * <br/>
 * Description: Simple genetic algorithm that uses relative representation
 * does not take solution feasability into account
 * Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000.
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
    protected double crossoverProbability;
    protected double mutationProbability;
    protected int populationSize;
    protected int tournamentBouteSize;
    protected int numElites;
    
    
    public GeneticAlgorithm()
    {
    	automaticConfiguration(20, this);
    }   
    
    @Override
    public void automaticallyConfigure(Problem problem)
    {
    	automaticConfiguration(((PSPProblem)problem).getDataset().length, this);
    }
    
    public static void automaticConfiguration(int numComponents, GeneticAlgorithm algorithm)
    {
    	algorithm.setSeed(System.currentTimeMillis());
    	algorithm.setCrossoverProbability(0.98); // high
    	algorithm.setMutationProbability(1.0 / numComponents); // 1 in N
    	algorithm.setPopulationSize(numComponents); // components
    	algorithm.setTournamentBouteSize(2); // binary
    	algorithm.setNumElites(0); // off
    }
    
    
    
    
    
    
    @Override
    public String getDetails()
    {
        return 
        "As described in Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operators. Bristol, UK: Institute of Physics (IoP) Publishing; 2000." +
        ", using a relative move representation.";
    }

    @Override
    protected void internalExecuteAlgorithm(Problem problem)
    {
        Random r = new Random(seed);
        PSPProblem p = (PSPProblem) problem;
        
        // create initial population
        LinkedList<PSPSolutionRelative> pop = new  LinkedList<PSPSolutionRelative>();
        while(pop.size() < populationSize)
        {
            pop.add(PSPUtils.generateRandomRelSolution(p, r));
        }
        // evaluate
        p.cost(pop);        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p, pop); 
            // select
            int numToSelect = ((populationSize%2)==0) ? populationSize : populationSize + 1; // must select an even number of parents
            LinkedList<PSPSolutionRelative> selected = EvolutionUtils.tournamentSelection(pop, numToSelect, p, r, tournamentBouteSize);        
            // reproduce
            LinkedList<PSPSolutionRelative> children = reproduce(selected, populationSize-numElites, r);            
            // evaluate
            p.cost(children);
            // add the best solutions from the last generation to the next generation
            if(numElites > 0)
            {
                EvolutionUtils.elitism(pop, children, populationSize, p);
            }
            // replace the last generation with the next generation
            pop = children;
        }
    }
    
    
    public LinkedList<PSPSolutionRelative> reproduce(LinkedList<PSPSolutionRelative> pop, int totalChildren, Random r)
    {             
        LinkedList<PSPSolutionRelative> children = new LinkedList<PSPSolutionRelative>();
        
        // always expect two children from two parents
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            PSPSolutionRelative p1 = pop.get(i);
            PSPSolutionRelative p2 = pop.get(i+1);
            // perform crossover
            byte [][] twoChildren = EvolutionUtils.uniformCrossover(p1.getPermutation(), p2.getPermutation(), r, crossoverProbability);
            for (int j = 0; children.size()<totalChildren && j < twoChildren.length; j++)
            {
                // perform mutation
                EvolutionUtils.mutatePermutation(twoChildren[j], r, mutationProbability);
                // create
                PSPSolutionRelative aChild = new PSPSolutionRelative(twoChildren[j]);
                // add
                children.add(aChild);
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
        // crossover
        if(crossoverProbability>1||crossoverProbability<0)
        {
            throw new InvalidConfigurationException("Invalid crossover " + crossoverProbability);
        }
        // mutation
        if(mutationProbability>1||mutationProbability<0)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutationProbability);
        }
        // popsize
        if(populationSize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + populationSize);
        }
        // boutsize
        if(tournamentBouteSize>populationSize||tournamentBouteSize<0)
        {
            throw new InvalidConfigurationException("Invalid boutSize " + tournamentBouteSize);
        }
        // elitism
        if(numElites>populationSize||numElites<0)
        {
            throw new InvalidConfigurationException("Invalid elitism " + numElites);
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

    public double getCrossoverProbability()
    {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability)
    {
        this.crossoverProbability = crossoverProbability;
    }

    public double getMutationProbability()
    {
        return mutationProbability;
    }

    public void setMutationProbability(double mutationProbability)
    {
        this.mutationProbability = mutationProbability;
    }

    public int getPopulationSize()
    {
        return populationSize;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }

    public int getTournamentBouteSize()
    {
        return tournamentBouteSize;
    }

    public void setTournamentBouteSize(int tournamentBouteSize)
    {
        this.tournamentBouteSize = tournamentBouteSize;
    }

    public int getNumElites()
    {
        return numElites;
    }

    public void setNumElites(int numElites)
    {
        this.numElites = numElites;
    }
    
    
    
}

