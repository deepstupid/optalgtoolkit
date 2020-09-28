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
package com.oat.domains.bcr;

import java.util.Arrays;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BitStringUtils;

/**
 * Type: CharRecAlgorithm<br/>
 * Date: 18/12/2006<br/>
 * <br/>
 * Description: Generic binary character recognition problem
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Provided a framework for a generic algorithms
 * </pre>
 */
public abstract class BCRAlgorithm extends Algorithm
{
    /**
     * Generic algorithm preperation before a run
     * @param problem
     */
    protected abstract void initialiseSystem(BCRProblem problem);      
    
    /**
     * The system must generate a response to each pattern it is exposed to
     * @param match
     * @param problem
     * @return
     */
    protected abstract boolean [] respondToPattern(MatchFunction match, Problem problem);
    
    
    @Override
    protected void internalExecuteAlgorithm(Problem problem)
    {
        BCRProblem p = (BCRProblem) problem;
        LinkedList<SystemSolution> pop = new LinkedList<SystemSolution>();
        // prepare the system
        initialiseSystem(p);
        // run the epochs
        while(p.canEvaluate())
        {            
            // create a new solution each epoch
            SystemSolution solution = new SystemSolution();
            // prepare the population
            pop.clear();
            pop.add(solution);            
            // TODO: fix this hack
            // problem.cost -> problem.problemSpecificCost -> solution.response -> this.respondToPattern            
            // evaluate the system
            p.cost(solution);
            // end of an epoch
            triggerIterationCompleteEvent(p, pop);
        }        
    }
    
    /**
     * Type: SystemSolution<br/>
     * Date: 11/07/2007<br/>
     * <br/>
     * Description: Provides an interface into the algorithm, as well as a distinct solution in time
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
    protected class SystemSolution extends BCRSolution
    {
        protected LinkedList<boolean[]> epochWinners = new LinkedList<boolean[]>();
        
        @Override
        public boolean [] response(MatchFunction m, Problem p)
        {
            // allow the algorithm to select the best matching unit
            boolean [] best = respondToPattern(m, p);
            // duplicate the unit
            boolean [] copy = ArrayUtils.copyArray(best);
            // store for later
            epochWinners.add(copy);
            // return the copy
            return copy;
        }
        
        @Override
        public String toString()
        {
            StringBuffer b = new StringBuffer(1000);
            for (int i = 0; i < epochWinners.size(); i++)
            {
                b.append(BitStringUtils.toString(epochWinners.get(i)));
                if(i < epochWinners.size()-1)
                {
                    b.append(",");
                }
            }
            return "["+b.toString()+"]";
        }
        
        @Override
        public boolean [][] getPatterns()
        {
            boolean [][] p = new boolean[epochWinners.size()][];
            for (int i = 0; i < p.length; i++)
            {
                p[i] = epochWinners.get(i); 
            }
            return p;
        }
        
        /**
         *  Must have the same epoch data
         */
        @Override
        public boolean equals(Object other)
        {            
            SystemSolution o = (SystemSolution) other;
            if(epochWinners.size() != o.epochWinners.size())
            {
                return false;
            }
            
            for (int i = 0; i < epochWinners.size(); i++)
            {
                if(!Arrays.equals(epochWinners.get(i), o.epochWinners.get(i)))
                {
                    return false;
                }
            }
            return true;
        }        
    } 
}
