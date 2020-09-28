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

/**
 * Type: RestrictedTournamentSelection<br/>
 * Date: 04/12/2006<br/>
 * <br/>
 * Description: Implementation of the Restructed Tournament Selection (RTS) algorithm
 * As specified in Finding Multimodal Solutions Using Restricted Tournament Selection (1995)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 09/01/2007   JBrownlee   Modified to use a generic replacement function
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * 							Modified to use Hamming distance, can be overridden for Euclidean distance
 * </pre>
 */
public class RestrictedTournamentSelection extends Algorithm
{
    protected long seed = System.currentTimeMillis();
    protected double crossover = 0.40;
    protected double mutation = 0.005;
    protected int popsize = 100;
    protected int windowSize = 10;
    

    @Override
    public String getDetails()
    {
        return getName() + ", " +
                "as specified in G. R. Harik. Finding Multimodal Solutions Using Restricted Tournament SelectionLarry Eshelman. Proceedings of the Sixth International Conference on Genetic Algorithms (ICGA)San Francisco, CA: Morgan Kaufmann; 1995: 24-31. ISBN: 1558603700. " +
                "Using Hamming distance.";
    }


    @Override
    public String getName()
    {
        return "Restricted Tournament Selection (RTS)";
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
                // perform replacements
                // perform replacements
            	doReplacement(pop, children, p, r, windowSize);
            }            
        }
    }
    
    /**
     * Perform replacement (Hamming distance based)
     * 
     * @param <T>
     * @param pop
     * @param children
     * @param p
     * @param r
     * @param sampleSize
     */
    public <T extends BFOSolution> void doReplacement(
            LinkedList<T> pop, 
            LinkedList<T> children, 
            Problem p,
            Random r,
            int sampleSize    		
    		)
    {    	
    	BFOUtils.hammingBasedReplacement(pop, children, p, r, sampleSize);
    }
    
    

    @Override
    public void validateConfiguration() throws InvalidConfigurationException
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
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // pop size
        if((popsize%2)!=0)
        {
            throw new InvalidConfigurationException("Invalid popsize, must be even " + popsize);
        }
        // bout size
        if(windowSize>popsize||windowSize<=0)
        {
            throw new InvalidConfigurationException("Invalid boutSize " + windowSize);
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

    public int getWindowSize()
    {
        return windowSize;
    }

    public void setWindowSize(int windowSize)
    {
        this.windowSize = windowSize;
    }    
}
