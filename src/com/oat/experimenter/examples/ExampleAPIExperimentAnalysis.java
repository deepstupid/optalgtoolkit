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

import com.oat.domains.cfo.CFODomain;
import com.oat.domains.cfo.algorithms.ParallelHillclimbingAlgorithm;
import com.oat.domains.cfo.algorithms.evolution.EvolutionStrategies;
import com.oat.domains.cfo.problems.dejong.TestFunctionF1;
import com.oat.domains.cfo.problems.dejong.TestFunctionF2;
import com.oat.domains.cfo.problems.dejong.TestFunctionF3;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.experimenter.ExperimentalRun;
import com.oat.experimenter.ExperimentalRunMatrix;
import com.oat.experimenter.ExperimentalRunUtils;
import com.oat.experimenter.RunResult;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.StatisticsReporting;
import com.oat.experimenter.stats.analysis.npopulation.ANOVATest;
import com.oat.experimenter.stats.analysis.npopulation.KruskalWallisTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;
import com.oat.experimenter.stats.analysis.twopopulation.StudentTTest;
import com.oat.experimenter.stats.normality.AndersonDarlingTest;
import com.oat.experimenter.stats.normality.CramerVonMisesCriterion;
import com.oat.experimenter.stats.normality.KolmogorovSmirnovTest;
import com.oat.probes.BestScoreProbe;
import com.oat.stopcondition.EvaluationsStopCondition;



/**
 * Description: Provides API examples of analysis tools
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
public class ExampleAPIExperimentAnalysis
{
	
	/**
	 * Demonstrate some of the analysis tools
	 */
	public void doAnalysis(Experiment exp, ExperimentalRunMatrix matrix)
	{
		System.out.println(">Analysis");		
		ExperimentalRun [][] runMatrix = null;
		try
		{
			runMatrix = matrix.fromFlatRunListToMatrix(exp.getRuns());
		}
		catch (ExperimentException e1)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e1);
		}
		
		// load the results
		RunResult [][][] results = null;
		try
		{
			results = matrix.loadRunResultsToMatrix(exp, exp.getRuns());
			System.out.println(">Successfully loaded results from disk.");
		}
		catch (ExperimentException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		
		//
		// Descriptive statistics
		//
		// calculate summaries
		String selectedStatistic = new BestScoreProbe().getName();
		RunStatisticSummary [][] summaries = null;
		try
		{
			summaries = ExperimentalRunMatrix.calculateRunSummaries(runMatrix, results, selectedStatistic);
		}
		catch (AnalysisException e1)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e1);
		}
		// output a table of means and standard deviations for the experiment
		System.out.println(">Summary Statistics:");
		String summaryReport = StatisticsReporting.getSummaryStatisticReport(summaries, matrix);
		System.out.println(summaryReport);
		
		System.out.println(StatisticsReporting.reportToString(summaries[0][0].prepareReport()));
		
		//
		// Normality Testing Examples 
		//
		// a single summary
		RunStatisticSummary summary = summaries[0][1];
		
		// report on kolmogorov Smirnov Normality
		KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
		// evaluate both algorithms on the second problem
		try
		{
			ksTest.evaluate(summary);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+ksTest.getName());
		System.out.println(StatisticsReporting.reportToString(ksTest.prepareReport()));
				
		// report on anderson Darling Normality
		AndersonDarlingTest adTest = new AndersonDarlingTest();
		// evaluate both algorithms on the second problem
		try
		{
			adTest.evaluate(summary);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+adTest.getName());
		System.out.println(StatisticsReporting.reportToString(adTest.prepareReport()));
		
		// report on cramer Von Mises Normality
		CramerVonMisesCriterion cvmTest = new CramerVonMisesCriterion();
		// evaluate both algorithms on the second problem
		try
		{
			cvmTest.evaluate(summary);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+cvmTest.getName());
		System.out.println(StatisticsReporting.reportToString(cvmTest.prepareReport()));
		
		
		//
		// Compare Populations
		//
		
		// parametric, two-populations
		StudentTTest studentTTest = new StudentTTest(); 
		// evaluate both algorithms on the second problem
		try
		{
			studentTTest.evaluate(summaries[1]);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+studentTTest.getName());
		System.out.println(StatisticsReporting.reportToString(studentTTest.prepareReport()));
		
		// parametric, N-populations
		ANOVATest anovaTest = new ANOVATest(); 
		// evaluate both algorithms on the second problem
		try
		{
			anovaTest.evaluate(summaries[1]);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+anovaTest.getName());
		System.out.println(StatisticsReporting.reportToString(anovaTest.prepareReport()));
		
		
		// non-parametric, two-populations
		MannWhitneyUTest mwuTest = new MannWhitneyUTest(); 
		// evaluate both algorithms on the second problem
		try
		{
			mwuTest.evaluate(summaries[1]);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+mwuTest.getName());
		System.out.println(StatisticsReporting.reportToString(mwuTest.prepareReport()));
		
		// non-parametric, N-populations
		KruskalWallisTest kwTest = new KruskalWallisTest(); 
		// evaluate both algorithms on the second problem
		try
		{
			kwTest.evaluate(summaries[1]);
		}
		catch (AnalysisException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		System.out.println(">"+kwTest.getName());
		System.out.println(StatisticsReporting.reportToString(kwTest.prepareReport()));
	}
	
	

	/**
	 * Prepares some runs and proceeds to demonstrate different analysis tools
	 */
	public void runExample()
	{
		//
		// Create an experiment
		//		
		Experiment exp = new Experiment();
		exp.setName("Example-API3");
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
		ExperimentalRunMatrix matrix = new ExperimentalRunMatrix();
		matrix.setRepeats(30);
		// algorithms				
		matrix.addAlgorithm(new EvolutionStrategies());
		matrix.addAlgorithm(new ParallelHillclimbingAlgorithm());
		// problems
		matrix.addProblem(new TestFunctionF1());
		matrix.addProblem(new TestFunctionF2());
		matrix.addProblem(new TestFunctionF3());
		// put the runs in the experiment
		try
		{
			matrix.toRunListAndAddToExperiment(exp);
			System.out.println(">Successfully created the runs and stored them in the experiment: " + exp);
		}
		catch (Exception e1)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e1);
		}
		
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
		// Do analysis
		//
		doAnalysis(exp, matrix);
		
		//
		// Delete ALL Runs
		//		
		System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
		while(exp.getRunsDefined() > 0)
		{
			ExperimentalRun run = exp.getRuns().getFirst();
			try
			{
				ExperimentalRunUtils.deleteRunAndExternalizeSchedule(exp, run);
				System.out.println(">Successfully deleted run: " + run);				
			}
			catch(ExperimentException e)
			{			
				throw new RuntimeException("Something unexpected happened in the example.", e);
			}
		}	
		System.out.println(">Total runs for the experiment is " + exp.getRunsDefined() + ", runs completed for the experiment is " + exp.getRunsCompleted());
		
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
		new ExampleAPIExperimentAnalysis().runExample();
	}
}
