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

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.BFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BitStringUtils;

/**
 * Type: ProbabilisticCrowding<br/>
 * Date: 04/12/2006<br/>
 * <br/>
 * Description: Probabilistic Crowding Genetic Algorithm (niching GA)
 * As described in: Probabilistic crowding deterministic crowding with probabilistic replacement (1999)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * 							Modified to use Hamming distance, can be overridden for Euclidean distance
 * </pre>
 */
public class ProbabilisticCrowding extends Algorithm
{    
    protected long seed = System.currentTimeMillis();
    protected double mutation = 0.005;
    protected double crossover = 0.70; // as per the paper
    protected int popsize = 100;

    @Override
    public String getDetails()
    {
        return "As specified in: O. J. Menshoel and D. E. Goldberg. Probabilistic crowding: deterministic crowding with probabilistic replacement. University of Illinois; 1999; IlliGAL Report No. 99005 . Department of General Engineering, 117 Transportation Building, 104 South Mathews Avenue, Urbana, IL 61801-2996. " +
                "Using Hamming Distance";
    }


    @Override
    public String getName()
    {
        return "Probabilistic Crowding (PC)";
    }

    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);     
        // prepare initial population
        LinkedList<BFOSolution> pop = BFOUtils.getRandomPopulationBinary(r, (BFOProblemInterface)p, popsize);     
        // evaluate
        p.cost(pop);        
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {           
            triggerIterationCompleteEvent(p,pop);
            // reproduce
            LinkedList<BFOSolution> children = BFOUtils.genericAlgorithmReproduce(pop, popsize, mutation, crossover, r);
            // evaluate
            p.cost(children);
            if(p.canEvaluate())
            {
                // replacements
                pop = replacements(pop, children, p, r);
            }            
        }
    }
    
    /**
     * Perform Deterministic crowding replacement strategy
     * Assumes children pop is ordered in the manner of the parents that created the children
     * That is first two children created by first two parents, etc...
     * 
     * @param pop
     * @param children
     * @param p
     * @param r
     */
    protected <T extends BFOSolution> LinkedList<T> replacements(
            LinkedList<T> pop, 
            LinkedList<T> children, 
            Problem p,
            Random r)
    {   
        LinkedList<T> np = new LinkedList<T>();
        
        // calculate normalized fitness for all solutions (pop and children)
        LinkedList<T> superpop = new LinkedList<T>();
        superpop.addAll(pop);
        superpop.addAll(children);
        AlgorithmUtils.calculateNormalizedRelativeFitness(superpop, p);
        
        // replacements
        for (int i = 0; i < children.size(); i+=2)
        {
            T c1 = children.get(i);
            T c2 = children.get(i+1);
            T p1 = pop.get(i);
            T p2 = pop.get(i+1);
            
            if(BitStringUtils.hammingDistance(p1,c1)+BitStringUtils.hammingDistance(p2,c2) 
                    <= BitStringUtils.hammingDistance(p1,c2)+BitStringUtils.hammingDistance(p2,c1))
            {
                if(canReplace(c1, p1, r))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p1);
                }
                if(canReplace(c2, p2, r))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p2);
                }
            }
            else
            {
                if(canReplace(c2, p1, r))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p1);
                }
                if(canReplace(c1, p2, r))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p2);
                }
            }
        }
        
        return np;
    }
    
    /**
     * Can s1 replace s2
     * @param s1
     * @param s2
     * @return
     */
    public <T extends BFOSolution> boolean canReplace(T s1, T s2, Random r)
    {
        double sum = s1.getNormalizedRelativeScore() + s2.getNormalizedRelativeScore();
        if(sum == 0) // to avoid divide by zero
        {
            // both zero normalized fitness
            return r.nextBoolean() ? true : false;
        }
        
        // check if the first solution can be selected
        if(r.nextDouble() < (s1.getNormalizedRelativeScore()/sum))
        {
            return true;
        }
        
        return false;
    }

    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {
        // mutation
        if(mutation>1||mutation<0)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutation);
        }
        // pop size
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // pop size
        if((popsize%2)!=0)
        {
            throw new InvalidConfigurationException("Invalid popsize, must be even " + popsize);
        }
        // crossover
        if(crossover>1||crossover<0)
        {
            throw new InvalidConfigurationException("Invalid crossover " + crossover);
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

    public double getCrossover()
    {
        return crossover;
    }

    public void setCrossover(double crossover)
    {
        this.crossover = crossover;
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
