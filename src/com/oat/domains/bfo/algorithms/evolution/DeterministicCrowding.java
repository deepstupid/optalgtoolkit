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
import com.oat.utils.BitStringUtils;

/**
 * Type: DeterministicCrowding<br/>
 * Date: 04/12/2006<br/>
 * <br/>
 * Description: Deterministic Crowding Genetic Algorithm (niching GA)
 * As described in Crowding and Preselection Revisited (1992)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 27/12/2006   JBrownlee   Re-ordered the strip/getbest to after replacements
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * 							Modified to use Hamming distance, can be overridden for Euclidean distance
 * </pre>
 */
public class DeterministicCrowding extends Algorithm
{   
    protected long seed = System.currentTimeMillis();
    protected double mutation = 0.005;
    protected int popsize = 100;


    @Override
    public String getDetails()
    {
        return "As specified in S. W. Mahfoud. Crowding and preselection revisitedReinhard Manner and Bernard Manderick. Parallel Problem Solving from Nature 2; Brussels, Belgium.  Elsevier Science Publishers; 1992: 27-36." +
                "Uses Hamming distance for replacement decisions";
    }


    @Override
    public String getName()
    {
        return "Deterministic Crowding (DC)";
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
            LinkedList<BFOSolution> children = BFOUtils.genericAlgorithmReproduce(pop, popsize, mutation, 1.0, r);
            // evaluate
            p.cost(children);
            // ensure that all children have been evaluated, and it is worth doing the replacement step
            if(p.canEvaluate())
            {
                // replacements
                pop = replacements(pop, children, p);
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
     */
    protected <T extends BFOSolution> LinkedList<T> replacements(
            LinkedList<T> pop, 
            LinkedList<T> children, 
            Problem p)
    {
        LinkedList<T> np = new LinkedList<T>();        
        
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
                if(p.isBetter(c1, p1))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p1);
                }
                if(p.isBetter(c2, p2))
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
                if(p.isBetter(c2, p1))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p1);
                }
                if(p.isBetter(c1, p2))
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
