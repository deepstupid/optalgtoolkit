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
import com.oat.utils.RandomUtils;

/**
 * Type: RandomSearch<br/>
 * Date: 18/12/2006<br/>
 * <br/>
 * Description: Random Search for the binary character recognition problem
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Updated to use the new system
 * 
 * </pre>
 */
public class RandomSearch extends BCRAlgorithm
{   
    protected long seed = System.currentTimeMillis();
    
    protected Random rand;
    protected boolean [][] system;
    
    

    @Override
    public String getName()
    {
        return "Random Search";
    }

    

    @Override
    protected void initialiseSystem(BCRProblem problem)
    {
        rand = new Random(seed);
        int numPatterns = problem.getTotalPatterns();
        int length = problem.getPatternLength();
        system = RandomUtils.randomBitStringSet(rand, length, numPatterns, 1);
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
        
        // generate a new random string
        boolean [] substitute = RandomUtils.randomBitString(rand, system[bestIndex].length);
        if(p.isBetter(match.match(substitute), affinities[bestIndex]))
        {
            system[bestIndex] = substitute;
        }
        // return the best possible solution
        return system[bestIndex];
    }

    

    @Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
		// TODO Auto-generated method stub
		
	}

	public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }
}
