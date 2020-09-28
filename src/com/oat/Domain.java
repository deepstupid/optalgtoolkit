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

import java.util.Collections;
import java.util.LinkedList;

import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.probes.BestScoreProbe;
import com.oat.probes.BestSolutionProbe;
import com.oat.probes.RunTimeMillisProbe;
import com.oat.probes.TotalEvaluationsProbe;
import com.oat.stopcondition.EvaluationConvergenceStopCondition;
import com.oat.stopcondition.EvaluationsStopCondition;
import com.oat.stopcondition.LackOfImprovementStopCondition;
import com.oat.stopcondition.RunTimeStopCondition;


/**
 * Description: Represents a collection of problem instances and algorithms (techniques) to address
 * those problems. A domain provides a classification or taxonomic grouping of algorithms and problems
 *  
 * Date: 20/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class Domain implements Comparable<Domain>
{
	
	/**
	 * Returns the master explorer panel for this domain, may be null
	 * @return - GUI element for the explorer interface, may be null if not supported
	 */
	public abstract MasterPanel getExplorerPanel();	
	
	/**
	 * Load a list of algorithms for the domain
	 * @return - list of all supported algorithm instances
	 * @throws Exception
	 */
	public abstract Algorithm [] loadAlgorithmList() throws Exception;
	/**
	 *  Load a list of problems for a domain
	 * @return - list of all supported problem instances
	 * @throws Exception
	 */
	public abstract Problem [] loadProblemList() throws Exception;	
	/**
	 * Returns the human readable name for a domain
	 * @return - human readable name of the domain
	 */
	public abstract String getHumanReadableName();
	/**
	 * Returns the short (package name) for a domain
	 * @return - short name for the domain
	 */
	public abstract String getShortName();
	
	@Override
	public int compareTo(Domain o)
	{
		return getShortName().compareTo(o.getShortName());
	}
	
	@Override
	public String toString()
	{
		return getShortName();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Domain)
		{
			return getShortName().equals(((Domain)o).getShortName());
		}
		
		return false;
	}
	
	
	/**
	 * Override to add domain specific stop conditions or customize the list
	 * @return - A list of stop conditions suitable for algorithm-problem runs in this domain
	 */
	public LinkedList<StopCondition> loadDomainStopConditions()
	{
		LinkedList<StopCondition> list = new LinkedList<StopCondition>();
		list.add(new EvaluationConvergenceStopCondition());
		list.add(new EvaluationsStopCondition());
		list.add(new RunTimeStopCondition());
		list.add(new LackOfImprovementStopCondition());
		
		Collections.sort(list);
		
		return list;
	}
	
	/**
	 * Override to add domain specific probes
	 * @return - list of run probes suitable for this domain
	 */
	public LinkedList<RunProbe> loadDomainRunProbes()
	{
		LinkedList<RunProbe> list = new LinkedList<RunProbe>();
		
		list.add(new BestSolutionProbe());
		list.add(new BestScoreProbe());
		list.add(new RunTimeMillisProbe());
		list.add(new TotalEvaluationsProbe());
		
		Collections.sort(list);
		
		return list;
	}
}
