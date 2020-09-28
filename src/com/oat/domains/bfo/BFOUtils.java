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
package com.oat.domains.bfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BitStringUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;

/**
 * Description: 
 *  
 * Date: 20/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BFOUtils
{
	
	/**
	 * Create a population of random bit string solution
	 * @param r
	 * @param p
	 * @param size
	 * @return
	 */
    public final static LinkedList<BFOSolution> getRandomPopulationBinary(
            Random r, 
            BFOProblemInterface p, 
            int size)
    {
        LinkedList<BFOSolution> pop = new LinkedList<BFOSolution>();
        
        while(pop.size() < size)
        {
            boolean [] string = RandomUtils.randomBitString(r, p.getBinaryStringLength());
            BFOSolution s = new BFOSolution(string);
            pop.add(s);
        }
        
        return pop;
    }
    
    /**
     * Calculate a derated fitness scoring using the fitness sharing method.
     * Requires that the provided solution "sol" have an assigned normalized relative 
     * fitness scoring.
     * 
     * @param <T>
     * @param sol
     * @param pop
     * @param shareRadius
     * @param alpha
     * @return
     */
    public static <T extends BFOSolution> double calculateDeratedFitness(
            T sol,
            LinkedList<T> pop,
            double shareRadius,
            double alpha)
    {                
        // calculate the share value for the current solution
        // that is the sum of the normalised fitness of all solutions within range
        double sum = 0.0;
        for (int i = 0; i < pop.size(); i++)
        {
            sum += shareFunction(sol, pop.get(i), shareRadius, alpha);
        }        
        // sum can NEVER be zero - have to at least be the same as self (share with self)
        // 0/radius^alpha == 0 (1-0 == 1), has to have a score of at least 1
        if(sum == 0 || AlgorithmUtils.isInvalidNumber(sum))
        {
            throw new AlgorithmRunException("Calculated an invalid share value " + sum);
        }
        // derate fitness (normalised fitness)
        double deratedFitness = (sol.getNormalizedRelativeScore() / sum);        
        // should NEVER be NaN, should at least have own fitness
        if(AlgorithmUtils.isInvalidNumber(deratedFitness))
        {
            throw new AlgorithmRunException("Assigned invalid number as derated fitness: f["+sol.getScore()+"], nf["+sol.getNormalizedRelativeScore()+"], df["+deratedFitness+"], alpha["+alpha+"], radius["+shareRadius+"].");
        }
        return deratedFitness;
    }
    
    /**
     * Triangle sharing function, used in the fitness sharing method of 
     * score de-ration (scaling)
     * @param s1
     * @param s2
     * @param shareRadius
     * @param alpha
     * @return
     */
    public static double shareFunction(
            BFOSolution s1, 
            BFOSolution s2,
            double shareRadius,
            double alpha)
    {
        // calculate distance between the strings
        double distance = BitStringUtils.hammingDistanceRatio(s1.getBitString(), s2.getBitString());
        // check if outside of the radius
        if(distance >= shareRadius)
        {
            return 0.0;
        }
        // calculate the share value
        return 1.0 - Math.pow((distance/shareRadius), alpha);
    }
    
    
    /**
     * Perform the canonical reproduction process for binary based function optimization solutions
     * 
     * @param pop - selected population
     * @param totalChildren - number of children to produce, must be <= the number of parents
     * @param mutationProbability 
     * @param crossoverProbability
     * @param rand
     * @return - unevaluated produced children
     */
    public static LinkedList<BFOSolution> genericAlgorithmReproduce(
            LinkedList<BFOSolution> pop, 
            int totalChildren, 
            double mutationProbability,
            double crossoverProbability,
            Random rand)
    {
        // ensure that the selected population size is even
        if((pop.size()%2) != 0)
        {
            throw new AlgorithmRunException("Selected population size is not even as expected " + pop.size());
        }
        // the number of children must be <= the number of parents
        else if(totalChildren > pop.size())
        {
            throw new AlgorithmRunException("The specified number of children "+totalChildren+" must be <= the number of parents " + pop.size());
        }
        
        // always mix up the selective set
        Collections.shuffle(pop, rand);
        
        LinkedList<BFOSolution> children = new LinkedList<BFOSolution>();
        for (int i = 0; children.size()<totalChildren && i < pop.size(); i+=2)
        {
            // get parents
        	BFOSolution p1 = pop.get(i);
        	BFOSolution p2 = pop.get(i+1);
            // perform crossover
            boolean [][] b = EvolutionUtils.onePointBinaryCrossover(p1.getBitString(), p2.getBitString(), rand, crossoverProbability);
            // add children if possible
            for (int j = 0; children.size()<totalChildren && j < b.length; j++)
            {
                // mutate
                EvolutionUtils.binaryMutate(b[j], rand, mutationProbability);
                // create
                BFOSolution c = new BFOSolution(b[j]);
                // add
                children.add(c);
            }            
        }
        return children;
    } 
    
    /**
     * Perform an hamming-based replacement strategy, suitable for used in
     * crowding-based niching genetic algorithms (simple crowding, and restricted
     * tournament selection)
     * 
     * @param pop - population to perform replacements upon
     * @param children - the population to replace with
     * @param problem - the problem domain
     * @param rand - random number generator
     * @param sampleSize - size of the sample to draw from the population to perform replacement.
     *                      if sample size == pop.size() then the entire population is used as the sample,
     *                      otherwise a sample of sampleSize is drawn without replacement
     * @return int - the number of replacements performed
     * @throws AlgorithmRunException - the sampleSize is > pop.size()
     */
    public static <T extends BFOSolution> int hammingBasedReplacement(
            LinkedList<T> pop, 
            LinkedList<T> children, 
            Problem p,
            Random rand,
            int sampleSize)
    {               
        if(sampleSize > pop.size())
        {
            throw new AlgorithmRunException("Sample size "+sampleSize+" is larger than the population size " + pop.size());            
        }
        
        LinkedList<T> sample = null;
        int numReplacements = 0;
        
        // go through each child and perform the replacement strategy
        for (T c : children)
        {
            // draw the sample
            sample = (sampleSize==pop.size()) ? pop : RandomUtils.randomSampleWithOutReselection(pop, sampleSize, rand);
            // locate most similar in sample
            T mostSimilar = getMostSimilarHamming(c, sample);
            // perform replacement if the child is of better fitness
            if(p.isBetter(c, mostSimilar))
            {
                // remove the most similar from the pop
                pop.remove(mostSimilar);
                // add child to pop
                pop.add(c);
                // count replacements 
                numReplacements++;
            }
        } 
        return numReplacements;
    }
    
    /**
     * Locate the most similar (Hamming distance) in a sample to a specific solution
     * @param <T>
     * @param s
     * @param pop
     * @return
     */
    public final static <T extends BFOSolution> T getMostSimilarHamming(T s, LinkedList<T> pop)
    {
        T best = null;
        double bestD = Double.POSITIVE_INFINITY;
        for(T f : pop)
        {
            double d = BitStringUtils.hammingDistance(s, f);
            if(d < bestD)
            {
                bestD = d;
                best = f;
            }
        }
        return best;
    }
}
