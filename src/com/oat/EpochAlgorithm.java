/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat;

import java.util.LinkedList;

/**
 * Description: Generic abstract algorithm that supports an epoch (generational)
 * execution behaviour, such as the genetic algorithm and ant colony algorithms
 * that work with populations of solutions at a time
 *  
 * Date: 10/09/2007<br/>
 * @author Jason Brownlee
 * @param <S> 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class EpochAlgorithm<S extends Solution> extends Algorithm
{     
    /**
     * Executes the epoch-based algorithm by first initializing the algorithm, executing for as
     * many epochs that the stop conditions allow, and then performing any required cleanup
     * Manages all evaluations of returned populations, and checking of the stop condition. Also
     * manages triggers to the epoch complete listeners.
     * 
     * @param problem
     * @throws AlgorithmRunException
     * @throws SolutionEvaluationException
     */
    protected void executeEpochAlgorithm(Problem problem) 
    	throws AlgorithmRunException, SolutionEvaluationException
    {
        // initialize the population
        LinkedList<S> population = internalInitialiseBeforeRun(problem);
        // evaluate 
        if(population!=null && !population.isEmpty())
        {
        	problem.cost(population);
        }
        // run for as long as there are evaluations
        while(problem.canEvaluate())
        {
            // epoch event - because the solutions are known to have been evaluated
            triggerIterationCompleteEvent(problem, population);
            // perform the epoch
            LinkedList<S> children = internalExecuteEpoch(problem, population);
            // evaluate the population
            problem.cost(children);
            if(problem.canEvaluate())
            {
	            // any additional things
	            internalPostEvaluation(problem, population, children);	            
	            // replace
	            population = children;
            }
        }
    }
    
    /**
     * Create the initial or base epoch of the run, may be null
     * @param problem
     * @return - first generation or epoch of solutions, may be null, solutions are not evaluated
     */
    protected abstract LinkedList<S> internalInitialiseBeforeRun(Problem problem);
    
    /**
     * Called as many times as allowed by the problems stop conditions. Executes a single epoch of the algorithm 
     * @param problem
     * @param population
     * @return - the solutions created, but not evaluated in this epoch
     */
    protected abstract LinkedList<S> internalExecuteEpoch(Problem problem, LinkedList<S> population);    
    
    /**
     * Any required post-epoch cleanup, such as use of the solutions created from the most recent epoch.
     * @param problem
     * @param oldPopulation
     * @param newPopulation
     */
    protected abstract void internalPostEvaluation(Problem problem, LinkedList<S> oldPopulation, LinkedList<S> newPopulation);
        
    @Override
    protected void internalExecuteAlgorithm(Problem aProblem) 
		throws AlgorithmRunException, SolutionEvaluationException
    {
    	executeEpochAlgorithm(aProblem);
    }
}
