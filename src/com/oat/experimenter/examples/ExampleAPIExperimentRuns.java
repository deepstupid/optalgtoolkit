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
package com.oat.experimenter.examples;

import java.io.File;
import java.util.LinkedList;

import com.oat.domains.bfo.algorithms.evolution.GeneticAlgorithm;
import com.oat.domains.cfo.CFODomain;
import com.oat.domains.cfo.algorithms.evolution.DifferentialEvolution;
import com.oat.domains.cfo.problems.dejong.TestFunctionF1;
import com.oat.domains.cfo.problems.dejong.TestFunctionF2;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Description: Provides API examples of
 *  
 * Date: 21/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleAPIExperimentRuns
{

	/**
	 * Example using the API to manage experimental runs
	 */
	public void runExample()
	{
		//
		// Create an experiment
		//		
		Experiment exp = new Experiment();
		exp.setName("Example-API2");
		exp.setDescription("Example to demonstrate the experimental runs API.");
		exp.setDomain(new CFODomain());
		exp.setStopCondition(new EvaluationsStopCondition());
		exp.setRunStatisticsList(exp.getDomain().loadDomainRunProbes()); // all of them		
		// retrieve the home directory location
		File homeDir = null;		
		try
		{
			homeDir = ExperimentUtils.getDefaultHomeDirectory();
		} 
		catch (ExperimentException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}		
		// attempt to store experiment
		try
		{
			exp.save(homeDir);
			System.out.println(">Successfully created experiment: " + exp);
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}	
		//
		// Add runs
		//
		
		// create the run
		ExperimentalRun run1 = new ExperimentalRun();
		run1.setAlgorithm(new GeneticAlgorithm());
		run1.setProblem(new TestFunctionF1());
		run1.setRepeats(30);
		run1.setId(ExperimentalRunUtils.getNextValidRunId(exp));
		// validate
		try
		{
			run1.validateConfiguration(exp.getStopCondition());
			System.out.println(">Successfully validated run: " + run1);
		}
		catch(Exception e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		// add to experiment
		exp.addRun(run1);
		
		// create another run
		ExperimentalRun run2 = new ExperimentalRun();
		run2.setAlgorithm(new DifferentialEvolution());
		run2.setProblem(new TestFunctionF2());
		run2.setRepeats(30);
		run2.setId(ExperimentalRunUtils.getNextValidRunId(exp));
		// validate
		try
		{
			run2.validateConfiguration(exp.getStopCondition());
			System.out.println(">Successfully validated run: " + run2);
		}
		catch(Exception e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		// add to experiment
		exp.addRun(run2);	
		
		//
		// Save the run Schedule
		//
		try
		{
			ExperimentalRunUtils.externaliseEntireRunSchedule(exp);
			System.out.println(">Successfully saved the run schedule for experiment: " + exp);
			System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}				

		// 
		// Load experiment again, with runs
		//
		String expName = exp.getName();
		exp = new Experiment();
		try
		{
			exp.load(homeDir, expName);
			System.out.println(">Successfully loaded the experiment: " + exp);
			System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}	
		
		//
		// Execute runs
		//
		LinkedList<ExperimentalRun> runs = exp.getRuns();
		for(ExperimentalRun run : runs)
		{
			try
			{
				ExperimentalRunUtils.executeRunAndStoreResult(run, exp);
				System.out.println(">Successfully completed run: " + run);
				System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
			}
			catch(ExperimentException e)
			{			
				throw new RuntimeException("Something unexpected happened in the example.", e);
			}
		}
		
		//
		// Delete experiment fails - cannot delete when runs are completed
		//
		try
		{
			exp.delete(); // fail
			throw new RuntimeException("Something unexpected happened in the example");
		}
		catch(ExperimentException e)
		{
			System.out.println(">Correctly failed to delete with completed runs: " + e.getMessage());
		}		
		
		//
		// Delete runs
		//		
		// delete first run
		ExperimentalRun run = exp.getRuns().getFirst();
		try
		{
			ExperimentalRunUtils.deleteRunAndExternalizeSchedule(exp, run);
			System.out.println(">Successfully deleted run: " + run);
			System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		// delete second run
		run = exp.getRuns().getFirst();
		try
		{
			ExperimentalRunUtils.deleteRunAndExternalizeSchedule(exp, run);
			System.out.println(">Successfully deleted run: " + run);
			System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}		
		
		//
		// Delete experiment
		//
		try
		{
			exp.delete();
			System.out.println(">Successfully deleted experiment: " + exp);
		}
		catch(ExperimentException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
	}
	
	
	
	public static void main(String[] args)
	{
		new ExampleAPIExperimentRuns().runExample();
	}
}
