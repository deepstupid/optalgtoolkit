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

import com.oat.Solution;

/**
 * Description:  Records the number of evaluations
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
public class TotalEvaluationsProbe extends GenericSolutionEvaluatedProbe
{
    protected long completedEvaluations;

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		completedEvaluations++;
	}

	@Override
	public String getName()
	{
		return "Total Evaluations";
	}

	@Override
	public Object getProbeObservation()
	{
		return new Long(completedEvaluations);
	}
	
	@Override
    public void reset()
    {
    	completedEvaluations = 0;
    }

	public long getCompletedEvaluations()
	{
		return completedEvaluations;
	}
	
}
