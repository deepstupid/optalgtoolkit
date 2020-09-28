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
package com.oat.domains.bfo.algorithms.physics;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.RandomUtils;



/**
 * Type: GeneralizedExtremalOptimization<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * 							Updated function calls to utilities where needed
 * </pre>
 */
public class GeneralizedExtremalOptimization extends Algorithm 
{    
    protected FitnessDeltaComparator comparator;
    
    protected long seed = System.currentTimeMillis();
    protected int initialPopulationSize = 100;
    protected double tau = 1.25;
    
    
    @Override
    public String getDetails()
    {
        return "Generalized Extremal Optimization (GEO): " +
                "as described in Fabiano Luis de Sousa; Fernando Manuel Ramos; Roberto Luiz Galski, and Issamu Muraoka. Chapter 3: Generalized extremal optimization: A new meta-heuristic inspired by a model of natural evolution. Leandro N. de Castro and Fernando J. Von Zuben. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 41-60." +
                "added an initial random sample to seed the starting point (like SA)";
    }
    
    public GeneralizedExtremalOptimization()
    {
        comparator = new FitnessDeltaComparator();
    }    
    
    protected static class GEOSolution extends BFOSolution
    {
        protected double fitnessDelta;
        
        public GEOSolution(boolean [] v)
        {
            super(v);
            fitnessDelta = 0.0;
        }
    }
    
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        LinkedList<GEOSolution> pop = new LinkedList<GEOSolution>();        
        // prepare initial population
        while(pop.size() < initialPopulationSize)
        {
            boolean [] b = RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength());
            GEOSolution s = new GEOSolution(b);
            pop.add(s);
        }         
        // evaluate
        p.cost(pop);
        GEOSolution current = AlgorithmUtils.getBest(pop, p);
        //pop = null; // no longer needed
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            // reproduce
            LinkedList<GEOSolution> children = generateChildren(p, current);
            // evaluate
            p.cost(children);            
            if(p.canEvaluate())
            {
                // set fitness deltas
                for (GEOSolution s : children)
                {
                    s.fitnessDelta = (s.getScore() - current.getScore()); 
                }
                // sort by fitness delta (asc)
                Collections.sort(children, comparator);
                // ensure that it worst from best
                if(!p.isMinimization())
                {
                    Collections.reverse(children);
                }
                // select a potential new solution
                GEOSolution newCurrent = null;
                do
                {
                    int selection = r.nextInt(children.size());
                    double prob = Math.pow(selection+1, -tau);
                    if(prob >= r.nextDouble())
                    {
                        // accepted
                        newCurrent = children.get(selection);
                    }
                }
                while(newCurrent == null);
                if(p.isBetter(newCurrent, current))
                {
                    current = newCurrent;
                }
                pop.clear();
                pop.add(current);
                triggerIterationCompleteEvent(p,pop);
            }
        }
    }    
    
    protected class FitnessDeltaComparator implements Comparator<GEOSolution>
    {        
        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         * @param o1
         * @param o2
         * @return
         */
        public int compare(GEOSolution b1, GEOSolution b2)
        {            
            if(b1.fitnessDelta < b2.fitnessDelta)
            {
                return -1;
            }
            else if(b1.fitnessDelta > b2.fitnessDelta)
            {
                return +1;
            }
            return 0;
        }
        
    }
    
  
    
    
    protected LinkedList<GEOSolution> generateChildren(Problem p, GEOSolution current)
    {
        LinkedList<GEOSolution> c = new LinkedList<GEOSolution>();        
        boolean [] parent = current.getBitString();
        
        for (int i = 0; i < parent.length; i++)
        {
        	// duplicate
            boolean [] child = ArrayUtils.copyArray(parent);
            // mutation
            child[i] = !child[i];
            // creation
            GEOSolution s = new GEOSolution(child);
            // storage
            c.add(s);
        }        
        
        return c;
    }
    
    

    @Override
    public String getName()
    {
        return "Generalized Extremal Optimization (GEO)";
    }

    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // popsize
        if(initialPopulationSize<=0)
        {
            throw new InvalidConfigurationException("Invalid initialPopulationSize " + initialPopulationSize);
        }
        // tau
        if(tau<=0)
        {
            throw new InvalidConfigurationException("Invalid tau " + tau);
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


    public int getInitialPopulationSize()
    {
        return initialPopulationSize;
    }


    public void setInitialPopulationSize(int initialPopulationSize)
    {
        this.initialPopulationSize = initialPopulationSize;
    }


    public double getTau()
    {
        return tau;
    }


    public void setTau(double tau)
    {
        this.tau = tau;
    }
}
