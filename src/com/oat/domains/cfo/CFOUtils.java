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
package com.oat.domains.cfo;

import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BitStringUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: FuncOptUtils<br/>
 * Date: 07/12/2006<br/>
 * <br/>
 * Description: Utility methods for the Function Optimization problem domain
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 06/01/2007   JBrownlee   Added additional check for total children in reproduceBinary()
 * 09/01/2007   JBrownlee   Added a generic population replacement function - used in niching
 *                          Added a function to generate a population of random-real valued solution
 *                          Added generic fitness sharing functionality
 * 07/08/2007   JBrownlee   Moved to com.oat.funcopt
 * 20/08/2007	JBrownlee	Moved fitness sharing utilities to BFOUtils (binary in nature)
 * </pre>
 */
public class CFOUtils
{    

	public static double [] decode(BFOSolution s, CFOProblemInterface p)
	{
		return BitStringUtils.decode(p.getDecodeMode(), s.getBitString(), p.getMinmax());
	}
	
	
    /**
     * Returns the length of a diagional line across the domain
     * from the min to the max coord - this is the maximum distance
     * any two points can be from each other in this problem domain
     * @return
     */
    public static double getLargestEuclideanDistanceInDomain(CFOProblemInterface p)
    {
        double [][] minmax = p.getMinmax();
        
        double [] mins = new double[p.getDimensions()];
        for (int i = 0; i < mins.length; i++)
        {
            mins[i] = minmax[i][0];
        }
        double [] maxs = new double[p.getDimensions()];
        for (int i = 0; i < maxs.length; i++)
        {
            maxs[i] = minmax[i][1];
        }
        return AlgorithmUtils.euclideanDistance(mins, maxs);
    }
    
    
    /**
     * Perform an euclidean-based replacement strategy, suitable for used in
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
    public static <T extends BFOSolution> int euclideanBasedReplacement(
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
            T mostSimilar = getMostSimilarEuclidean(c, sample, (CFOProblemInterface)p);
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
     * Locate the most similar solution in the provided collection tot he specified solution
     * @param s
     * @param pop
     * @return
     */
    public final static <T extends BFOSolution> T getMostSimilarEuclidean(
    		T s, 
    		LinkedList<T> pop, 
    		CFOProblemInterface p)
    {
    	
        T best = null;
        double bestD = Double.POSITIVE_INFINITY;
        double [] d1 = decode(s, p);
        
        for(T f : pop)
        {        	
        	double [] d2 = decode(f, p);
        	double d = AlgorithmUtils.euclideanDistance(d1, d2);
        	
            if(d < bestD)
            {
                bestD = d;
                best = f;
            }
        }
        return best;
    }
    
   
  /*  public final static <T extends Solution> T getMostSimilarEuclidean(
    		double[] s, 
    		double[][] pop, 
    		LinkedList<T> popSolutions,
    		CFOProblemInterface p)
    {
    	
        T best = null;
        double bestD = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < pop.length; i++)
		{
        	double d = AlgorithmUtils.euclideanDistance(s, pop[i]);
        	
            if(d < bestD)
            {
                bestD = d;
                best = popSolutions.get(i);
            }
		}
        
        return best;
    }*/
    
    
    /**
     * Get a random population of real-valued solutions for function optimization
     * 
     * @param r
     * @param p
     * @param size
     * @return
     */
    public final static LinkedList<CFOSolution> getRandomPopulationReal(
            Random r, 
            CFOProblemInterface p, 
            int size)
    {
        LinkedList<CFOSolution> pop = new LinkedList<CFOSolution>();
        
        while(pop.size() < size)
        {
            CFOSolution s = new CFOSolution(RandomUtils.randomPointInRange(r, p.getMinmax()));
            pop.add(s);
        }
        
        return pop;
    }
    
    

    
    
    /**
     * Mutates each dimension with the probability of "mutation" using a gaussian random number.
     * The size of the gaussian is the size of the dimension, and the stdev manages the fall off  
     * 
     * @param s
     * @param a
     * @param r
     * @param mutation - the probability of each dimension being mutated
     * @param stdev - the standard deviation of the gaussian 
     */
    public final static void realValueGlobalGaussianMutate(
            double [] coord, 
            CFOProblemInterface p, 
            Random r, 
            double mutation, 
            double stdev)
    {   
        double [][] minmax = p.getMinmax();
        for (int i = 0; i < coord.length; i++)
        {
            if(r.nextDouble() < mutation)
            {
                double range = (minmax[i][1] - minmax[i][0]); 
                coord[i] += (r.nextGaussian() * (range * stdev));
            }
        }
        AlgorithmUtils.fixCoordBounds(coord, p.getMinmax(), p.isToroidal());
    }
    
    /**
     * Mutates each element of the real-valued solution coordinate 
     * using a random gaussian multiplied by the provided factor
     * 
     * The resulting coordinate if fixed to be valid within the problem domain (range)
     * 
     * @param s
     * @param a
     * @param r
     * @param factor
     */
    public final static void realValueLocalGaussianMutate(CFOSolution s, CFOProblemInterface a, Random r, double factor)
    {   
        double [] p = s.getCoordinate();
        for (int i = 0; i < p.length; i++)
        {
            p[i] = p[i] + (factor * r.nextGaussian());    
        }
        AlgorithmUtils.fixCoordBounds(p, a.getMinmax(), a.isToroidal());
    }
    
    
    
    /**
     * Calculate the Euclidean distance between two CFO solution
     * @param s1
     * @param s2
     * @return
     */
    public final static <T extends CFOSolution> double euclideanDistance(T s1, T s2)
    {
        return AlgorithmUtils.euclideanDistance(s1.getCoordinate(), s2.getCoordinate());
    }
    
    
    public final static <T extends BFOSolution> double [][] decodeGroup(CFOProblemInterface p, LinkedList<T> pop)
    {
    	double [][] coords = new double[pop.size()][];
    	
    	for (int i = 0; i < coords.length; i++)
		{
    		coords[i] = CFOUtils.decode(pop.get(i), p);
		}
    	
    	return coords;
    }
}
