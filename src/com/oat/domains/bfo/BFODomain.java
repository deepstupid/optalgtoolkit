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
package com.oat.domains.bfo;

import com.oat.Algorithm;
import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.Problem;
import com.oat.explorer.domains.bfo.gui.panels.BFOMasterPanel;
import com.oat.explorer.gui.panels.MasterPanel;

/**
 * Description: 
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
public class BFODomain extends Domain
{
	public static String ALGORITHM_LIST_FILE = "algorithms.bfo.properties";
	public static String PROBLEM_LIST_FILE = "problems.bfo.properties";

	@Override
	public String getHumanReadableName()
	{
		return "Binary Function Optimization";
	}

	@Override
	public String getShortName()
	{
		return "BFO";
	}

	@Override
	public Algorithm[] loadAlgorithmList()
		throws Exception
	{
		return DomainUtils.defaultLoadAlgorithmList(ALGORITHM_LIST_FILE);
	}

	@Override
	public Problem [] loadProblemList()
		throws Exception
	{
		return DomainUtils.defaultLoadProblemList(PROBLEM_LIST_FILE);
	}
	
	@Override
	public MasterPanel getExplorerPanel()
	{
		return new BFOMasterPanel();
	}
}
