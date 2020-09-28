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
import com.oat.stopcondition.RequestStopCondition;

/**
 * Type: ExampleStoppingARun<br/>
 * Date: 01/12/2006<br/>
 * <br/>
 * Description: Example of stopping a run
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleStoppingARun
{
    /**
     * Example of performing a very long run, then using the stopping functionality to 
     * stop the run 
     * @param args
     */
    public static void main(String[] args)
    {
    	CFODomain domain = new CFODomain();
        // prepare the problem domain
        final TestFunctionF1 problem = new TestFunctionF1();
        // request stop, stop condition         
        RequestStopCondition stopCondition = new RequestStopCondition(); 
        // make the problem higher dimensionality
        problem.setDimensions(100);
        // prepare the algorithm
        final GeneticAlgorithm algorithm = new GeneticAlgorithm();
        // use of a consistant random seed gurantees the same result each run (reproducible)
        // for different results each run use System.currentTimeMillis()
        // this system time seed is the default random number seed for all randomized algorithms
        algorithm.setSeed(1);
                
        // run the algorithm in another thread
        final AlgorithmExecutor executor = new AlgorithmExecutor(problem, algorithm, stopCondition);
        LinkedList<RunProbe> probes = domain.loadDomainRunProbes();
        executor.addRunProbes(probes);
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                	executor.executeAndWait();
                }
                catch (Exception e)
                {
                    e.printStackTrace(); // invalid config
                }
            }
        }).start(); // start the thread

        // wait 10 seconds - let the algorithm run for 10 seconds in the other thread
        try
        {
            System.out.println("Waiting 10 seconds for algorithm to execute in another thread before stopping...");
            Thread.sleep(10*1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        // request a stop, and wait for the thing to actually implement the request
        stopCondition.requestStopAndWaitForTriggered();
        // TODO - should add this functionality to the executor because there is a race condition for the results
        
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
