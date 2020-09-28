/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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

import java.util.Date;

import com.oat.utils.BeanUtils;

/**
 * Date: 30/07/2007<br/>
 * <br/>
 * Description: Generic stop condition, used to stop a problem-algorithm run
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 03/09/2007	JBrownlee	Reorganized internal structure to be more reusable and generic
 * 
 * </pre>
 *
 */
public abstract class StopCondition 
	implements Configurable, Comparable<StopCondition>, UserFriendly, Populator<StopCondition>
{
	/**
	 * Whether or not the stop condition was triggered
	 */
	protected volatile boolean triggered;
	/**
	 * The date time of the trigger, null if not triggered
	 */
	protected Date triggeredDateTime;		
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    /**
     * Validate any configuration for the stop condition
     * @throws InvalidConfigurationException
     */
    public void validateConfiguration() throws InvalidConfigurationException
    {}
    
    /**
     * Implementation of the stop condition
     * @return - true if the stop condition should be triggered
     */
    public abstract boolean mustStopInternal();
    
    
    /**
     * Prepare the stop condition before a run, default implementation calls reset
     * @param p
     * @param a
     * @throws InitialisationException
     */
    public void initialiseBeforeRun(Problem p, Algorithm a)
    	throws InitialisationException
    {
    	// reset before run
    	reset();
    }
    
    /**
     * Clean-up the stop condition after it has been triggered, after then end of the run.
     * Default implementation is empty.
     * @param p
     * @param a
     * @throws InitialisationException
     */
    public void cleanupAfterRun(Problem p, Algorithm a)
		throws InitialisationException
	{}
    
    /**
     * Whether or not the stop condition has been triggered. This is the primary interface into repeatedly
     * checking the stop condition.
     * @return - true if the stop condition has just been triggered or has been triggered in a previous call
     */
    public boolean mustStop()
    {
    	// assume no stoppage
    	boolean mustStop = false;
    	
    	// check if already triggered
    	if(triggered)
    	{
    		mustStop = true;
    	}
    	// check stop condition
    	else if(mustStopInternal())    		
    	{
    		triggered = true;
    		triggeredDateTime = new Date();
    		mustStop = true; 
    	}
    	
    	return mustStop;
    }    
    
    /**
     * Resets the internal state of the stop condition
     * Should be ovveriden if such functionality is required.
     */
    public void reset()
    {
    	triggered = false;
    	triggeredDateTime = null;
    }
    
    @Override
    public String getDetails()
    {
    	StringBuffer b = new StringBuffer();
    	b.append("Name="+getName()+",");
    	b.append("Triggered="+isTriggered()+",");
    	b.append("TriggeredTime="+getTriggeredDateTime());
    	return b.toString();
    }
    
    /**
     * Whether or not the stop condition was triggered, that mustStop() returned true at least once
     * @return - true if mustStop() returned true at least once during a run
     */
	public boolean isTriggered()
	{
		return triggered;
	}

	/**
	 * The date/time the stop condition was triggered
	 * @return - Date object, or null if not triggered
	 */
	public Date getTriggeredDateTime()
	{
		return triggeredDateTime;
	}

	@Override
	public int compareTo(StopCondition o)
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
	public void populateFromInstance(StopCondition other)
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
