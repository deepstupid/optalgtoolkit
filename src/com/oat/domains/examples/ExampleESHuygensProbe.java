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
import java.util.Properties;

import com.oat.AlgorithmExecutor;
import com.oat.RunProbe;
import com.oat.domains.cfo.algorithms.evolution.EvolutionStrategies;
import com.oat.domains.hbs.HBSDomain;
import com.oat.domains.hbs.HuygensProblem;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Type: ExampleESHuygensProbe<br/>
 * Date: 28/11/2006<br/>
 * <br/>
 * Description: Huygens Probe Example Code
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Adjusted so that my personal details were not in the example
 * </pre>
 */
public class ExampleESHuygensProbe
{
	public final static String EMAIL = "jbrownlee@ict.swin.edu.au";
	
	public final static void setSwinburneProxy()
	{
		Properties prop = System.getProperties();
		prop.put("http.proxySet", "true");
		prop.put("http.proxyHost", "wwwproxy.swin.edu.au");
		prop.put("http.proxyPort", "8000");
	}
	
	
    /**
	 * Example code that shows how to configure and run a simple genetic
	 * algorithm for a function optimization problem instance.
	 * 
	 * @param args
	 */
    public static void main(String[] args)
    {
    	HBSDomain domain = new HBSDomain();
        // prepare proxy information (swinburne university of technology)
    	setSwinburneProxy();
        // prepare the problem domain
    	HuygensProblem problem = new HuygensProblem();
        // maximum function evaluations for this run
    	EvaluationsStopCondition stopCondition = new EvaluationsStopCondition(2000);
        // set problem series
        problem.setSeries(21);
        // set problem landscape
        problem.setLandscape(99);
        // set user email address
        problem.setEmail(EMAIL); // TODO: Set your email address
        // prepare the algorithm
        EvolutionStrategies algorithm = new EvolutionStrategies();
        // automatically configure
        algorithm.automaticallyConfigure(problem);        
        // moderate number of individuals
        algorithm.setPopsize(10);        
        // run the algorithm
        AlgorithmExecutor executor = new AlgorithmExecutor(problem, algorithm, stopCondition);
        LinkedList<RunProbe> probes = domain.loadDomainRunProbes();
        executor.addRunProbes(probes);
        try
        {
        	System.out.println("Running...");
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
