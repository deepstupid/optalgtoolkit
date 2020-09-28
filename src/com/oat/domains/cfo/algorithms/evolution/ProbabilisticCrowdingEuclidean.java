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
package com.oat.domains.cfo.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Problem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.algorithms.evolution.ProbabilisticCrowding;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOUtils;
import com.oat.utils.AlgorithmUtils;

/**
 * Type: ProbabilisticCrowdingEuclidean<br/>
 * Date: 04/12/2006<br/>
 * <br/>
 * Description: Deterministic Crowding Genetic Algorithm (niching GA)
 * As described in Crowding and Preselection Revisited (1992)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 20/08/2007	JBrownlee	Updated to extend the binary version with Euclidean distance instead of Hamming
 * </pre>
 */
public class ProbabilisticCrowdingEuclidean extends ProbabilisticCrowding
{   


    @Override
    public String getDetails()
    {
        return "As specified in: O. J. Menshoel and D. E. Goldberg. Probabilistic crowding: deterministic crowding with probabilistic replacement. University of Illinois; 1999; IlliGAL Report No. 99005 . Department of General Engineering, 117 Transportation Building, 104 South Mathews Avenue, Urbana, IL 61801-2996. " +
                "Using Euclidean Distance";
    }


    @Override
    public String getName()
    {
        return "Probabilistic Crowding Euclidean (DC)";
    }

    /**
     * Perform Deterministic crowding replacement strategy
     * Assumes children pop is ordered in the manner of the parents that created the children
     * That is first two children created by first two parents, etc...
     * 
     * @param pop
     * @param children
     * @param p
     */
    @Override
    protected <T extends BFOSolution> LinkedList<T> replacements(
            LinkedList<T> pop, 
            LinkedList<T> children, 
            Problem p,
            Random r)
    {
        LinkedList<T> np = new LinkedList<T>();
        
        // calculate normalized fitness for all solutions (pop and children)
        LinkedList<T> superpop = new LinkedList<T>();
        superpop.addAll(pop);
        superpop.addAll(children);
        AlgorithmUtils.calculateNormalizedRelativeFitness(superpop, p);  
        
        // replacements
        for (int i = 0; i < children.size(); i+=2)
        {
            T c1 = children.get(i);
            T c2 = children.get(i+1);
            T p1 = pop.get(i);
            T p2 = pop.get(i+1);
        	
            double [] c1c = CFOUtils.decode(c1, (CFOProblemInterface)p);
            double [] c2c = CFOUtils.decode(c2, (CFOProblemInterface)p);
            double [] p1c = CFOUtils.decode(p1, (CFOProblemInterface)p);
            double [] p2c = CFOUtils.decode(p2, (CFOProblemInterface)p);
            
            if(AlgorithmUtils.euclideanDistance(p1c,c1c)+AlgorithmUtils.euclideanDistance(p2c,c2c) 
                    <= AlgorithmUtils.euclideanDistance(p1c,c2c)+AlgorithmUtils.euclideanDistance(p2c,c1c))
            
            {
                if(canReplace(c1, p1, r))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p1);
                }
                if(canReplace(c2, p2, r))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p2);
                }
            }
            else
            {
                if(canReplace(c2, p1, r))
                {
                    np.add(c2);
                }
                else
                {
                    np.add(p1);
                }
                if(canReplace(c1, p2, r))
                {
                    np.add(c1);
                }
                else
                {
                    np.add(p2);
                }
            }
                
        }
        
        return np;
    }  
}
