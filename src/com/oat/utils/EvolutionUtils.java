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
package com.oat.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.Solution;


/**
 * Type: EvolutionUtils<br/>
 * Date: 07/12/2006<br/>
 * <br/>
 * Description: Utilities common to evolutionary algorithms
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 09/01/2007   JBrownlee   Added a generic elitism function
 *                          Added a generic parent-child replacement strategy
 * </pre>
 */
public class EvolutionUtils
{
    
    
    /**
     * Create a new population from a parent and child population based 
     * on the solution scoring.
     * Assumes a one-to-one line up between parent and child solutions 
     * in the provided population lists. parent1 created child1, etc...
     * 
     * @param <T>
     * @param parentPopulation
     * @param childPopulation
     * @param problem
     * @return
     * @throws AlgorithmRunException
     */
    public static <T extends Solution> LinkedList<T> elitistReplacement(
            LinkedList<T> parentPopulation,
            LinkedList<T> childPopulation,
            Problem problem)
            throws AlgorithmRunException
    {
        LinkedList<T> nextGeneration = new LinkedList<T>();
        // ensure both the same size
        if(parentPopulation.size() != childPopulation.size())
        {
            throw new AlgorithmRunException("Parent "+parentPopulation.size()+" and child "+childPopulation.size()+" population sizes do not match as expected.");
        }
        for (int i = 0; i < parentPopulation.size(); i++)
        {
            T p = parentPopulation.get(i);
            T c = childPopulation.get(i);
            nextGeneration.add(problem.isBetter(p, c) ? p : c);
        }
        return nextGeneration;
    }
    
    
    
    /**
     * Generic elitism, assumes that there is room in the next generation 
     * for the best solutions from the last generation.
     * This function DOES change the last generation by sorting it.
     * 
     * @param <T>
     * @param lastGeneration
     * @param nextGeneration
     * @param populationSize
     * @param problem
     * @throws AlgorithmRunException - very careful error detection for population sizing
     */
    public static <T extends Solution> void elitism(
            LinkedList<T> lastGeneration,
            LinkedList<T> nextGeneration,
            int populationSize,
            Problem problem)
        throws AlgorithmRunException
    {   
        // special case where population size is too large
        if(nextGeneration.size() > populationSize)
        {
            throw new AlgorithmRunException("Unable to add elite solutions, next generation exceeds the population size.");
        }        
        // calculate the number of elites
        int numElites = populationSize - nextGeneration.size();        
        // check that some elitism is performed
        if(numElites <= 0)
        {
            throw new AlgorithmRunException("Unable to add elite solutions, next generation is already full.");
        }
        if(numElites > lastGeneration.size())
        {
            throw new AlgorithmRunException("The number of desired elite solutions "+numElites+" exceeds the size of the last generation "+lastGeneration.size());
        }
        // order the population by solution quality
        Collections.sort(lastGeneration);
        // fill the next generation with the best solutions of the previous generation
        for (int i = 0; i < numElites; i++)
        {
            if(problem.isMinimization())
            {
                // take from the start of the list
                nextGeneration.add(lastGeneration.get(i));
            }
            else
            {
                // take from the end of the list
                nextGeneration.add(lastGeneration.get(lastGeneration.size()-1-i));
            }
        }
        
    }
    
    
    /**
     * Elitist selection strategy, trims the provided population to the specified size using an elists selection strategy
     * that removes those solutions with the lowest fitness scorings.
     * @param pop
     * @param desiredSize
     * @param p
     */
    public static <T extends Solution> void elitistSelectionStrategy(LinkedList<T> pop, int desiredSize, Problem p)
    {
        if(pop.isEmpty() || pop.size() < desiredSize || desiredSize<=0)
        {
            throw new AlgorithmRunException("Unable to perform elitst selection strategy popsize["+pop.size()+"], desiredsize["+desiredSize+"]");
        }
        // see if we can avoid the sort
        if(pop.size() != desiredSize)
        {
            // order by fitness
            Collections.sort(pop);
            // remove solutions from the population
            while(pop.size() > desiredSize)
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
        }
    }
    
    
    /**
     * Mutate the provided binary string using bit flips with the specified
     * probability on each bit of flipping
     * @param string
     * @param r
     * @param mutation
     */
    public final static void binaryMutate(boolean [] string, Random r, double probability)
    {        
        // do the mutation thing
        for (int i = 0; i < string.length; i++)
        {
            if(r.nextDouble() < probability)
            {
                string[i] = !string[i]; // invert the bit
            }
        }
    }

    /**
     * Tournament selection, selection of the bout does not permit reselection, 
     * although the selected population does (as expected)
     * 
     * @param pop
     * @param numToSelection
     * @param p
     * @param r
     * @param boutSize
     * @return
     */
    public final static <T extends Solution> LinkedList<T> tournamentSelection(
            LinkedList<T> pop, 
            int numToSelection,
            Problem p,
            Random r,
            int boutSize)
    {
        LinkedList<T> selected = new LinkedList<T>();
        
        while(selected.size() < numToSelection)
        {
            // draw a random sample from the population without reselection
            LinkedList<T> sample = RandomUtils.randomSampleWithOutReselection(pop, boutSize, r);
            // locate the best in the sample
            T best = AlgorithmUtils.getBest(sample, p);
            // safety
            if(best == null)
            {
                throw new AlgorithmRunException("Unable to locate best solution in population subset, must population contains many solutions with NaN fitness!");
            }
            // add to the population
            selected.add(best);
        }
        
        return selected;
    }
    
    
    /**
     * Biased roulette wheel selection 
     * @param <T>
     * @param pop
     * @param numToSelection
     * @param p
     * @param r
     * @return
     */
    public final static <T extends Solution> LinkedList<T> biasedRouletteWheelSelection(
            LinkedList<T> pop, 
            int numToSelection,
            Problem p,
            Random r)
    {
        throw new AlgorithmRunException("Not implemented!");
    }
    

    /**
     * Performs a swap mutation on elements in the data with the specified probability
     * 
     * @param data
     * @param r
     * @param probability
     * @throws AlgorithmRunException - probability out of bounds
     */
    public final static void mutatePermutation(int [] data, Random r, double probability)
        throws AlgorithmRunException
    {                
        if(!AlgorithmUtils.inBounds(probability, 0, 1))
        {
            throw new AlgorithmRunException("Probability out of bounds [0,1] " + probability);
        }
        
        for (int i = 0; i < data.length; i++)
        {
            if(r.nextDouble() <= probability)
            {
                // random swap
                ArrayUtils.swap(i, r.nextInt(data.length), data);
            } 
        }
    }

    /**
     * Performs a swap mutation on elements in the data with the specified probability
     * @param data
     * @param r
     * @param probability
     */
    public final static void mutatePermutation(byte [] data, Random r, double probability)
    {                
        for (int i = 0; i < data.length; i++)
        {
            if(r.nextDouble() <= probability)
            {
                // random swap
                ArrayUtils.swap(i, r.nextInt(data.length), data);
            } 
        }
    }
    
    /**
     * Perform a binary crossover operation. Copies the first part from p1, and the second part to p2
     * The first part being from zero to the cutpoint and the second being from the cut point
     * to the end of the array
     * @param v
     * @param p1
     * @param p2
     * @param cutpoint
     */
    public final static void performBinaryCrossover(boolean [] v, boolean [] p1, boolean [] p2, int cutpoint)
    {
        System.arraycopy(p1, 0, v, 0, cutpoint);
        System.arraycopy(p2, cutpoint, v, cutpoint, v.length-cutpoint);
    }

    /**
     * Perform a binary crossover operation (one point), which results in two new binary strings
     * The probability specifies the likely hood of the crossover occuring, if not, then two clones are created
     * 
     * @param p1
     * @param p2
     * @param r
     * @param probability
     * @return
     */
    public final static boolean [][] onePointBinaryCrossover(boolean [] p1, boolean [] p2, Random r, double probability)
    {      
        // make a cut - or no cut as it were
        int cutPoint = (r.nextDouble()<probability) ? r.nextInt(p1.length) : 0;
        // create vectors
        boolean [] v1 = new boolean[p1.length];
        boolean [] v2 = new boolean[p1.length];
        // prepare vectors
        performBinaryCrossover(v1, p1, p2, cutPoint); // normal
        performBinaryCrossover(v2, p2, p1, cutPoint); // reversed
        // store children
        return new boolean[][]{v1,v2};
    }

    /**
     * Perform uniform crossover between the two parents to create two children.
     * The crossover occurs with the probability specified, if it does not occur,
     * then two clones are created
     * @param p1
     * @param p2
     * @param r
     * @param proability
     * @return
     */
    public final static double [][] uniformCrossover(double [] p1, double [] p2, Random r, double proability)
    {
        double [] child1 = new double[p1.length];
        double [] child2 = new double[p1.length];
        if(r.nextDouble() < proability)
        {
            for (int i = 0; i < p1.length; i++)
            {
                child1[i] = r.nextBoolean() ? p1[i] : p2[i];
                child2[i] = r.nextBoolean() ? p1[i] : p2[i];
            }     
        }
        else
        {
            for (int i = 0; i < p1.length; i++)
            {
                child1[i] = p1[i];
                child2[i] = p2[i];
            }
        }
        return new double[][]{child1, child2};       
    }
    
    
    /**
     * Perform uniform crossover between the two parents to create two children.
     * The crossover occurs with the probability specified, if it does not occur,
     * then two clones are created
     * @param p1
     * @param p2
     * @param r
     * @param proability
     * @return
     */
    public final static byte [][] uniformCrossover(byte [] p1, byte [] p2, Random r, double proability)
    {
        byte [] child1 = new byte[p1.length];
        byte [] child2 = new byte[p1.length];
        if(r.nextDouble() < proability)
        {
            for (int i = 0; i < p1.length; i++)
            {
                child1[i] = r.nextBoolean() ? p1[i] : p2[i];
                child2[i] = r.nextBoolean() ? p1[i] : p2[i];
            }     
        }
        else
        {
            for (int i = 0; i < p1.length; i++)
            {
                child1[i] = p1[i];
                child2[i] = p2[i];
            }
        }
        return new byte[][]{child1, child2};       
    }

}
