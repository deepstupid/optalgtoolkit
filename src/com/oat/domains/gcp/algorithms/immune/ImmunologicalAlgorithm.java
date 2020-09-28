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
package com.oat.domains.gcp.algorithms.immune;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.gcp.GCPSolution;
import com.oat.domains.gcp.GCPUtils;
import com.oat.domains.gcp.GCProblem;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.ImmuneSystemUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: ImmunologicalAlgorithm<br/>
 * Date: 12/12/2006<br/>
 * <br/>
 * Description: Immunological Algorithm (IA) 
 * Vincenzo Cutello; Giuseppe Nicosia, and Mario Pavone. A Hybrid Immune Algorithm with Information Gain for the Graph Coloring Problem. Proceedings, Part I:  Genetic and Evolutionary Computation Conference (GECCO 2003); Chicago, IL, USA. Berlin / Heidelberg: Springer; 2003: 171-182. Lecture Notes in Computer Science . v. 2723 ). ISBN: 3-540-40602-6.
 * Really just a pre-cursor to CLIGA
 * 
 * - Does not implement local search
 * - Does not implement information gain
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Updated to used evolution utils for swap mutation
 * 
 * </pre>
 */
public class ImmunologicalAlgorithm extends Algorithm
{   
    public enum AGING_POLICY {ELITIST, PURE}
    
    protected long seed = System.currentTimeMillis();
    protected int popSize = 50; // d
    protected int numClones = 10; // dup
    protected double k = 0.0001; // TODO - what is a good value for this?
    protected double maxAge = 25; // tau Beta
    protected AGING_POLICY agingPolicy = AGING_POLICY.ELITIST;
   
    
    
    protected class IASolution extends GCPSolution
    {
        int [] nodeVisitOrder;
        
        public IASolution(GCProblem p, int [] aNodeVisitOrder)
        {
            super(p);
            nodeVisitOrder = aNodeVisitOrder;
        }        
    }
    
    
    
    @Override
    public String getDetails()
    {
        return 
        "As described in: Vincenzo Cutello; Giuseppe Nicosia, and Mario Pavone. A Hybrid Immune Algorithm with Information Gain for the Graph Coloring Problem. Proceedings, Part I:  Genetic and Evolutionary Computation Conference (GECCO 2003); Chicago, IL, USA. Berlin / Heidelberg: Springer; 2003: 171-182. Lecture Notes in Computer Science . v. 2723 ). ISBN: 3-540-40602-6.";
    }


    @Override
    public String getName()
    {
        return "Immunological Algorithm (IA)";
    }

    @Override
    protected void internalExecuteAlgorithm(Problem problem)
    {
        Random r = new Random(seed);
        GCProblem p = (GCProblem) problem;
        // prepare initial population
        LinkedList<IASolution> pop = new LinkedList<IASolution>();
        while(pop.size() < popSize)
        {
            pop.add(createRandomSolution(p, r));
        }
        // evaluate
        p.cost(pop);
        // run main algorithm loop
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // clone and mutate
            LinkedList<IASolution> clones = cloneAndMutate(pop, p, r);
            
            // may have produced no clones
            if(clones.isEmpty())
            {
                continue;
            }
            
            // evaluate
            p.cost(clones);
            if((p.canEvaluate()))
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
                // and explictly does not using a "birthing" feature
            }
        }
    }
    
    
    protected IASolution createRandomSolution(GCProblem p, Random r)
    {
        int [] nodeVisitOrder = new int[p.getTotalNodes()];
        // linear normal ordering
        for (int i = 0; i < nodeVisitOrder.length; i++)
        {
            nodeVisitOrder[i] = i + 1;
        }
        // randomise
        RandomUtils.randomShuffle(nodeVisitOrder, r);
        // create
        IASolution s = new IASolution(p, nodeVisitOrder);
        // assign colors
        GCPUtils.nodeVisitOrderingToColorAssignments(p, nodeVisitOrder, s.getNodeColors());
        return s;
    }
    
    
    protected void performAging(LinkedList<IASolution> pop, Problem p, Random r)
    {
        IASolution bestInPop = AlgorithmUtils.getBest(pop, p);
        // prevent from being deleted
        if(agingPolicy == AGING_POLICY.ELITIST)
        {
            pop.remove(bestInPop);
        }
        
        if(!pop.isEmpty())
        {
            for (Iterator<IASolution> iterator = pop.iterator(); iterator.hasNext();)
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

    
    protected LinkedList<IASolution> cloneAndMutate(LinkedList<IASolution> pop, GCProblem p, Random r)
    {
        AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
        
        LinkedList<IASolution> clones = new LinkedList<IASolution>();
        
        for(IASolution b : pop)
        {
            // check if this solution can be cloned
            if(r.nextDouble() < ImmuneSystemUtils.cloningPotentialCLIGA(b.getNormalizedRelativeScore(), b.getNodeColors().length, k))
            {            
                for (int i = 0; i < numClones; i++)
                {
                    // copy
                    int [] clonedNodeVisitOrder = ArrayUtils.copyArray(b.nodeVisitOrder);
                    // mutate
                    mutate(clonedNodeVisitOrder, b.getNormalizedRelativeScore(), b.nodeVisitOrder.length, r);
                    // create
                    IASolution clone = new IASolution(p, clonedNodeVisitOrder);
                    // assign the new colours
                    GCPUtils.nodeVisitOrderingToColorAssignments(p, clonedNodeVisitOrder, clone.getNodeColors());
                    // store
                    clones.add(clone);
                }
            }
        }
        
        return clones;
    }
    

    
    
    /**
     * Perform random swaps on a node ordering
     * @param nodeVisitOrder
     * @param normalizedFitness
     * @param length
     */
    protected void mutate(int [] nodeVisitOrder, double normalizedFitness, int length, Random r)
    {
        double prob = mutatePoteitnal(normalizedFitness, length);
        // mutate using swap
        EvolutionUtils.mutatePermutation(nodeVisitOrder, r, prob);        
    }
    
    protected double mutatePoteitnal(double normalizedFitness, int length)
    {
        // TODO - specified but produces terrible results!!! Invalid probabilities
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
