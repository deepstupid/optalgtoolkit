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
package com.oat.domains.bcr.algorithms;

import java.util.Random;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bcr.BCRAlgorithm;
import com.oat.domains.bcr.BCRProblem;
import com.oat.domains.bcr.MatchFunction;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: ParallelMutationHillClimber<br/>
 * Date: 12/07/2007<br/>
 * <br/>
 * Description: Select the best matching unit and hill climb it
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class ParallelMutationHillClimber extends BCRAlgorithm
{
    public final static int ITERATION_SIZE = 1000;
    
    protected long seed = System.currentTimeMillis();
    protected double mutation = 1.0/120; 
    protected int populationSize = 16; // 2*8
    
    protected Random rand;
    protected boolean [][] system;    
    

    @Override
    public String getName()
    {
        return "Parallel Mutation Hill Climber";
    }

    

    @Override
    protected void initialiseSystem(BCRProblem problem)
    {
        rand = new Random(seed);
        system = RandomUtils.randomBitStringSet(rand, problem.getPatternLength(), populationSize, 1);
    }

    @Override
    protected boolean[] respondToPattern(MatchFunction match, Problem p)
    {
        // locate the best pattern
        int bestIndex = 0;
        double [] affinities = new double[system.length];        
        for (int i = 0; i < affinities.length; i++)
        {
            affinities[i] = match.match(system[i]);
            if(p.isBetter(affinities[i], affinities[bestIndex]))
            {
                bestIndex = i;
            }
        }
        boolean [] response = system[bestIndex];        
        // duplicate 
        boolean [] substitute = ArrayUtils.copyArray(response);
        // mutate
        EvolutionUtils.binaryMutate(substitute, rand, mutation);
        // check
        if(p.isBetterOrSame(match.match(substitute), affinities[bestIndex]))
        {
            response = system[bestIndex] = substitute;
        }
        // return the best possible solution
        return response;
    }

    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {       
        if(mutation<0||mutation>1)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutation);
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

    public int getPopulationSize()
    {
        return populationSize;
    }

    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }    
}
