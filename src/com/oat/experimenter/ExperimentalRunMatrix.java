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
package com.oat.experimenter;

import java.util.Iterator;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;

/**
 * Description: Represents a matrix of Experimental Runs
 * This is a useful construct as the majority of experimental runs will be of a set 
 * of n-algorithms against a set of n-problems 
 *  
 * Date: 22/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExperimentalRunMatrix
{
	/**
	 * List of algorithms to form an axis of the matrix
	 */
	protected final LinkedList<Algorithm> algorithms;
	/**
	 * List of problem instances to form an axis of the matrix
	 */
	protected final LinkedList<Problem> problems;
	/**
	 * The number of repeats for each experimental run in the matrix
	 * Defaults to ExperimentUtils.DEFAULT_REPEATS
	 */
	protected int repeats = ExperimentUtils.DEFAULT_REPEATS;
	
	/**
	 * Default Constructor
	 */
	public ExperimentalRunMatrix()
	{
		algorithms = new LinkedList<Algorithm>();
		problems = new LinkedList<Problem>();
	}	
	
	/**
	 * Empty out the matrix
	 */
	public void clear()
	{
		algorithms.clear();
		problems.clear();
		repeats = ExperimentUtils.DEFAULT_REPEATS;
	}
	
	/**
	 * The total number of experimental runs in the matrix 
	 * @return
	 */
	public int totalRuns()
	{
		return algorithms.size() * problems.size();
	}
	
	
	public boolean isEmpty()
	{
		return totalRuns() == 0;
	}
	
	

	
	/**
	 * Populates the matrix information (algorithms, problems, repeats)
	 * from the provided run list.
	 * Assumes the run list was created using this construct. Specifically, it assumes
	 * that each instance of each problem and algorithm (class level) is identical 
	 * in configuration
	 * @param experimentalRuns
	 * @throws ExperimentException
	 */
	public void populateFromRunList(LinkedList<ExperimentalRun> experimentalRuns)
		throws ExperimentException
	{
		if(experimentalRuns.isEmpty())
		{
			return;
		}
		
		// find distinct problems
		for(ExperimentalRun run : experimentalRuns)
		{
			Class c = run.getProblem().getClass();
			// see if problem is new
			boolean isDistinct = true;
			for (int i = 0; isDistinct && i < problems.size(); i++)
			{
				if(problems.get(i).getClass().equals(c) &&
						problems.get(i).getName().equals(run.getProblem().getName()))
				{
					isDistinct = false;
				}
			}
			if(isDistinct)
			{
				problems.add(run.getProblem());
			}
		}		
		// find distinct algorithms
		for(ExperimentalRun run : experimentalRuns)
		{
			Class c = run.getAlgorithm().getClass();
			// see if problem is new
			boolean isDistinct = true;
			for (int i = 0; isDistinct && i < algorithms.size(); i++)
			{
				if(algorithms.get(i).getClass().equals(c) &&
						algorithms.get(i).getName().equals(run.getAlgorithm().getName()))
				{
					isDistinct = false;
				}
			}
			if(isDistinct)
			{
				algorithms.add(run.getAlgorithm());
			}
		}	
		
		// validation
		if(totalRuns() != experimentalRuns.size())
		{
			throw new ExperimentException("The total number of runs "+experimentalRuns.size()+" does not match the expected " + totalRuns());
		}
		
		// find repeats
		repeats =  experimentalRuns.getFirst().getRepeats();
	}
	
	
	/**
	 * Calculate summary statistics for the provided result matrix
	 * Returns summaries in the same ordering
	 * 
	 * @param runResults
	 * @param selectedStatistic
	 * @return
	 */
	public static RunStatisticSummary [][] calculateRunSummaries(
			ExperimentalRun [][] runMatrix,
			RunResult [][][] runResults, 
			String selectedStatistic)
		throws AnalysisException
	{
		RunStatisticSummary [][] matrix = new RunStatisticSummary[runResults.length][runResults[0].length];
		
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[0].length; j++)
			{
				matrix[i][j] = new RunStatisticSummary();
				matrix[i][j].calculate(runMatrix[i][j], runResults[i][j], selectedStatistic);
			}
		}
		
		return matrix;
	}
	
	/**
	 * Loads a set of run results as a matrix.
	 * Assumes that the run list and resultant experimental results are ordered 
	 * as though they were created using this construct. The returned
	 * result matrix is ordered as follows: [problem][algorithm][repeats].
	 * Requires that this construct is initialzied with information about the runs
	 * via populateFromRunList()
	 * 
	 * @param experiment
	 * @param experimentalRuns
	 * @return
	 * @throws ExperimentException
	 */
	public RunResult [][][] loadRunResultsToMatrix(
			Experiment experiment,
			LinkedList<ExperimentalRun> experimentalRuns)
		throws ExperimentException
	{
		int expected = totalRuns();
		RunResult [][][] matrix = new RunResult[problems.size()][algorithms.size()][];
		
		if(experimentalRuns.size() != expected)
		{
			throw new ExperimentException("Run list size "+experimentalRuns.size()+" does not match expected " + expected);
		}
		
		int count = 0;
		for (int i = 0; i < problems.size(); i++)
		{
			for (int j = 0; j < algorithms.size(); j++)
			{
				ExperimentalRun run = experimentalRuns.get(count++);
				// load the results for this run
				if(run.isCompleted())
				{
					matrix[i][j] = ExperimentalRunUtils.loadRunResult(experiment, run);
				}
			}
		}
		
		return matrix;
	}
	
	
	
	/**
	 * Presumes the provided run list was created from this instance.
	 * Creates an n*n matrix of runs from the list as follows: [problems][algorithms]
	 * 
	 * @param experimentalRuns
	 * @return
	 * @throws ExperimentException
	 */
	public ExperimentalRun [][] fromFlatRunListToMatrix(LinkedList<ExperimentalRun> experimentalRuns)
		throws ExperimentException
	{
		int expected = totalRuns();
		ExperimentalRun [][] matrix = new ExperimentalRun[problems.size()][algorithms.size()];
		
		if(experimentalRuns.size() != expected)
		{
			throw new ExperimentException("Run list size "+experimentalRuns.size()+" does not match expected " + expected);
		}
		
		int count = 0;
		for (int i = 0; i < problems.size(); i++)
		{
			for (int j = 0; j < algorithms.size(); j++)
			{
				matrix[i][j] = experimentalRuns.get(count++);
			}
		}
		
		return matrix;
	}
	
	/**
	 * Presumes the provided run list was NOT created from this instance, but contains enough pieces
	 * Creates an n*n matrix of runs from the list as follows: [problems][algorithms]
	 * 
	 * @param experimentalRuns
	 * @return
	 * @throws ExperimentException
	 */
	public ExperimentalRun [][] fromUnorderedFlatRunListToMatrix(LinkedList<ExperimentalRun> experimentalRuns)
		throws ExperimentException
	{
		LinkedList<ExperimentalRun> list = (LinkedList<ExperimentalRun>) experimentalRuns.clone();
		
		int expected = totalRuns();
		ExperimentalRun [][] matrix = new ExperimentalRun[problems.size()][algorithms.size()];
		
		if(list.size() != expected)
		{
			throw new ExperimentException("Run list size "+list.size()+" does not match expected " + expected);
		}
		
		for (int i = 0; i < problems.size(); i++)
		{
			Problem p = problems.get(i);
			
			for (int j = 0; j < algorithms.size(); j++)
			{
				Algorithm a = algorithms.get(j);
				boolean found = false;
				
				// find the run for this algorithm-problem combination
				for (Iterator<ExperimentalRun> it = list.iterator(); !found && it.hasNext();)
				{
					ExperimentalRun r = it.next();
					// match on algorithm
					if(r.getAlgorithm().getClass().equals(a.getClass()) && r.getAlgorithm().getName().equals(a.getName()) &&
							// match on problem
							r.getProblem().getClass().equals(p.getClass()) && r.getProblem().getName().equals(p.getName()))
					{
						// store
						matrix[i][j] = r;
						// remove from list
						it.remove();						
						// stop searching
						found = true;
					}
				}
				
				if(!found)
				{
					throw new ExperimentException("Failed to locate a run with algorithm "+a.getName()+" and problem "+p.getName());
				}
			}
		}
		
		return matrix;
	}
	
	
	
	/**
	 * Converts the defined algorithms and problems into a run 
	 * list that is stored within the provided experiment. Run id's are valid 
	 * within the context of the experiment, and run instances are validated 
	 * before being added.
	 * The list is processed by problem and then by algorithm.
	 * 
	 * @param experiment
	 * @throws ExperimentException
	 */
	public void toRunListAndAddToExperiment(Experiment experiment)
		throws InvalidConfigurationException
	{
		int nextRunId = ExperimentalRunUtils.getNextValidRunIdNumber(experiment);
		// process each problem
		for(Problem p : problems)
		{
			// process each algorithm
			for(Algorithm a : algorithms)
			{
				// create
				ExperimentalRun run = new ExperimentalRun();
				if(a.isUserConfigurable())
				{
					// different instances
					try
					{
						Algorithm aCopy = a.getClass().newInstance();
						aCopy.populateFromInstance(a);
						run.setAlgorithm(aCopy);
					}
					catch (Exception e)
					{
						throw new InvalidConfigurationException("Error duplicating algorithm instance");
					}
				}
				else
				{
					// same instance
					run.setAlgorithm(a);
				}
				
				if(p.isUserConfigurable())
				{
					// different instances
					try
					{
						Problem pCopy = p.getClass().newInstance();
						pCopy.populateFromInstance(p);
						run.setProblem(pCopy);
					}
					catch (Exception e)
					{
						throw new InvalidConfigurationException("Error duplicating algorithm instance");
					}
				}
				else
				{
					// same instance
					run.setProblem(p);
				}
				run.setId(ExperimentalRunUtils.toRunId(nextRunId++));
				run.setRepeats(repeats);
				// validate
				run.validateConfiguration(experiment.getStopCondition());
				// add
				experiment.addRun(run);
			}
		}		
	}
	
	/**
	 * Creates a run list from the algorithms and problems defined.
	 * Each run is assigned a run id starting from zero, and a repeat
	 * as defined in this construct.
	 * The list is processed by problem and then by algorithm.
	 * 
	 * @return
	 */
	public LinkedList<ExperimentalRun> toRunList()
	{	
		LinkedList<ExperimentalRun> list = new LinkedList<ExperimentalRun>();
		int count = 0;
	
		// process each problem
		for(Problem p : problems)
		{
			// process each algorithm
			for(Algorithm a : algorithms)
			{
				// create
				ExperimentalRun run = new ExperimentalRun();
				if(a.isUserConfigurable())
				{
					// different instances
					try
					{
						Algorithm aCopy = a.getClass().newInstance();
						aCopy.populateFromInstance(a);
						run.setAlgorithm(aCopy);
					}
					catch (Exception e)
					{
						throw new RuntimeException("Error duplicating algorithm instance");
					}
				}
				else
				{
					// same instance
					run.setAlgorithm(a);
				}
				
				if(p.isUserConfigurable())
				{
					// different instances
					try
					{
						Problem pCopy = p.getClass().newInstance();
						pCopy.populateFromInstance(p);
						run.setProblem(pCopy);
					}
					catch (Exception e)
					{
						throw new RuntimeException("Error duplicating algorithm instance");
					}
				}
				else
				{
					// same instance
					run.setProblem(p);
				}
				run.setId(ExperimentalRunUtils.toRunId(count++));
				run.setRepeats(repeats);
				// add
				list.add(run);
			}
		}		
		
		return list;
	}
	

	
	
	public void addProblem(Problem p)
	{
		problems.add(p);
	}
	
	public void addProblems(Problem [] p)
	{
		for (int i = 0; i < p.length; i++)
		{
			problems.add(p[i]);
		}
	}
	
	public void addProblems(LinkedList<Problem> p)
	{
		problems.addAll(p);
	}	
	
	public void addAlgorithm(Algorithm a)
	{
		algorithms.add(a);
	}
	
	public void addAlgorithms(Algorithm [] a)
	{
		for (int i = 0; i < a.length; i++)
		{
			algorithms.add(a[i]);
		}
	}
	
	public void addAlgorithms(LinkedList<Algorithm> a)
	{
		algorithms.addAll(a);
	}


	public int getRepeats()
	{
		return repeats;
	}


	public void setRepeats(int repeats)
	{
		this.repeats = repeats;
	}

	public LinkedList<Algorithm> getAlgorithms()
	{
		return algorithms;
	}

	public LinkedList<Problem> getProblems()
	{
		return problems;
	}
	
	
}
