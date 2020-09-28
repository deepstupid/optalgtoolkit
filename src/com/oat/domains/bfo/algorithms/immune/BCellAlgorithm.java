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

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.BFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: BCellAlgorithm<br/>
 * Date: 22/11/2006<br/>
 * <br/>
 * Description:
 * As described in: Assessing the performance of two immune inspired algorithms and a hybrid genetic algorithm for function optimisation (2004)
 * Verified in: Chasing chaos (2003)
 * 
 * - mutation parameter for the chance of each bit in the selected contigious region being flipped
 * - adjusted to work with limited evaluations
 * - 64-bits per parameter
 * - support gray code and binary
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Quick hack to check for any more evaluations after cloning stage, so that
 *                          replacement scoring check cannot fail
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * 
 * </pre>
 */
public class BCellAlgorithm extends Algorithm
{    
    protected long seed = System.currentTimeMillis();
    protected int popsize = 4; // P
    protected int numClones = 4; // C
    protected int numRandoms = 1; // number of the clone that will be random
    protected double mutateFactor = 0.05; // chance for each bit being flipped
        
    @Override
    public String getDetails()
    {
        return "B-Cell Algorithm (BCA): " +
                "As described in: Jon Timmis; C. Edmonds, and Johnny Kelsey. Assessing the Performance of Two Immune Inspired Algorithms and a Hybrid Genetic Algorithm for Function Optimisation. Proceedings of the Congress on Evolutionary Computation (CEC04); Potland, Oregon. USA. USA: IEEE Press; 2004: 1044-1051. " +
                "Verified as described in: Johnny Kelsey; J. Timmis, and A. Hone. Chasing chaos. The 2003 Congress on Evolutionary Computation, (CEC '03)USA: IEEE Computer Society; 2003: 413-419.";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        // prepare initial population
        LinkedList<BFOSolution> pop = BFOUtils.getRandomPopulationBinary(r, (BFOProblemInterface)p, popsize);   
        // evaluate
        p.cost(pop);
        
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            LinkedList<BFOSolution> newpop = new LinkedList<BFOSolution>();
            boolean wasReplacement = false;
            // apply cloning process for each member of the pop
            for (int i = 0; p.canEvaluate() && i < pop.size(); i++)
            {
            	BFOSolution s = pop.get(i);
            	BFOSolution bestOfClone = generateCloneAndGetBest(p,s, r);
                
                // check for no more evals
                // TODO - make more robust
                if(!p.canEvaluate())
                {
                    continue;
                }
                
                // best of clone could be null if we ran out of evaluations
                if(bestOfClone != null)
                {
                    if(p.isBetter(bestOfClone, s))
                    {
                        newpop.add(bestOfClone);
                        wasReplacement = true;
                    }
                    else
                    {
                        newpop.add(s);
                    }
                }
            }
            // we have stopped because we have completed a cycle or run out of evals            
            if(wasReplacement)
            {
                pop = newpop;
            }            
        }
    }    
    
    protected BFOSolution generateCloneAndGetBest(Problem p, BFOSolution parent, Random r)
    {
        LinkedList<BFOSolution> clone = new LinkedList<BFOSolution>();        
        // add a random
        for (int i = 0; i < numRandoms; i++)
        {
            boolean [] b = RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength());
            BFOSolution s = new BFOSolution(b);
            clone.add(s);
        }
        // create clones
        for (int i = 0; i < numClones-numRandoms; i++)
        {
            // create clone
        	BFOSolution c = cloneAndMutate(parent, r);
            clone.add(c);
        }
        // evaluate the cost of the new pop
        p.cost(clone);
        // return the best solution in the clone
        return (BFOSolution) AlgorithmUtils.getBest(clone, p);
    }
    
    protected BFOSolution cloneAndMutate(BFOSolution parent, Random r)
    {
        // clone
        boolean [] b = new boolean[parent.getBitString().length];
        System.arraycopy(parent.getBitString(),0,b,0,b.length);
        // mutate
        int start = r.nextInt(b.length);
        int length = r.nextInt(b.length-start); // some valid length, including zero - no change
        for (int i = start; i < start+length; i++)
        {
            if(r.nextDouble() <= mutateFactor)
            {
                b[i] = !b[i]; // flip the bit
            }
        }        
        return new BFOSolution(b);
    }

    @Override
    public String getName()
    {
        return "B-Cell Algorithm (BCA)";
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
        // mutate factor
        if(mutateFactor<=0)
        {
            throw new InvalidConfigurationException("Invalid mutateFactor " + mutateFactor);
        }
        // num clones
        if(numClones<0)
        {
            throw new InvalidConfigurationException("Invalid numClones " + numClones);
        }
        // num randoms
        if(numRandoms>numClones||numRandoms<0)
        {
            throw new InvalidConfigurationException("Invalid numRandoms " + numRandoms);
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


    public int getNumClones()
    {
        return numClones;
    }


    public void setNumClones(int numClones)
    {
        this.numClones = numClones;
    }


    public int getNumRandoms()
    {
        return numRandoms;
    }


    public void setNumRandoms(int numRandoms)
    {
        this.numRandoms = numRandoms;
    }


    public double getMutateFactor()
    {
        return mutateFactor;
    }


    public void setMutateFactor(double mutateFactor)
    {
        this.mutateFactor = mutateFactor;
    }
}
