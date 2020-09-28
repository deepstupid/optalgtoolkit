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

import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BeanUtils;

/**
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Generic problem specification
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 25/12/2006   JBrownlee   Removed the isMinimise instance variable 
 *                          and renamed the accessor method (abstract)
 *                          Renamed various methods and added comments.
 * 27/12/2006   JBrownlee   Added increment methods for function evaluations, additional safety
 *                          Added more comments, renamed various methods
 * 15/01/2007   JBrownlee   Added an isMaximisation() function for symmetry 
 * 06/07/2007   JBrownlee   Added hasConfiguration(), default to false
 * 07/07/2007   JBrownlee   Shifted validation of max evaluations from the mutator function into before-run-validation
 * 04/09/2007	JBrownlee	Refactored methods and interfaces, use stop conditions
 * </pre>
 */
public abstract class Problem
	implements Comparable<Problem>, Configurable, UserFriendly, Populator<Problem>
{
	/**
	 * Collection of all objects interested in valid solution evaluation events
	 */
    protected final LinkedList<SolutionEvaluationListener> solutionEvaluationListeners;
    /**
     * Collection of all stop conditions for the problem execution
     */
    protected final LinkedList<StopCondition> stopConditions;    

    /**
     * Constructor
     */
    public Problem()
    {
        solutionEvaluationListeners = new LinkedList<SolutionEvaluationListener>();
        stopConditions = new LinkedList<StopCondition>();
    }
    
    /**
     * Triggers a solution evaluation event, notifies all registered
     * listeners that a solution has been evaluated
     * @param s
     */
    protected void triggerSolutionEvaluationEvent(Solution evaluatedSolution)
    {
        for (SolutionEvaluationListener a : solutionEvaluationListeners)
        {
            a.solutionEvaluatedEvent(evaluatedSolution);
        }
    }

    /**
     * Is s1 better than s2?
     * 
     * @param s1
     * @param s2
     * @return true if the fist solution has a BETTER quality than the first
     * @throws SolutionEvaluationException
     */
    public boolean isBetter(Solution s1, Solution s2)
    	throws SolutionEvaluationException
    {
        return isBetter(s1.getScore(), s2.getScore());
    }
    /**
     * Is s1 better or the same as s2?
     * 
     * @param s1
     * @param s2
     * @return true if the fist solution has a BETTER or EQUAL quality than the first
     * @throws SolutionEvaluationException
     */
    public boolean isBetterOrSame(Solution s1, Solution s2)
    	throws SolutionEvaluationException
    {
        return isBetter(s1.getScore(), s2.getScore()) || s1.getScore()==s2.getScore();
    }
    
    /**
     * s1 better or equal to s2?
     * @param s1
     * @param s2
     * @return true if the fist solution has a BETTER or EQUAL quality than the first
     */
    public boolean isBetterOrSame(double s1, double s2)
    {
        if(s1==s2)
        {
            return true;
        }
        else if (isMinimization())
        {
            return s1 < s2;
        }

        return s1 > s2;
    }
    
    /**
     * Is s1 better than s2?
     * 
     * @param s1
     * @param s2
     * @return true if the fist solution has a BETTER quality than the first
     */
    public boolean isBetter(double s1, double s2)
    {
        if (isMinimization())
        {
            return s1 < s2;
        }

        return s1 > s2;
    }    
    
    /**
     * Provides an interface for evaluating the cost for as many of the solutions in the
     * collection as the stop condition permits
     * @param <S>
     * @param solutionCollection
     * @throws SolutionEvaluationException
     */
    public <S extends Solution> void cost(LinkedList<S> solutionCollection)
    	throws SolutionEvaluationException
    {
        for (S solution : solutionCollection)
        {
            cost(solution);
        }
    }   

    /**
     * Provides an interface for evaluating the cost for as many of the solutions in the
     * collection as the stop condition permits
     * @param <S>
     * @param solutionCollection
     * @throws SolutionEvaluationException
     */
    public <S extends Solution> void cost(S [] solutionCollection)
    	throws SolutionEvaluationException
    {
        for (S solution : solutionCollection)
        {
            cost(solution);
        }
    }  

    /**
     * Evaluates the provided solution, testing that the solution is valid and that 
     * a stop condition has not been triggered.
     * @param solution
     * @throws SolutionEvaluationException
     */
    public void cost(Solution solution)
        throws SolutionEvaluationException
    {               
    	// check if the algorithm cannot stop
    	if(!canEvaluate())
    	{
    		return;
    	}
    	
        // check that the solution has not already been evaluated
        if(solution.isEvaluated())
        {
            return;
        }
        // check that the solution is valid
        checkSolutionForSafety(solution);
        // evaluate using problem specific evaluation
        double score = problemSpecificCost(solution);
        // ensure scoring is valid
        if(AlgorithmUtils.isInvalidNumber(score))
        {
            throw new SolutionEvaluationException("Problem specific cost function returned invalid solution scoring " + score + " solution="+solution.toString());
        }
        // store the scoring in the solution
        solution.evaluated(score);
        // notify listeners that another solution has been evaluated
        triggerSolutionEvaluationEvent(solution);
    }
    
    /**
     * Whether or not the goal of this problem instance is to locate the largest
     * (maximum) cost value
     * @return true if the problem is a maximization problem
     */
    public boolean isMaximisation()
    {
        return !isMinimization();
    }

    @Override
    public boolean isUserConfigurable()
    {
    	return false;
    }

    /**
     * Register a listener interested in valid solution evaluations
     * @param l
     */
    public void addListener(SolutionEvaluationListener l)
    {
        solutionEvaluationListeners.add(l);
    }
    
    /**
     * Deregister a listener 
     * @param l
     * @return - if the listener was previously registered and is now successfully deregistered
     */
    public boolean removeListener(SolutionEvaluationListener l)
    {
    	return solutionEvaluationListeners.remove(l);
    }

    /**
     * Registers the stop condition with the problem
     * @param s
     */
    public void addStopCondition(StopCondition s)
    {
    	stopConditions.add(s);
    }
    
    /**
     * Registers a set of stop conditions with the problem
     * @param s
     */
    public void addStopConditions(LinkedList<StopCondition> s)
    {
    	stopConditions.addAll(s);
    }
    
    /**
     * Remove a stop condition from the problem
     * @param s
     * @return true if the stop condition was previously registered and was removed successfully.
     */
    public boolean removeStopCondition(StopCondition s)
    {
    	return stopConditions.remove(s);
    }

    /**
     * Provides access to all listeners to solution evaluations
     * @return - list of all registered solution evaluation listeners
     */
    public LinkedList<SolutionEvaluationListener> getSolutionEvaluationListeners()
	{
		return solutionEvaluationListeners;
	}

    /**
     * Provides access to all stop conditions of the problem
     * @return - all registered stop conditions
     */
	public LinkedList<StopCondition> getStopConditions()
	{
		return stopConditions;
	}	

	@Override
    public String toString()
    {
        return getName();
    }

	@Override
    public int compareTo(Problem o)
    {
        return getName().compareTo(o.getName());
    }

    /**
     * Called before the problem is considered in a run
     * Default implementation is empty. Should be overriden for any pre-run initialisation needs.
     * @throws InitialisationException
     */
    public void initialiseBeforeRun()
        throws InitialisationException
    {}
   
    /**
     * Called after the problem is considered in a run. Default implementation is empty.
     * Should be overriden for any post-run clean-up
     * @throws InitialisationException
     */
    public void cleanupAfterRun()
    	throws InitialisationException
    {}

    
    @Override
    public String getDetails()
    {
    	StringBuffer b = new StringBuffer();
    	b.append("Name="+getName()+", ");
    	b.append("Minimization="+isMinimization());
    	return b.toString();
    }
    
    /**
     * Scans the stop conditions to decide whether or not further evaluations are possible
     * @return - true if none of the stop conditions have been triggered
     */
    public boolean canEvaluate()
    {
    	for(StopCondition s : stopConditions)
    	{
    		// check for stop for any reason
    		if(s.mustStop())
    		{
    			return false;
    		}
    	}
    	
    	return true;
    }
    

    @Override
	public void validateConfiguration() throws InvalidConfigurationException
	{
    	if(stopConditions.isEmpty())
    	{
    		throw new InvalidConfigurationException("No stop condition's defined");
    	}
    	// internal configuration validation
    	validateConfigurationInternal();
	}    
    /**
     * Override this to validate any user-configurable parameters of the problem instance.
     * Any implementations must 
     * @throws InvalidConfigurationException
     */
    protected void validateConfigurationInternal() throws InvalidConfigurationException
    {}
 
    @Override
    public String getConfigurationDetails()
    {
        return BeanUtils.getBeanDetails(this);
    }
	
	@Override
	public void populateFromInstance(Problem other)
	{
		// values
		BeanUtils.beanPopulate(other, this);
		// stop conditions				
		addStopConditions(other.getStopConditions());
		// internal prep
		try
		{
			initialiseBeforeRun();
			cleanupAfterRun();
		}
		catch (InitialisationException e)
		{
			throw new RuntimeException("Error: " + e.getMessage(), e);
		}
	}


	@Override
	public void populateFromString(String s)
	{		
		BeanUtils.populateBeanFromString(s, this);
		// internal prep
		try
		{
			initialiseBeforeRun();
			cleanupAfterRun();
		}
		catch (InitialisationException e)
		{
			throw new RuntimeException("Error: " + e.getMessage(), e);
		}
	}


	@Override
	public String toPopulateString()
	{
		return BeanUtils.getBeanDetails(this);
	}
    
    

    /**
     * Calculate the problem specific costing of the provided solution.
     * This is only called if the number of function evaluations is below 
     * the specified maximum
     * @param solution
     * @return - a scoring for the solution, cannot be NaN or Infinity
     */
    protected abstract double problemSpecificCost(Solution solution) throws SolutionEvaluationException;    
    /**
     * Checks the provided solution to ensure it is valid and able to be evaluated.
     * If invalid, an AlgorithmRunException should be thrown
     * @param solution
     * @throws SolutionEvaluationException
     */
    public abstract void checkSolutionForSafety(Solution solution) throws SolutionEvaluationException;
    /**
     * Whether or not the goal of this problem instance is to locate the smallest
     * (minimal) cost value
     * @return - true if this problem is a minimization (of cost or score) problem
     */
    public abstract boolean isMinimization();
}
