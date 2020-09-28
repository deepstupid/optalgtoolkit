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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.BFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.ImmuneSystemUtils;

/**
 * Type: CLIGA<br/>
 * Date: 08/12/2006<br/>
 * <br/>
 * Description: CLIGA Algorithm 
 * As specified in: Vincenzo Cutello and Giuseppe Nicosia. Chapter VI. The Clonal Selection Principle for In Silico and In Vivo Computing. Leandro Nunes de Castro and Fernando J. Von Zuben, Editor. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 104-146.
 * 
 * - Does not implement information gain
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Use evolution utils for mutation
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * </pre>
 */
public class CLIGA extends Algorithm
{   
    public enum AGING_POLICY {ELITIST, PURE}
    
    protected long seed = System.currentTimeMillis();
    protected int popSize = 50; // d
    protected int numClones = 10; // dup
    protected double k = 0.0001; // TODO - what is a good value for this?
    protected double maxAge = 25; // tau Beta
    protected AGING_POLICY agingPolicy = AGING_POLICY.ELITIST;
   
    
    
    @Override
    public String getDetails()
    {
        return "As described in: Vincenzo Cutello and Giuseppe Nicosia. Chapter VI. The Clonal Selection Principle for In Silico and In Vivo Computing. Leandro Nunes de Castro and Fernando J. Von Zuben, Editor. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 104-146.";
    }


    @Override
    public String getName()
    {
        return "Cloning, Information Gain, Aging (CLIGA)";
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
            
            // may have produced no clones
            if(clones.isEmpty())
            {
                continue;
            }
            
            // evaluate
            p.cost(clones);
            if(p.canEvaluate())
            {
                // union
                pop.addAll(clones);
                
                // perform aging
                performAging(pop, p, r);            
                // TODO aging could kill all solutions, literature does not specify what to do
                if(pop.isEmpty())
                {
                    break; // stop
                }
                
                // TODO - the literature does not specify any selection strategy to trim pop size
            }
        }
    }
    
    
    protected void performAging(LinkedList<BFOSolution> pop, Problem p, Random r)
    {
    	BFOSolution bestInPop = AlgorithmUtils.getBest(pop, p);
        // prevent from being deleted
        if(agingPolicy == AGING_POLICY.ELITIST)
        {
            pop.remove(bestInPop);
        }
        
        if(!pop.isEmpty())
        {
            for (Iterator<BFOSolution> iterator = pop.iterator(); iterator.hasNext();)
            {
                iterator.next();
                // check if this solution can die
                if(r.nextDouble() < ImmuneSystemUtils.calculateDeletionPotentialCLIGA(maxAge))
                {
                    iterator.remove();
                }
            }
        }
        // put back in
        if(agingPolicy == AGING_POLICY.ELITIST)
        {
            pop.add(bestInPop);
        }
    }

    
    protected LinkedList<BFOSolution> cloneAndMutate(
            LinkedList<BFOSolution> pop, 
            Problem p, 
            Random r)
    {
        AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
        
        LinkedList<BFOSolution> clones = new LinkedList<BFOSolution>();
        
        for(BFOSolution b : pop)
        {
            // check if this solution can be cloned
            if(r.nextDouble() < ImmuneSystemUtils.cloningPotentialCLIGA(b.getNormalizedRelativeScore(), b.getBitString().length, k))
            {            
                for (int i = 0; i < numClones; i++)
                {
                    // copy
                    boolean [] bitString = ArrayUtils.copyArray(b.getBitString());
                    // mutate
                    mutate(bitString, b.getNormalizedRelativeScore(), b.getBitString().length, r);
                    // create
                    BFOSolution clone = new BFOSolution(bitString);
                    // store
                    clones.add(clone);
                }
            }
        }
        
        return clones;
    }
    
    protected void mutate(boolean [] bitString, double normalizedFitness, int length, Random r)
    {
        double prob = mutatePoteitnal(normalizedFitness, length);
        // mutate
        EvolutionUtils.binaryMutate(bitString, r, prob);
    }
    
    protected double mutatePoteitnal(double normalizedFitness, int length)
    {
        // TODO - unspecified in literature
        // NOTE: this results in *really* invalid values
        //double prob =  1.0 - (length / normalizedFitness);
        
        double prob = 0.05;
        
        // safety
        if(!AlgorithmUtils.inBounds(prob, 0, 1))
        {
            throw new AlgorithmRunException("Unexpected mutation potential f["+normalizedFitness+"], length["+length+"] " +prob);
        }
        return prob;
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
        // maxAge
        if(maxAge <= 0)
        {
            throw new InvalidConfigurationException("Invalid maxAge " + maxAge);
        }
        // k 
        if(k <= 0)
        {
            throw new InvalidConfigurationException("Invalid k " + k);
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


    public double getK()
    {
        return k;
    }


    public void setK(double k)
    {
        this.k = k;
    }


    public double getMaxAge()
    {
        return maxAge;
    }


    public void setMaxAge(double maxAge)
    {
        this.maxAge = maxAge;
    }


    public AGING_POLICY getAgingPolicy()
    {
        return agingPolicy;
    }


    public void setAgingPolicy(AGING_POLICY agingPolicy)
    {
        this.agingPolicy = agingPolicy;
    }
}
