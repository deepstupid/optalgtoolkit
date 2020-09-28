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
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: AdaptiveClonalSelection<br/>
 * Date: 13/11/2006<br/>
 * <br/>
 * Description: Parameter free adaptive clonal selection
 * 
 * As described in: Parameter free adaptive clonal selection (2004)
 * 
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
public class AdaptiveClonalSelection extends Algorithm
{    
    public final static double ES_ADJUSTMENT_PARAM = 1.3;
    
    // paramters
    protected long seed = System.currentTimeMillis();
    protected int randomReplacements = 0;
    protected int popsize = 100;
    
    // automatic parameters
    protected int numClones = 1; // nc
    protected double cloneFactorStrategy = 0.5;
    protected int clonePopSubset = 1; // |F|
    protected double clonePopSubsetStrategy = 0.5;    
    // for reversion if new parameters result in no improvement
    protected double oldCloneFactorStrategy = 1;
    protected double oldNumClones = 0.5;    
    protected double oldClonePopSubsetStrategy = 1;
    protected double oldClonePopSubset = 0.5;
    
    
    @Override
    public String getDetails()
    {
        return "Adaptive Clonal Selection (ACS): " +
                "As described in: Simon M. Garrett. Parameter-free, adaptive clonal selection. Congress on Evolutionary Computing (CEC 2004); Portland Oregon, USA. USA: IEEE Press; 2004: 1052-1058. ISBN: 0-7803-8515-2. " +
                "Slightly simplified prediction of t+1 fitness scheme for automatic parameter adaptation - same result";
    }
    
    public void initialiseAllAutomaticVariables()
    {
        numClones = 1; // nc
        cloneFactorStrategy = 0.5;
        clonePopSubset = 1; // |F|
        clonePopSubsetStrategy = 0.5;    
        oldCloneFactorStrategy = 1;
        oldNumClones = 0.5;  
        oldClonePopSubsetStrategy = 1;
        oldClonePopSubset = 0.5;
    }
    
    
    protected class ACSSolution extends CFOSolution
    {
        protected final double [] stdevs;
        
        public ACSSolution(double [] aCoord, CFOProblemInterface p, Random r)
        {
            super(aCoord);
            stdevs = new double[aCoord.length];
            prepare(p, r);
        }
        
        public ACSSolution(ACSSolution e)
        {
            super(e);
            stdevs = new double[e.stdevs.length];
            System.arraycopy(e.stdevs, 0, stdevs, 0, stdevs.length);
        }
        
        /**
         * only needed for initial random pop
         * @param p
         */
        protected void prepare(CFOProblemInterface p, Random r)
        {
            double [][] minmax = p.getMinmax();
            
            for (int i = 0; i < p.getDimensions(); i++)
            {
                stdevs[i] = (minmax[i][1]-minmax[i][0]) * r.nextDouble();
            }
        }

        public double[] getStdevs()
        {
            return stdevs;
        }
    }
    

    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);        
        LinkedList<ACSSolution> pop = new LinkedList<ACSSolution>();        
        
        // create initial population
        while(pop.size() < popsize)
        {
            pop.add(new ACSSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax()), (CFOProblemInterface)p, r));
        } 
        p.cost(pop);
        double lastPopBestFitness = AlgorithmUtils.getBest(pop, p).getScore();
        
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // clone and mutate current population
            LinkedList<ACSSolution> children = generateChildren(pop, p, r);
            // union with current population
            pop.addAll(children);
            // add random solutions
            for (int i = 0; i < randomReplacements; i++)
            {
                pop.add(new ACSSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax()), (CFOProblemInterface)p, r));
            }
            // costing
            p.cost(pop);            
            // elitest selection
            if(p.canEvaluate())
            {
                EvolutionUtils.elitistSelectionStrategy(pop, popsize, p);
                // automatically adjust parameters
                double currentPopBestFitness = AlgorithmUtils.getBest(pop, p).getScore();
                updateAutomaticParameters(lastPopBestFitness, currentPopBestFitness, p, r);
                lastPopBestFitness = currentPopBestFitness;
            }
        }
    }
    
    protected void updateAutomaticParameters(double lastBest, double currentBest, Problem p, Random r)
    {
        // really, all we care about is, whether or not there was an improvement with the new paramters
        if(p.isBetter(currentBest, lastBest))
        {            
            // use the new parameters as the best known
            oldCloneFactorStrategy = cloneFactorStrategy;
            oldNumClones = numClones;
            oldClonePopSubsetStrategy = clonePopSubsetStrategy;
            oldClonePopSubset = clonePopSubsetStrategy;
        }
        else
        {
            // do nothing, the next batch of parameters will be generated from the current 'old' (best known)
        }
        
        // generate new parameters
        updateCloneFactor(r);
        updateClonePopSubset(r);
    }
    
    
    protected void updateCloneFactor(Random r)
    {
        cloneFactorStrategy = (r.nextBoolean() ? oldCloneFactorStrategy/ES_ADJUSTMENT_PARAM : oldCloneFactorStrategy*ES_ADJUSTMENT_PARAM);
        numClones = (int) Math.round(RandomUtils.randomGaussian(oldNumClones, oldCloneFactorStrategy, r));
        // paramter fixing
        if(numClones < 1)
        {
            numClones = 1;            
        }
        else if(numClones > popsize)
        {
            numClones = popsize;
        }
    }
    protected void updateClonePopSubset(Random r)
    {
        clonePopSubsetStrategy = (r.nextBoolean() ? oldClonePopSubsetStrategy/ES_ADJUSTMENT_PARAM : oldClonePopSubsetStrategy*ES_ADJUSTMENT_PARAM);
        clonePopSubset = (int) Math.round(RandomUtils.randomGaussian(oldClonePopSubset, oldClonePopSubsetStrategy, r));
        // paramter fixing
        if(clonePopSubset < 1)
        {
            clonePopSubset = 1;            
        }
        else if(clonePopSubset > popsize)
        {
            clonePopSubset = popsize;
        }
    }
    


    protected LinkedList<ACSSolution> generateChildren(LinkedList<ACSSolution> pop, Problem p, Random r)
    {
        LinkedList<ACSSolution> np = new LinkedList<ACSSolution>();
        
        // calculate relative normalized fitness
        AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);        
        // clone and mutate in one step
        for (int i = 0; i < clonePopSubset; i++)
        {
            // ensure we only clone the best F
            ACSSolution s = (p.isMinimization()) ? pop.get(i) : pop.get((pop.size()-1) - i);
            for (int j = 0; j < numClones; j++)
            {
                ACSSolution c = cloneAndMutate(s, (CFOProblemInterface)p, r);
                np.add(c);
            }
        }
        
        return np;
    }
    
    protected ACSSolution cloneAndMutate(ACSSolution parent, CFOProblemInterface p, Random r)
    {
        ACSSolution clone = new ACSSolution(parent);
        
        // mutate stdev's
        for (int i = 0; i < clone.stdevs.length; i++)
        {
            clone.stdevs[i] = (r.nextBoolean() ? clone.stdevs[i]/ES_ADJUSTMENT_PARAM : clone.stdevs[i]*ES_ADJUSTMENT_PARAM);
        } 
        
        // mutate coords        
        double [] coord = clone.getCoordinate();
        for (int i = 0; i < coord.length; i++)
        {
            coord[i] = RandomUtils.randomGaussian(coord[i], clone.stdevs[i], r);
        }
        
        AlgorithmUtils.fixCoordBounds(coord, p.getMinmax(), p.isToroidal());
        
        return clone;
    }

    
    @Override
    public String getName()
    {
        return "Adaptive Clonal Selection (ACS)";
    }
    
    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // pop size
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // random replacements
        if(randomReplacements>popsize||randomReplacements<0)
        {
            throw new InvalidConfigurationException("Invalid randomReplacements " + randomReplacements);
        }
        // initialise all automatic parameters
        initialiseAllAutomaticVariables();
    }
    

    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
    }

    public int getRandomReplacements()
    {
        return randomReplacements;
    }

    public void setRandomReplacements(int randomReplacements)
    {
        this.randomReplacements = randomReplacements;
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
