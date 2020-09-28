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
package com.oat.domains.gcp;

import java.util.Arrays;

import com.oat.Algorithm;
import com.oat.Domain;
import com.oat.DomainUtils;
import com.oat.Problem;
import com.oat.explorer.domains.gcp.gui.panels.GCPMasterPanel;
import com.oat.explorer.gui.panels.MasterPanel;
import com.oat.utils.FileUtils;

/**
 * 
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
public class GCPDomain extends Domain
{
	public final static String ALGORITHM_LIST_FILE = "algorithms.gcp.properties";
	public final static String PROBLEM_LIST_FILE = "problems.gcp.properties";
	
	
	@Override
	public String getHumanReadableName()
	{
		return "Graph Coloring Problem";
	}

	@Override
	public String getShortName()
	{
		return "GCP";
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
        Problem[] list = new GCProblem[classList.length];
        
        for (int i = 0; i < classList.length; i++)
        {
            try
            {
                list[i] = new GCProblem(classList[i]);
            }
            catch (Exception e)
            {
                throw new Exception("Unable to load problem class from properties file: " + classList[i]+"\n"+e.getMessage(), e);
            }
        }
        Arrays.sort(list);
        return list;
	}

	@Override
	public MasterPanel getExplorerPanel()
	{
		return new GCPMasterPanel();
	}
}
