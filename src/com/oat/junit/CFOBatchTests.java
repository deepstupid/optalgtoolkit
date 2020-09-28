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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

import com.oat.Algorithm;
import com.oat.AlgorithmExecutor;
import com.oat.Domain;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cfo.CFODomain;
import com.oat.domains.cfo.CFOProblem;
import com.oat.domains.cfo.algorithms.RandomSearch;
import com.oat.domains.cfo.problems.dejong.TestFunctionF1;
import com.oat.probes.BestSolutionProbe;
import com.oat.probes.TotalEvaluationsProbe;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * Description: Test CFO in batch
 *  
 * Date: 04/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class CFOBatchTests
{
	/**
	 * Ensuring that the runs produce the same result
	 */
    @Test
    public void testAllAlgorithmsForConsistency()
    {
    	Domain domain = new CFODomain(); // CFO
    	Problem problem = new TestFunctionF1(); // De Jong F1

    	
    	AlgorithmExecutor executor = new AlgorithmExecutor();
    	BestSolutionProbe solutionProbe = new BestSolutionProbe();
    	TotalEvaluationsProbe evalsProbe = new TotalEvaluationsProbe();
    	EvaluationsStopCondition sc = new EvaluationsStopCondition(1000);
    	
    	executor.setProblem(problem);
    	executor.addRunProbe(solutionProbe);
    	executor.addRunProbe(evalsProbe);
    	executor.addStopCondition(sc);    	
    	
    	Algorithm [] algorithms = null;
    	try
		{
			algorithms = domain.loadAlgorithmList();			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Failed to load algorithms: " + e.getMessage());			
		}
		
		for (int i = 0; i < algorithms.length; i++)
		{
			// set the algorithm
			executor.setAlgorithm(algorithms[i]);
			Solution s1 = null;
			long e1 = 0;
			Solution s2 = null;
			long e2 = 0;
			try
			{
				// first round
				executor.executeAndWait();
				s1 = solutionProbe.getBestSolution();
				e1 = evalsProbe.getCompletedEvaluations();
				// second round
				executor.executeAndWait();
				s2 = solutionProbe.getBestSolution();
				e2 = evalsProbe.getCompletedEvaluations();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fail("Failed on the execution of: " + algorithms[i].getName() + ": " + e.getMessage());			
			}
			
			// expect evaluations to be the same
			assertEquals(e1, e2);
			// expect scores to be the same
			assertEquals(s1.getScore(), s2.getScore());
			
			System.out.println("> " + algorithms[i].getName());
		}    	
    } 
    
    
    /**
     * Looking for exceptions
     */
    @Test
    public void testAllAlgorithmsOnAllProblems()
    {
    	Domain domain = new CFODomain(); // CFO
    	
    	AlgorithmExecutor executor = new AlgorithmExecutor();
    	BestSolutionProbe solutionProbe = new BestSolutionProbe();
    	TotalEvaluationsProbe evalsProbe = new TotalEvaluationsProbe();
    	EvaluationsStopCondition sc = new EvaluationsStopCondition(1000);
    	
    	executor.addRunProbe(solutionProbe);
    	executor.addRunProbe(evalsProbe);
    	executor.addStopCondition(sc);    	
    	
    	Algorithm [] algorithms = null;
    	Problem [] problems = null;
    	try
		{
			algorithms = domain.loadAlgorithmList();
			problems = domain.loadProblemList();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Failed to load algorithms and problems: " + e.getMessage());			
		}
		
		// for all algorithms
		for (int i = 0; i < algorithms.length; i++)
		{
			// set the algorithm
			executor.setAlgorithm(algorithms[i]);
			
			// for all problems
			for (int j = 0; j < problems.length; j++)
			{
				// set the problem
				executor.setProblem(problems[j]);				
				
				try
				{
					// first round
					executor.executeAndWait();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					fail("Failed on the execution of: " + executor.getAlgorithm().getName() +" on " + executor.getProblem().getName() + ": " + e.getMessage());			
				}
				
				System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName());
			}
		}    	
    }
    
    
    /**
     * Looking for exceptions when dimensionality of CFO problems is changed
     */
    @Test
    public void testAllProblemsVariableDimensionality()
    {
    	Algorithm algorithm = new RandomSearch(); // random search
    	Domain domain = new CFODomain(); // CFO
    	
    	AlgorithmExecutor executor = new AlgorithmExecutor();
    	BestSolutionProbe solutionProbe = new BestSolutionProbe();
    	TotalEvaluationsProbe evalsProbe = new TotalEvaluationsProbe();
    	EvaluationsStopCondition sc = new EvaluationsStopCondition(1000);
    	
    	executor.setAlgorithm(algorithm);
    	executor.addRunProbe(solutionProbe);
    	executor.addRunProbe(evalsProbe);
    	executor.addStopCondition(sc);    	
    	
    	
    	Problem [] problems = null;
    	try
		{
			problems = domain.loadProblemList();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Failed to load algorithms and problems: " + e.getMessage());			
		}
		
		// for all algorithms
		Random r = new Random(1);
		for (int i = 0; i < problems.length; i++)
		{	
			CFOProblem p = (CFOProblem) problems[i];			
			// set the problem
			executor.setProblem(p);				
			
			// try different dimensionality			
			try
			{
	            if(p.isDimensionalitySupported(CFOProblem.SUPPORTED_DIMENSIONS.ONE_DIMENSIONAL))
	            {
	                p.setDimensions(1);
	                executor.executeAndWait();
	                System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName() + ", dimensions="+p.getDimensions());
	            }
	            if(p.isDimensionalitySupported(CFOProblem.SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL))
	            {
	                p.setDimensions(2);
	                executor.executeAndWait();
	                System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName() + ", dimensions="+p.getDimensions());
	            }
	            if(p.isDimensionalitySupported(CFOProblem.SUPPORTED_DIMENSIONS.THREE_DIMENSIONAL))
	            {
	                p.setDimensions(3);
	                executor.executeAndWait();
	                System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName() + ", dimensions="+p.getDimensions());
	            }
	            if(p.isDimensionalitySupported(CFOProblem.SUPPORTED_DIMENSIONS.ANY))
	            {
	                // 10 random dimension tests            		            	
	                for (int j = 0; j < 10; j++)
	                {
	                    int dim = r.nextInt(100) + 4;// start with 4D 
		                p.setDimensions(dim);
		                executor.executeAndWait();
		                System.out.println("> " + executor.getAlgorithm().getName() + " on " + executor.getProblem().getName() + ", dimensions="+p.getDimensions());
	                }
	            }				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				fail("Failed on the execution of: " + executor.getAlgorithm().getName() +" on " 
						+ executor.getProblem().getName() + " with dimensionality " 
						+  ((CFOProblem)executor.getProblem()).getDimensions() + ": " + e.getMessage());			
			}
		}    	
    }
}
