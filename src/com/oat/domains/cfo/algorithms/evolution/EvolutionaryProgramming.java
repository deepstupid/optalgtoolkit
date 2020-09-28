/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2007  Jason Brownlee

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
package com.oat.domains.cfo.algorithms.evolution;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: EvolutionaryProgramming<br/>
 * Date: 09/01/2007<br/>
 * <br/>
 * Description: Evolutionary Programming (EP) and Fast EP as specified in: 
 *  Evolutionary programming made faster (1999)
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
public class EvolutionaryProgramming extends Algorithm
	implements AutomaticallyConfigurableAlgorithm
{
	// configuration
    protected long seed;
    protected int populationSize;
    protected double rFactor;
    protected double rPrimeFactor;
    protected int numOpponents;
    protected boolean updateStdevFirst;
    protected boolean useFastEPUpdate;
    
    // util
    protected WinsComparator comparator = new WinsComparator();
    
    
    
    public EvolutionaryProgramming()
    {
    	automaticConfiguration(2, this);
    }   
    
    @Override
    public void automaticallyConfigure(Problem problem)
    {
    	automaticConfiguration(((CFOProblemInterface)problem).getDimensions(), this);
    }
    
    public static void automaticConfiguration(int numDimensions, EvolutionaryProgramming algorithm)
    {
    	algorithm.setSeed(System.currentTimeMillis());
    	algorithm.setPopulationSize(100); // ?
    	algorithm.setRFactor(defaultRFactor(numDimensions));
    	algorithm.setRPrimeFactor(defaultRPrimeFactor(numDimensions));
    	algorithm.setNumOpponents(10); // ?
    	algorithm.setUpdateStdevFirst(true); // better
    	algorithm.setUseFastEPUpdate(true); // better result
    }
    
    
    
    public static double defaultRFactor(double numDimensions)
    {
        return Math.sqrt(2 * Math.sqrt(numDimensions));
    }
    public static double defaultRPrimeFactor(double numDimensions)
    {
        return Math.pow(Math.sqrt(2 * numDimensions), -1);
    }
    
    @Override
    public String getDetails()
    {
        return "As specified in: Evolutionary programming made faster (1999) " +
        "Supports updating stdev's before updating coordinates by default. ";
    }

    @Override
    public String getName()
    {
        return "Evolutionary Programming (EP)";
    }

    protected class EPSolution extends CFOSolution
    {
        private final double [] stdevs;
        private int numWins;
        
        public EPSolution(EPSolution parent)
        {
            super(ArrayUtils.copyArray(parent.getCoordinate()));
            stdevs = ArrayUtils.copyArray(parent.stdevs);
        }
        
        public EPSolution(double [] aCoord)
        {
            super(aCoord);
            stdevs = new double[aCoord.length];
            Arrays.fill(stdevs, 3); // default specified in paper
        }         
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        // prepare initial population
        LinkedList<EPSolution> pop = new LinkedList<EPSolution>();
        while(pop.size() < populationSize)
        {
            EPSolution s = new EPSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax()));
            pop.add(s);
        }
        p.cost(pop);
        // run until no more evaluations
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p, pop);
            // prepare the next generation
            LinkedList<EPSolution> nextGen = createNextGeneration(pop, r, (CFOProblemInterface)p);
            // evaluate
            p.cost(nextGen);
            // perform tournament-like selection
            if(p.canEvaluate())
            {
                pop = tournamentLikeSelection(nextGen, pop, p, r);
            }
        }
    }
    
    protected LinkedList<EPSolution> tournamentLikeSelection(
            LinkedList<EPSolution> children, 
            LinkedList<EPSolution> parents,
            Problem p,
            Random r)
    {        
        LinkedList<EPSolution> union = new LinkedList<EPSolution>();
        union.addAll(parents);
        union.addAll(children);
        Collections.shuffle(union, r); // in case not many solutions win
        // perform tournaments
        for(EPSolution e : union)
        {
            // get tournament sample
            LinkedList<EPSolution> sample = RandomUtils.randomSampleWithReselection(union, numOpponents, r);
            boolean isBetter = true; // optimistic
            for (EPSolution s : sample)
            {
                // not better or equal - loose chance to win
                if(!p.isBetter(e, s) && e.getScore()!=s.getScore())
                {
                    isBetter = false;
                    break;
                }
            }
            if(isBetter)
            {
                e.numWins++;
            }
        }
        // order by the number of wins, ascending
        Collections.sort(union, comparator);
        // take all solutions with the most wins       
        LinkedList<EPSolution> pop = new LinkedList<EPSolution>();
        for (int i = 0; i < populationSize; i++)
        {
            // work backwards because sorted ascending
            pop.add(union.get(union.size()-1-i));
        }
        
        return pop;
    }
    
    protected class WinsComparator implements Comparator<EPSolution>
    {
        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         */
        public int compare(EPSolution p1, EPSolution p2)
        {            
            if(p1.numWins < p2.numWins)
            {
                return -1;
            }
            else if(p1.numWins > p2.numWins)
            {
                return +1;
            }
            return 0; // same
        }
        
    }
    
    /**
     * Prepare the next generation of coordinates using the EP methodology
     * @param parents
     * @param r
     * @param p
     * @return
     */
    protected LinkedList<EPSolution> createNextGeneration(LinkedList<EPSolution> parents, Random r, CFOProblemInterface p)
    {
        LinkedList<EPSolution> children = new LinkedList<EPSolution>();
        for(EPSolution s : parents)
        {            
            // clone the parent
            EPSolution c = new EPSolution(s);            
            
            // NOTE: the order of the two update can be swapped,
            // and may give better performance according to the paper...
            if(updateStdevFirst)
            {
                // update standard deviations
                updateStdevs(c.stdevs, r);
                // update coordinate
                updateCoordinates(c.getCoordinate(), c.stdevs, r, p);
            }
            else
            {
                // update coordinate
                updateCoordinates(c.getCoordinate(), c.stdevs, r, p);
                // update standard deviations
                updateStdevs(c.stdevs, r);
            }            

            // add to the next generation
            children.add(c);
        }
        return children;
    }
    
    protected void updateStdevs(double [] stdevs, Random r)
    {
        for (int i = 0; i < stdevs.length; i++)
        {
            stdevs[i] = stdevs[i] * Math.exp(rPrimeFactor * r.nextGaussian() + rFactor * r.nextGaussian());
        }
    }
    
    protected void updateCoordinates(double [] coord, double [] stdevs, Random r, CFOProblemInterface p)
    {
        for (int i = 0; i < coord.length; i++)
        {
            coord[i] = coord[i] + stdevs[i] * ((useFastEPUpdate) ? RandomUtils.nextCauchy(r) : r.nextGaussian());
        }
        
        // bound the coordinate
        AlgorithmUtils.fixCoordBounds(coord, p.getMinmax(), p.isToroidal());
    }

    @Override
    public void validateConfiguration() 
        throws InvalidConfigurationException
    {
        if(populationSize<=0)
        {
            throw new InvalidConfigurationException("Invalid population size " + populationSize);
        }
        if(numOpponents<=0 || numOpponents>2*populationSize)
        {
            throw new InvalidConfigurationException("Invalid num opponents " + numOpponents);
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
    public int getPopulationSize()
    {
        return populationSize;
    }
    public void setPopulationSize(int populationSize)
    {
        this.populationSize = populationSize;
    }
    public double getRFactor()
    {
        return rFactor;
    }
    public void setRFactor(double factor)
    {
        rFactor = factor;
    }
    public double getRPrimeFactor()
    {
        return rPrimeFactor;
    }
    public void setRPrimeFactor(double primeFactor)
    {
        rPrimeFactor = primeFactor;
    }
    public int getNumOpponents()
    {
        return numOpponents;
    }
    public void setNumOpponents(int numOpponents)
    {
        this.numOpponents = numOpponents;
    }
    public boolean getUpdateStdevFirst()
    {
        return updateStdevFirst;
    }
    public void setUpdateStdevFirst(boolean updateStdevFirst)
    {
        this.updateStdevFirst = updateStdevFirst;
    }
    public boolean getUseFastEPUpdate()
    {
        return useFastEPUpdate;
    }
    public void setUseFastEPUpdate(boolean useFastEPUpdate)
    {
        this.useFastEPUpdate = useFastEPUpdate;
    }
}
