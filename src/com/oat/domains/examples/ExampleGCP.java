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
import com.oat.domains.gcp.GCPDomain;
import com.oat.domains.gcp.GCProblem;
import com.oat.domains.gcp.algorithms.RandomSearch;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Type: ExampleGCP<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description: Example of using the graph coloring problem
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleGCP
{
    /**
     * Example implementation of the Random Search algorithm for
     * a GCP problem (queen16_16.col)
     * @param args
     */
    public static void main(String[] args)
    {
    	GCPDomain domain = new GCPDomain();
        // prepare the problem domain (queen16_16.col, loaded from file)
        GCProblem problem = new GCProblem("gcp/queen16_16.col");
        // maximum function evaluations for this run
        EvaluationsStopCondition stopCondition = new EvaluationsStopCondition(1000); 
        // prepare the algorithm
        RandomSearch algorithm = new RandomSearch();
        // use of a consistant random seed gurantees the same result each run (reproducible)
        // for different results each run use System.currentTimeMillis()
        // this system time seed is the default random number seed for all randomized algorithms
        algorithm.setSeed(1);
        
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
