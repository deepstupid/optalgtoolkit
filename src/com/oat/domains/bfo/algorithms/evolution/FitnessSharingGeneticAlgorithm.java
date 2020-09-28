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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.BFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: FitnessSharingGeneticAlgorithm<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description: As specified in: David E. Goldberg. Genetic Algorithms in Search, Optimization and Machine Learning. USA, Canada: Addison Wesley Publishing Company, Inc.; 1989.
 * Use re-scaled fitness to de-rate as specified in: When Sharing Fails (2001)
 * 
 * Fixes
 * - share radius cannot be zero
 * - normalized relative fitness for deration
 * - checks for non-zero share values
 * - random selection if sum of derated fitnesses is zero (in SUS)
 * - lots of checks everywhere
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 09/01/2007   JBrownlee   Deleted elitism, does not belong, messes up the algorithm
 *                          Updated to use a generic fitness sharing method
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * </pre>
 */
public class FitnessSharingGeneticAlgorithm extends Algorithm
{       
    protected long seed = System.currentTimeMillis();
    protected double crossover = 0.95;
    protected double mutation = 0.005;
    protected int popsize = 100;
    protected double shareRadius = 0.2;
    protected double alpha = 1.0;
    
    
    protected static class FSSolution extends BFOSolution
    {
        private double deratedFitness = Double.NaN;
        
        public FSSolution(boolean [] v)
        {
            super(v);
        } 
    }
    
    
    @Override
    public String getDetails()
    {
        return
        "Fitness Sharing Genetic Algorithm (FSGA) (niching GA): " +
        "As described in: David E. Goldberg. Genetic Algorithms in Search, Optimization and Machine Learning. USA, Canada: Addison Wesley Publishing Company, Inc.; 1989. " +
        "Using Stochastic Universal Sampling (SUS), " +
        "using genotype distance function (Hamming distance / bit-string length), " +
        "repaired de-rated fitness equation (positive with zero offset - linear mapping) as specified in: " +
        "When Sharing Fails (2001).";
    }
    

    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        LinkedList<FSSolution> pop = new LinkedList<FSSolution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            boolean [] b = RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength());
            FSSolution s = new FSSolution(b);
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // derate fitness
            derateFitness(pop, p);
            // select
            LinkedList<FSSolution> selected = stochasticUniversalSampling(pop, popsize, r);
            // reproduce
            pop = reproduce(selected, popsize, p, r);            
            // evaluate
            p.cost(pop);
        }
    }
    
    
    /**
     * Ensures de-rated fitness is always positive, 1.0 offset, and
     * given the same fitness distribution (linear mapping)
     * 
     * @param pop
     * @param p
     */
    protected void derateFitness(LinkedList<FSSolution> pop, Problem p)
    {
        // calculate normalised relative fitness values
        AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
        // derate the fitness of each solution
        for(FSSolution s : pop)
        {
            s.deratedFitness = BFOUtils.calculateDeratedFitness(s, pop, shareRadius, alpha);
        }
    }
    
    protected LinkedList<FSSolution> reproduce(LinkedList<FSSolution> pop, int totalChildren, Problem p, Random r)
    {
        LinkedList<FSSolution> children = new LinkedList<FSSolution>();
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            // select parents
            FSSolution p1 = pop.get(i);
            FSSolution p2 = pop.get(i+1);
            // create children
            boolean [][] b = EvolutionUtils.onePointBinaryCrossover(p1.getBitString(), p2.getBitString(), r, crossover);            
            for (int j = 0; children.size()<totalChildren && j < b.length; j++)
            {
                // mutate
                EvolutionUtils.binaryMutate(b[j], r, mutation);
                // create
                FSSolution s = new FSSolution(b[j]);
                // add
                children.add(s);
            }            
        }
        return children;
    }    
    
    /**
     * Select the parents of the next generation using Stochastic Universal
     * Selection (SUS). Selection is performed using the solutions de-rated fitness.
     * If the sum of the derated fitness values is zero, then a random selective set
     * with re-selection is returned.
     * 
     * @param pop
     * @param numToSelection
     * @param r
     * @return
     */
    protected LinkedList<FSSolution> stochasticUniversalSampling(
            LinkedList<FSSolution> pop, 
            int numToSelection,
            Random r)
    {        
        // shuffle the population 
        Collections.shuffle(pop, r);
        // ensure the number of solutions being selected is even (needed for corssover)
        if((numToSelection%2) != 0)
        {
            numToSelection++; // need one so crossover works
        }
        // calculate the summed derated fitness
        double summedFitness = 0.0;
        for(FSSolution f : pop)
        {
            summedFitness += f.deratedFitness;
        }        
        // check if the derated fitness of all solutions is zero
        if(summedFitness == 0)
        {
            return RandomUtils.randomSampleWithReselection(pop, numToSelection, r);
        }

        LinkedList<FSSolution> selected = new LinkedList<FSSolution>();
        double sum = 0.0;
        double singleMarker = 1.0 / numToSelection;
        double positionOfMarker = singleMarker * r.nextDouble();

        for (int i = 0; selected.size()<numToSelection && i < pop.size(); i++)
        {
            // calculate this individuals slice of pie
            double slice = (pop.get(i).deratedFitness / summedFitness);
            // sum the fitness searching for marker locations
            sum += slice;
            // process the marker on current summed fitness
            // can be 0 - n copies of this individual depending on size of
            // fitness
            while (selected.size()<numToSelection && positionOfMarker < sum)
            {
                // add the current individual
                selected.add(pop.get(i));
                // increment the marker
                positionOfMarker += singleMarker;
            }
        }
        
        // ensure safety
        if(selected.size() < numToSelection)
        {
            // add randoms to fill up to quota
            selected.addAll(RandomUtils.randomSampleWithReselection(pop, numToSelection-selected.size(), r));
        }
        
        return selected;
    }  
    
    
    

    @Override
    public String getName()
    {
        return "Fitness Sharing Genetic Algorithm (FSGA)";
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
        // popsize
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // share radius
        if(shareRadius>1||shareRadius<=0) // cannot be zero
        {
            throw new InvalidConfigurationException("Invalid shareRadius " + shareRadius);
        }
        // alpha
        if(alpha<0)
        {
            throw new InvalidConfigurationException("Invalid alpha " + alpha);
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
    
    public double getShareRadius()
    {
        return shareRadius;
    }


    public void setShareRadius(double shareRadius)
    {
        this.shareRadius = shareRadius;
    }


    public double getAlpha()
    {
        return alpha;
    }


    public void setAlpha(double alpha)
    {
        this.alpha = alpha;
    }    
}

