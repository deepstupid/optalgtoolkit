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
package com.oat.domains.tsp;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import com.oat.Algorithm;
import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.Problem;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.domains.tsp.probes.PercentageOfOptimal;
import com.oat.domains.tsp.stopconditions.FoundOptimaStopCondition;
import com.oat.explorer.domains.tsp.gui.panels.TSPMasterPanel;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.utils.FileUtils;

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
public class TSPDomain extends Domain
{
	public final static String ALGORITHM_LIST_FILE = "algorithms.tsp.properties";
	public final static String PROBLEM_LIST_FILE = "problems.tsp.properties";    

	@Override
	public String getHumanReadableName()
	{
		return "Traveling Salesman Problem";
	}

	@Override
	public String getShortName()
	{
		return "TSP";
	}

	@Override
	public Algorithm[] loadAlgorithmList() throws Exception
	{
		return DomainUtils.defaultLoadAlgorithmList(ALGORITHM_LIST_FILE);
	}

	@Override
	public Problem[] loadProblemList() throws Exception
	{
        String [] classList = FileUtils.loadClassList(PROBLEM_LIST_FILE);
        Problem[] list = new TSPProblem[classList.length];
        
        for (int i = 0; i < classList.length; i++)
        {
            try
            {
                String problemFile = classList[i];
                String solutionFile = problemFile.substring(0, problemFile.indexOf('.')) + ".opt.tour";
                list[i] = new TSPProblem(problemFile, solutionFile);
            }
            catch (Exception e)
            {
                throw new Exception("Unable to load problem class from properties file: " + classList[i]);
            }
        }
        Arrays.sort(list);
        return list;
	}

	@Override
	public MasterPanel getExplorerPanel()
	{
		return new TSPMasterPanel();
	}
	
	@Override
	public LinkedList<StopCondition> loadDomainStopConditions()
	{
		LinkedList<StopCondition> list = super.loadDomainStopConditions();
		list.add(new FoundOptimaStopCondition());		
		Collections.sort(list);		
		return list;
	}
	

	@Override
	public LinkedList<RunProbe> loadDomainRunProbes()
	{
		LinkedList<RunProbe> list = super.loadDomainRunProbes();
		
		list.add(new PercentageOfOptimal());
		
		Collections.sort(list);
		
		return list;
	}
}
