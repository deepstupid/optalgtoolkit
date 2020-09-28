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
package com.oat.domains.cfo.algorithms.immune;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.domains.cfo.CFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ImmuneSystemUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: OptaiNET<br/>
 * Date: 22/11/2006<br/>
 * <br/>
 * Description:
 * 
 * As described in: An artificial immune network for multimodal function optimization (2002)
 * and described in: A Comment on opt-AINet: An Immune Network Algorithm for Optimisation (2004)
 * Also compaired for consistancy against Paul Andrews 
 * implementation here: http://www.elec.york.ac.uk/ARTIST/code.php
 * 
 * Changes:
 * - supports limited function evaluations
 * - normalised fitness supports minimisation and maximisation
 * - sigma is a ratio of the total size of the domain (generalised)
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Added another check for no more evaluations during local improvements
 *                          to ensure that a score check does not occur for unevaluated solutions
 * 17/01/2007   Jbrownlee   Fixed a bug that caused the iteration trigger and fitness normalization to
 *                          occur in a situation where solutions may not be evaluated.
 * </pre>
 */
public class OptaiNET extends Algorithm
{    
    // parameters
    protected long seed = System.currentTimeMillis();
    protected double supressionThreshold = 0.2; // sigma-s
    protected int initialTotalCells = 20; // N
    protected int totalClonesPerCell = 10; // Nc
    protected double percentageNewComers = 0.40; // d
    protected double scaleAffinityProportionalSelection = 1; // beta
    protected double stabalisationFactor = 0.0001;

    
    @Override
    public String getDetails()
    {
        return "Optimized Artificial Immune Network (opt-aiNET): " +
                "As described in: Leandro N. de Castro and Jon Timmis. An artificial immune network for multimodal function optimization. Proceedings of the 2002 Congress on Evolutionary Computation (CEC '02); Honolulu, HI, USA. USA: IEEE Computer Society; 2002: 699-704. ISBN: 0-7803-7282-4. " +
                "And fixes in: Jon Timmis and Camilla Edmonds. A Comment on opt-AINet: An Immune Network Algorithm for Optimisation D. Kalyanmoy et al, Editor. Proceedings, Part I. Genetic and Evolutionary Computation Conference (GECCO 2004); Seattle, WA, USA. Germany: Springer; 2004: 308-317. Lecture Notes in Computer Science. v. 3102 ). ISBN: 0302-9743. " +
                "Also checked against Paul Andrew's implementation: http://www.elec.york.ac.uk/ARTIST/code.php, " +
                "Generalized sigma as ratio of the total size of the domain";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        double largestDistance = CFOUtils.getLargestEuclideanDistanceInDomain((CFOProblemInterface)p);
        LinkedList<CFOSolution> pop = new LinkedList<CFOSolution>();        
        
        // create initial population
        while(pop.size() < initialTotalCells)
        {
            CFOSolution s = new CFOSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax()));
            pop.add(s);
        }  
        // run the main loop of the algorithm        
        while(p.canEvaluate())
        {                        
            // evaluate the network (memory) cells both in terms of fitness (if needed) and normalised fitness
            // may have just started (all random), or may be in the loop i which case there may be 'd' random insertions
            // conventional cost allocation
            p.cost(pop);
            
            if(!p.canEvaluate())
            {
                continue; // end
            }
            
            triggerIterationCompleteEvent(p,pop);
            // normalised cost
            AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
            
            
            // perform local improvements
            performLocalImprovements(pop, p, r);
            
            // check if it is worth consolidating the pop
            if(p.canEvaluate())
            {                
                // consolidate
                consolidateNetworkCells(pop, largestDistance, p);                
                // add random solutions
                int newComers = (int) Math.round(pop.size() * percentageNewComers);
                for (int i = 0; i < newComers; i++)
                {
                    CFOSolution s = new CFOSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax()));
                    pop.add(s);
                }
            }
        }
    }
    
    /**
     * Given a population size of n, refine each cell until the fitness of the entire 
     * population stabalises
     * 
     * @param pop
     * @param p
     */
    protected void performLocalImprovements(LinkedList<CFOSolution> pop, Problem p, Random r)
    {
        // clonal selection phase (local search for each memory cell)
        boolean hasStabalised = false; 
        double averageFitness = AlgorithmUtils.calculateFitnessStatistics(pop, p)[AlgorithmUtils.AVG];
       
        // stabalisation occurs when the change in average fitness of the memory
        // cell population is below a desired level - fitness has stabalised
        while(p.canEvaluate() && !hasStabalised)
        {
            // this is the new population of memory cells which we are currently selecting
            LinkedList<CFOSolution> tmpPop = new LinkedList<CFOSolution>();                
            boolean wasReplacement = false;
            
            // for each memory cell, create a clone, locate the best in the clone pool
            // if the best is better than the parent of the clone, accept.               
            for(CFOSolution s : pop)
            {
                // ensure there are evaluations left to continue the cloning process
                if(!p.canEvaluate())
                {
                    // stop creating clones
                    tmpPop.add(s);
                }
                else
                {
                    // create clone and get best of clone
                    CFOSolution bestOfClone = generateCloneAndGetBest(p,s, r);
                    
                    // TODO be more robust
                    if(!p.canEvaluate())
                    {
                        break; // stop
                    }
                    
                    // best could be null if we ran out of evaluations
                    if(bestOfClone != null)
                    {
                        tmpPop.add((p.isBetter(s, bestOfClone)) ? s : bestOfClone); // add the better of the two
                        wasReplacement = true;
                    }
                }
            }
            
            // we have a new population of "best" memory cells - which may or may not be complete
            pop.clear();
            pop.addAll(tmpPop);
            
            if(wasReplacement)
            {
                // calculate new normalized fitness - for mutation
                AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
                // check if the memory cells have stabalised - in that the change in fitness is small enough
                double newAverage = AlgorithmUtils.calculateFitnessStatistics(pop, p)[AlgorithmUtils.AVG];
                if(Math.abs(averageFitness-newAverage) <= stabalisationFactor)
                {
                    hasStabalised = true;
                }
                else
                {
                    averageFitness = newAverage; // we have a new average
                }
            }
            else
            {
                // there was no replacements, thus there is no change in average fitness
                hasStabalised = true;
            }
        }
    }
    
    /**
     * Consolidate the population based on affinity
     * 
     * @param pop
     * @param p
     */
    protected void consolidateNetworkCells(LinkedList<CFOSolution> pop, double largestDistance, Problem prob)
    {
        // safety - check for bugs
        if(pop.isEmpty())
        {
            throw new AlgorithmRunException("Consolidation provided with empty population!");
        }
        
        CFOSolution [] list = pop.toArray(new CFOSolution[pop.size()]);
        int totalRemoved = 0;
        
        for (int i = 0; i < list.length; i++)
        {
            CFOSolution c1 = list[i];
            // ensure that the current solution still exists in the population
            if(!pop.contains(c1))
            {
                continue;
            }
            // search for all solutions that are within the distance radius
            // and remove all solutions in the radius with worse fitness
            for (int j = i + 1; j < list.length; j++)
            {
                CFOSolution c2 = list[j];
                if(!pop.contains(c2))
                {
                    continue;
                }                
                double dist = CFOUtils.euclideanDistance(c1, c2);
                if(dist/largestDistance <= supressionThreshold)
                {
                    if(prob.isBetter(c1, c2))
                    {
                        pop.remove(c2);
                        totalRemoved++;
                    }
                }
            }
        }
        
        // safety - check for bugs
        if(pop.isEmpty())
        {
            throw new AlgorithmRunException("Consolidation has deleted the entire population!");
        }
    }
    
    
    protected CFOSolution generateCloneAndGetBest(Problem p, CFOSolution parent, Random r)
    {
        LinkedList<CFOSolution> clone = new LinkedList<CFOSolution>();
        
        for (int i = 0; i < totalClonesPerCell; i++)
        {
            // create clone
            CFOSolution c = new CFOSolution(parent);
            // mutate clone
            double alpha = ImmuneSystemUtils.mutationProbabilityOPAINET(parent.getNormalizedRelativeScore(), scaleAffinityProportionalSelection);
            // do mutation
            CFOUtils.realValueLocalGaussianMutate(c, (CFOProblemInterface)p, r, alpha);
            clone.add(c);
        }
        // evaluate the cost of the new pop
        p.cost(clone);
        // return the best solution in the clone
        return AlgorithmUtils.getBest(clone, p);
    }
    
    @Override
    public String getName()
    {
        return "Optimized Artificial Immune Network (opt-aiNET)";
    }
    
    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // supression threshold
        if(supressionThreshold<0 || supressionThreshold>1)
        {
            throw new InvalidConfigurationException("Invalid supressionThreshold " + supressionThreshold);
        }
        // init total cells
        if(initialTotalCells<=0)
        {
            throw new InvalidConfigurationException("Invalid initialTotalCells " + initialTotalCells);
        }
        // clones per cell
        if(totalClonesPerCell<=0)
        {
            throw new InvalidConfigurationException("Invalid totalClonesPerCell " + totalClonesPerCell);
        }
        // new comers
        if(percentageNewComers<0 || percentageNewComers>1)
        {
            throw new InvalidConfigurationException("Invalid percentageNewComers " + percentageNewComers);
        }
        // scale affinity selection
        if(scaleAffinityProportionalSelection<1)
        {
            throw new InvalidConfigurationException("Invalid scaleAffinityProportionalSelection " + scaleAffinityProportionalSelection);
        }
        // stabalisation
        if(stabalisationFactor<0 || stabalisationFactor>1)
        {
            throw new InvalidConfigurationException("Invalid stabalisationFactor " + stabalisationFactor);
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

    public double getSupressionThreshold()
    {
        return supressionThreshold;
    }

    public void setSupressionThreshold(double supressionThreshold)
    {
        this.supressionThreshold = supressionThreshold;
    }

    public int getInitialTotalCells()
    {
        return initialTotalCells;
    }

    public void setInitialTotalCells(int initialTotalCells)
    {
        this.initialTotalCells = initialTotalCells;
    }

    public int getTotalClonesPerCell()
    {
        return totalClonesPerCell;
    }

    public void setTotalClonesPerCell(int totalClonesPerCell)
    {
        this.totalClonesPerCell = totalClonesPerCell;
    }

    public double getPercentageNewComers()
    {
        return percentageNewComers;
    }

    public void setPercentageNewComers(double percentageNewComers)
    {
        this.percentageNewComers = percentageNewComers;
    }

    public double getScaleAffinityProportionalSelection()
    {
        return scaleAffinityProportionalSelection;
    }

    public void setScaleAffinityProportionalSelection(double scaleAffinityProportionalSelection)
    {
        this.scaleAffinityProportionalSelection = scaleAffinityProportionalSelection;
    }

    public double getStabalisationFactor()
    {
        return stabalisationFactor;
    }

    public void setStabalisationFactor(double stabalisationFactor)
    {
        this.stabalisationFactor = stabalisationFactor;
    }

}
