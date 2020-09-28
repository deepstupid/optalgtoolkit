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
package com.oat.domains.examples;

import com.oat.AlgorithmExecutor;
import com.oat.domains.cfo.CFOSolution;
import com.oat.domains.cfo.algorithms.evolution.DifferentialEvolution;
import com.oat.domains.cfo.problems.geatbx.SchwefelsFunction;
import com.oat.probes.BestSolutionProbe;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Type: ExampleFuncOptVariableDimensionality<br/>
 * Date: 30/11/2006<br/>
 * <br/>
 * Description: Provides a code example for variable dimensionality function optimization
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleFuncOptVariableDimensionality
{
    /**
     * Provides a code example for variable dimensionality function optimization
     * Executes Differential Evolution of Schwefels Function for dimensions 1 to 10 inclusive
     * @param args
     */
    public static void main(String[] args)
    {
        // prepare the problem domain
        SchwefelsFunction problem = new SchwefelsFunction();
        // maximum function evaluations for this run
        EvaluationsStopCondition stopCondition = new EvaluationsStopCondition(10000); 
        // prepare the algorithm
        DifferentialEvolution algorithm = new DifferentialEvolution();
        // use of a consistant random seed gurantees the same result each run (reproducible)
        // for different results each run use System.currentTimeMillis()
        // this system time seed is the default random number seed for all randomized algorithms
        algorithm.setSeed(1);
        // execute the algorithm for each dimension from 1 to 10 inclusive
        AlgorithmExecutor executor = new AlgorithmExecutor(problem, algorithm, stopCondition);
        BestSolutionProbe probe = new BestSolutionProbe();
        executor.addRunProbe(probe);
        for (int i = 1; i <= 10; i++)
        { 
            // set the desired dimensionality
            problem.setDimensions(i);
            try
            {
            	executor.executeAndWait();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
            // retrieve the best solution of the run
            CFOSolution bestSolution = (CFOSolution) probe.getBestSolution();
            // this problem only has one optima
            CFOSolution singleOptima = problem.getGlobalOptima()[0]; 
            // display run information
            System.out.println("> dim["+i+"], score["+bestSolution.getScore()+"], bestPossibleScore["+singleOptima.getScore()+"]");
        }        
    }
}
