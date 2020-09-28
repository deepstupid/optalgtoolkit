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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.ImmuneSystemUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: OptIA<br/>
 * Date: 06/12/2006<br/>
 * <br/>
 * Description: opt-IMMALG Algorithm
 * As specified in:
 * V. Cutello; G. Nicosia; M. Pavone, and G. Narzisi. Real Coded Clonal Selection Algorithm for Unconstrained Global Numerical Optimization using a Hybrid Inversely Proportional Hypermutation Operator. 21st Annual ACM Symposium on Applied Computing (SAC); Dijon, France.  2006: 950-954. ACM . 
 * 
 * Problems
 * - does not seem to work very well, perhaps there is a bug in the implementation
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
public class OptIMMALG extends Algorithm
{
    public static enum MUTATION_FUNCTION {CLONALG, OPTAINET}
    
    // parameters
    protected long seed = System.currentTimeMillis();
    protected int popSize = 100; //d
    protected int numClones = 2; // dup (duplication parameter)
    protected int maxAge = 20; // tauB (max age clones can live)
    protected double mutateFactor = 2.5; //  rho   
    protected MUTATION_FUNCTION mutationFunction = MUTATION_FUNCTION.CLONALG;
    
    
    /**
     * Type: OptIMMALGSolution<br/>
     * Date: 06/12/2006<br/>
     * <br/>
     * Description: A customised solution that has an age
     * <br/>
     * @author Jason Brownlee
     */
    protected class OptIMMALGSolution extends CFOSolution
    {
        protected int age;
        protected double parentsScore;
        
        public OptIMMALGSolution(double [] b)
        {
            super(b);
            age = 0;
            parentsScore = Double.NaN;
        }
        
        public OptIMMALGSolution(double [] s, OptIMMALGSolution b)
        {
            super(s);
            age = b.age;
            parentsScore = b.getScore();
        }
    }
    
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        // prepare initial population
        LinkedList<OptIMMALGSolution> pop = new LinkedList<OptIMMALGSolution>();
        while(pop.size() < popSize)
        {
            double [] s = RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax());
            pop.add(new OptIMMALGSolution(s));
        }
        p.cost(pop); // eval

        while(p.canEvaluate())
        {
            // notify listeners that another generation has completed
            triggerIterationCompleteEvent(p, pop);   
            // need normalized relative fitness of pop
            AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
            
            // perform hyper mutation
            LinkedList<OptIMMALGSolution> children = performCloningAndMutation(pop, (CFOProblemInterface)p, r);
            // evaluate
            p.cost(children);            
            // use simple (take best) selection approach
            if(p.canEvaluate())
            {
                // update the age of the clones - those clones with a better fitness than parents have an age of zero
                updateCloneAges(children, p);
                // create super pop
                pop.addAll(children);
                LinkedList<OptIMMALGSolution> duplicate = new LinkedList<OptIMMALGSolution>();
                duplicate.addAll(pop);
                // perform aging
                performAging(pop);
                // order 
                Collections.sort(pop);
                // trim if required
                while(pop.size() > popSize)
                {
                    if(p.isMinimization())
                    {
                        pop.removeLast(); // remove worst
                    }
                    else
                    {
                        pop.removeFirst(); // remove worst
                    }
                }
                // add randoms from union of the two old pops if undersized
                while(pop.size() < popSize)
                {
                    pop.add(duplicate.get(r.nextInt(duplicate.size())));
                }
            }         
            // increase age of the entire population
            increaseAge(pop);
        }
    }
    
    /**
     * Update clones ages if they are better than there parents
     * @param pop
     * @param p
     */
    protected void updateCloneAges(LinkedList<OptIMMALGSolution> pop, Problem p)
    {
        for(OptIMMALGSolution b : pop)
        {
            // safety
            if(Double.isNaN(b.parentsScore))
            {
                throw new AlgorithmRunException("Clone does not have a parent!");
            }
            if(p.isBetter(b.getScore(), b.parentsScore))
            {
                b.age = 0; // reset age
            }
        }
    }
    
    protected void increaseAge(LinkedList<OptIMMALGSolution> pop)
    {
        for(OptIMMALGSolution b : pop)
        {
            b.age++;
        }    
    }
    
    protected LinkedList<OptIMMALGSolution> performCloningAndMutation(LinkedList<OptIMMALGSolution> pop, CFOProblemInterface p, Random r)
    {
        LinkedList<OptIMMALGSolution> clones = new LinkedList<OptIMMALGSolution>();      

        for(OptIMMALGSolution b : pop)
        {
            for (int i = 0; i < numClones; i++)
            {
                // copy
                double [] s = ArrayUtils.copyArray(b.getCoordinate());
                // mutate
                double prob = mutationProbability(b.getNormalizedRelativeScore());
                mutate(p, b.getCoordinate(), s, prob, r);
                // create and add
                OptIMMALGSolution clone = new OptIMMALGSolution(s, b);
                clones.add(clone);
            }            
        }        
        
        return clones;
    }    
    
    
    protected void mutate(CFOProblemInterface p, double [] parent, double [] child, double prob, Random r)
    {
        for (int i = 0; i < child.length; i++)
        {            
            if(r.nextDouble() <= prob)
            {
                int index = i;
                if(p.getDimensions()!=1)
                {
                    while(index != i)
                    {
                        index = r.nextInt(parent.length);
                    }
                }
                
                double xr = parent[index];
                double Beta = r.nextDouble();
                child[i] = ((1.0-Beta)*child[i]) + (Beta * xr);
            }
        }
        
        // fix the coords in case they are out of bounds
        AlgorithmUtils.fixCoordBounds(child, p.getMinmax(), p.isToroidal());
    }
    
    
    protected void performAging(LinkedList<OptIMMALGSolution> p)
    {
        for (Iterator<OptIMMALGSolution> iterator = p.iterator(); iterator.hasNext();)
        {
            OptIMMALGSolution a = iterator.next();
            if(isTooOld(a))
            {
                iterator.remove();
            }            
        }
    }
    
    protected boolean isTooOld(OptIMMALGSolution b)
    {
        if(b.age >= maxAge+1)
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Use the configured mutation function
     * @param f - normalized fitness
     * @return
     */
    protected double mutationProbability(double f)
    {
        // safety
        if(!AlgorithmUtils.inBounds(f,0,1))
        {
            throw new AlgorithmRunException("Expected fitness to be normalized " + f);
        }
        
        double p = 0.0;
        
        switch(mutationFunction)
        {
        case CLONALG:
            p = ImmuneSystemUtils.mutationProbabilityCLONALG(f, mutateFactor);
            break;
        case OPTAINET:
            p = ImmuneSystemUtils.mutationProbabilityOPAINET(f, mutateFactor);
            break;
        default:
            throw new AlgorithmRunException("Unknown mutation probability function: " + mutationFunction);
        }

        return p; 
    }
    

    @Override
    public String getDetails()
    {
        return "As specified in V. Cutello; G. Nicosia; M. Pavone, and G. Narzisi. Real Coded Clonal Selection Algorithm for Unconstrained Global Numerical Optimization using a Hybrid Inversely Proportional Hypermutation Operator. 21st Annual ACM Symposium on Applied Computing (SAC); Dijon, France.  2006: 950-954. ACM .";
    }

    @Override
    public String getName()
    {
        return "Optimization Immune Algorithm (opt-IMMALG)";
    }



    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {
        // popsize
        if(popSize <= 0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popSize);
        }
        // numclones
        if(numClones <= 0 || numClones>popSize)
        {
            throw new InvalidConfigurationException("Invalid numClones " + numClones);
        }
        // maxAge
        if(maxAge <= 0)
        {
            throw new InvalidConfigurationException("Invalid maxAge " + maxAge);
        }
        // mutateFactor
        if(mutateFactor <= 0)
        {
            throw new InvalidConfigurationException("Invalid mutateFactor " + mutateFactor);
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

    public int getMaxAge()
    {
        return maxAge;
    }

    public void setMaxAge(int maxAge)
    {
        this.maxAge = maxAge;
    }

    public double getMutateFactor()
    {
        return mutateFactor;
    }

    public void setMutateFactor(double mutateFactor)
    {
        this.mutateFactor = mutateFactor;
    }

    public MUTATION_FUNCTION getMutationFunction()
    {
        return mutationFunction;
    }

    public void setMutationFunction(MUTATION_FUNCTION mutationFunction)
    {
        this.mutationFunction = mutationFunction;
    }
}

