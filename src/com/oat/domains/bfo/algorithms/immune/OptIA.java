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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Type: OptIA<br/>
 * Date: 06/12/2006<br/>
 * <br/>
 * Description: opt-IA Algorithm
 * As specified in:
 * - Vincenzo Cutello; G. Narzisi; Giuseppe Nicosia; Mario Pavone, and G. Sorace. How to Escape Traps using Clonal Selection Algorithms. The First International Conference on Informatics in Control, Automation and Robotics, ICINCO 2004; Setubal, Portugal.  INSTICC Press; 2004: 322-326. 
 * 
 * Questions
 * - The paper poorly describes how to mutate for M (HyperMacroMutation), I assumeded to use 'c'
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Updated to use binary mutation in evolutionary utils
 * 
 * </pre>
 */
public class OptIA extends Algorithm
{
    public enum HYPER_MUTATION {STATIC, INVERSELY_PROPORTIONAL, NEITHER}
    
    // parameters
    protected long seed = System.currentTimeMillis();
    protected int popSize = 10; //d
    protected int numClones = 1; // dup (duplication parameter)
    protected int maxAge = 20; // tauB (max age clones can live)
    protected double c = 0.5; //  related to mutation   
    protected HYPER_MUTATION mutation = HYPER_MUTATION.STATIC;
    protected boolean useHyperMacroMutation = true;
    
    
    /**
     * Type: OptIABinarySolution<br/>
     * Date: 06/12/2006<br/>
     * <br/>
     * Description: A customised solution that has an age
     * <br/>
     * @author Jason Brownlee
     */
    protected class OptIABinarySolution extends BFOSolution
    {
        protected int age;
        protected double parentsScore;
        
        public OptIABinarySolution(boolean [] b)
        {
            super(b);
            age = 0;
            parentsScore = Double.NaN;
        }
        
        public OptIABinarySolution(OptIABinarySolution b)
        {
            super(b);
            age = b.age;
            parentsScore = b.getScore();
        }
    }
    
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        // prepare initial population
        LinkedList<OptIABinarySolution> pop = new LinkedList<OptIABinarySolution>();
        while(pop.size() < popSize)
        {
            pop.add(new OptIABinarySolution(RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength())));
        }
        p.cost(pop); // eval

        while(p.canEvaluate())
        {
            // notify listeners that another generation has completed
            triggerIterationCompleteEvent(p, pop);
            // need normalized relative fitness of pop
            AlgorithmUtils.calculateNormalizedRelativeFitness(pop, p);
            
            // perform hyper mutation
            LinkedList<OptIABinarySolution> hyper = performHyperMutation(pop, p, r);
            if(!hyper.isEmpty())
            {
                p.cost(hyper);
                if(!p.canEvaluate())
                {
                    break;
                }
                updateCloneAges(hyper, p);
            }
            
            // perform hyper macro mutation
            LinkedList<OptIABinarySolution> macro = performHyperMacroMutation(pop, p, r);     
            if(!macro.isEmpty())
            {
                p.cost(macro);
                if(!p.canEvaluate())
                {
                    break;
                }
                updateCloneAges(macro, p);
            }
            
            // combine (age, remove duplicates, trim, birth, and we are done)
            pop = merge(pop, hyper, macro, p, r);
            p.cost(pop); // evaluate in case random solutions were inserted            
            // increase age of the entire population
            increaseAge(pop);
        }
    }
    
    /**
     * Update clones ages if they are better than there parents
     * @param pop
     * @param p
     */
    protected void updateCloneAges(LinkedList<OptIABinarySolution> pop, Problem p)
    {
        for(OptIABinarySolution b : pop)
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
    
    protected void increaseAge(LinkedList<OptIABinarySolution> pop)
    {
        for(OptIABinarySolution b : pop)
        {
            b.age++;
        }    
    }
    
    protected LinkedList<OptIABinarySolution> performHyperMutation(LinkedList<OptIABinarySolution> pop, Problem p, Random r)
    {
        LinkedList<OptIABinarySolution> clones = new LinkedList<OptIABinarySolution>();       
                
        if(mutation != HYPER_MUTATION.NEITHER)
        {
            for(OptIABinarySolution b : pop)
            {
                for (int i = 0; i < numClones; i++)
                {
                    OptIABinarySolution clone = new OptIABinarySolution(b);
                    // do mutation                    
                    if(mutation == HYPER_MUTATION.STATIC)
                    {
                        mutateStaticHypermutation(clone.getBitString(), r);
                    }
                    else if(mutation == HYPER_MUTATION.INVERSELY_PROPORTIONAL)
                    {
                        mutateInverselyProportionalHypermutation(clone.getBitString(), b.getNormalizedRelativeScore(), r);
                    }
                    clones.add(clone);
                }            
            }
        }
        
        return clones;
    }
    
    protected LinkedList<OptIABinarySolution> performHyperMacroMutation(LinkedList<OptIABinarySolution> pop, Problem p, Random r)
    {
        LinkedList<OptIABinarySolution> clones = new LinkedList<OptIABinarySolution>();
        
        if(useHyperMacroMutation)
        {
            for(OptIABinarySolution b : pop)
            {
                for (int i = 0; i < numClones; i++)
                {
                    OptIABinarySolution clone = new OptIABinarySolution(b);                    
                    // do mutation
                    mutateHyperMacroMutation(clone.getBitString(), r);
                    clones.add(clone);
                }            
            }
        }
        
        return clones;
    }
    
    /**
     * H1 - Static Hyper Mutation
     * @param b
     */
    protected void mutateStaticHypermutation(boolean [] b, Random r)
    {
        // bimary mutate
        EvolutionUtils.binaryMutate(b, r, c);
        
//        for (int i = 0; i < b.length; i++)
//        {
//            if(r.nextDouble() <= c)
//            {
//                b[i] = !b[i];
//            }
//        }
    }
    
    /**
     * H2 - Inversely Proportional Hypermutation
     * @param b
     */
    protected void mutateInverselyProportionalHypermutation(boolean [] b, double f, Random r)
    {        
        double prob = ((1.0 - (Math.E/f)) * (c * b.length)) + (c * b.length);
        
        // safety - the above equation does not take to zeros very well
        if(!AlgorithmUtils.inBounds(prob, 0.0, 1.0))
        {
            if(prob < 0)
            {
                prob = 0;
            }
            else if(prob > 1)
            {
                prob = 1;
            }
            else if(AlgorithmUtils.isInvalidNumber(prob))
            {
                throw new AlgorithmRunException("Calculated (H2) - Inversely Proportional Hypermutation is out of bounds: v["+prob+"], f["+f+"], c["+c+"]");
            }
        }
        // binary mutate
        EvolutionUtils.binaryMutate(b, r, prob);
        
//        for (int i = 0; i < b.length; i++)
//        {
//            if(r.nextDouble() <= prob)
//            {
//                b[i] = !b[i];
//            }
//        }
    }
    
    protected void mutateHyperMacroMutation(boolean [] b, Random r)
    {
        // (i + 1 <= j <= length)
        int j = r.nextInt(b.length-1)+1; // j in [1,length]
        int i = (j==1) ? 0 : r.nextInt(j-1); // i in [0, j-1]
        
        // test for safety
        if(!(i+1 <= j))
        {
            throw new AlgorithmRunException("Invalid i or j failed: i + 1 <= j, i["+i+"], j["+j+"]");
        }
        else if(!(j <= b.length))
        {
            throw new AlgorithmRunException("Invalid i or j failed: j <= length, i["+i+"], j["+j+"]");
        }
        
        // TODO: Assume to use 'c' to mutate, the paper is not clear!
        for (; i <=j; i++)
        {
            if(r.nextDouble() <= c) // c?
            {
                b[i] = !b[i];
            }
        }
    }    
    
    protected LinkedList<OptIABinarySolution> merge(
            LinkedList<OptIABinarySolution> pop,
            LinkedList<OptIABinarySolution> popHyp,
            LinkedList<OptIABinarySolution> popMacro,
            Problem p,
            Random r)
    {
        // create a super pop
        LinkedList<OptIABinarySolution> superpop = new LinkedList<OptIABinarySolution>();
        superpop.addAll(pop);
        superpop.addAll(popHyp);
        superpop.addAll(popMacro);
        // perform aging
        performAging(superpop);
        // remove duplicates
        AlgorithmUtils.removeDuplicates(superpop);
        // sort
        if(!superpop.isEmpty())
        {
            Collections.sort(superpop);
            // trim to size
            while(superpop.size() > popSize)
            {
                if(p.isMinimization())
                {
                    superpop.removeLast(); // remove the worst
                }
                else
                {
                    superpop.removeFirst(); // remove the worst
                }
            }
        }
        // birth phase (add new randoms) if pop is too small
        while(superpop.size() < popSize)
        {
            superpop.add(new OptIABinarySolution(RandomUtils.randomBitString(r, ((BFOProblemInterface)p).getBinaryStringLength())));
        }
        
        return superpop;
    }
    
    protected void performAging(LinkedList<OptIABinarySolution> p)
    {
        for (Iterator<OptIABinarySolution> iterator = p.iterator(); iterator.hasNext();)
        {
            OptIABinarySolution a = iterator.next();
            if(isTooOld(a))
            {
                iterator.remove();
            }            
        }
    }
    
    protected boolean isTooOld(OptIABinarySolution b)
    {
        if(b.age >= maxAge+1)
        {
            return true;
        }
        
        return false;
    }
    

    @Override
    public String getDetails()
    {
        return "As specified in: Vincenzo Cutello; G. Narzisi; Giuseppe Nicosia; Mario Pavone, and G. Sorace. How to Escape Traps using Clonal Selection Algorithms. The First International Conference on Informatics in Control, Automation and Robotics, ICINCO 2004; Setubal, Portugal.  INSTICC Press; 2004: 322-326.";
    }
    

    @Override
    public String getName()
    {
        return "Optimization Immune Algorithm (opt-IA)";
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
        // c
        if(c < 0 || c > 1)
        {
            throw new InvalidConfigurationException("Invalid c " + c);
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

    public double getC()
    {
        return c;
    }

    public void setC(double c)
    {
        this.c = c;
    }

    public HYPER_MUTATION getMutation()
    {
        return mutation;
    }

    public void setMutation(HYPER_MUTATION mutation)
    {
        this.mutation = mutation;
    }

    public boolean getUseHyperMacroMutation()
    {
        return useHyperMacroMutation;
    }

    public void setUseHyperMacroMutation(boolean useHyperMacroMutation)
    {
        this.useHyperMacroMutation = useHyperMacroMutation;
    }

    
    
}

