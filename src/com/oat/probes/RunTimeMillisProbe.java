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
package com.oat.probes;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.RunProbe;

/**
 * Description: 
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
public class RunTimeMillisProbe extends RunProbe
{	
	protected long startTime;
	protected long endTime;
	

	@Override
	public String getName()
	{
		return "Run Time Milliseconds";
	}

	@Override
	public Object getProbeObservation()
	{
		if(startTime == 0)
		{
			return "No Start";
		}
		else if(endTime == 0)
		{
			return "No End";
		}
		
		return endTime - startTime;
	}

	@Override
    public void initialiseBeforeRun(Problem p, Algorithm a) throws InitialisationException
    {
    	super.initialiseBeforeRun(p, a);
    	startTime = System.currentTimeMillis();
    }
    
	@Override
    public void cleanupAfterRun(Problem p, Algorithm a) throws InitialisationException
    {
    	super.cleanupAfterRun(p, a);
    	endTime = System.currentTimeMillis();
    }
	
	@Override 
	public void reset()
	{
		startTime = endTime = 0;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public long getEndTime()
	{
		return endTime;
	}	
}
