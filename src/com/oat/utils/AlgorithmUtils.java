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
import java.util.Iterator;
import java.util.LinkedList;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.Solution;


/**
 * Type: AlgorithmUtils<br/>
 * Date: 17/11/2006<br/>
 * <br/>
 * Description: Common algorithm utilities
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   Jbrownlee   getBestInPopulation uses type erasure for all solution-like things
 *                          stricter and better use. Renamed the method to be clear that it will delete
 *                          un-evaluated solutions
 *                          Further, created a new stripping method
 * 15/01/2007   JBrownlee   Fixed bug in getBest() - basically it didn't work, now it does.
 * </pre>
 */
public class AlgorithmUtils
{
    public final static int MIN=0, MAX=1, AVG=2, BEST=3;
    
    /**
     * Fitness statistics of the population
     * All solutions without a score (NaN) are ignored
     * 
     * @param pop
     * @param p
     * @return double []
     *  [0] = min fitness
     *  [1] = max fitness
     *  [2] = avg fitness
     *  [3] = best fitness
     *  null if the population is empty
     */
    public static <T extends Solution> double [] calculateFitnessStatistics(LinkedList<T> pop, Problem p)
    {
        if(pop.isEmpty())
        {
            return null;
        }
        
        double [] v = new double[4];
        v[MIN] = Double.POSITIVE_INFINITY;
        v[MAX] = Double.NEGATIVE_INFINITY;
        int count = 0;
        
        for (T s : pop)            
        {                    
            if(s.isEvaluated())
            {
                double c = s.getScore();
                count++; // count useful fitness
                v[AVG] += c; // sum useful fitness
                // determine fitness bounds
                if(c > v[MAX])
                {
                    v[MAX] = c;
                }
                if(c < v[MIN])
                {
                    v[MIN] = c;
                }
            }
        }
        
        v[AVG] = v[AVG] / count;
        v[BEST] = (p.isMinimization()) ? v[MIN] : v[MAX];
        return v;
    }
    
    
    /**
     * Calculates normalised scores for all solutions relative to the fitness
     * scores of solutions in the population
     * 
     * Always scores in a maximising manner such that the higher the normalised value
     * the better the solution
     * 
     * @param pop
     * @param p
     */
    public static <T extends Solution> void calculateNormalizedRelativeFitness(LinkedList<T> pop, Problem p)
    {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        
        if(pop.isEmpty())
        {
            throw new AlgorithmRunException("Unable to calculate normalized relative fitness, empty population.");
        }
        
        // determine bounds
        for (T s : pop)
        {
            if(s.isEvaluated())
            {
                double c = s.getScore();
                if(c < min)
                {
                    min = c;
                }
                if(c > max)
                {
                    max = c;
                }
            }
        }
        
        // check for the case where all fitness are relatively the same and the range is zero
        if(Double.isInfinite(min) || Double.isInfinite(max) || (max-min) == 0)
        {
            // 0/0 == NaN, thus set the normalized fitness of all solutions to 1 
            for (T s : pop)
            {
                s.setNormalizedRelativeScore(1.0);
            }
            return;
        }
        
        for (T s : pop)
        {
            double n = (s.getScore() - min) / (max-min);
            // invert for minimisation function to make maximised scores
            n = (p.isMinimization()) ? 1-n : n;
            
            // safety
            if(isInvalidNumber(n))
            {
                throw new AlgorithmRunException("Normalized relative fitness is NaN. min="+min+", max="+max+", range="+(max-min)+" score="+s.getScore());
            }
            
            s.setNormalizedRelativeScore(n);
        }
    }    
    
    /**
     * Get the best from the provided population including the current best
     * which may or may not be in the current population
     * 
     * Solutions in the population with an invalid fitness (NaN) are removed from the population
     * Thus, it is possible to have a population returned that is empty.
     * 
     * @param pop
     * @param p
     * @param currentBest
     * @return
     */
    public static <T extends Solution> T stripUnevaluatedAndGetBestInPopulation(
            LinkedList<T> pop, 
            Problem p, 
            T currentBest)
    {
        // discard all those without evaluation
        stripUnevaluatedSolutions(pop);
        
        if(!pop.isEmpty())
        {
            // sort ascending
            Collections.sort(pop);
            // get potential new best
            T newBest = (p.isMinimization()) ? pop.getFirst() : pop.getLast();            
            // no current best
            if(currentBest == null)
            {
                return newBest; // by default
            }
            // check for bew best solution
            if(p.isBetter(newBest, currentBest))
            {
                return newBest; // is better
            }
        }
        
        return currentBest;
    }
    
    /**
     * Delete all those solutions that have not been evaluated from the population
     * @param <T>
     * @param population
     */
    public static <T extends Solution> void stripUnevaluatedSolutions(LinkedList<T> population)
    {
        // discard all those without evaluation        
        for (Iterator<T> iter = population.iterator(); iter.hasNext();)
        {
            T s = iter.next();
            if(!s.isEvaluated())
            {
                iter.remove();
            }                
        }
    }
    
    
    /**
     * Returns the best solution in the population. The ordering of the population is not changed.
     * All solutions with invalid score (NaN) are ignored.
     * 
     * @param pop
     * @param p
     * @return - the best scoring solution in the population
     */
    public static <T extends Solution> T getBest(LinkedList<T> pop, Problem p)
    {
        LinkedList<T> tmp = new LinkedList<T>();
        
        for(T s : pop)
        {
            if(s.isEvaluated())
            {
                tmp.add(s);
            }
        }        
        if(tmp.isEmpty())
        {
            return null;
        }        
        // sort by fitness
        Collections.sort(tmp);
        // return the best
        return (p.isMinimization()) ? tmp.getFirst() : tmp.getLast();
    }
    
    /**
     * Check if a population of solutions is all the same (.equals())s
     * 
     * @param pop
     * @return
     */
    public static <T extends Solution> boolean isConverged(LinkedList<T> pop)
    {
        for (int i = 0; i < pop.size(); i++)
        {
            T s1 = pop.get(i);
            for (int j = i+1; j < pop.size(); j++)
            {
                T s2 = pop.get(j);
                if(!s1.equals(s2))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    
    /**
     * Checks if the provided value is out side the specified bounds or is not a number (NaN) or infinite
     * 
     * @param v
     * @param min
     * @param max
     * @return
     */
    public final static boolean inBounds(double v, double min, double max)
    {
        if(v < min || v > max || isInvalidNumber(v))
        {
            return false;
        }
        return true;
    }
    
    /**
     * Whether or not the provided number is NaN or Infinite
     * @param v
     * @return
     */
    public final static boolean isInvalidNumber(double v)
    {
        if(Double.isNaN(v) || Double.isInfinite(v))
        {
            return true;
        }
        return false;
    }
    
    
    /**
     * Remove duplicates from the provided collection of solutions
     * Uses the .equals method in the Solution object, assumed to be specalised
     * for specific problem representations.
     * @param s
     */
    public final static <T extends Solution> void removeDuplicates(LinkedList<T> ss)
    {
        if(ss.isEmpty())
        {
            return;
        }
        int initialSize = ss.size();
        int totalRemoved = 0;
        // TODO What is the type safe way of doing this?
        T [] s = (T []) ss.toArray(new Solution[ss.size()]);
        for (int i = 0; i < s.length; i++)
        {
            if(s[i] == null)
            {
                continue;
            }
            for (int j = i+1; j < s.length; j++)
            {
                if(s[j] == null)
                {
                    continue;
                }
                if(s[i].equals(s[j]))
                {
                    s[j] = null;
                    totalRemoved++;
                }
            }
        }
        
        ss.clear();
        for (int i = 0; i < s.length; i++)
        {
            if (s[i] != null)
            {
                ss.add(s[i]);
            }
        }
        
        // safety
        if(ss.size() != initialSize - totalRemoved)
        {
            throw new AlgorithmRunException("Failed to effectively remove duplicates, collection size does not match expected.");
        }

    }


    /**
     * Treats the boundaries of the domain as solid, and reflects coordinates back into the 
     * domain if they lie outside of the domain 
     * @param coord - the coordinate to reflect (if required)
     * @param minmax - minmax - bounds of the coordinate space [] = each dimension [i][2] = {min,max} format
     */
    private static void reflectCoord(double [] coord, double [][] minmax)
    {             
        for (int i = 0; i < coord.length; i++)
        {            
            // a bounce could bounce beyond the opposite end of the domain
            while(coord[i] > minmax[i][1] || coord[i] < minmax[i][0])
            {                
                // too large
                while(coord[i] > minmax[i][1])
                {
                    // subtract the difference
                    double diff = Math.abs(coord[i] - minmax[i][1]);
                    // always smaller
                    coord[i] = (minmax[i][1] - diff);
                    
                }
                // too small
                while(coord[i] < minmax[i][0])
                {  
                    double diff = Math.abs(coord[i] - minmax[i][0]);
                    // always larger
                    coord[i] = (minmax[i][0] + diff);                    
                } 
            }
        }
    }


    /**
     * Wrap the provided coordinate back within the bounds of the domain,
     * if it lies out side of the boundary of the domain. Assumes the domain
     * is a torrid
     * @param coord - the coordinate to fix
     * @param minmax - minmax - bounds of the coordinate space [] = each dimension [i][2] = {min,max} format
     */
    private static void wrapCoord(double [] coord, double [][] minmax)
    {        
        for (int i = 0; i < coord.length; i++)
        {
            // a wrap could bounce beyond the opposite end of the domain
            while(coord[i] > minmax[i][1] || coord[i] < minmax[i][0])                
            { 
                // too large
                while(coord[i] > minmax[i][1])
                {
                    coord[i] -= minmax[i][1]; // wrap
                }
                // too small
                while(coord[i] < minmax[i][0])
                {
                    coord[i] += minmax[i][1]; // wrap
                } 
            }
        }
    }


    /**
     * Repair the provided coordinate if it lies outside of the specified domain
     * @param coord - the coordinate to repair
     * @param minmax - minmax - bounds of the coordinate space [] = each dimension [i][2] = {min,max} format
     * @param isToroidal - whether or not the domain is a torrid, or solid boundary hyperspace
     */
    public static void fixCoordBounds(double [] coord, double [][] minmax, boolean isToroidal)
    {
        if(isToroidal)
        {
            wrapCoord(coord, minmax);
        }
        else
        {
            reflectCoord(coord, minmax);
        }
    }


    /**
     * Calculates the Euclidean Distance between the two coordinates.
     * Calcualted as the square root of the sum of the squared differences
     * 
     * @param c1
     * @param c2
     * @return
     */
    public final static double euclideanDistance(double [] c1, double [] c2)
    {        
        // root of the sum of the squared differences (Euclidean)        
        double sum = 0.0;
        for (int i = 0; i < c1.length; i++)
        {
            sum += Math.pow((c1[i] - c2[i]), 2);
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Calculates the Euclidean Distance between the two coordinates.
     * Calcualted as the square root of the sum of the squared differences
     * 
     * @param c1
     * @param c2
     * @return
     */
    public final static double euclideanDistance(double [] c1, float [] c2)
    {        
        // root of the sum of the squared differences (Euclidean)        
        double sum = 0.0;
        for (int i = 0; i < c1.length; i++)
        {
            sum += Math.pow((c1[i] - c2[i]), 2);
        }
        return Math.sqrt(sum);
    }
}
