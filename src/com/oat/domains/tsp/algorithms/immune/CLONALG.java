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
package com.oat.domains.tsp.algorithms.immune;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.domains.tsp.TSPUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.ImmuneSystemUtils;



/**
 * Type: CLONALG<br/>
 * Date: 13/12/2006<br/>
 * <br/>
 * Description: Clonal Selection Algorithm (CSA)
 * Renamed to CLONALG 
 * 
 * As specified in:
 *  Leandro N. de Castro and Fernando J. Von Zuben. Learning and optimization using the clonal selection principle. IEEE Transactions on Evolutionary Computation. 2002 Jun; 6(3):239-251. ISSN: 1089-778X.
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   Jbrownlee   Random moved to method variable rather than instance variable
 *                          
 * </pre>
 */
public class CLONALG extends Algorithm
{    
    protected long seed = System.currentTimeMillis();
    protected int populationSize = 50; // N    
    protected int selectionSize = 50; // n
    protected double cloneFactor = 0.1; // beta
    protected double mutateFactor = 2.5; // rho
    protected int randomReplacements = 5; // d
    

    @Override
    public String getDetails()
    {
        return "Clonal Selection Algorithm (CLONALG): " +
                "as described in: Leandro N. de Castro and Fernando J. Von Zuben. Learning and optimization using the clonal selection principle. IEEE Transactions on Evolutionary Computation. 2002 Jun; 6(3):239-251. ISSN: 1089-778X.";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem problem)
    {
        TSPSolution best = null;
        Random r = new Random(seed);
        TSPProblem p = (TSPProblem) problem;
        // prepare initial population
        
        LinkedList<TSPSolution> pop = new  LinkedList<TSPSolution>();
        // generate initial population
        while(pop.size() < populationSize)
        {
            TSPSolution s = TSPUtils.generateRandomSolution(p, r); 
            pop.add(s);
        } 
        // evaluate
        p.cost(pop);                
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // select 
            Collections.sort(pop);
            LinkedList<TSPSolution> selected = select(pop, p);
            // clone and mutate
            LinkedList<TSPSolution> children = generateChildren(selected, p, r);
            // evaluate
            p.cost(children);
            if(!p.canEvaluate())
            {
                continue;
            }
            // union the populations
            pop.addAll(children);
            // select the N best
            EvolutionUtils.elitistSelectionStrategy(pop, populationSize, p);
            // random replacements
            if(randomReplacements > 0 && p.canEvaluate())
            {   
                // create
                LinkedList<TSPSolution> randoms = new LinkedList<TSPSolution>();
                while(randoms.size() < randomReplacements)
                {
                    TSPSolution s = TSPUtils.generateRandomSolution(p, r); 
                    randoms.add(s);
                } 
                // evaluate
                p.cost(randoms);
                Collections.sort(pop);
                // make room - remember pop is still sorted
                for (int i = 0; i < randomReplacements; i++)
                {
                    if(p.isMinimization())
                    {
                        pop.removeLast();
                    }
                    else
                    {
                        pop.removeFirst();
                    }
                }
                pop.addAll(randoms);
            }            
        }
    }
    
    protected LinkedList<TSPSolution> select(LinkedList<TSPSolution> pop, TSPProblem p)
    {
        LinkedList<TSPSolution> selected = new LinkedList<TSPSolution>();
        for (int i = 0; i < selectionSize; i++)
        {
            if(p.isMinimization())
            {
                selected.add(pop.get(i));
            }
            else
            {
                selected.add(pop.get(pop.size()-1-i));
            }
        }
        return selected;
    }
    
    protected LinkedList<TSPSolution> generateChildren(
            LinkedList<TSPSolution> pop, 
            TSPProblem p,
            Random r)
    {
        LinkedList<TSPSolution> np = new LinkedList<TSPSolution>();
        
        // calculate relative normalized fitness
        AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
        // determine clone size
        int Nc = ImmuneSystemUtils.numClonesCLONALG_OPT(cloneFactor, populationSize);        
        // clone and mutate in one step
        for (TSPSolution s : pop)
        {
            // determine mutation probability
            double mutation = ImmuneSystemUtils.mutationProbabilityCLONALG(s.getNormalizedRelativeScore(), mutateFactor);
            // create mutated clones
            for (int i = 0; i < Nc; i++)
            {
                TSPSolution c = cloneAndMutate(s, mutation, r);
                np.add(c);
            }
        }
        
        return np;
    }
    
    protected TSPSolution cloneAndMutate(TSPSolution parent, double probability, Random r)
    {
        // clone
        int [] clone = ArrayUtils.copyArray(parent.getPermutation());
        // mutate
        EvolutionUtils.mutatePermutation(clone, r, probability);
        // create
        return new TSPSolution(clone);
    }
    

    @Override
    public String getName()
    {
        return "Clonal Selection Algorithm (CLONALG)";
    }

    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // populationSize
        if(populationSize<=0)
        {
            throw new InvalidConfigurationException("Invalid populationSize " + populationSize);
        }
        // selectionSize
        if(selectionSize<0 || selectionSize > populationSize)
        {
            throw new InvalidConfigurationException("Invalid selectionSize " + selectionSize);
        }
        // clonefactor
        if(cloneFactor<=0)
        {
            throw new InvalidConfigurationException("Invalid cloneFactor " + cloneFactor);
        }
        // mutate factor
        if(mutateFactor<1)
        {
            throw new InvalidConfigurationException("Invalid mutateFactor " + mutateFactor);
        }
        // random replacements
        if(randomReplacements>populationSize||randomReplacements<0)
        {
            throw new InvalidConfigurationException("Invalid randomReplacements " + randomReplacements);
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

    public int getPopulationSize()
    {
        return populationSize;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }

    public int getSelectionSize()
    {
        return selectionSize;
    }

    public void setSelectionSize(int selectionSize)
    {
        this.selectionSize = selectionSize;
    }

    public double getCloneFactor()
    {
        return cloneFactor;
    }

    public void setCloneFactor(double cloneFactor)
    {
        this.cloneFactor = cloneFactor;
    }

    public double getMutateFactor()
    {
        return mutateFactor;
    }

    public void setMutateFactor(double mutateFactor)
    {
        this.mutateFactor = mutateFactor;
    }

    public int getRandomReplacements()
    {
        return randomReplacements;
    }

    public void setRandomReplacements(int randomReplacements)
    {
        this.randomReplacements = randomReplacements;
    }
    
}
