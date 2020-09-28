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

/**
 * Description: A generic probe for gathering information about a problem-algorithm run
 *  
 * Date: 10/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class RunProbe
	implements Comparable<RunProbe>, UserFriendly
{    
    /**
     * Provides access to the observation made during a run
     * @return - observation made during a run
     */
    public abstract Object getProbeObservation();    
    
	
	@Override
	public String getDetails()
	{
		return "";
	}    
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    /**
     * Resets the probe before a run. Default implementation is empty.
     * Should be overriden to reset this probe
     */
    public void reset()
    {}
    
    
    /**
     * Initializes the probe before a run. calls reset. should be overriden to apply any required initialisation
     * of the probe
     * @param p
     * @param a
     * @throws InitialisationException
     */
    public void initialiseBeforeRun(Problem p, Algorithm a) throws InitialisationException
    {
    	reset();
    }
    
    /**
     * Clean up the resources used by the probe at the end of a run. Default implementation is empty.
     * @param p
     * @param a
     * @throws InitialisationException
     */
    public void cleanupAfterRun(Problem p, Algorithm a) throws InitialisationException
    {}


	@Override
	public int compareTo(RunProbe o)
	{
		return getName().compareTo(o.getName());
	}
}
