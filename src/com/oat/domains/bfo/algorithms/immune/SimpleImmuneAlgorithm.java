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
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;

/**
 * Type: SimpleImmuneAlgorithm<br/>
 * Date: 08/12/2006<br/>
 * <br/>
 * Description: Simple Immune Algorithm (SIA)
 * As specified in: Vincenzo Cutello and Giuseppe Nicosia. Chapter VI. The Clonal Selection Principle for In Silico and In Vivo Computing. Leandro Nunes de Castro and Fernando J. Von Zuben, Editor. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 104-146.
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
public class SimpleImmuneAlgorithm extends Algorithm
{       
    protected long seed = System.currentTimeMillis();
    protected int popSize = 50; // d
    protected int numClones = 10; // dup

    

    @Override
    public String getDetails()
    {
        return "As described in: Vincenzo Cutello and Giuseppe Nicosia. Chapter VI. The Clonal Selection Principle for In Silico and In Vivo Computing. Leandro Nunes de Castro and Fernando J. Von Zuben, Editor. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 104-146.";
    }


    @Override
    public String getName()
    {
        return "Simple Immune Algorithm (SIA)";
    }

    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        // prepare initial population
        LinkedList<BFOSolution> pop = BFOUtils.getRandomPopulationBinary(r, (BFOProblemInterface)p, popSize);
        // evaluate
        p.cost(pop);
        // run main algorithm loop
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // clone and mutate
            LinkedList<BFOSolution> clones = cloneAndMutate(pop, p, r);
            // evaluate
            p.cost(clones);
            if(p.canEvaluate())
            {
                // union
                pop.addAll(clones);
                // select
                EvolutionUtils.elitistSelectionStrategy(pop, popSize, p);
            }
        }
    }    
    
    protected LinkedList<BFOSolution> cloneAndMutate(LinkedList<BFOSolution> pop, Problem p, Random r)
    {
        LinkedList<BFOSolution> clones = new LinkedList<BFOSolution>();
        
        for(BFOSolution b : pop)
        {
            for (int i = 0; i < numClones; i++)
            {
                // copy
                boolean [] bitString = ArrayUtils.copyArray(b.getBitString());
                // mutate
                mutate(bitString, r);
                // create
                BFOSolution clone = new BFOSolution(bitString);
                // store
                clones.add(clone);
            }
        }
        
        return clones;
    }
    
    protected void mutate(boolean [] b, Random r)
    {
        int selection = r.nextInt(b.length);
        b[selection] = !b[selection];
    }

    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {
        // popsize
        if(popSize <= 0)
        {
            throw new InvalidConfigurationException("Invalid popSize " + popSize);
        }
        // numClones
        if(numClones <= 0)
        {
            throw new InvalidConfigurationException("Invalid numClones " + numClones);
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

    public int getPopSize()
    {
        return popSize;
    }

    public void setPopSize(int popSize)
    {
        this.popSize = popSize;
    }

    public int getNumClones()
    {
        return numClones;
    }

    public void setNumClones(int numClones)
    {
        this.numClones = numClones;
    }
}
