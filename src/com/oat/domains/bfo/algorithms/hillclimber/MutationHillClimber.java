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
package com.oat.domains.bfo.algorithms.hillclimber;

import java.util.Random;

import com.oat.Algorithm;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: MutationHillClimber<br/>
 * Date: 03/07/2007<br/>
 * <br/>
 * Description: Mutation Hill Climber, like an ES(1+1)
 * Heinz Muhlenbein. How Genetic Algorithms Really Work: I. Mutation and Hillclimbing. Parallel Problem Solving from Nature 2, PPSN-II; Brussels, Belgium.  Elsevier; 1992: 15-26. 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 10/07/2007   JBrownlee   Added the appropriate reference
 * 
 * </pre>
 */
public class MutationHillClimber extends Algorithm
	implements AutomaticallyConfigurableAlgorithm
{   
    /**
     * Random number seed, default is system time 
     */
    protected long seed;
    /**
     * Mutation rate
     */
    protected double mutationRate;
    
    
    
    public MutationHillClimber()
    {
    	automaticallyConfigure(30, this);
    }    
            
    @Override
	public void automaticallyConfigure(Problem problem)
	{
    	automaticallyConfigure(((BFOProblemInterface)problem).getBinaryStringLength(), this);		
	}
    
    
    public static void automaticallyConfigure(int stringLength, MutationHillClimber a)
    {
    	a.setSeed(System.currentTimeMillis());
    	a.setMutationRate(1.0/stringLength);
    }
    
    

	@Override
    public String getDetails()
    {
        return "As implemented in Heinz Muhlenbein. How Genetic Algorithms Really Work: I. Mutation and Hillclimbing. Parallel Problem Solving from Nature 2, PPSN-II; Brussels, Belgium.  Elsevier; 1992: 15-26. ";
    }
    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {    	
        if(!AlgorithmUtils.inBounds(mutationRate, 0, 1))
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutationRate);
        }
    }
    
    
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        // the present point
        BFOSolution point = new BFOSolution(RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength()));
        p.cost(point);
        
        while(p.canEvaluate())
        {
            // copy
            boolean [] mutant = ArrayUtils.copyArray(point.getBitString());
            // mutate
            EvolutionUtils.binaryMutate(mutant, r, mutationRate);
            // create
            BFOSolution newPoint = new BFOSolution(mutant);
            // evaluate
            p.cost(newPoint);
            if(p.canEvaluate())
            {
	            // compare - accept if better or the same
	            if(p.isBetterOrSame(newPoint, point))
	            {
	                point = newPoint;
	            }
            }
        }
    }


    @Override
    public String getName()
    {
        return "Mutation Hill Climber";
    }

    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public double getMutationRate()
    {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate)
    {
        this.mutationRate = mutationRate;
    }
    
}
