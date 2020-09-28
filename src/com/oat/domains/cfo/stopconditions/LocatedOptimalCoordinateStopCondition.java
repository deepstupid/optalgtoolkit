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
package com.oat.domains.cfo.stopconditions;

import java.util.Arrays;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.stopcondition.GeneticSolutionEvaluatedStopCondition;

/**
 * Description: 
 *  
 * Date: 05/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class LocatedOptimalCoordinateStopCondition extends
		GeneticSolutionEvaluatedStopCondition
{
	protected CFOSolution [] knownOptima;
	protected boolean locatedOptima;

	@Override
	public String getName()
	{
		return "Located Optimal Coordinate";
	}

	@Override
	public boolean mustStopInternal()
	{
		return locatedOptima;
	}

	@Override
	public void solutionEvaluatedEvent(Solution evaluatedSolution)
	{
		CFOSolution s = (CFOSolution) evaluatedSolution;
		
		for (int i = 0; !locatedOptima && i < knownOptima.length; i++)
		{
			if(Arrays.equals(s.getCoordinate(), knownOptima[i].getCoordinate()))
			{
				locatedOptima = true;
			}
		}
	}
	
	@Override
    public void initialiseBeforeRun(Problem p, Algorithm a)
		throws InitialisationException
	{
		super.initialiseBeforeRun(p, a);
		
		CFOProblemInterface cfo = (CFOProblemInterface) p;
		knownOptima = cfo.getGlobalOptima();
		if(knownOptima==null)
		{
			throw new InitialisationException("Unable to use stop condition, problem contans no known global optima!");
		}
	}
	
	@Override
    public void reset()
    {
    	super.reset();
    	knownOptima = null;
    	locatedOptima = false;
    }
	
	@Override
    public boolean isUserConfigurable()
    {
    	return false;
    }

}
