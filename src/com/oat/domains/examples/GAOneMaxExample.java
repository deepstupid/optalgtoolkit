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
import com.oat.RunProbe;
import com.oat.domains.bfo.BFODomain;
import com.oat.domains.bfo.algorithms.evolution.GeneticAlgorithm;
import com.oat.domains.bfo.problems.OneMax;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Description: 
 *  
 * Date: 03/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GAOneMaxExample
{

	
	public static void main(String[] args)
	{
		BFODomain domain = new BFODomain();
		AlgorithmExecutor exec = new AlgorithmExecutor();
		exec.setAlgorithm(new GeneticAlgorithm());
		exec.setProblem(new OneMax());
		exec.addStopCondition(new EvaluationsStopCondition());
		exec.addRunProbes(domain.loadDomainRunProbes());
		
		// do the thing
		try
		{
			exec.executeAndWait();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Complete");
		System.out.println("Problem Details: " + exec.getProblem().getDetails());
		System.out.println("Problem Configuration: " + exec.getProblem().getConfigurationDetails());
		System.out.println("Algorithm Details: " + exec.getAlgorithm().getDetails());
		System.out.println("Algorithm Configuration: " + exec.getAlgorithm().getConfigurationDetails());
		for(RunProbe p : exec.getRunProbes())
		{
			System.out.println(p.getName() + ": " + p.getProbeObservation());
		}
	}
}
