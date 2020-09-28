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

import java.util.LinkedList;

import com.oat.AlgorithmExecutor;
import com.oat.RunProbe;
import com.oat.domains.bfo.algorithms.evolution.GeneticAlgorithm;
import com.oat.domains.cfo.CFODomain;
import com.oat.domains.cfo.problems.dejong.TestFunctionF1;
import com.oat.stopcondition.EvaluationsStopCondition;
import com.oat.utils.BinaryDecodeMode;

/**
 * Type: ExampleGAFuncOpt<br/>
 * Date: 28/11/2006<br/>
 * <br/>
 * Description: Function Optimization Example Code
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleGAFuncOpt
{
    /**
     * Example code that shows how to configure and run a simple genetic algorithm
     * for a function optimization problem instance.
     * @param args
     */
    public static void main(String[] args)
    {
    	CFODomain domain = new CFODomain();
        // prepare the problem domain
        TestFunctionF1 problem = new TestFunctionF1();
        // maximum function evaluations for this run
        EvaluationsStopCondition stopCondition = new EvaluationsStopCondition(1000);
        // prepare the algorithm
        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        // use of a consistant random seed gurantees the same result each run (reproducible)
        // for different results each run use System.currentTimeMillis()
        // this system time seed is the default random number seed for all randomized algorithms
        algorithm.setSeed(1);
        // 99% chance of crossing over two parents (high crossover)
        algorithm.setCrossover(0.99); 
        // 1% chance of flipping each bit (high mutation)
        algorithm.setMutation(0.01); 
        // moderate number of individuals
        algorithm.setPopsize(80); 
        // moderate selective pressure        
        algorithm.setBoutSize(7); 
        // number of best solutions to save each generation        
        algorithm.setElitism(3); 
        // use gray code to decode binary into real-values
        problem.setDecodeMode(BinaryDecodeMode.GrayCode); 
        // run the algorithm        
        AlgorithmExecutor executor = new AlgorithmExecutor(problem, algorithm, stopCondition);
        LinkedList<RunProbe> probes = domain.loadDomainRunProbes();
        executor.addRunProbes(probes);
        try
        {
        	executor.executeAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        // problem details
        System.out.println("Problem Details");
        System.out.println(" >Problem: " + problem.getName());
        System.out.println(" >Problem Details: " + problem.getDetails());
        System.out.println(" >Problem Configuration: " + problem.getConfigurationDetails());
        // algorithm details        
        System.out.println("Algorithm Details");
        System.out.println(" >Algorithm: " + algorithm.getName());
        System.out.println(" >Algorithm Details: " + algorithm.getDetails());
        System.out.println(" >Algorithm Configuration: " + algorithm.getConfigurationDetails());
        // run details
        System.out.println("Run Details");
        for(RunProbe probe : probes)
        {
        	System.out.println(" >"+probe.getName() + ": " + probe.getProbeObservation());
        }
    }
}
