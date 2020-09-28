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
package com.oat.domains.bfo.algorithms.immune;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.ImmuneSystemUtils;
import com.oat.utils.RandomUtils;



/**
 * Type: CLONALG<br/>
 * Date: 06/12/2006<br/>
 * <br/>
 * Description: Clonal Selection Algorithm (CSA)
 * Renamed to CLONALG 
 * 
 * As specified in:
 *  Leandro N. de Castro and Fernando J. Von Zuben. Learning and optimization using the clonal selection principle. IEEE Transactions on Evolutionary Computation. 2002 Jun; 6(3):239-251. ISSN: 1089-778X.
 *  Updated based on the changes specified in: Vincenzo Cutello; G. Narzisi; Giuseppe Nicosia; Mario Pavone, and G. Sorace. How to Escape Traps using Clonal Selection Algorithms. The First International Conference on Informatics in Control, Automation and Robotics, ICINCO 2004; Setubal, Portugal.  INSTICC Press; 2004: 322-326. 
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 
 * </pre>
 */
public class CLONALG extends Algorithm
{    
    public enum MUTATION_FUNCTION {CLONALG, OPTAINET}
    public enum SELECTION_FUNCTION {CLONALG, CLONALG1, CLONALG2}
    
    // parameters
    protected long seed = System.currentTimeMillis();
    protected int popsize = 50; // N    
    protected double cloneFactor = 0.1; // beta
    protected double mutateFactor = 2.5; // rho
    protected int randomReplacements = 1; // d
    protected MUTATION_FUNCTION mutationFunction = MUTATION_FUNCTION.CLONALG;
    protected SELECTION_FUNCTION selectionFunction = SELECTION_FUNCTION.CLONALG;
    

    @Override
    public String getDetails()
    {
        return getName() +
        "(and CLONALG1, CLONALG2) "+
        " As described in: Leandro N. de Castro and Fernando J. Von Zuben. Learning and optimization using the clonal selection principle. IEEE Transactions on Evolutionary Computation. 2002 Jun; 6(3):239-251. ISSN: 1089-778X. " +
        "Updated based on the changes specified in: Vincenzo Cutello; G. Narzisi; Giuseppe Nicosia; Mario Pavone, and G. Sorace. How to Escape Traps using Clonal Selection Algorithms. The First International Conference on Informatics in Control, Automation and Robotics, ICINCO 2004; Setubal, Portugal.  INSTICC Press; 2004: 322-326. ";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        // prepare initial population
        LinkedList<BFOSolution> pop = generateRandoms(p, popsize, r); 
        // evaluate
        p.cost(pop);
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            // notify listeners
            triggerIterationCompleteEvent(p,pop);  
            // calculate normalized fitness of the parents
            AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
            
            // prepare the next generation              
            switch(selectionFunction)
            {
            case CLONALG:
                prepareNextGenCLONALG(pop, p, r);
                break;
            case CLONALG1:                
                prepareNextGenCLONALG1(pop, p, r);
                break;
            case CLONALG2:
                prepareNextGenCLONALG2(pop, p, r);
                break;
            default:
                throw new AlgorithmRunException("Unknown selection function " + selectionFunction);
            }

            // assume children are evaluated, and population is sorted

            // do random replacements
            if(randomReplacements > 0 && p.canEvaluate())
            {
                LinkedList<BFOSolution> randoms = generateRandoms(p, randomReplacements, r);
                // evaluate randoms
                p.cost(randoms);
                Collections.sort(pop);
                // make room - pop is sorted
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
    
    /**
     * Classical CLONALG
     * @param pop
     * @param p
     */
    protected void prepareNextGenCLONALG(LinkedList<BFOSolution> pop, Problem p, Random r)
    {
        LinkedList<BFOSolution> children = new LinkedList<BFOSolution>();
        // clone and mutate
        for (BFOSolution b : pop)
        {
            LinkedList<BFOSolution> clone = createCloneAndMutate(p, b, r);
            children.addAll(clone);
        }        
        // evaluate children
        p.cost(children);
        pop.addAll(children);
        // trim to size
        if(p.canEvaluate() && pop.size()>popsize)
        {
            Collections.sort(pop);
            while(pop.size() > popsize)
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
        }
    }
    
    
    /**
     * Modified CLONALG1 as specified in: How to Escape Traps using Clonal Selection Algorithms (2004)
     * @param pop
     * @param p
     */
    protected void prepareNextGenCLONALG1(LinkedList<BFOSolution> pop, Problem p, Random r)
    {
        LinkedList<BFOSolution> nextgen = new LinkedList<BFOSolution>();
        
        // clone and mutate
        for (BFOSolution b : pop)
        {
            // create
            LinkedList<BFOSolution> clone = createCloneAndMutate(p, b, r);
            // evaluate
            p.cost(clone);        
            // add best from clone
            nextgen.add(AlgorithmUtils.getBest(clone, p));
            if(!p.canEvaluate())
            {
                break;
            }
        } 
        pop.clear();
        pop.addAll(nextgen);
        //Collections.sort(nextgen);
        // TODO - does this need to trim????

    }
    
    
    /**
     * Modified CLONALG2 as specified in: How to Escape Traps using Clonal Selection Algorithms (2004)
     * @param pop
     * @param p
     */
    protected void prepareNextGenCLONALG2(LinkedList<BFOSolution> pop, Problem p, Random r)
    {
        LinkedList<BFOSolution> children = new LinkedList<BFOSolution>();
        // clone and mutate
        for (BFOSolution b : pop)
        {
            LinkedList<BFOSolution> clone = createCloneAndMutate(p, b, r);
            children.addAll(clone);
        }        
        // evaluate children
        p.cost(children);
        // discard current pop
        pop.clear();
        pop.addAll(children);
        // trim to size
        if(p.canEvaluate() && pop.size()>popsize)
        {
            Collections.sort(pop);
            while(pop.size() > popsize)
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
        }
    }
    
    
    protected LinkedList<BFOSolution> generateRandoms(Problem p, int numRandoms, Random r)
    {
        LinkedList<BFOSolution> randoms = new LinkedList<BFOSolution>();
        while(randoms.size() < numRandoms)
        {
            randoms.add(new BFOSolution(RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength())));
        }
        return randoms;
    }
    
    protected LinkedList<BFOSolution> createCloneAndMutate(Problem p, BFOSolution s, Random r)
    {
        LinkedList<BFOSolution> clone = new LinkedList<BFOSolution>();
        
        int size = ImmuneSystemUtils.numClonesCLONALG_OPT(cloneFactor, popsize);
        double mutationProb = mutationProbability(s.getNormalizedRelativeScore());
        while(clone.size() < size)
        {
            // copy
            BFOSolution b = new BFOSolution(s);
            // mutate
            EvolutionUtils.binaryMutate(b.getBitString(), r, mutationProb);
            // add
            clone.add(b);
        }
        
        return clone;
    }
    
    
    
    /**
     * Use the configured mutation function
     * @param f - normalized fitness
     * @return
     */
    protected double mutationProbability(double f)
    {
        // safety
        if(!AlgorithmUtils.inBounds(f,0,1))
        {
            throw new AlgorithmRunException("Expected fitness to be normalized " + f);
        }
        
        double p = 0.0;
        
        switch(mutationFunction)
        {
        case CLONALG:
            p = ImmuneSystemUtils.mutationProbabilityCLONALG(f, mutateFactor);
            break;
        case OPTAINET:
            p = ImmuneSystemUtils.mutationProbabilityOPAINET(f, mutateFactor);
            break;
        default:
            throw new AlgorithmRunException("Unknown mutation probability function: " + mutationFunction);
        }

        return p; 
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
        // popsize
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
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
        if(randomReplacements>popsize||randomReplacements<0)
        {
            throw new InvalidConfigurationException("Invalid randomReplacements " + randomReplacements);
        }
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

    public int getPopsize()
    {
        return popsize;
    }

    public void setPopsize(int popsize)
    {
        this.popsize = popsize;
    }

    public int getRandomReplacements()
    {
        return randomReplacements;
    }

    public void setRandomReplacements(int randomReplacements)
    {
        this.randomReplacements = randomReplacements;
    }

    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public MUTATION_FUNCTION getMutationFunction()
    {
        return mutationFunction;
    }

    public void setMutationFunction(MUTATION_FUNCTION mutationFunction)
    {
        this.mutationFunction = mutationFunction;
    }

    public SELECTION_FUNCTION getSelectionFunction()
    {
        return selectionFunction;
    }

    public void setSelectionFunction(SELECTION_FUNCTION selectionFunction)
    {
        this.selectionFunction = selectionFunction;
    }
    
    
}
