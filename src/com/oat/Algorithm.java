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
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Generic algorithm definition
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Modified such that bestEver and lastExecutionTime are private
 *                          This means that each algorithm implementation cannot use bestEver, and thus
 *                          must use a stack variable - cleaner
 * 23/12/2006   JBrownlee   Refactored some methods and instance variables
 * 27/12/2006   JBrownlee   Adjusted run path to use a listener to locate the best solution
 *                          found from a run
 * 15/01/2007   JBrownlee   Adjusted executeAndWait() so that the listener is removed in the finally block
 *                          stops zombie listeners sticking around.
 * 11/07/2007   JBrownlee   Added support for automatic configuration
 * 04/09/2007	JBrownlee	Refactored methods and interfaces, collect information using probes
 * </pre>
 * 
 */
public abstract class Algorithm 
	implements Comparable<Algorithm>, Configurable, UserFriendly, Populator<Algorithm>
{
	/**
	 * Collection of listeners interested in algorithms that signal epoch competion events 
	 */
    protected final LinkedList<AlgorithmEpochCompleteListener> listeners;

    /**
     * Constructor
     */
    public Algorithm()
    {
        listeners = new LinkedList<AlgorithmEpochCompleteListener>();
    }
    
    /**
     * Mechanism that allows interested algorithms to trigger an epoch completion event
     * Such algorithms should instead extend from EpochAlgorithm if possible. 
     * @param <S>
     * @param p
     * @param pop
     */
    protected <S extends Solution> void triggerIterationCompleteEvent(Problem p, LinkedList<S> pop)
    {
    	if(pop==null || pop.isEmpty())
    	{
    		return;
    	}
    	
        for (AlgorithmEpochCompleteListener n : listeners)
        {
            n.epochCompleteEvent(p, pop);
        }
    }
  
    /**
     * Mechanism that allows interested algorithms to trigger an epoch completion event
     * Such algorithms should instead extend from EpochAlgorithm if possible. 
     * @param p
     * @param pop
     */
    protected void triggerIterationCompleteEvent(Problem p, Solution [] pop)
    {
        LinkedList<Solution> l = new LinkedList<Solution>();
        for (int i = 0; i < pop.length; i++)
        {
            l.add(pop[i]);
        }        
        triggerIterationCompleteEvent(p, l);
    }

    /**
     * Accessor for all listeners interested in epoch completion events
     * @return - All registered listeners
     */ 
    public LinkedList<AlgorithmEpochCompleteListener> getListeners()
    {
        return listeners;
    }
    /**
     * Registers a listeners for epoch completion events
     * @param l
     */
    public void addAlgorithmIterationCompleteListener(AlgorithmEpochCompleteListener l)
    {
        listeners.add(l);
    }
    /**
     * Removes a registered listener
     * @param l
     * @return - whether or not the listener to be removed was indeed registered and successfully removed
     */
    public boolean removeAlgorithmIterationCompleteListener(AlgorithmEpochCompleteListener l)
    {       
        return listeners.remove(l);
    }    
	
    /**
     * Called before the algorithm is executed on the problem. Should be overriden
     * by algorithms interested in pre-run initialisation 
     * @param problem
     * @throws InitialisationException
     */
	public void initialiseBeforeRun(Problem problem)
		throws InitialisationException
	{}
		
	/**
	 * Called after an algorithm has been executed on a problem. Should be overriden
	 * by algorithms interested in post-run cleanup (such as releasing unneed resources)
	 * @param problem
	 * @throws InitialisationException
	 */
	public void cleanupAfterRun(Problem problem)
		throws InitialisationException
	{}
	
	/**
	 * Primary interface for executing an algorithm on a problem. Assumes that 
	 * suitable initialisation and cleanup on algorithms problems is managed elsewhere
	 * @param aProblem
	 * @throws AlgorithmRunException
	 * @throws SolutionEvaluationException
	 */
    public void executeAndWait(Problem aProblem)        
		throws AlgorithmRunException, SolutionEvaluationException
	{        
	    internalExecuteAlgorithm(aProblem);
	}	
  
    /**
     * Required functionality for a technique to implement their problem solving capability
     * @param aProblem
     * @throws AlgorithmRunException
     * @throws SolutionEvaluationException
     */
    protected abstract void internalExecuteAlgorithm(Problem aProblem) 
    	throws AlgorithmRunException, SolutionEvaluationException;

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int compareTo(Algorithm o)
    {
        return getName().compareTo(o.getName());
    }
    
    @Override
    public boolean isUserConfigurable()
    {
    	return true;
    }
    
    @Override
    public String getConfigurationDetails()
    {
        return BeanUtils.getBeanDetails(this);
    }
    
    @Override
    public String getDetails()
    {
    	return "";
    }

	@Override
	public void populateFromInstance(Algorithm other)
	{
		BeanUtils.beanPopulate(other, this);		
	}

	@Override
	public void populateFromString(String s)
	{		
		BeanUtils.populateBeanFromString(s, this);
	}

	@Override
	public String toPopulateString()
	{
		return BeanUtils.getBeanDetails(this);
	}
}
