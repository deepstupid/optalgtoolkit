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
package com.oat;

import java.util.LinkedList;

import com.oat.utils.BeanUtils;

/**
 * Description: Responsible for managing the execution of an algorithm on a problem
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
public class AlgorithmExecutor
	implements Configurable, Clearable
{	
	/**
	 * Algorithm to execute
	 */
	protected Algorithm algorithm;
	/**
	 * Problem to execute
	 */
	protected Problem problem;
	/**
	 * List of stop condition to apply to the run
	 */
	protected LinkedList<StopCondition> stopConditions;
	/**
	 * List of probes used to collect information about a run
	 */
	protected LinkedList<RunProbe> runProbes;

	/**
	 * Constructor
	 */
	public AlgorithmExecutor()
	{
		stopConditions = new LinkedList<StopCondition>();
		runProbes = new LinkedList<RunProbe>();
	}	
	
	/**
	 * Construct an instance of the executor initialized with a simple algorithm-problem-stopcondition combination
	 *  
	 * @param p
	 * @param a
	 * @param c
	 */
	public AlgorithmExecutor(Problem p, Algorithm a, StopCondition c)
	{
		this();
		setProblem(p);
		setAlgorithm(a);
		addStopCondition(c);
	}
	
	@Override
	public void validateConfiguration()
		throws InvalidConfigurationException
	{
		// validate this object
		if(algorithm == null)
		{
			throw new InvalidConfigurationException("No algorithm set");
		}
		else if(problem == null)
		{
			throw new InvalidConfigurationException("No problem set");
		}
		else if(stopConditions.isEmpty())
		{
			throw new InvalidConfigurationException("No stop conditions set");
		}
		else if(runProbes.isEmpty())
		{
			throw new InvalidConfigurationException("No run probes set");
		}
		
		// remove all stop conditions
		problem.getStopConditions().clear();
		// add stop conditions
		for(StopCondition s : stopConditions)
		{
			problem.addStopCondition(s);
		}
		
		// validate things
		for(StopCondition s : stopConditions)
		{
			s.validateConfiguration();
		}
		algorithm.validateConfiguration();
		problem.validateConfiguration();
	}
	
	/**
	 * Called automatically before a run is executed to initialize all aspects of the run
	 * @throws InitialisationException
	 */
	public void initialiseBeforeRun()
		throws InitialisationException
	{
		// problem
		problem.initialiseBeforeRun();
		// algorithm
		algorithm.initialiseBeforeRun(problem);
		for(StopCondition s : stopConditions)
		{
			s.initialiseBeforeRun(problem, algorithm);
		}
		for(RunProbe r : runProbes)
		{
			r.initialiseBeforeRun(problem, algorithm);
		}
	}
	/**
	 * Called automatically after the run to ensure all un-needed resources are released
	 * @throws InitialisationException
	 */
	public void cleanupAfterRun()
		throws InitialisationException
	{
		// problem
		problem.cleanupAfterRun();
		// algorithm
		algorithm.cleanupAfterRun(problem);
		
		for(StopCondition s : stopConditions)
		{
			s.cleanupAfterRun(problem, algorithm);
		}
		for(RunProbe r : runProbes)
		{
			r.cleanupAfterRun(problem, algorithm);
		}
	}
	
	/**
	 * Executes the configured run including all required validation, initialization, and clean-up. Does
	 * not return until the configured run stops (a stop condition is triggered)
	 * @throws InvalidConfigurationException
	 * @throws InitialisationException
	 * @throws AlgorithmRunException
	 * @throws SolutionEvaluationException
	 */
	public void executeAndWait()
		throws InvalidConfigurationException, InitialisationException, AlgorithmRunException, SolutionEvaluationException
	{        
		// validate everything
		validateConfiguration();
		// initialise everything before the run
		initialiseBeforeRun();
        // execute the algorithm
        try
        {
            // execute the algorithm
            algorithm.executeAndWait(problem);
        }
        // ensure maintenance is always performed
        finally
        {
        	cleanupAfterRun();
        }        
	}	
	

	@Override
	public boolean isUserConfigurable()
	{
		return true;
	}

	@Override
	public void clear()
	{
		algorithm = null;
		problem = null;
		stopConditions.clear();
		runProbes.clear();
	}
	

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}


	public void setAlgorithm(Algorithm algorithm)
	{
		this.algorithm = algorithm;
	}


	public Problem getProblem()
	{
		return problem;
	}


	public void setProblem(Problem problem)
	{
		this.problem = problem;
	}


	public LinkedList<StopCondition> getStopConditions()
	{
		return stopConditions;
	}


	public LinkedList<RunProbe> getRunProbes()
	{
		return runProbes;
	}
	
	public void addStopCondition(StopCondition s)
	{
		stopConditions.add(s);
	}
	
	public void addRunProbe(RunProbe p)
	{
		runProbes.add(p);
	}
	
	public void addStopConditions(LinkedList<StopCondition> s)
	{
		stopConditions.addAll(s);
	}
	
	public void addRunProbes(LinkedList<RunProbe> s)
	{
		runProbes.addAll(s);
	}
	
	public void addRunProbes(RunProbe [] s)
	{
		for(RunProbe r : s)
		{
			addRunProbe(r);
		}
	}


	@Override
	public String getConfigurationDetails()
	{
		return BeanUtils.getBeanDetails(this);
	}	
}
