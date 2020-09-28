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
package com.oat.junit;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.oat.Algorithm;
import com.oat.AlgorithmExecutor;
import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.Problem;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Description: 
 *  
 * Date: 05/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExternaliseTests
{
	@Test
	public void testExternalizeToString()
	{
		try
		{
			Domain [] domains = DomainUtils.loadDomainList(DomainUtils.DOMAIN_LIST);
			AlgorithmExecutor executor = new AlgorithmExecutor();
			EvaluationsStopCondition s = new EvaluationsStopCondition(100);
			
			for (int i = 0; i < domains.length; i++)
			{
				System.out.println(">testing "+domains[i].getHumanReadableName());
				// clear
				executor.clear();
				// prepare the executor for the domain
				executor.addStopCondition(s);
				executor.addRunProbes(domains[i].loadDomainRunProbes());
				Algorithm [] algorithms = domains[i].loadAlgorithmList();
				Problem [] problems = domains[i].loadProblemList();
				for (int j = 0; j < problems.length; j++)
				{
					executor.setProblem(problems[j]);
					// recreate from string
					String pd = executor.getProblem().toPopulateString();
					executor.getProblem().populateFromString(pd);
					
					for (int j2 = 0; j2 < algorithms.length; j2++)
					{
						executor.setAlgorithm(algorithms[j2]);
						// recreate from string
						String ad = executor.getAlgorithm().toPopulateString();
						executor.getAlgorithm().populateFromString(ad);
						
						// do the deed
						executor.executeAndWait();
						//System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName());
					}
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	
	@Test
	public void testPopulateFromInstance()
	{
		try
		{
			Domain [] domains = DomainUtils.loadDomainList(DomainUtils.DOMAIN_LIST);
			AlgorithmExecutor executor = new AlgorithmExecutor();
			EvaluationsStopCondition s = new EvaluationsStopCondition(100);
			
			for (int i = 0; i < domains.length; i++)
			{
				System.out.println(">testing "+domains[i].getHumanReadableName());
				// clear
				executor.clear();
				// prepare the executor for the domain
				executor.addStopCondition(s);
				executor.addRunProbes(domains[i].loadDomainRunProbes());
				Algorithm [] algorithms = domains[i].loadAlgorithmList();
				Problem [] problems = domains[i].loadProblemList();
				for (int j = 0; j < problems.length; j++)
				{
					executor.setProblem(problems[j]);
					// create a new one
					Problem p = executor.getProblem().getClass().newInstance();
					p.populateFromInstance(executor.getProblem());					
					executor.setProblem(p);
					
					for (int j2 = 0; j2 < algorithms.length; j2++)
					{
						executor.setAlgorithm(algorithms[j2]);
						// recreate from string
						Algorithm a = executor.getAlgorithm().getClass().newInstance();
						a.populateFromInstance(executor.getAlgorithm());					
						executor.setAlgorithm(a);
						
						// do the deed
						executor.executeAndWait();
						//System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName());
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
}
